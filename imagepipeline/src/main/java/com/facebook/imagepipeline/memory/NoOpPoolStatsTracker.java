/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.memory;

/** Empty implementation of PoolStatsTracker that does not perform any tracking. */
public class NoOpPoolStatsTracker implements PoolStatsTracker {
  private static NoOpPoolStatsTracker sInstance = null;

  private NoOpPoolStatsTracker() { }

  public static synchronized NoOpPoolStatsTracker getInstance() {
    if (sInstance == null) {
      sInstance = new NoOpPoolStatsTracker();
    }
    return sInstance;
  }

  @Override
  public void setBasePool(final BasePool basePool) { }

  @Override
  public void onValueReuse(final int bucketedSize) { }

  @Override
  public void onSoftCapReached() { }

  @Override
  public void onHardCapReached() { }

  @Override
  public void onAlloc(final int size) { }

  @Override
  public void onFree(final int sizeInBytes) { }

  @Override
  public void onValueRelease(final int sizeInBytes) { }
}
