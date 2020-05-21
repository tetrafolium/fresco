/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.memory;

import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.ResourceReleaser;
import java.util.Map;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A special byte-array pool designed to minimize allocations.
 *
 * <p>The length of each bucket's free list is capped at the number of threads using the pool.
 *
 * <p>The free list of each bucket uses {@link OOMSoftReference}s.
 */
@ThreadSafe
public class FlexByteArrayPool {

  private final ResourceReleaser<byte[]> mResourceReleaser;
  @VisibleForTesting final SoftRefByteArrayPool mDelegatePool;

  public FlexByteArrayPool(final MemoryTrimmableRegistry memoryTrimmableRegistry, final PoolParams params) {
    Preconditions.checkArgument(params.maxNumThreads > 0);
    mDelegatePool =
        new SoftRefByteArrayPool(
            memoryTrimmableRegistry, params, NoOpPoolStatsTracker.getInstance());
    mResourceReleaser =
        new ResourceReleaser<byte[]>() {
          @Override
          public void release(final byte[] unused) {
            FlexByteArrayPool.this.release(unused);
          }
        };
  }

  public CloseableReference<byte[]> get(final int size) {
    return CloseableReference.of(mDelegatePool.get(size), mResourceReleaser);
  }

  public void release(final byte[] value) {
    mDelegatePool.release(value);
  }

  public Map<String, Integer> getStats() {
    return mDelegatePool.getStats();
  }

  public int getMinBufferSize() {
    return mDelegatePool.getMinBufferSize();
  }

  @VisibleForTesting
  static class SoftRefByteArrayPool extends GenericByteArrayPool {
    public SoftRefByteArrayPool(
        final MemoryTrimmableRegistry memoryTrimmableRegistry,
        final PoolParams poolParams,
        final PoolStatsTracker poolStatsTracker) {
      super(memoryTrimmableRegistry, poolParams, poolStatsTracker);
    }

    @Override
    Bucket<byte[]> newBucket(final int bucketedSize) {
      return new OOMSoftReferenceBucket<>(
          getSizeInBytes(bucketedSize), mPoolParams.maxNumThreads, 0);
    }
  }
}
