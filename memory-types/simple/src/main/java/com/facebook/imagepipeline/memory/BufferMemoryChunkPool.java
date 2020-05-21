/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.memory;

import com.facebook.common.internal.DoNotStrip;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import javax.annotation.concurrent.ThreadSafe;

/** Manages a pool of buffer memory chunks ({@link BufferMemoryChunk}) */
@ThreadSafe
@DoNotStrip
public class BufferMemoryChunkPool extends MemoryChunkPool {

  @DoNotStrip
  public BufferMemoryChunkPool(
      final MemoryTrimmableRegistry memoryTrimmableRegistry,
      final PoolParams poolParams,
      final PoolStatsTracker bufferMemoryChunkPoolStatsTracker) {
    super(memoryTrimmableRegistry, poolParams, bufferMemoryChunkPoolStatsTracker);
  }

  @Override
  protected BufferMemoryChunk alloc(final int bucketedSize) {
    return new BufferMemoryChunk(bucketedSize);
  }
}
