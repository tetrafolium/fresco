/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.cache;

import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.imagepipeline.image.CloseableImage;
import javax.annotation.Nullable;

public class BitmapCountingMemoryCacheFactory {

  public static CountingMemoryCache<CacheKey, CloseableImage> get(
      final Supplier<MemoryCacheParams> bitmapMemoryCacheParamsSupplier,
      final MemoryTrimmableRegistry memoryTrimmableRegistry,
      final @Nullable CountingMemoryCache.EntryStateObserver<CacheKey> observer) {
    return get(
        bitmapMemoryCacheParamsSupplier,
        memoryTrimmableRegistry,
        new BitmapMemoryCacheTrimStrategy(),
        observer);
  }

  public static CountingMemoryCache<CacheKey, CloseableImage> get(
      final Supplier<MemoryCacheParams> bitmapMemoryCacheParamsSupplier,
      final MemoryTrimmableRegistry memoryTrimmableRegistry,
      final CountingMemoryCache.CacheTrimStrategy trimStrategy,
      final @Nullable CountingMemoryCache.EntryStateObserver<CacheKey> observer) {

    ValueDescriptor<CloseableImage> valueDescriptor =
        new ValueDescriptor<CloseableImage>() {
          @Override
          public int getSizeInBytes(final CloseableImage value) {
            return value.getSizeInBytes();
          }
        };

    CountingMemoryCache<CacheKey, CloseableImage> countingCache =
        new CountingMemoryCache<>(
            valueDescriptor, trimStrategy, bitmapMemoryCacheParamsSupplier, observer);

    memoryTrimmableRegistry.registerMemoryTrimmable(countingCache);

    return countingCache;
  }
}
