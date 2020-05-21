/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.listener;

import com.facebook.common.logging.FLog;
import com.facebook.imagepipeline.producers.ProducerContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class ForwardingRequestListener2 implements RequestListener2 {
  private static final String TAG = "ForwardingRequestListener2";

  private final List<RequestListener2> mRequestListeners;

  public ForwardingRequestListener2(final Set<RequestListener2> requestListeners) {
    mRequestListeners = new ArrayList<>(requestListeners.size());
    for (RequestListener2 requestListener : requestListeners) {
      if (requestListener != null) {
        mRequestListeners.add(requestListener);
      }
    }
  }

  public ForwardingRequestListener2(final RequestListener2... requestListeners) {
    mRequestListeners = new ArrayList<>(requestListeners.length);
    for (RequestListener2 requestListener : requestListeners) {
      if (requestListener != null) {
        mRequestListeners.add(requestListener);
      }
    }
  }

  public void addRequestListener(final RequestListener2 requestListener) {
    mRequestListeners.add(requestListener);
  }

  @Override
  public void onRequestStart(final ProducerContext producerContext) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener2 listener = mRequestListeners.get(i);
      try {
        listener.onRequestStart(producerContext);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onRequestStart", exception);
      }
    }
  }

  @Override
  public void onProducerStart(final ProducerContext producerContext, final String producerName) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener2 listener = mRequestListeners.get(i);
      try {
        listener.onProducerStart(producerContext, producerName);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onProducerStart", exception);
      }
    }
  }

  @Override
  public void onProducerFinishWithSuccess(
      final ProducerContext producerContext,
      final String producerName,
      final @Nullable Map<String, String> extraMap) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener2 listener = mRequestListeners.get(i);
      try {
        listener.onProducerFinishWithSuccess(producerContext, producerName, extraMap);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onProducerFinishWithSuccess", exception);
      }
    }
  }

  @Override
  public void onProducerFinishWithFailure(
      final ProducerContext producerContext,
      final String producerName,
      final Throwable t,
      final @Nullable Map<String, String> extraMap) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener2 listener = mRequestListeners.get(i);
      try {
        listener.onProducerFinishWithFailure(producerContext, producerName, t, extraMap);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onProducerFinishWithFailure", exception);
      }
    }
  }

  @Override
  public void onProducerFinishWithCancellation(
      final ProducerContext producerContext,
      final String producerName,
      final @Nullable Map<String, String> extraMap) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener2 listener = mRequestListeners.get(i);
      try {
        listener.onProducerFinishWithCancellation(producerContext, producerName, extraMap);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onProducerFinishWithCancellation", exception);
      }
    }
  }

  @Override
  public void onProducerEvent(
      final ProducerContext producerContext, final String producerName, final String producerEventName) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener2 listener = mRequestListeners.get(i);
      try {
        listener.onProducerEvent(producerContext, producerName, producerEventName);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onIntermediateChunkStart", exception);
      }
    }
  }

  @Override
  public void onUltimateProducerReached(
      final ProducerContext producerContext, final String producerName, final boolean successful) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener2 listener = mRequestListeners.get(i);
      try {
        listener.onUltimateProducerReached(producerContext, producerName, successful);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onProducerFinishWithSuccess", exception);
      }
    }
  }

  @Override
  public void onRequestSuccess(final ProducerContext producerContext) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener2 listener = mRequestListeners.get(i);
      try {
        listener.onRequestSuccess(producerContext);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onRequestSuccess", exception);
      }
    }
  }

  @Override
  public void onRequestFailure(final ProducerContext producerContext, final Throwable throwable) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener2 listener = mRequestListeners.get(i);
      try {
        listener.onRequestFailure(producerContext, throwable);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onRequestFailure", exception);
      }
    }
  }

  @Override
  public void onRequestCancellation(final ProducerContext producerContext) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      RequestListener2 listener = mRequestListeners.get(i);
      try {
        listener.onRequestCancellation(producerContext);
      } catch (Exception exception) {
        // Don't punish the other listeners if we're given a bad one.
        onException("InternalListener exception in onRequestCancellation", exception);
      }
    }
  }

  @Override
  public boolean requiresExtraMap(final ProducerContext producerContext, final String producerName) {
    final int numberOfListeners = mRequestListeners.size();
    for (int i = 0; i < numberOfListeners; ++i) {
      if (mRequestListeners.get(i).requiresExtraMap(producerContext, producerName)) {
        return true;
      }
    }
    return false;
  }

  private void onException(final String message, final Throwable t) {
    FLog.e(TAG, message, t);
  }
}
