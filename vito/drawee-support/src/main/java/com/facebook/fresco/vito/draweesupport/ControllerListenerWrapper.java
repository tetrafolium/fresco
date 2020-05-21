/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.fresco.vito.draweesupport;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.drawee.backends.pipeline.info.ImageOrigin;
import com.facebook.drawee.backends.pipeline.info.ImageOriginListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.fresco.ui.common.DimensionsInfo;
import com.facebook.fresco.ui.common.OnDrawControllerListener;
import com.facebook.fresco.vito.listener.ImageListener;
import com.facebook.imagepipeline.image.ImageInfo;
import javax.annotation.Nullable;

public class ControllerListenerWrapper implements ImageListener {

  /**
   * Create a new controller listener wrapper or return null if the listener is null.
   *
   * @param controllerListener the controller listener to wrap
   * @return the wrapped controller listener or null if no wrapping required
   */
  @Nullable
  public static ControllerListenerWrapper create(
      final @Nullable ControllerListener<ImageInfo> controllerListener) {
    return controllerListener == null ? null : new ControllerListenerWrapper(controllerListener);
  }

  private final ControllerListener<ImageInfo> mControllerListener;
  private @Nullable ImageOriginListener mImageOriginListener;

  @VisibleForTesting
  ControllerListenerWrapper(final ControllerListener<ImageInfo> controllerListener) {
    mControllerListener = controllerListener;
  }

  public ControllerListenerWrapper setImageOriginListener(final ImageOriginListener imageOriginListener) {
    mImageOriginListener = imageOriginListener;
    return this;
  }

  @Override
  public void onSubmit(final long id, final Object callerContext) {
    mControllerListener.onSubmit(toStringId(id), callerContext);
  }

  @Override
  public void onPlaceholderSet(final long id, final @Nullable Drawable placeholder) {
    // Not present in old API
  }

  @Override
  public void onFinalImageSet(
      final long id,
      final @ImageOrigin int imageOrigin,
      final @Nullable ImageInfo imageInfo,
      final @Nullable Drawable drawable) {
    String stringId = toStringId(id);
    if (mImageOriginListener != null) {
      mImageOriginListener.onImageLoaded(stringId, imageOrigin, true, "ControllerListenerWrapper");
    }
    Animatable animatable = drawable instanceof Animatable ? (Animatable) drawable : null;
    mControllerListener.onFinalImageSet(stringId, imageInfo, animatable);
  }

  @Override
  public void onIntermediateImageSet(final long id, final @Nullable ImageInfo imageInfo) {
    mControllerListener.onIntermediateImageSet(toStringId(id), imageInfo);
  }

  @Override
  public void onIntermediateImageFailed(final long id, final Throwable throwable) {
    mControllerListener.onIntermediateImageFailed(toStringId(id), throwable);
  }

  @Override
  public void onFailure(final long id, final @Nullable Drawable error, final Throwable throwable) {
    mControllerListener.onFailure(toStringId(id), throwable);
  }

  @Override
  public void onRelease(final long id) {
    mControllerListener.onRelease(toStringId(id));
  }

  private static String toStringId(final long id) {
    return "v" + id;
  }

  @Override
  public void onImageDrawn(final String id, final ImageInfo imageInfo, final DimensionsInfo dimensionsInfo) {
    if (mControllerListener instanceof OnDrawControllerListener) {
      ((OnDrawControllerListener) mControllerListener).onImageDrawn(id, imageInfo, dimensionsInfo);
    }
  }
}
