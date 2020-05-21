/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.listener;

import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.infer.annotation.OkToExtend;
import java.util.Map;
import javax.annotation.Nullable;

@OkToExtend
public class BaseRequestListener implements RequestListener {

  @Override
  public void onRequestStart(
      final ImageRequest request, final Object callerContext, final String requestId, final boolean isPrefetch) { }

  @Override
  public void onRequestSuccess(final ImageRequest request, final String requestId, final boolean isPrefetch) { }

  @Override
  public void onRequestFailure(
      final ImageRequest request, final String requestId, final Throwable throwable, final boolean isPrefetch) { }

  @Override
  public void onRequestCancellation(final String requestId) { }

  @Override
  public void onProducerStart(final String requestId, final String producerName) { }

  @Override
  public void onProducerEvent(final String requestId, final String producerName, final String eventName) { }

  @Override
  public void onProducerFinishWithSuccess(
      final String requestId, final String producerName, final @Nullable Map<String, String> extraMap) { }

  @Override
  public void onProducerFinishWithFailure(
      final String requestId, final String producerName, final Throwable t, final @Nullable Map<String, String> extraMap) { }

  @Override
  public void onProducerFinishWithCancellation(
      final String requestId, final String producerName, final @Nullable Map<String, String> extraMap) { }

  @Override
  public void onUltimateProducerReached(
      final String requestId, final String producerName, final boolean successful) { }

  @Override
  public boolean requiresExtraMap(final String requestId) {
    return false;
  }
}
