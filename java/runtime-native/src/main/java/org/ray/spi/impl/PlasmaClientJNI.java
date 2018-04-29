package org.ray.spi.impl;

import java.nio.ByteBuffer;

/**
 * JNI static methods for PlasmaClient
 */
public class PlasmaClientJNI {

  native public static long connect(String configFile, String overwrites, String store_socket_name,
      String manager_socket_name, int release_delay);

  native public static void disconnect(long conn);

  native public static ByteBuffer create(long conn, byte[] object_id, int size, byte[] metadata)
      throws PlasmaObjectExistsException, PlasmaOutOfMemoryException;

  native public static byte[] hash(long conn, byte[] object_id);

  native public static void seal(long conn, byte[] object_id);

  native public static void release(long conn, byte[] object_id);

  native public static ByteBuffer[][] get(long conn, byte[][] object_ids, int timeout_ms);

  native public static boolean contains(long conn, byte[] object_id);

  native public static void fetch(long conn, byte[][] object_ids);

  native public static byte[][] wait(long conn, byte[][] object_ids, int timeout_ms,
      int num_returns);

  native public static long evict(long conn, long num_bytes);

}
