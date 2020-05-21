/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.listener;

import com.facebook.common.logging.FLog;
import com.facebook.imagepipeline.request.ImageRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class ForwardingRequestListener implements RequestListener {
  private static final String TAG = "ForwardingRequestListener";

  private final List<RequestListener> mRequestListeners;

  public ForwardingRequestListener(final Set<RequestListener> requestListeners) {
    mRequestListeners = new ArrayList<>(requestListeners.size());
    for (RequestListener requestListener : requestListeners) {
      if (requestListener != null) {
        mRequestListeners.add(requestListener);
      }
    }
  }

  public ForwardingRequestListener(final RequestListener... requestListeners) {
    mRequestListeners = new ArrayList<>(requestListeners.length);
    for (RequestListener requestListener : requestListeners) {
      if (requestListener != null) {
        mRequestListeners.add(requestListener);
      }
    }
  }

  public void addRequestListener(final RequestListener requestListener) {
    mRequestListeners.add(requestListener);
  }

  @Override
  public void onRequestStart(
      final ImageRequest request, final Object callerContext, final String requestId, final boolean isPrefetch) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener listener = mRequestListeners.get(i);
      try {
        listener.onRequestStart(request, callerContext, requestId, isPrefetch);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onRequestStart", exception);
      }
    }
  }

  @Override
  public void onProducerStart(final String requestId, final String producerName) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener listener = mRequestListeners.get(i);
      try {
        listener.onProducerStart(requestId, producerName);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onProducerStart", exception);
      }
    }
  }

  @Override
  public void onProducerFinishWithSuccess(
      final String requestId, final String producerName, final @Nullable Map<String, String> extraMap) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener listener = mRequestListeners.get(i);
      try {
        listener.onProducerFinishWithSuccess(requestId, producerName, extraMap);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onProducerFinishWithSuccess", exception);
      }
    }
  }

  @Override
  public void onProducerFinishWithFailure(
      final String requestId, final String producerName, final Throwable t, final @Nullable Map<String, String> extraMap) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener listener = mRequestListeners.get(i);
      try {
        listener.onProducerFinishWithFailure(requestId, producerName, t, extraMap);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onProducerFinishWithFailure", exception);
      }
    }
  }

  @Override
  public void onProducerFinishWithCancellation(
      final String requestId, final String producerName, final @Nullable Map<String, String> extraMap) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener listener = mRequestListeners.get(i);
      try {
        listener.onProducerFinishWithCancellation(requestId, producerName, extraMap);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onProducerFinishWithCancellation", exception);
      }
    }
  }

  @Override
  public void onProducerEvent(final String requestId, final String producerName, final String producerEventName) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener listener = mRequestListeners.get(i);
      try {
        listener.onProducerEvent(requestId, producerName, producerEventName);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onIntermediateChunkStart", exception);
      }
    }
  }

  @Override
  public void onUltimateProducerReached(final String requestId, final String producerName, final boolean successful) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener listener = mRequestListeners.get(i);
      try {
        listener.onUltimateProducerReached(requestId, producerName, successful);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onProducerFinishWithSuccess", exception);
      }
    }
  }

  @Override
  public void onRequestSuccess(final ImageRequest request, final String requestId, final boolean isPrefetch) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener listener = mRequestListeners.get(i);
      try {
        listener.onRequestSuccess(request, requestId, isPrefetch);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onRequestSuccess", exception);
      }
    }
  }

  @Override
  public void onRequestFailure(
      final ImageRequest request, final String requestId, final Throwable throwable, final boolean isPrefetch) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener listener = mRequestListeners.get(i);
      try {
        listener.onRequestFailure(request, requestId, throwable, isPrefetch);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onRequestFailure", exception);
      }
    }
  }

  @Override
  public void onRequestCancellation(final String requestId) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener listener = mRequestListeners.get(i);
      try {
        listener.onRequestCancellation(requestId);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onRequestCancellation", exception);
      }
    }
  }

  @Override
  public boolean requiresExtraMap(final String id) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      if (mRequestListeners.get(i).requiresExtraMap(id)) {
        return true;
      }
    }
    return false;
  }

  private void onException(final String message, final Throwable t) {
    FLog.e(TAG, message, t);
  }
}
