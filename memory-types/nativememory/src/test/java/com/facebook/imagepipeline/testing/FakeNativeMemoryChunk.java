/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.testing;

import com.facebook.imagepipeline.memory.MemoryChunk;
import com.facebook.imagepipeline.memory.NativeMemoryChunk;

/**
 * A fake implementation of {@link com.facebook.imagepipeline.memory.NativeMemoryChunk} to allow us
 * to test out pools and other functionality. This uses byte arrays instead of actual native memory,
 * but supports the same public interface
 */
public class FakeNativeMemoryChunk extends NativeMemoryChunk {
  private byte[] mBuf;

  public FakeNativeMemoryChunk(final int bufSize) {
    super();
    mBuf = new byte[bufSize];
  }

  @Override
  public void close() {
    mBuf = null;
  }

  @Override
  public boolean isClosed() {
    return mBuf == null;
  }

  @Override
  public int getSize() {
    return mBuf.length;
  }

  @Override
  public int write(final int nativeMemoryOffset, final byte[] byteArray, final int byteArrayOffset, final int count) {
    int numToWrite = Math.min(count, mBuf.length - nativeMemoryOffset);
    System.arraycopy(byteArray, byteArrayOffset, mBuf, nativeMemoryOffset, numToWrite);
    return numToWrite;
  }

  @Override
  public byte read(final int nativeMemoryOffset) {
    return mBuf[nativeMemoryOffset];
  }

  @Override
  public int read(final int nativeMemoryOffset, final byte[] byteArray, final int byteArrayOffset, final int count) {
    int numToRead = Math.min(count, mBuf.length - nativeMemoryOffset);
    System.arraycopy(mBuf, nativeMemoryOffset, byteArray, byteArrayOffset, numToRead);
    return numToRead;
  }

  @Override
  public void copy(final int offset, final MemoryChunk other, final int otherOffset, final int count) {
    FakeNativeMemoryChunk that = (FakeNativeMemoryChunk) other;
    int numToCopy = Math.min(count, mBuf.length - offset);
    System.arraycopy(mBuf, offset, that.mBuf, otherOffset, numToCopy);
  }
}
