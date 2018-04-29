package org.ray.spi.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.ray.api.UniqueID;
import org.ray.spi.ObjectStoreLink;
import org.ray.spi.model.RayBuffer;
import org.ray.util.logger.RayLog;


/**
 * The PlasmaClient is used to interface with a plasma store and manager.
 *
 * The PlasmaClient can ask the PlasmaStore to allocate a new buffer, seal a buffer, and get a
 * buffer. Buffers are referred to by object IDs.
 */
public class PlasmaClient implements ObjectStoreLink {

  private final long conn;

  protected void finalize() {
    PlasmaClientJNI.disconnect(this.conn);
  }

  // use plasma client to initialize the underlying jni system as well via config and config-overwrites
  public PlasmaClient(String configFile, String configOverwrites, String storeSocketName,
      String managerSocketName, int releaseDelay) {
    this.conn = PlasmaClientJNI.connect(
        configFile, configOverwrites, storeSocketName, managerSocketName, releaseDelay);
  }

  // interface methods --------------------

  @Override
  public void put(UniqueID objectId, byte[] value, byte[] metadata) {
    ByteBuffer buf = null;
    try {
      buf = PlasmaClientJNI.create(conn, objectId.getBytes(),
          value.length, metadata);
    } catch (PlasmaObjectExistsException | PlasmaOutOfMemoryException e) {
      // TODO print logs;
      RayLog.core.error("ObjectId " + objectId + " error at PlasmaClient put", e);
    }
    if (buf == null) {
      return;
    }

    buf.put(value);
    PlasmaClientJNI.seal(conn, objectId.getBytes());
    PlasmaClientJNI.release(conn, objectId.getBytes());
  }

  @Override
  public List<RayBuffer> get(List<UniqueID> objectIds, int timeoutMs, boolean isMetadata) {
    byte[][] ids = getIdBytes(objectIds);
    ByteBuffer[][] bufs = PlasmaClientJNI.get(conn, ids, timeoutMs);
    assert bufs.length == objectIds.size();

    List<RayBuffer> ret = new ArrayList<>();
    for (int i = 0; i < bufs.length; i++) {
      UniqueID oid = objectIds.get(i);
      ByteBuffer buf = bufs[i][isMetadata ? 1 : 0];
      if (buf == null) {
        ret.add(new RayBuffer(null, null));
      } else {
        byte[] bb = new byte[buf.remaining()];
        buf.get(bb);
        ret.add(new RayBuffer(bb, (byte[] b) -> this.release(oid)));
      }
    }
    return ret;
  }

  private static byte[][] getIdBytes(List<UniqueID> objectIds) {
    int size = objectIds.size();
    byte[][] ids = new byte[size][];
    for (int i = 0; i < size; i++) {
      ids[i] = objectIds.get(i).getBytes();
    }
    return ids;
  }

  @Override
  public List<UniqueID> wait(List<UniqueID> objectIds, int timeoutMs, int numReturns) {
    byte[][] ids = getIdBytes(objectIds);
    byte[][] readys = PlasmaClientJNI.wait(conn, ids, timeoutMs, numReturns);

    List<UniqueID> ret = new ArrayList<>();
    for (byte[] ready : readys) {
      for (UniqueID id : objectIds) {
        if (Arrays.equals(ready, id.getBytes())) {
          ret.add(id);
          break;
        }
      }
    }

    assert (ret.size() == readys.length);
    return ret;
  }

  @Override
  public byte[] hash(UniqueID objectId) {
    return PlasmaClientJNI.hash(conn, objectId.getBytes());
  }

  @Override
  public void fetch(List<UniqueID> objectIds) {
    byte[][] ids = getIdBytes(objectIds);
    PlasmaClientJNI.fetch(conn, ids);
  }

  @Override
  public long evict(long numBytes) {
    return PlasmaClientJNI.evict(conn, numBytes);
  }

  // wrapper methods --------------------

  /**
   * Seal the buffer in the PlasmaStore for a particular object ID.
   * Once a buffer has been sealed, the buffer is immutable and can only be accessed through get.
   *
   * @param objectId used to identify an object.
   */
  public void seal(UniqueID objectId) {
    PlasmaClientJNI.seal(conn, objectId.getBytes());
  }

  /**
   * Notify Plasma that the object is no longer needed.
   *
   * @param objectId used to identify an object.
   */
  public void release(UniqueID objectId) {
    PlasmaClientJNI.release(conn, objectId.getBytes());
  }

  /**
   * Check if the object is present and has been sealed in the PlasmaStore.
   *
   * @param objectId used to identify an object.
   */
  public boolean contains(UniqueID objectId) {
    return PlasmaClientJNI.contains(conn, objectId.getBytes());
  }
}
