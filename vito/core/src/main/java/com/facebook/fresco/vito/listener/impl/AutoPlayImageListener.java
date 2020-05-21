/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.fresco.vito.listener.impl;

import android.graphics.drawable.Drawable;
import com.facebook.drawee.backends.pipeline.info.ImageOrigin;
import com.facebook.fresco.animation.drawable.AnimatedDrawable2;
import com.facebook.fresco.vito.listener.BaseImageListener;
import com.facebook.imagepipeline.image.ImageInfo;
import javax.annotation.Nullable;

/** Autoplays animated images */
public class AutoPlayImageListener extends BaseImageListener {

  private static @Nullable AutoPlayImageListener INSTANCE;

  private AutoPlayImageListener() { }

  public static AutoPlayImageListener getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new AutoPlayImageListener();
    }
    return INSTANCE;
  }

  @Override
  public void onSubmit(final long id, final Object callerContext) { }

  @Override
  public void onPlaceholderSet(final long id, final @Nullable Drawable placeholder) { }

  public void onFinalImageSet(
      final long id,
      final @ImageOrigin int imageOrigin,
      final @Nullable ImageInfo imageInfo,
      final @Nullable Drawable drawable) {
    if (drawable instanceof AnimatedDrawable2) {
      ((AnimatedDrawable2) drawable).start();
    }
  }

  @Override
  public void onIntermediateImageSet(final long id, final @Nullable ImageInfo imageInfo) { }

  @Override
  public void onIntermediateImageFailed(final long id, final Throwable throwable) { }

  @Override
  public void onFailure(final long id, final @Nullable Drawable error, final Throwable throwable) { }

  @Override
  public void onRelease(final long id) { }
}
