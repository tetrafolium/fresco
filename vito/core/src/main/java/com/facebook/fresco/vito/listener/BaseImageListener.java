/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.fresco.vito.listener;

import android.graphics.drawable.Drawable;
import com.facebook.drawee.backends.pipeline.info.ImageOrigin;
import com.facebook.fresco.ui.common.DimensionsInfo;
import com.facebook.imagepipeline.image.ImageInfo;
import javax.annotation.Nullable;

public class BaseImageListener implements ImageListener {

  @Override
  public void onSubmit(final long id, final Object callerContext) { }

  @Override
  public void onPlaceholderSet(final long id, final @Nullable Drawable placeholder) { }

  @Override
  public void onFinalImageSet(
      final long id,
      final @ImageOrigin int imageOrigin,
      final @Nullable ImageInfo imageInfo,
      final @Nullable Drawable drawable) { }

  @Override
  public void onIntermediateImageSet(final long id, final @Nullable ImageInfo imageInfo) { }

  @Override
  public void onIntermediateImageFailed(final long id, final Throwable throwable) { }

  @Override
  public void onFailure(final long id, final @Nullable Drawable error, final Throwable throwable) { }

  @Override
  public void onRelease(final long id) { }

  @Override
  public void onImageDrawn(final String id, final ImageInfo imageInfo, final DimensionsInfo dimensionsInfo) { }
}
