/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.producers;

import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.image.EncodedImageOrigin;
import com.facebook.imagepipeline.request.ImageRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

/**
 * ProducerContext that can be cancelled. Exposes low level API to manipulate state of the
 * ProducerContext.
 */
public class BaseProducerContext implements ProducerContext {

  private static final String ORIGIN_SUBCATEGORY_DEFAULT = "default";

  private final ImageRequest mImageRequest;
  private final String mId;
  private final @Nullable String mUiComponentId;
  private final ProducerListener2 mProducerListener;
  private final Object mCallerContext;
  private final ImageRequest.RequestLevel mLowestPermittedRequestLevel;
  private final Map<String, Object> mExtras = new HashMap<>();

  @GuardedBy("this")
  private boolean mIsPrefetch;

  @GuardedBy("this")
  private Priority mPriority;

  @GuardedBy("this")
  private boolean mIsIntermediateResultExpected;

  @GuardedBy("this")
  private boolean mIsCancelled;

  @GuardedBy("this")
  private final List<ProducerContextCallbacks> mCallbacks;

  private final ImagePipelineConfig mImagePipelineConfig;

  private EncodedImageOrigin mEncodedImageOrigin = EncodedImageOrigin.NOT_SET;

  public BaseProducerContext(
      final ImageRequest imageRequest,
      final String id,
      final ProducerListener2 producerListener,
      final Object callerContext,
      final ImageRequest.RequestLevel lowestPermittedRequestLevel,
      final boolean isPrefetch,
      final boolean isIntermediateResultExpected,
      final Priority priority,
      final ImagePipelineConfig imagePipelineConfig) {
    this(
        imageRequest,
        id,
        null,
        producerListener,
        callerContext,
        lowestPermittedRequestLevel,
        isPrefetch,
        isIntermediateResultExpected,
        priority,
        imagePipelineConfig);
  }

  public BaseProducerContext(
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
    mImageRequest = imageRequest;
    mId = id;
    mUiComponentId = uiComponentId;
    mProducerListener = producerListener;
    mCallerContext = callerContext;
    mLowestPermittedRequestLevel = lowestPermittedRequestLevel;

    mIsPrefetch = isPrefetch;
    mPriority = priority;
    mIsIntermediateResultExpected = isIntermediateResultExpected;

    mIsCancelled = false;
    mCallbacks = new ArrayList<>();

    mImagePipelineConfig = imagePipelineConfig;
  }

  @Override
  public ImageRequest getImageRequest() {
    return mImageRequest;
  }

  @Override
  public String getId() {
    return mId;
  }

  @Nullable
  public String getUiComponentId() {
    return mUiComponentId;
  }

  @Override
  public ProducerListener2 getProducerListener() {
    return mProducerListener;
  }

  @Override
  public Object getCallerContext() {
    return mCallerContext;
  }

  @Override
  public ImageRequest.RequestLevel getLowestPermittedRequestLevel() {
    return mLowestPermittedRequestLevel;
  }

  @Override
  public synchronized boolean isPrefetch() {
    return mIsPrefetch;
  }

  @Override
  public synchronized Priority getPriority() {
    return mPriority;
  }

  @Override
  public synchronized boolean isIntermediateResultExpected() {
    return mIsIntermediateResultExpected;
  }

  public synchronized boolean isCancelled() {
    return mIsCancelled;
  }

  @Override
  public void addCallbacks(final ProducerContextCallbacks callbacks) {
    boolean cancelImmediately = false;
    synchronized (this) {
      mCallbacks.add(callbacks);
      if (mIsCancelled) {
        cancelImmediately = true;
      }
    }

    if (cancelImmediately) {
      callbacks.onCancellationRequested();
    }
  }

  @Override
  public ImagePipelineConfig getImagePipelineConfig() {
    return mImagePipelineConfig;
  }

  @Override
  public EncodedImageOrigin getEncodedImageOrigin() {
    return mEncodedImageOrigin;
  }

  public void setEncodedImageOrigin(final EncodedImageOrigin encodedImageOrigin) {
    mEncodedImageOrigin = encodedImageOrigin;
  }

  /** Cancels the request processing and calls appropriate callbacks. */
  public void cancel() {
    BaseProducerContext.callOnCancellationRequested(cancelNoCallbacks());
  }

  /**
   * Changes isPrefetch property.
   *
   * <p>This method does not call any callbacks. Instead, caller of this method is responsible for
   * iterating over returned list and calling appropriate method on each callback object. {@see
   * #callOnIsPrefetchChanged}
   *
   * @return list of callbacks if the value actually changes, null otherwise
   */
  @Nullable
  public synchronized List<ProducerContextCallbacks> setIsPrefetchNoCallbacks(final boolean isPrefetch) {
    if (isPrefetch == mIsPrefetch) {
      return null;
    }
    mIsPrefetch = isPrefetch;
    return new ArrayList<>(mCallbacks);
  }

