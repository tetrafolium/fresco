/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.samples.comparison.instrumentation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.util.AttributeSet;
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.SimpleDraweeControllerBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import javax.annotation.Nullable;

/** {@link SimpleDraweeView} with instrumentation. */
public class InstrumentedDraweeView extends SimpleDraweeView implements Instrumented {

  private Instrumentation mInstrumentation;
  private ControllerListener<Object> mListener;

  public InstrumentedDraweeView(final Context context, final GenericDraweeHierarchy hierarchy) {
    super(context, hierarchy);
    init();
  }

  public InstrumentedDraweeView(final Context context) {
    super(context);
    init();
  }

  public InstrumentedDraweeView(final Context context, final AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public InstrumentedDraweeView(final Context context, final AttributeSet attrs, final int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  private void init() {
    mInstrumentation = new Instrumentation(this);
    mListener =
        new BaseControllerListener<Object>() {
          @Override
          public void onSubmit(final String id, final Object callerContext) {
            mInstrumentation.onStart();
          }

          @Override
          public void onFinalImageSet(
              final String id, final @Nullable Object imageInfo, final @Nullable Animatable animatable) {
            mInstrumentation.onSuccess();
          }

          @Override
          public void onFailure(final String id, final Throwable throwable) {
            mInstrumentation.onFailure();
          }

          @Override
          public void onRelease(final String id) {
            mInstrumentation.onCancellation();
          }
        };
  }

  @Override
  public void initInstrumentation(final String tag, final PerfListener perfListener) {
    mInstrumentation.init(tag, perfListener);
  }

  @Override
  public void onDraw(final Canvas canvas) {
    super.onDraw(canvas);
    mInstrumentation.onDraw(canvas);
  }

  @Override
  public void setImageURI(final Uri uri, final @Nullable Object callerContext) {
    SimpleDraweeControllerBuilder controllerBuilder =
        getControllerBuilder()
            .setUri(uri)
            .setCallerContext(callerContext)
            .setOldController(getController());
    if (controllerBuilder instanceof AbstractDraweeControllerBuilder) {
      ((AbstractDraweeControllerBuilder<?, ?, ?, ?>) controllerBuilder)
          .setControllerListener(mListener);
    }
    setController(controllerBuilder.build());
  }

  public ControllerListener<Object> getListener() {
    return mListener;
  }
}
