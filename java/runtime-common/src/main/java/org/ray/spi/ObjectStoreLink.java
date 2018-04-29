package org.ray.spi;

import java.util.Collections;
import java.util.List;
import org.ray.api.UniqueID;
import org.ray.spi.model.RayBuffer;

/**
 * Object store interface, which provides the capabilities to put and get raw byte array, and serves
 * as the underlying data store of {@code org.ray.spi.ObjectStoreProxy}.
 */
public interface ObjectStoreLink {

  int GET_TIMEOUT_MS = 1000;
  int WAIT_TIMEOUT_MS = 1 << 30;

  /**
   * Put value in the local plasma store with object ID <tt>objectId</tt>.
   *
   * @param objectId The object ID of the value to be put.
   * @param value The value to put in the object store.
   * @param metadata encodes whatever metadata the user wishes to encode.
   */
  void put(UniqueID objectId, byte[] value, byte[] metadata);

  /**
   * Create a buffer from the PlasmaStore based on the <tt>objectId</tt>.
   *
   * @param objectId The object ID used to identify the object.
   * @param timeoutMs The number of milliseconds that the get call should block before timing out
   * and returning. Pass -1 if the call should block and 0 if the call should return immediately.
   * @param isMetadata false if get data, otherwise get metadata.
   * @return A PlasmaBuffer wrapping the object.
   */
  default RayBuffer get(UniqueID objectId, int timeoutMs, boolean isMetadata) {
    return get(Collections.singletonList(objectId), timeoutMs, isMetadata).get(0);
  }

  /**
   * Create buffers from the PlasmaStore based on <tt>objectIds</tt>.
   *
   * @param objectIds List of object IDs used to identify some objects.
   * @param timeoutMs The number of milliseconds that the get call should block before timing out
   * and returning. Pass -1 if the call should block and 0 if the call should return immediately.
   * @param isMetadata false if get data, otherwise get metadata.
   * @return List of PlasmaBuffers wrapping objects.
   */
  List<RayBuffer> get(List<UniqueID> objectIds, int timeoutMs, boolean isMetadata);

  /**
   * Wait until <tt>numReturns</tt> objects in <tt>objectIds</tt> are ready.
   *
   * @param objectIds List of object IDs to wait for.
   * @param timeoutMs Return to the caller after <tt>timeoutMs</tt> milliseconds.
   * @param numReturns We are waiting for this number of objects to be ready.
   * @return List of object IDs that are ready
   */
  List<UniqueID> wait(List<UniqueID> objectIds, int timeoutMs, int numReturns);

  /**
   * Compute the hash of an object in the object store.
   *
   * @param objectId The object ID used to identify the object.
   * @return A digest byte array contains object's SHA256 hash. <tt>null</tt> means that the object
   * isn't in the object store.
   */
  byte[] hash(UniqueID objectId);

  /**
   * Fetch the object with the given ID from other plasma manager instances.
   *
   * @param objectId The object ID used to identify the object.
   */
  default void fetch(UniqueID objectId) {
    fetch(Collections.singletonList(objectId));
  }

  /**
   * Fetch the objects with the given IDs from other plasma manager instances.
   *
   * @param objectIds List of object IDs used to identify the objects.
   */
  void fetch(List<UniqueID> objectIds);

  /**
   * Evict some objects to recover given count of bytes.
   *
   * @param numBytes The number of bytes to attempt to recover.
   * @return The number of bytes that have been evicted.
   */
  long evict(long numBytes);
}
