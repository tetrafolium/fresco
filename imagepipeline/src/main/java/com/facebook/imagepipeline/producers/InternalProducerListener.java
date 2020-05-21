/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.producers;

import java.util.Map;
import javax.annotation.Nullable;

public class InternalProducerListener implements ProducerListener2 {

  private final ProducerListener mProducerListener;
  private final @Nullable ProducerListener2 mProducerListener2;

  public InternalProducerListener(
      final ProducerListener producerListener, final @Nullable ProducerListener2 producerListener2) {
    mProducerListener = producerListener;
    mProducerListener2 = producerListener2;
  }

  public ProducerListener getProducerListener() {
    return mProducerListener;
  }

  @Nullable
  public ProducerListener2 getProducerListener2() {
    return mProducerListener2;
  }

  @Override
  public void onProducerStart(final ProducerContext context, final String producerName) {
    if (mProducerListener != null) {
      mProducerListener.onProducerStart(context.getId(), producerName);
    }
    if (mProducerListener2 != null) {
      mProducerListener2.onProducerStart(context, producerName);
    }
  }

  @Override
  public void onProducerEvent(final ProducerContext context, final String producerName, final String eventName) {
    if (mProducerListener != null) {
      mProducerListener.onProducerEvent(context.getId(), producerName, eventName);
    }
    if (mProducerListener2 != null) {
      mProducerListener2.onProducerEvent(context, producerName, eventName);
    }
  }

  @Override
  public void onProducerFinishWithSuccess(
      final ProducerContext context, final String producerName, final @Nullable Map<String, String> extraMap) {
    if (mProducerListener != null) {
      mProducerListener.onProducerFinishWithSuccess(context.getId(), producerName, extraMap);
    }
    if (mProducerListener2 != null) {
      mProducerListener2.onProducerFinishWithSuccess(context, producerName, extraMap);
    }
  }

  @Override
  public void onProducerFinishWithFailure(
      final ProducerContext context,
      final String producerName,
      final Throwable t,
      final @Nullable Map<String, String> extraMap) {
    if (mProducerListener != null) {
      mProducerListener.onProducerFinishWithFailure(context.getId(), producerName, t, extraMap);
    }
    if (mProducerListener2 != null) {
      mProducerListener2.onProducerFinishWithFailure(context, producerName, t, extraMap);
    }
  }

  @Override
  public void onProducerFinishWithCancellation(
      final ProducerContext context, final String producerName, final @Nullable Map<String, String> extraMap) {
    if (mProducerListener != null) {
      mProducerListener.onProducerFinishWithCancellation(context.getId(), producerName, extraMap);
    }
    if (mProducerListener2 != null) {
      mProducerListener2.onProducerFinishWithCancellation(context, producerName, extraMap);
    }
  }

  @Override
  public void onUltimateProducerReached(
      final ProducerContext context, final String producerName, final boolean successful) {
    if (mProducerListener != null) {
      mProducerListener.onUltimateProducerReached(context.getId(), producerName, successful);
    }
    if (mProducerListener2 != null) {
      mProducerListener2.onUltimateProducerReached(context, producerName, successful);
    }
  }

  @Override
  public boolean requiresExtraMap(final ProducerContext context, final String producerName) {
    boolean required = false;
    if (mProducerListener != null) {
      required = mProducerListener.requiresExtraMap(context.getId());
    }
    if (!required && mProducerListener2 != null) {
      required = mProducerListener2.requiresExtraMap(context, producerName);
    }
    return required;
  }
}
