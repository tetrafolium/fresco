/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.drawee.backends.pipeline.info.internal;

import com.facebook.drawee.backends.pipeline.info.ImageLoadStatus;
import com.facebook.drawee.backends.pipeline.info.ImageOrigin;
import com.facebook.drawee.backends.pipeline.info.ImageOriginListener;
import com.facebook.drawee.backends.pipeline.info.ImagePerfMonitor;
import com.facebook.drawee.backends.pipeline.info.ImagePerfState;

public class ImagePerfImageOriginListener implements ImageOriginListener {

  private final ImagePerfState mImagePerfState;
  private final ImagePerfMonitor mImagePerfMonitor;

  public ImagePerfImageOriginListener(
      final ImagePerfState imagePerfState, final ImagePerfMonitor imagePerfMonitor) {
    mImagePerfState = imagePerfState;
    mImagePerfMonitor = imagePerfMonitor;
  }

  @Override
  public void onImageLoaded(
      final String controllerId,
      final @ImageOrigin int imageOrigin,
      final boolean successful,
      final String ultimateProducerName) {
    mImagePerfState.setImageOrigin(imageOrigin);
    mImagePerfState.setUltimateProducerName(ultimateProducerName);
    mImagePerfMonitor.notifyStatusUpdated(mImagePerfState, ImageLoadStatus.ORIGIN_AVAILABLE);
  }
}
