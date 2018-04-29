package org.ray.spi.model;

import java.util.function.Consumer;

/**
 * Represents a managed raw byte buffer.
 */
public class RayBuffer {

  private final byte[] buffer;
  private final Consumer<byte[]> finalizer;
  private boolean isReleased = false;

  public RayBuffer(byte[] buffer) {
    this(buffer, null);
  }

  public RayBuffer(byte[] buffer, Consumer<byte[]> finalizer) {
    this.buffer = buffer;
    this.finalizer = finalizer;
  }

  public byte[] buffer() {
    return buffer;
  }

  public void release() {
    if (finalizer != null) {
      assert !isReleased;
      finalizer.accept(buffer);
      isReleased = true;
    }
  }

  protected void finalize() {
    assert finalizer == null || isReleased;
  }
}
