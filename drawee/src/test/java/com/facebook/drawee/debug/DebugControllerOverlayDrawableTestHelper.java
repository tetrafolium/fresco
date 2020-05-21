/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.drawee.debug;

import static org.junit.Assert.assertEquals;

import com.facebook.drawee.drawable.ScalingUtils;

public class DebugControllerOverlayDrawableTestHelper {

  public DebugControllerOverlayDrawable mOverlayDrawable;

  public DebugControllerOverlayDrawableTestHelper() {
    mOverlayDrawable = new DebugControllerOverlayDrawable();
  }

  public void assertOverlayColorOk(
      final int imageWidth,
      final int imageHeight,
      final int drawableWidth,
      final int drawableHeight,
      final ScalingUtils.ScaleType scaleType) {
    mOverlayDrawable.setBounds(0, 0, drawableWidth, drawableHeight);
    assertEquals(
        DebugControllerOverlayDrawable.TEXT_COLOR_IMAGE_OK,
        mOverlayDrawable.determineSizeHintColor(imageWidth, imageHeight, scaleType));
  }

  public void assertOverlayColorAlmostOk(
      final int imageWidth,
      final int imageHeight,
      final int drawableWidth,
      final int drawableHeight,
      final ScalingUtils.ScaleType scaleType) {
    mOverlayDrawable.setBounds(0, 0, drawableWidth, drawableHeight);
    assertEquals(
        DebugControllerOverlayDrawable.TEXT_COLOR_IMAGE_ALMOST_OK,
        mOverlayDrawable.determineSizeHintColor(imageWidth, imageHeight, scaleType));
  }

  public void assertOverlayColorNotOk(
      final int imageWidth,
      final int imageHeight,
      final int drawableWidth,
      final int drawableHeight,
      final ScalingUtils.ScaleType scaleType) {
    mOverlayDrawable.setBounds(0, 0, drawableWidth, drawableHeight);
    assertEquals(
        DebugControllerOverlayDrawable.TEXT_COLOR_IMAGE_NOT_OK,
        mOverlayDrawable.determineSizeHintColor(imageWidth, imageHeight, scaleType));
  }
}
