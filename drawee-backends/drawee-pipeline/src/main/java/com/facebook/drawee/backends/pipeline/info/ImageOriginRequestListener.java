/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.drawee.backends.pipeline.info;

import com.facebook.imagepipeline.listener.BaseRequestListener;
import javax.annotation.Nullable;

/**
 * Image origin request listener that maps all image requests for a given Drawee controller to an
 * {@link ImageOrigin} and corresponding {@link ImageOriginListener}.
 */
public class ImageOriginRequestListener extends BaseRequestListener {

  private String mControllerId;
  private final @Nullable ImageOriginListener mImageOriginLister;

  public ImageOriginRequestListener(
      final String controllerId, final @Nullable ImageOriginListener imageOriginLister) {
    mImageOriginLister = imageOriginLister;
    init(controllerId);
  }

  /**
   * Re-initialize the listener in case the underlying controller ID changes.
   *
   * @param controllerId the new controller ID
   */
  public void init(final String controllerId) {
    mControllerId = controllerId;
  }

  @Override
  public void onUltimateProducerReached(
      final String requestId, final String ultimateProducerName, final boolean successful) {
    if (mImageOriginLister != null) {
      mImageOriginLister.onImageLoaded(
          mControllerId,
          ImageOriginUtils.mapProducerNameToImageOrigin(ultimateProducerName),
          successful,
          ultimateProducerName);
    }
  }
}
