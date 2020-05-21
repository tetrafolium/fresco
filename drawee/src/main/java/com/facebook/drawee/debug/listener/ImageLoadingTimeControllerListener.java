/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.drawee.debug.listener;

import android.graphics.drawable.Animatable;
import com.facebook.drawee.controller.BaseControllerListener;
import javax.annotation.Nullable;

/**
 * Currently we are measuring this from Submit to Final Image.But can be extended to include
 * intermediate time and failure cases also
 */
public class ImageLoadingTimeControllerListener extends BaseControllerListener {

  private long mRequestSubmitTimeMs = -1L;

  private long mFinalImageSetTimeMs = -1L;

  private @Nullable ImageLoadingTimeListener mImageLoadingTimeListener;

  public ImageLoadingTimeControllerListener(
      final @Nullable ImageLoadingTimeListener imageLoadingTimeListener) {
    mImageLoadingTimeListener = imageLoadingTimeListener;
  }

  @Override
  public void onSubmit(final String id, final Object callerContext) {
    mRequestSubmitTimeMs = System.currentTimeMillis();
  }

  @Override
  public void onFinalImageSet(
      final String id, final @Nullable Object imageInfo, final @Nullable Animatable animatable) {
    mFinalImageSetTimeMs = System.currentTimeMillis();
    if (mImageLoadingTimeListener != null) {
      mImageLoadingTimeListener.onFinalImageSet(mFinalImageSetTimeMs - mRequestSubmitTimeMs);
    }
  }
}