  /**
   * Changes priority.
   *
   * <p>This method does not call any callbacks. Instead, caller of this method is responsible for
   * iterating over returned list and calling appropriate method on each callback object. {@see
   * #callOnPriorityChanged}
   *
   * @return list of callbacks if the value actually changes, null otherwise
   */
  @Nullable
  public synchronized List<ProducerContextCallbacks> setPriorityNoCallbacks(final Priority priority) {
    if (priority == mPriority) {
      return null;
    }
    mPriority = priority;
    return new ArrayList<>(mCallbacks);
  }

  /**
   * Changes isIntermediateResultExpected property.
   *
   * <p>This method does not call any callbacks. Instead, caller of this method is responsible for
   * iterating over returned list and calling appropriate method on each callback object. {@see
   * #callOnIntermediateResultChanged}
   *
   * @return list of callbacks if the value actually changes, null otherwise
   */
  @Nullable
  public synchronized List<ProducerContextCallbacks> setIsIntermediateResultExpectedNoCallbacks(
      final boolean isIntermediateResultExpected) {
    if (isIntermediateResultExpected == mIsIntermediateResultExpected) {
      return null;
    }
    mIsIntermediateResultExpected = isIntermediateResultExpected;
    return new ArrayList<>(mCallbacks);
  }

  /**
   * Marks this ProducerContext as cancelled.
   *
   * <p>This method does not call any callbacks. Instead, caller of this method is responsible for
   * iterating over returned list and calling appropriate method on each callback object. {@see
   * #callOnCancellationRequested}
   *
   * @return list of callbacks if the value actually changes, null otherwise
   */
  @Nullable
  public synchronized List<ProducerContextCallbacks> cancelNoCallbacks() {
    if (mIsCancelled) {
      return null;
    }
    mIsCancelled = true;
    return new ArrayList<>(mCallbacks);
  }

  /**
   * Calls {@code onCancellationRequested} on each element of the list. Does nothing if list == null
   */
  public static void callOnCancellationRequested(
      final @Nullable List<ProducerContextCallbacks> callbacks) {
    if (callbacks == null) {
      return;
    }
    for (ProducerContextCallbacks callback : callbacks) {
      callback.onCancellationRequested();
    }
  }

  /** Calls {@code onIsPrefetchChanged} on each element of the list. Does nothing if list == null */
  public static void callOnIsPrefetchChanged(final @Nullable List<ProducerContextCallbacks> callbacks) {
    if (callbacks == null) {
      return;
    }
    for (ProducerContextCallbacks callback : callbacks) {
      callback.onIsPrefetchChanged();
    }
  }

  /**
   * Calls {@code onIsIntermediateResultExpected} on each element of the list. Does nothing if list
   * == null
   */
  public static void callOnIsIntermediateResultExpectedChanged(
      final @Nullable List<ProducerContextCallbacks> callbacks) {
    if (callbacks == null) {
      return;
    }
    for (ProducerContextCallbacks callback : callbacks) {
      callback.onIsIntermediateResultExpectedChanged();
    }
  }

  /** Calls {@code onPriorityChanged} on each element of the list. Does nothing if list == null */
  public static void callOnPriorityChanged(final @Nullable List<ProducerContextCallbacks> callbacks) {
    if (callbacks == null) {
      return;
    }
    for (ProducerContextCallbacks callback : callbacks) {
      callback.onPriorityChanged();
    }
  }

  @Override
  public void setExtra(final String key, final @Nullable Object value) {
    mExtras.put(key, value);
  }

  @Override
  public void putExtras(final @Nullable Map<String, ?> extras) {
    if (extras != null) {
      mExtras.putAll(extras);
    }
  }

  @Nullable
  @Override
  public <T> T getExtra(final String key) {
    //noinspection unchecked
    return (T) mExtras.get(key);
  }

  @Nullable
  @Override
  public <E> E getExtra(final String key, final E valueIfNotFound) {
    Object maybeValue = mExtras.get(key);
    if (maybeValue == null) {
      return valueIfNotFound;
    }
    //noinspection unchecked
    return (E) maybeValue;
  }

  @Override
  public Map<String, Object> getExtras() {
    return mExtras;
  }

  @Override
  public void putOriginExtra(final @Nullable String origin, final @Nullable String subcategory) {
    mExtras.put(ExtraKeys.ORIGIN, origin);
    mExtras.put(ExtraKeys.ORIGIN_SUBCATEGORY, subcategory);
  }

  @Override
  public void putOriginExtra(final @Nullable String origin) {
    mExtras.put(ExtraKeys.ORIGIN, origin);
    mExtras.put(ExtraKeys.ORIGIN_SUBCATEGORY, ORIGIN_SUBCATEGORY_DEFAULT);
  }
}
