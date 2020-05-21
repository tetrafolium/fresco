/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.testing;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TestScheduledExecutorService extends TestExecutorService
    implements ScheduledExecutorService {

  public TestScheduledExecutorService(final FakeClock fakeClock) {
    super(fakeClock);
  }

  @Override
  public ScheduledFuture<?> schedule(final Runnable runnable, final long delay, final TimeUnit timeUnit) {
    return new TestScheduledFuture(
        getFakeClock(), scheduledQueue, TimeUnit.MILLISECONDS.convert(delay, timeUnit), runnable);
  }

  @Override
  public <V> ScheduledFuture<V> schedule(final Callable<V> callable, final long delay, final TimeUnit timeUnit) {
    return new TestScheduledFuture<V>(
        getFakeClock(), scheduledQueue, TimeUnit.MILLISECONDS.convert(delay, timeUnit), callable);
  }

  @Override
  public ScheduledFuture<?> scheduleAtFixedRate(
      final Runnable runnable, final long initialDelay, final long period, final TimeUnit timeUnit) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ScheduledFuture<?> scheduleWithFixedDelay(
      final Runnable runnable, final long initialDelay, final long delay, final TimeUnit timeUnit) {
    throw new UnsupportedOperationException();
  }
}
