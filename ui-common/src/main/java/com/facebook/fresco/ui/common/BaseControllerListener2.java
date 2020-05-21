// (c) Facebook, Inc. and its affiliates. Confidential and proprietary.

package com.facebook.fresco.ui.common;

import javax.annotation.Nullable;

/* Experimental */
@Deprecated
public class BaseControllerListener2<INFO> implements ControllerListener2<INFO> {

  private static final ControllerListener2 NO_OP_LISTENER = new BaseControllerListener2();

  public static <I> ControllerListener2<I> getNoOpListener() {
    //noinspection unchecked
    return (ControllerListener2<I>) NO_OP_LISTENER;
  }

  @Override
  public void onSubmit(final String id, final Object callerContext) { }

  @Override
  public void onFinalImageSet(final String id, final @Nullable INFO imageInfo, final Extras extraData) { }

  @Override
  public void onIntermediateImageSet(final String id, final @Nullable INFO imageInfo) { }

  @Override
  public void onIntermediateImageFailed(final String id) { }

  @Override
  public void onFailure(final String id, final Throwable throwable) { }

  @Override
  public void onRelease(final String id) { }
}
