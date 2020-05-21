/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.fresco.vito.core.impl;

import android.graphics.drawable.Drawable;
import com.facebook.drawee.backends.pipeline.info.ImageOrigin;
import com.facebook.drawee.backends.pipeline.info.internal.ImagePerfControllerListener2;
import com.facebook.fresco.ui.common.ControllerListener2;
import com.facebook.fresco.vito.core.CombinedImageListener;
import com.facebook.fresco.vito.core.VitoImageRequest;
import com.facebook.fresco.vito.core.VitoImageRequestListener;
import com.facebook.fresco.vito.core.VitoUtils;
import com.facebook.fresco.vito.listener.ImageListener;
import com.facebook.imagepipeline.image.ImageInfo;
import javax.annotation.Nullable;

public class CombinedImageListenerImpl implements CombinedImageListener {

  private @Nullable VitoImageRequestListener mVitoImageRequestListener;
  private @Nullable ImageListener mImageListener;
  private ControllerListener2<ImageInfo> mControllerListener2 =
      ImagePerfControllerListener2.getNoOpListener();

  @Override
  public void setImageListener(final @Nullable ImageListener imageListener) {
    mImageListener = imageListener;
  }

  @Override
  public void setVitoImageRequestListener(
      final @Nullable VitoImageRequestListener vitoImageRequestListener) {
    mVitoImageRequestListener = vitoImageRequestListener;
  }

  @Override
  public void setControllerListener2(final ControllerListener2<ImageInfo> controllerListener2) {
    mControllerListener2 = controllerListener2;
  }

  @Override
  public void onSubmit(final long id, final VitoImageRequest imageRequest, final @Nullable Object callerContext) {
    if (mVitoImageRequestListener != null) {
      mVitoImageRequestListener.onSubmit(id, imageRequest, callerContext);
    }
    if (mImageListener != null) {
      mImageListener.onSubmit(id, callerContext);
    }
    mControllerListener2.onSubmit(VitoUtils.getStringId(id), callerContext);
  }

  @Override
  public void onPlaceholderSet(
      final long id, final VitoImageRequest imageRequest, final @Nullable Drawable placeholder) {
    if (mVitoImageRequestListener != null) {
      mVitoImageRequestListener.onPlaceholderSet(id, imageRequest, placeholder);
    }
    if (mImageListener != null) {
      mImageListener.onPlaceholderSet(id, placeholder);
    }
  }

  @Override
  public void onFinalImageSet(
      final long id,
      final VitoImageRequest imageRequest,
      final @ImageOrigin int imageOrigin,
      final @Nullable ImageInfo imageInfo,
      final @Nullable ControllerListener2.Extras extras,
      final @Nullable Drawable drawable) {
    if (mVitoImageRequestListener != null) {
      mVitoImageRequestListener.onFinalImageSet(
          id, imageRequest, imageOrigin, imageInfo, extras, drawable);
    }
    if (mImageListener != null) {
      mImageListener.onFinalImageSet(id, imageOrigin, imageInfo, drawable);
    }
    mControllerListener2.onFinalImageSet(VitoUtils.getStringId(id), imageInfo, extras);
  }

  @Override
  public void onIntermediateImageSet(
      final long id, final VitoImageRequest imageRequest, final @Nullable ImageInfo imageInfo) {
    if (mVitoImageRequestListener != null) {
      mVitoImageRequestListener.onIntermediateImageSet(id, imageRequest, imageInfo);
    }
    if (mImageListener != null) {
      mImageListener.onIntermediateImageSet(id, imageInfo);
    }
    mControllerListener2.onIntermediateImageSet(VitoUtils.getStringId(id), imageInfo);
  }

  @Override
  public void onIntermediateImageFailed(
      final long id, final VitoImageRequest imageRequest, final Throwable throwable) {
    if (mVitoImageRequestListener != null) {
      mVitoImageRequestListener.onIntermediateImageFailed(id, imageRequest, throwable);
    }
    if (mImageListener != null) {
      mImageListener.onIntermediateImageFailed(id, throwable);
    }
    mControllerListener2.onIntermediateImageFailed(VitoUtils.getStringId(id));
  }

  @Override
  public void onFailure(
      final long id,
      final VitoImageRequest imageRequest,
      final @Nullable Drawable error,
      final @Nullable Throwable throwable) {
    if (mVitoImageRequestListener != null) {
      mVitoImageRequestListener.onFailure(id, imageRequest, error, throwable);
    }
    if (mImageListener != null) {
      mImageListener.onFailure(id, error, throwable);
    }
    mControllerListener2.onFailure(VitoUtils.getStringId(id), throwable);
  }

  @Override
  public void onRelease(final long id, final VitoImageRequest imageRequest) {
    if (mVitoImageRequestListener != null) {
      mVitoImageRequestListener.onRelease(id, imageRequest);
    }
    if (mImageListener != null) {
      mImageListener.onRelease(id);
    }
    mControllerListener2.onRelease(VitoUtils.getStringId(id));
  }
}
