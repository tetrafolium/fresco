/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.drawee.controller;

import android.graphics.drawable.Animatable;
import javax.annotation.Nullable;

/** Convenience class that has empty implementation of {@link ControllerListener}. */
public class BaseControllerListener<INFO> implements ControllerListener<INFO> {

  private static final ControllerListener<Object> NO_OP_LISTENER =
      new BaseControllerListener<Object>();

  public static <INFO> ControllerListener<INFO> getNoOpListener() {
    // Listener only receives <INFO>, it never produces one.
    // That means if it can accept Object, it can very well accept <INFO>.
    return (ControllerListener<INFO>) NO_OP_LISTENER;
  }

  @Override
  public void onSubmit(final String id, final Object callerContext) { }

  @Override
  public void onFinalImageSet(
      final String id, final @Nullable INFO imageInfo, final @Nullable Animatable animatable) { }

  @Override
  public void onIntermediateImageSet(final String id, final @Nullable INFO imageInfo) { }

  @Override
  public void onIntermediateImageFailed(final String id, final Throwable throwable) { }

  @Override
  public void onFailure(final String id, final Throwable throwable) { }

  @Override
  public void onRelease(final String id) { }
}
