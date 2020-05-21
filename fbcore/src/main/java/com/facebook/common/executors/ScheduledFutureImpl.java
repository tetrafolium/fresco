/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.common.executors;

import android.os.Handler;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;

/** A {@link ScheduledFuture} for {@link HandlerExecutorServiceImpl}. */
public class ScheduledFutureImpl<V> implements RunnableFuture<V>, ScheduledFuture<V> {

  private final Handler mHandler;
  private final FutureTask<V> mListenableFuture;

  public ScheduledFutureImpl(final Handler handler, final Callable<V> callable) {
    mHandler = handler;
    mListenableFuture = new FutureTask<V>(callable);
  }

  public ScheduledFutureImpl(final Handler handler, final Runnable runnable, final @Nullable V result) {
    mHandler = handler;
    mListenableFuture = new FutureTask<V>(runnable, result);
  }

  @Override
  public long getDelay(final TimeUnit unit) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int compareTo(final Delayed other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void run() {
    mListenableFuture.run();
  }

  @Override
  public boolean cancel(final boolean mayInterruptIfRunning) {
    return mListenableFuture.cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled() {
    return mListenableFuture.isCancelled();
  }

  @Override
  public boolean isDone() {
    return mListenableFuture.isDone();
  }

  @Override
  public V get() throws InterruptedException, ExecutionException {
    return mListenableFuture.get();
  }

  @Override
  public V get(final long timeout, final TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    return mListenableFuture.get(timeout, unit);
  }
}
