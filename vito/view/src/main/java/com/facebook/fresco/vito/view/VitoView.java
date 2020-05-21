/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.fresco.vito.view;

import android.net.Uri;
import android.view.View;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.fresco.vito.listener.ImageListener;
import com.facebook.fresco.vito.options.ImageOptions;
import com.facebook.imagepipeline.multiuri.MultiUri;

/** You must initialize this class before use by calling {@link #init(Implementation)}. */
@Deprecated /* Experimental */
public class VitoView {

  public interface Implementation {
    void show(
        @Nullable Uri uri,
        @Nullable MultiUri multiUri,
        ImageOptions imageOptions,
        @Nullable Object callerContext,
        @Nullable ImageListener imageListener,
        final View target);
  }

  private static final Class<?> TAG = VitoView.class;
  private static volatile boolean sIsInitialized = false;

  private static Implementation sImplementation;

  private VitoView() { }

  public static void init(final Implementation implementation) {
    if (sIsInitialized) {
      FLog.w(TAG, "VitoView has already been initialized!");
      return;
    } else {
      sIsInitialized = true;
    }
    sImplementation = implementation;
  }

  /*
   * Display an image with default options
   */
  public static void show(final @Nullable Uri uri, final View target) {
    sImplementation.show(uri, null, ImageOptions.defaults(), null, null, target);
  }

  /*
   * Display an image
   */
  public static void show(final @Nullable Uri uri, final ImageOptions imageOptions, final View target) {
    sImplementation.show(uri, null, imageOptions, null, null, target);
  }

  /*
   * Display an image
   */
  public static void show(
      final @Nullable Uri uri,
      final ImageOptions imageOptions,
      final @Nullable Object callerContext,
      final @Nullable ImageListener imageListener,
      final View target) {
    sImplementation.show(uri, null, imageOptions, callerContext, imageListener, target);
  }

  /*
   * Display an image with default options
   */
  public static void show(final @Nullable MultiUri multiUri, final View target) {
    sImplementation.show(null, multiUri, ImageOptions.defaults(), null, null, target);
  }

  /*
   * Display an image
   */
  public static void show(
      final @Nullable MultiUri multiUri, final ImageOptions imageOptions, final View target) {
    sImplementation.show(null, multiUri, imageOptions, null, null, target);
  }
}
