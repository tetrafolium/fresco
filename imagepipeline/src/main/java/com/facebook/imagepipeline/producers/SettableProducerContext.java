/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.producers;

import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/** ProducerContext that allows the client to change its internal state. */
@ThreadSafe
public class SettableProducerContext extends BaseProducerContext {

  public SettableProducerContext(final ProducerContext context) {
    this(
        context.getImageRequest(),
        context.getId(),
        context.getUiComponentId(),
        context.getProducerListener(),
        context.getCallerContext(),
        context.getLowestPermittedRequestLevel(),
        context.isPrefetch(),
        context.isIntermediateResultExpected(),
        context.getPriority(),
        context.getImagePipelineConfig());
  }

  public SettableProducerContext(final ImageRequest overrideRequest, final ProducerContext context) {
    this(
        overrideRequest,
        context.getId(),
        context.getUiComponentId(),
        context.getProducerListener(),
        context.getCallerContext(),
        context.getLowestPermittedRequestLevel(),
        context.isPrefetch(),
        context.isIntermediateResultExpected(),
        context.getPriority(),
        context.getImagePipelineConfig());
  }

  public SettableProducerContext(
      final ImageRequest imageRequest,
      final String id,
      final ProducerListener2 producerListener,
      final Object callerContext,
      final ImageRequest.RequestLevel lowestPermittedRequestLevel,
      final boolean isPrefetch,
      final boolean isIntermediateResultExpected,
      final Priority priority,
      final ImagePipelineConfig imagePipelineConfig) {
    super(
        imageRequest,
        id,
        producerListener,
        callerContext,
        lowestPermittedRequestLevel,
        isPrefetch,
        isIntermediateResultExpected,
        priority,
        imagePipelineConfig);
  }

  public SettableProducerContext(
      final ImageRequest imageRequest,
      final String id,
      final @Nullable String uiComponentId,
      final ProducerListener2 producerListener,
      final Object callerContext,
      final ImageRequest.RequestLevel lowestPermittedRequestLevel,
      final boolean isPrefetch,
      final boolean isIntermediateResultExpected,
      final Priority priority,
      final ImagePipelineConfig imagePipelineConfig) {
    super(
        imageRequest,
        id,
        uiComponentId,
        producerListener,
        callerContext,
        lowestPermittedRequestLevel,
        isPrefetch,
        isIntermediateResultExpected,
        priority,
        imagePipelineConfig);
  }

  /**
   * Set whether the request is a prefetch request or not.
   *
   * @param isPrefetch
   */
  public void setIsPrefetch(final boolean isPrefetch) {
    BaseProducerContext.callOnIsPrefetchChanged(setIsPrefetchNoCallbacks(isPrefetch));
  }

  /**
   * Set whether intermediate result is expected or not
   *
   * @param isIntermediateResultExpected
   */
  public void setIsIntermediateResultExpected(final boolean isIntermediateResultExpected) {
    BaseProducerContext.callOnIsIntermediateResultExpectedChanged(
        setIsIntermediateResultExpectedNoCallbacks(isIntermediateResultExpected));
  }

  /**
   * Set the priority of the request
   *
   * @param priority
   */
  public void setPriority(final Priority priority) {
    BaseProducerContext.callOnPriorityChanged(setPriorityNoCallbacks(priority));
  }
}
