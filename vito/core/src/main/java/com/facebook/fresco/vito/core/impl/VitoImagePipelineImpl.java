/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.fresco.vito.core.impl;

import android.content.res.Resources;
import android.net.Uri;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.fresco.vito.core.ImagePipelineUtils;
import com.facebook.fresco.vito.core.VitoImagePipeline;
import com.facebook.fresco.vito.core.VitoImageRequest;
import com.facebook.fresco.vito.core.VitoUtils;
import com.facebook.fresco.vito.options.ImageOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.multiuri.MultiUri;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import javax.annotation.Nullable;

/** Vito image pipeline to fetch an image for a given VitoImageRequest. */
public class VitoImagePipelineImpl implements VitoImagePipeline {

  private final ImagePipeline mImagePipeline;
  private final ImagePipelineUtils mImagePipelineUtils;

  public VitoImagePipelineImpl(final ImagePipeline imagePipeline, final ImagePipelineUtils imagePipelineUtils) {
    mImagePipeline = imagePipeline;
    mImagePipelineUtils = imagePipelineUtils;
  }

  @Override
  public VitoImageRequest createImageRequest(
      final Resources resources,
      final @Nullable Uri uri,
      final @Nullable MultiUri multiUri,
      final @Nullable ImageOptions options,
      final @Nullable Object callerContext) {
    if (options == null) {
      options = ImageOptions.defaults();
    }
    ImageRequest imageRequest = mImagePipelineUtils.buildImageRequest(uri, options);
    return new VitoImageRequest(
        resources,
        uri,
        multiUri,
        options,
        imageRequest,
        mImagePipeline.getCacheKey(imageRequest, callerContext));
  }

  @Override
  @Nullable
  public CloseableReference<CloseableImage> getCachedImage(final VitoImageRequest imageRequest) {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("VitoImagePipeline#getCachedImage");
    }
    try {
      CloseableReference<CloseableImage> cachedImageReference =
          mImagePipeline.getCachedImage(imageRequest.cacheKey);
      if (CloseableReference.isValid(cachedImageReference)) {
        return cachedImageReference;
      }
      return null;
    } finally {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
    }
  }

  @Override
  public DataSource<CloseableReference<CloseableImage>> fetchDecodedImage(
      final VitoImageRequest imageRequest,
      final @Nullable Object callerContext,
      final @Nullable RequestListener requestListener,
      final @Nullable long uiComponentId) {
    return mImagePipeline.fetchDecodedImage(
        imageRequest.imageRequest,
        callerContext,
        ImageRequest.RequestLevel.FULL_FETCH,
        mImagePipeline.getRequestListenerForRequest(imageRequest.imageRequest, requestListener),
        VitoUtils.getStringId(uiComponentId));
  }
}
