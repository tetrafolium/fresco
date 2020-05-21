/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.producers;

/**
 * Swallow result producer.
 *
 * <p>This producer just inserts a consumer that swallows results into the stack of consumers.
 */
public class SwallowResultProducer<T> implements Producer<Void> {
  private final Producer<T> mInputProducer;

  public SwallowResultProducer(final Producer<T> inputProducer) {
    mInputProducer = inputProducer;
  }

  @Override
  public void produceResults(final Consumer<Void> consumer, final ProducerContext producerContext) {
    DelegatingConsumer<T, Void> swallowResultConsumer =
        new DelegatingConsumer<T, Void>(consumer) {
          @Override
          protected void onNewResultImpl(final T newResult, final @Status int status) {
            if (isLast(status)) {
              getConsumer().onNewResult(null, status);
            }
          }
        };
    mInputProducer.produceResults(swallowResultConsumer, producerContext);
  }
}
