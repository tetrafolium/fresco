/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.drawee.backends.pipeline.info.internal;

import com.facebook.common.time.MonotonicClock;
import com.facebook.drawee.backends.pipeline.info.ImagePerfState;
import com.facebook.imagepipeline.listener.BaseRequestListener;
import com.facebook.imagepipeline.request.ImageRequest;

public class ImagePerfRequestListener extends BaseRequestListener {

  private final MonotonicClock mClock;
  private final ImagePerfState mImagePerfState;

  public ImagePerfRequestListener(final MonotonicClock monotonicClock, final ImagePerfState imagePerfState) {
    mClock = monotonicClock;
    mImagePerfState = imagePerfState;
  }

  @Override
  public void onRequestStart(
      final ImageRequest request, final Object callerContext, final String requestId, final boolean isPrefetch) {
    mImagePerfState.setImageRequestStartTimeMs(mClock.now());

    mImagePerfState.setImageRequest(request);
    mImagePerfState.setCallerContext(callerContext);
    mImagePerfState.setRequestId(requestId);
    mImagePerfState.setPrefetch(isPrefetch);
  }

  @Override
  public void onRequestSuccess(final ImageRequest request, final String requestId, final boolean isPrefetch) {
    mImagePerfState.setImageRequestEndTimeMs(mClock.now());

    mImagePerfState.setImageRequest(request);
    mImagePerfState.setRequestId(requestId);
    mImagePerfState.setPrefetch(isPrefetch);
  }

  @Override
  public void onRequestFailure(
      final ImageRequest request, final String requestId, final Throwable throwable, final boolean isPrefetch) {
    mImagePerfState.setImageRequestEndTimeMs(mClock.now());

    mImagePerfState.setImageRequest(request);
    mImagePerfState.setRequestId(requestId);
    mImagePerfState.setPrefetch(isPrefetch);
  }

  @Override
  public void onRequestCancellation(final String requestId) {
    mImagePerfState.setImageRequestEndTimeMs(mClock.now());

    mImagePerfState.setRequestId(requestId);
  }
}
