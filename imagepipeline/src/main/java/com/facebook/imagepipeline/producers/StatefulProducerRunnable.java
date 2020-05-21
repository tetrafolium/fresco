/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.producers;

import com.facebook.common.executors.StatefulRunnable;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * {@link StatefulRunnable} intended to be used by producers.
 *
 * <p>Class implements common functionality related to handling producer instrumentation and
 * resource management.
 */
public abstract class StatefulProducerRunnable<T> extends StatefulRunnable<T> {

  private final Consumer<T> mConsumer;
  private final ProducerListener2 mProducerListener;
  private final String mProducerName;
  private final ProducerContext mProducerContext;
  private final @Nullable String mOrigin;
  private final @Nullable String mOriginSubcategory;

  public StatefulProducerRunnable(
      final Consumer<T> consumer,
      final ProducerListener2 producerListener,
      final ProducerContext producerContext,
      final String producerName,
      final @Nullable String origin,
      final @Nullable String originSubcategory) {
    mConsumer = consumer;
    mProducerListener = producerListener;
    mProducerName = producerName;
    mProducerContext = producerContext;
    mOrigin = origin;
    mOriginSubcategory = originSubcategory;

    mProducerListener.onProducerStart(mProducerContext, mProducerName);
  }

  @Override
  protected void onSuccess(final T result) {
    mProducerListener.onProducerFinishWithSuccess(
        mProducerContext,
        mProducerName,
        mProducerListener.requiresExtraMap(mProducerContext, mProducerName)
            ? getExtraMapOnSuccess(result)
            : null);
    mProducerContext.putOriginExtra(mOrigin, mOriginSubcategory);
    mConsumer.onNewResult(result, Consumer.IS_LAST);
  }

  @Override
  protected void onFailure(final Exception e) {
    mProducerListener.onProducerFinishWithFailure(
        mProducerContext,
        mProducerName,
        e,
        mProducerListener.requiresExtraMap(mProducerContext, mProducerName)
            ? getExtraMapOnFailure(e)
            : null);
    mProducerContext.putOriginExtra(mOrigin, mOriginSubcategory);
    mConsumer.onFailure(e);
  }

  @Override
  protected void onCancellation() {
    mProducerListener.onProducerFinishWithCancellation(
        mProducerContext,
        mProducerName,
        mProducerListener.requiresExtraMap(mProducerContext, mProducerName)
            ? getExtraMapOnCancellation()
            : null);
    mProducerContext.putOriginExtra(mOrigin, mOriginSubcategory);
    mConsumer.onCancellation();
  }

  /** Create extra map for result */
  protected @Nullable Map<String, String> getExtraMapOnSuccess(final T result) {
    return null;
  }

  /** Create extra map for exception */
  protected @Nullable Map<String, String> getExtraMapOnFailure(final Exception exception) {
    return null;
  }

  /** Create extra map for cancellation */
  protected @Nullable Map<String, String> getExtraMapOnCancellation() {
    return null;
  }

  @Override
  protected abstract void disposeResult(T result);
}
