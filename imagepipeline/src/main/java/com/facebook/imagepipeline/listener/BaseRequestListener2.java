// (c) Facebook, Inc. and its affiliates. Confidential and proprietary.

package com.facebook.imagepipeline.listener;

import androidx.annotation.NonNull;
import com.facebook.imagepipeline.producers.ProducerContext;
import java.util.Map;
import javax.annotation.Nullable;

public class BaseRequestListener2 implements RequestListener2 {

  @Override
  public void onRequestStart(final @NonNull ProducerContext producerContext) { }

  @Override
  public void onRequestSuccess(final @NonNull ProducerContext producerContext) { }

  @Override
  public void onRequestFailure(final @NonNull ProducerContext producerContext, final Throwable throwable) { }

  @Override
  public void onRequestCancellation(final @NonNull ProducerContext producerContext) { }

  @Override
  public void onProducerStart(
      final @NonNull ProducerContext producerContext, final @NonNull String producerName) { }

  @Override
  public void onProducerEvent(
      final @NonNull ProducerContext producerContext,
      final @NonNull String producerName,
      final @NonNull String eventName) { }

  @Override
  public void onProducerFinishWithSuccess(
      final @NonNull ProducerContext producerContext,
      final @NonNull String producerName,
      final @Nullable Map<String, String> extraMap) { }

  @Override
  public void onProducerFinishWithFailure(
      final @NonNull ProducerContext producerContext,
      final String producerName,
      final Throwable t,
      final @Nullable Map<String, String> extraMap) { }

  @Override
  public void onProducerFinishWithCancellation(
      final @NonNull ProducerContext producerContext,
      final @NonNull String producerName,
      final @Nullable Map<String, String> extraMap) { }

  @Override
  public void onUltimateProducerReached(
      final @NonNull ProducerContext producerContext, final @NonNull String producerName, final boolean successful) { }

  @Override
  public boolean requiresExtraMap(
      final @NonNull ProducerContext producerContext, final @NonNull String producerName) {
    return false;
  }
}
