/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.cache;

import com.facebook.cache.common.CacheKey;

/** Class that does no stats tracking at all */
public class NoOpImageCacheStatsTracker implements ImageCacheStatsTracker {
  private static NoOpImageCacheStatsTracker sInstance = null;

  private NoOpImageCacheStatsTracker() { }

  public static synchronized NoOpImageCacheStatsTracker getInstance() {
    if (sInstance == null) {
      sInstance = new NoOpImageCacheStatsTracker();
    }
    return sInstance;
  }

  @Override
  public void onBitmapCachePut(final CacheKey cacheKey) { }

  @Override
  public void onBitmapCacheHit(final CacheKey cacheKey) { }

  @Override
  public void onBitmapCacheMiss(final CacheKey cacheKey) { }

  @Override
  public void onMemoryCachePut(final CacheKey cacheKey) { }

  @Override
  public void onMemoryCacheHit(final CacheKey cacheKey) { }

  @Override
  public void onMemoryCacheMiss(final CacheKey cacheKey) { }

  @Override
  public void onStagingAreaHit(final CacheKey cacheKey) { }

  @Override
  public void onStagingAreaMiss(final CacheKey cacheKey) { }

  @Override
  public void onDiskCacheHit(final CacheKey cacheKey) { }

  @Override
  public void onDiskCacheMiss(final CacheKey cacheKey) { }

  @Override
  public void onDiskCacheGetFail(final CacheKey cacheKey) { }

  @Override
  public void onDiskCachePut(final CacheKey cacheKey) { }

  @Override
  public void registerBitmapMemoryCache(final MemoryCache<?, ?> bitmapMemoryCache) { }

  @Override
  public void registerEncodedMemoryCache(final MemoryCache<?, ?> encodedMemoryCache) { }
}
