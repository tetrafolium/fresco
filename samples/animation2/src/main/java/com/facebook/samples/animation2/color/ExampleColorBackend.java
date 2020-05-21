/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.samples.animation2.color;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import com.facebook.fresco.animation.backend.AnimationBackend;
import com.facebook.samples.animation2.SampleData;

/** Example color backend that takes a list of colors and cycles through them. */
public class ExampleColorBackend implements AnimationBackend {

  /**
   * Creates a simple animation backend that cycles through a list of colors.
   *
   * @return the backend to use
   */
  public static AnimationBackend createSampleColorAnimationBackend(final Resources resources) {
    // Get the animation duration in ms for each color frame
    int frameDurationMs = resources.getInteger(android.R.integer.config_mediumAnimTime);
    // Create and return the backend
    return new ExampleColorBackend(SampleData.COLORS, frameDurationMs);
  }

  private final Paint mPaint = new Paint();
  private final int[] mColors;
  private final int mFrameDurationMs;

  private Rect mBounds;

  private ExampleColorBackend(final int[] colors, final int frameDurationMs) {
    mColors = colors;
    mFrameDurationMs = frameDurationMs;
  }

  @Override
  public int getFrameCount() {
    return mColors.length;
  }

  @Override
  public int getFrameDurationMs(final int frameNumber) {
    return mFrameDurationMs;
  }

  @Override
  public int getLoopCount() {
    return 3;
  }

  @Override
  public boolean drawFrame(final Drawable parent, final Canvas canvas, final int frameNumber) {
    if (mBounds == null) {
      return false;
    }
    mPaint.setColor(mColors[frameNumber]);
    canvas.drawRect(mBounds, mPaint);
    return true;
  }

  @Override
  public void setAlpha(final @IntRange(from = 0, to = 255) int alpha) {
    mPaint.setAlpha(alpha);
  }

  @Override
  public void setColorFilter(final @Nullable ColorFilter colorFilter) {
    mPaint.setColorFilter(colorFilter);
  }

  @Override
  public void setBounds(final Rect bounds) {
    mBounds = bounds;
  }

  @Override
  public int getSizeInBytes() {
    return mColors.length * 4;
  }

  @Override
  public void clear() { }

  @Override
  public int getIntrinsicWidth() {
    return INTRINSIC_DIMENSION_UNSET;
  }

  @Override
  public int getIntrinsicHeight() {
    return INTRINSIC_DIMENSION_UNSET;
  }
}
