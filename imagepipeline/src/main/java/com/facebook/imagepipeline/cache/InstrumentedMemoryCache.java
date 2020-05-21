/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.cache;

import com.facebook.common.internal.Predicate;
import com.facebook.common.references.CloseableReference;

public class InstrumentedMemoryCache<K, V> implements MemoryCache<K, V> {

  private final MemoryCache<K, V> mDelegate;
  private final MemoryCacheTracker mTracker;

  public InstrumentedMemoryCache(final MemoryCache<K, V> delegate, final MemoryCacheTracker tracker) {
    mDelegate = delegate;
    mTracker = tracker;
  }

  @Override
  public CloseableReference<V> get(final K key) {
    CloseableReference<V> result = mDelegate.get(key);
    if (result == null) {
      mTracker.onCacheMiss(key);
    } else {
      mTracker.onCacheHit(key);
    }
    return result;
  }

  @Override
  public void probe(final K key) {
    mDelegate.probe(key);
  }

  @Override
  public CloseableReference<V> cache(final K key, final CloseableReference<V> value) {
    mTracker.onCachePut(key);
    return mDelegate.cache(key, value);
  }

  @Override
  public int removeAll(final Predicate<K> predicate) {
    return mDelegate.removeAll(predicate);
  }

  @Override
  public boolean contains(final Predicate<K> predicate) {
    return mDelegate.contains(predicate);
  }

  @Override
  public boolean contains(final K key) {
    return mDelegate.contains(key);
  }

  @Override
  public int getCount() {
    return mDelegate.getCount();
  }

  @Override
  public int getSizeInBytes() {
    return mDelegate.getSizeInBytes();
  }
}
