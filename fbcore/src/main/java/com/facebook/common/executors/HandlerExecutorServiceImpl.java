/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.common.executors;

import android.os.Handler;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

/** A {@link HandlerExecutorService} implementation. */
public class HandlerExecutorServiceImpl extends AbstractExecutorService
    implements HandlerExecutorService {

  private final Handler mHandler;

  public HandlerExecutorServiceImpl(final Handler handler) {
    mHandler = handler;
  }

  @Override
  public void shutdown() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Runnable> shutdownNow() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isShutdown() {
    return false;
  }

  @Override
  public boolean isTerminated() {
    return false;
  }

  @Override
  public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void execute(final Runnable command) {
    mHandler.post(command);
  }

  @Override
  protected <T> ScheduledFutureImpl<T> newTaskFor(final Runnable runnable, final @Nullable T value) {
    return new ScheduledFutureImpl<T>(mHandler, runnable, value);
  }

  @Override
  protected <T> ScheduledFutureImpl<T> newTaskFor(final Callable<T> callable) {
    return new ScheduledFutureImpl<T>(mHandler, callable);
  }

  @Override
  public ScheduledFuture<?> submit(final Runnable task) {
    return submit(task, (Void) null);
  }

  @Override
  public <T> ScheduledFuture<T> submit(final Runnable task, final @Nullable T result) {
    if (task == null) throw new NullPointerException();
    ScheduledFutureImpl<T> future = newTaskFor(task, result);
    execute(future);
    return future;
  }

  @Override
  public <T> ScheduledFuture<T> submit(final Callable<T> task) {
    if (task == null) throw new NullPointerException();
    ScheduledFutureImpl<T> future = newTaskFor(task);
    execute(future);
    return future;
  }

  @Override
  public ScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit) {
    ScheduledFutureImpl<?> future = newTaskFor(command, null);
    mHandler.postDelayed(future, unit.toMillis(delay));
    return future;
  }

  @Override
  public <V> ScheduledFuture<V> schedule(final Callable<V> callable, final long delay, final TimeUnit unit) {
    ScheduledFutureImpl<V> future = newTaskFor(callable);
    mHandler.postDelayed(future, unit.toMillis(delay));
    return future;
  }

  @Override
  public ScheduledFuture<?> scheduleAtFixedRate(
      final Runnable command, final long initialDelay, final long period, final TimeUnit unit) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ScheduledFuture<?> scheduleWithFixedDelay(
      final Runnable command, final long initialDelay, final long delay, final TimeUnit unit) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void quit() {
    mHandler.getLooper().quit();
  }

  @Override
  public boolean isHandlerThread() {
    return Thread.currentThread() == mHandler.getLooper().getThread();
  }
}
