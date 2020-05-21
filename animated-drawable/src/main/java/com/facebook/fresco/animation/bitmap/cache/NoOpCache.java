/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.fresco.animation.bitmap.cache;

import android.graphics.Bitmap;
import com.facebook.common.references.CloseableReference;
import com.facebook.fresco.animation.bitmap.BitmapAnimationBackend;
import com.facebook.fresco.animation.bitmap.BitmapFrameCache;
import javax.annotation.Nullable;

/** No-op bitmap cache that doesn't do anything. */
public class NoOpCache implements BitmapFrameCache {

  @Nullable
  @Override
  public CloseableReference<Bitmap> getCachedFrame(final int frameNumber) {
    return null;
  }

  @Nullable
  @Override
  public CloseableReference<Bitmap> getFallbackFrame(final int frameNumber) {
    return null;
  }

  @Nullable
  @Override
  public CloseableReference<Bitmap> getBitmapToReuseForFrame(
      final int frameNumber, final int width, final int height) {
    return null;
  }

  @Override
  public boolean contains(final int frameNumber) {
    return false;
  }

  @Override
  public int getSizeInBytes() {
    return 0;
  }

  @Override
  public void clear() {
    // no-op
  }

  @Override
  public void onFrameRendered(
      final int frameNumber,
      final CloseableReference<Bitmap> bitmapReference,
      final @BitmapAnimationBackend.FrameType int frameType) {
    // no-op
  }

  @Override
  public void onFramePrepared(
      final int frameNumber,
      final CloseableReference<Bitmap> bitmapReference,
      final @BitmapAnimationBackend.FrameType int frameType) {
    // Does not cache anything
  }

  @Override
  public void setFrameCacheListener(final FrameCacheListener frameCacheListener) {
    // Does not cache anything
  }
}
