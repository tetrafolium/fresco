/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.fresco.samples.showcase.imagepipeline.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.RequiresApi;
import javax.annotation.Nullable;

public class ResizableFrameLayout extends FrameLayout {

  public interface SizeChangedListener {

    void onSizeChanged(int widthPx, int heightPx);
  }

  private View mCornerIndicator;
  private boolean mResizing;
  private float mLastX;
  private float mLastY;
  private int mMaximumWidth;
  private int mMaximumHeight;
  private boolean mUpdateMaximumDimensionOnNextSizeChange;

  private @Nullable SizeChangedListener mSizeChangedListener;

  public ResizableFrameLayout(final Context context) {
    super(context);
  }

  public ResizableFrameLayout(final Context context, final AttributeSet attrs) {
    super(context, attrs);
  }

  public ResizableFrameLayout(final Context context, final AttributeSet attrs, final int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void init(final View cornerIndicator) {
    mCornerIndicator = cornerIndicator;

    mMaximumWidth = getWidth();
    mMaximumHeight = getHeight();
  }

  public void setUpdateMaximumDimensionOnNextSizeChange(
      final boolean updateMaximumDimensionOnNextSizeChange) {
    mUpdateMaximumDimensionOnNextSizeChange = updateMaximumDimensionOnNextSizeChange;
  }

  public void setSizeChangedListener(final @Nullable SizeChangedListener sizeChangedListener) {
    mSizeChangedListener = sizeChangedListener;
  }

  @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
  @Override
  public boolean onInterceptTouchEvent(final MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN
        && event.getX() >= mCornerIndicator.getX()
        && event.getY() >= mCornerIndicator.getY()) {
      ViewGroup.LayoutParams layoutParams = getLayoutParams();
      layoutParams.width = getWidth();
      layoutParams.height = getHeight();
      setLayoutParams(layoutParams);
      mResizing = true;
      return true;
    }

    return false;
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  @Override
  public boolean onTouchEvent(final MotionEvent event) {
    if (!mResizing) {
      return false;
    }

    switch (event.getAction()) {
      case MotionEvent.ACTION_MOVE:
        ViewGroup.LayoutParams layoutParams = getLayoutParams();

        layoutParams.width += event.getX() - mLastX;
        layoutParams.width =
            Math.max(Math.min(layoutParams.width, mMaximumWidth), getMinimumWidth());

        layoutParams.height += event.getY() - mLastY;
        layoutParams.height =
            Math.max(Math.min(layoutParams.height, mMaximumHeight), getMinimumHeight());
        setLayoutParams(layoutParams);
    }

    mLastX =
        Math.max(
            Math.min(event.getX(), mMaximumWidth), getMinimumWidth() - mCornerIndicator.getWidth());
    mLastY =
        Math.max(
            Math.min(event.getY(), mMaximumHeight),
            getMinimumHeight() - mCornerIndicator.getHeight());
    return true;
  }

  @Override
  protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    if (mUpdateMaximumDimensionOnNextSizeChange) {
      mMaximumWidth = w;
      mMaximumHeight = h;
      mUpdateMaximumDimensionOnNextSizeChange = false;
    }
    if (mSizeChangedListener != null) {
      mSizeChangedListener.onSizeChanged(w, h);
    }
  }
}
