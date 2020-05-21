/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.drawee.generic;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.drawee.drawable.DrawableParent;
import com.facebook.drawee.drawable.FadeDrawable;
import com.facebook.drawee.drawable.ForwardingDrawable;
import com.facebook.drawee.drawable.MatrixDrawable;
import com.facebook.drawee.drawable.ScaleTypeDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.SettableDraweeHierarchy;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import javax.annotation.Nullable;

/**
 * A SettableDraweeHierarchy that displays placeholder image until the actual image is set. If
 * provided, failure image will be used in case of failure (placeholder otherwise). If provided,
 * retry image will be used in case of failure when retrying is enabled. If provided, progressbar
 * will be displayed until fully loaded. Each image can be displayed with a different scale type (or
 * no scaling at all). Fading between the layers is supported. Rounding is supported.
 *
 * <p>Example hierarchy with a placeholder, retry, failure and the actual image:
 *
 * <pre>
 *  o RootDrawable (top level drawable)
 *  |
 *  +--o FadeDrawable
 *     |
 *     +--o ScaleTypeDrawable (placeholder branch, optional)
 *     |  |
 *     |  +--o Drawable (placeholder image)
 *     |
 *     +--o ScaleTypeDrawable (actual image branch)
 *     |  |
 *     |  +--o ForwardingDrawable (actual image wrapper)
 *     |     |
 *     |     +--o Drawable (actual image)
 *     |
 *     +--o null (progress bar branch, optional)
 *     |
 *     +--o Drawable (retry image branch, optional)
 *     |
 *     +--o ScaleTypeDrawable (failure image branch, optional)
 *        |
 *        +--o Drawable (failure image)
 *  </pre>
 *
 * <p>Note:
 *
 * <ul>
 *   <li>RootDrawable and FadeDrawable are always created.
 *   <li>All branches except the actual image branch are optional (placeholder, failure, retry,
 *       progress bar). If some branch is not specified it won't be created. Index in FadeDrawable
 *       will still be reserved though.
 *   <li>If overlays and/or background are specified, they are added to the same fade drawable, and
 *       are always being displayed.
 *   <li>ScaleType and Matrix transformations will be added only if specified. If both are
 *       unspecified, then the branch for that image is attached to FadeDrawable directly. Matrix
 *       transformation is only supported for the actual image, and it is not recommended to be
 *       used.
 *   <li>Rounding, if specified, is applied to all layers. Rounded drawable can either wrap
 *       FadeDrawable, or if leaf rounding is specified, each leaf drawable will be rounded
 *       separately.
 *   <li>A particular drawable instance should be used by only one DH. If more than one DH is being
 *       built with the same builder, different drawable instances must be specified for each DH.
 * </ul>
 */
public class GenericDraweeHierarchy implements SettableDraweeHierarchy {

  private static final int BACKGROUND_IMAGE_INDEX = 0;
  private static final int PLACEHOLDER_IMAGE_INDEX = 1;
  private static final int ACTUAL_IMAGE_INDEX = 2;
  private static final int PROGRESS_BAR_IMAGE_INDEX = 3;
  private static final int RETRY_IMAGE_INDEX = 4;
  private static final int FAILURE_IMAGE_INDEX = 5;
  private static final int OVERLAY_IMAGES_INDEX = 6;

  private final Drawable mEmptyActualImageDrawable = new ColorDrawable(Color.TRANSPARENT);

  private final Resources mResources;
  private @Nullable RoundingParams mRoundingParams;

  private final RootDrawable mTopLevelDrawable;
  private final FadeDrawable mFadeDrawable;
  private final ForwardingDrawable mActualImageWrapper;

  GenericDraweeHierarchy(final GenericDraweeHierarchyBuilder builder) {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("GenericDraweeHierarchy()");
    }
    mResources = builder.getResources();
    mRoundingParams = builder.getRoundingParams();

    mActualImageWrapper = new ForwardingDrawable(mEmptyActualImageDrawable);

    int numOverlays = (builder.getOverlays() != null) ? builder.getOverlays().size() : 1;

    // make sure there is at least one overlay to make setOverlayImage(Drawable)
    // method work.
    if (numOverlays == 0) {
      numOverlays = 1;
    }

    numOverlays += (builder.getPressedStateOverlay() != null) ? 1 : 0;

    // layer indices and count
    int numLayers = OVERLAY_IMAGES_INDEX + numOverlays;

    // array of layers
    Drawable[] layers = new Drawable[numLayers];
    layers[BACKGROUND_IMAGE_INDEX] = buildBranch(builder.getBackground(), null);
    layers[PLACEHOLDER_IMAGE_INDEX] =
        buildBranch(builder.getPlaceholderImage(), builder.getPlaceholderImageScaleType());
    layers[ACTUAL_IMAGE_INDEX] =
        buildActualImageBranch(
            mActualImageWrapper,
            builder.getActualImageScaleType(),
            builder.getActualImageFocusPoint(),
            builder.getActualImageColorFilter());
    layers[PROGRESS_BAR_IMAGE_INDEX] =
        buildBranch(builder.getProgressBarImage(), builder.getProgressBarImageScaleType());
    layers[RETRY_IMAGE_INDEX] =
        buildBranch(builder.getRetryImage(), builder.getRetryImageScaleType());
    layers[FAILURE_IMAGE_INDEX] =
        buildBranch(builder.getFailureImage(), builder.getFailureImageScaleType());
    if (numOverlays > 0) {
      int index = 0;
      if (builder.getOverlays() != null) {
        for (Drawable overlay : builder.getOverlays()) {
          layers[OVERLAY_IMAGES_INDEX + index++] = buildBranch(overlay, null);
        }
      } else {
        index = 1; // reserve space for one overlay
      }
      if (builder.getPressedStateOverlay() != null) {
        layers[OVERLAY_IMAGES_INDEX + index] = buildBranch(builder.getPressedStateOverlay(), null);
      }
    }

    // fade drawable composed of layers
    mFadeDrawable = new FadeDrawable(layers);
    mFadeDrawable.setTransitionDuration(builder.getFadeDuration());

    // rounded corners drawable (optional)
    Drawable maybeRoundedDrawable =
        WrappingUtils.maybeWrapWithRoundedOverlayColor(mFadeDrawable, mRoundingParams);

    // top-level drawable
    mTopLevelDrawable = new RootDrawable(maybeRoundedDrawable);
    mTopLevelDrawable.mutate();

    resetFade();
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
  }

  @Nullable
  private Drawable buildActualImageBranch(
      final Drawable drawable,
      final @Nullable ScalingUtils.ScaleType scaleType,
      final @Nullable PointF focusPoint,
      final @Nullable ColorFilter colorFilter) {
    drawable.setColorFilter(colorFilter);
    drawable = WrappingUtils.maybeWrapWithScaleType(drawable, scaleType, focusPoint);
    return drawable;
  }

  /** Applies scale type and rounding (both if specified). */
  @Nullable
  private Drawable buildBranch(
      final @Nullable Drawable drawable, final @Nullable ScalingUtils.ScaleType scaleType) {
    drawable = WrappingUtils.maybeApplyLeafRounding(drawable, mRoundingParams, mResources);
    drawable = WrappingUtils.maybeWrapWithScaleType(drawable, scaleType);
    return drawable;
  }

  private void resetActualImages() {
    mActualImageWrapper.setDrawable(mEmptyActualImageDrawable);
  }

  private void resetFade() {
    if (mFadeDrawable != null) {
      mFadeDrawable.beginBatchMode();
      // turn on all layers (backgrounds, branches, overlays)
      mFadeDrawable.fadeInAllLayers();
      // turn off branches (leaving backgrounds and overlays on)
      fadeOutBranches();
      // turn on placeholder
      fadeInLayer(PLACEHOLDER_IMAGE_INDEX);
      mFadeDrawable.finishTransitionImmediately();
      mFadeDrawable.endBatchMode();
    }
  }

  private void fadeOutBranches() {
    fadeOutLayer(PLACEHOLDER_IMAGE_INDEX);
    fadeOutLayer(ACTUAL_IMAGE_INDEX);
    fadeOutLayer(PROGRESS_BAR_IMAGE_INDEX);
    fadeOutLayer(RETRY_IMAGE_INDEX);
    fadeOutLayer(FAILURE_IMAGE_INDEX);
  }

  private void fadeInLayer(final int index) {
    if (index >= 0) {
      mFadeDrawable.fadeInLayer(index);
    }
  }

  private void fadeOutLayer(final int index) {
    if (index >= 0) {
      mFadeDrawable.fadeOutLayer(index);
    }
  }

  private void setProgress(final float progress) {
    Drawable progressBarDrawable = mFadeDrawable.getDrawable(PROGRESS_BAR_IMAGE_INDEX);
    if (progressBarDrawable == null) {
      return;
    }

    // display progressbar when not fully loaded, hide otherwise
    if (progress >= 0.999f) {
      if (progressBarDrawable instanceof Animatable) {
        ((Animatable) progressBarDrawable).stop();
      }
      fadeOutLayer(PROGRESS_BAR_IMAGE_INDEX);
    } else {
      if (progressBarDrawable instanceof Animatable) {
        ((Animatable) progressBarDrawable).start();
      }
      fadeInLayer(PROGRESS_BAR_IMAGE_INDEX);
    }
    // set drawable level, scaled to [0, 10000] per drawable specification
    progressBarDrawable.setLevel(Math.round(progress * 10000));
  }

  // SettableDraweeHierarchy interface

  @Override
  public Drawable getTopLevelDrawable() {
    return mTopLevelDrawable;
  }

  @Override
  public void reset() {
    resetActualImages();
    resetFade();
  }

  @Override
  public void setImage(final Drawable drawable, final float progress, final boolean immediate) {
    drawable = WrappingUtils.maybeApplyLeafRounding(drawable, mRoundingParams, mResources);
    drawable.mutate();
    mActualImageWrapper.setDrawable(drawable);
    mFadeDrawable.beginBatchMode();
    fadeOutBranches();
    fadeInLayer(ACTUAL_IMAGE_INDEX);
    setProgress(progress);
    if (immediate) {
      mFadeDrawable.finishTransitionImmediately();
    }
    mFadeDrawable.endBatchMode();
  }

  @Override
  public void setProgress(final float progress, final boolean immediate) {
    if (mFadeDrawable.getDrawable(PROGRESS_BAR_IMAGE_INDEX) == null) {
      return;
    }
    mFadeDrawable.beginBatchMode();
    setProgress(progress);
    if (immediate) {
      mFadeDrawable.finishTransitionImmediately();
    }
    mFadeDrawable.endBatchMode();
  }

  @Override
  public void setFailure(final Throwable throwable) {
    mFadeDrawable.beginBatchMode();
    fadeOutBranches();
    if (mFadeDrawable.getDrawable(FAILURE_IMAGE_INDEX) != null) {
      fadeInLayer(FAILURE_IMAGE_INDEX);
    } else {
      fadeInLayer(PLACEHOLDER_IMAGE_INDEX);
    }
    mFadeDrawable.endBatchMode();
  }

  @Override
  public void setRetry(final Throwable throwable) {
    mFadeDrawable.beginBatchMode();
    fadeOutBranches();
    if (mFadeDrawable.getDrawable(RETRY_IMAGE_INDEX) != null) {
      fadeInLayer(RETRY_IMAGE_INDEX);
    } else {
      fadeInLayer(PLACEHOLDER_IMAGE_INDEX);
    }
    mFadeDrawable.endBatchMode();
  }

  @Override
  public void setControllerOverlay(final @Nullable Drawable drawable) {
    mTopLevelDrawable.setControllerOverlay(drawable);
  }

  @Override
  public Rect getBounds() {
    return mTopLevelDrawable.getBounds();
  }

  // Helper methods for accessing layers

  /**
   * Gets the lowest parent drawable for the layer at the specified index.
   *
   * <p>Following drawables are considered as parents: FadeDrawable, MatrixDrawable,
   * ScaleTypeDrawable. This is because those drawables are added automatically by the hierarchy (if
   * specified), whereas their children are created externally by the client code. When we need to
   * change the previously set drawable this is the parent whose child needs to be replaced.
   */
  private DrawableParent getParentDrawableAtIndex(final int index) {
    DrawableParent parent = mFadeDrawable.getDrawableParentForIndex(index);
    if (parent.getDrawable() instanceof MatrixDrawable) {
      parent = (MatrixDrawable) parent.getDrawable();
    }
    if (parent.getDrawable() instanceof ScaleTypeDrawable) {
      parent = (ScaleTypeDrawable) parent.getDrawable();
    }
    return parent;
  }

  /**
   * Sets the drawable at the specified index while keeping the old scale type and rounding. In case
   * the given drawable is null, scale type gets cleared too.
   */
  private void setChildDrawableAtIndex(final int index, final @Nullable Drawable drawable) {
    if (drawable == null) {
      mFadeDrawable.setDrawable(index, null);
      return;
    }
    drawable = WrappingUtils.maybeApplyLeafRounding(drawable, mRoundingParams, mResources);
    getParentDrawableAtIndex(index).setDrawable(drawable);
  }

  /**
   * Gets the ScaleTypeDrawable at the specified index. In case there is no child at the specified
   * index, a NullPointerException is thrown. In case there is a child, but the ScaleTypeDrawable
   * does not exist, the child will be wrapped with a new ScaleTypeDrawable.
   */
  private ScaleTypeDrawable getScaleTypeDrawableAtIndex(final int index) {
    DrawableParent parent = getParentDrawableAtIndex(index);
    if (parent instanceof ScaleTypeDrawable) {
      return (ScaleTypeDrawable) parent;
    } else {
      return WrappingUtils.wrapChildWithScaleType(parent, ScalingUtils.ScaleType.FIT_XY);
    }
  }

  /** Returns whether the given layer has a scale type drawable. */
  private boolean hasScaleTypeDrawableAtIndex(final int index) {
    DrawableParent parent = getParentDrawableAtIndex(index);
    return (parent instanceof ScaleTypeDrawable);
  }

  // Mutability

  /** Sets the fade duration. */
  public void setFadeDuration(final int durationMs) {
    mFadeDrawable.setTransitionDuration(durationMs);
  }

  /** Gets the fade duration. */
  public int getFadeDuration() {
    return mFadeDrawable.getTransitionDuration();
  }

  /** Sets the actual image focus point. */
  public void setActualImageFocusPoint(final PointF focusPoint) {
    Preconditions.checkNotNull(focusPoint);
    getScaleTypeDrawableAtIndex(ACTUAL_IMAGE_INDEX).setFocusPoint(focusPoint);
  }

  /** Sets the actual image scale type. */
  public void setActualImageScaleType(final ScalingUtils.ScaleType scaleType) {
    Preconditions.checkNotNull(scaleType);
    getScaleTypeDrawableAtIndex(ACTUAL_IMAGE_INDEX).setScaleType(scaleType);
  }

  public @Nullable ScalingUtils.ScaleType getActualImageScaleType() {
    if (!hasScaleTypeDrawableAtIndex(ACTUAL_IMAGE_INDEX)) {
      return null;
    }
    return getScaleTypeDrawableAtIndex(ACTUAL_IMAGE_INDEX).getScaleType();
  }

  /** Sets the color filter to be applied on the actual image. */
  public void setActualImageColorFilter(final ColorFilter colorfilter) {
    mActualImageWrapper.setColorFilter(colorfilter);
  }

  /** Gets the non-cropped post-scaling bounds of the actual image. */
  public void getActualImageBounds(final RectF outBounds) {
    mActualImageWrapper.getTransformedBounds(outBounds);
  }

  /** Sets a new placeholder drawable with old scale type. */
  public void setPlaceholderImage(final @Nullable Drawable drawable) {
    setChildDrawableAtIndex(PLACEHOLDER_IMAGE_INDEX, drawable);
  }

  /** Sets a new placeholder drawable with scale type. */
  public void setPlaceholderImage(final Drawable drawable, final ScalingUtils.ScaleType scaleType) {
    setChildDrawableAtIndex(PLACEHOLDER_IMAGE_INDEX, drawable);
    getScaleTypeDrawableAtIndex(PLACEHOLDER_IMAGE_INDEX).setScaleType(scaleType);
  }

  /** @return true if there is a placeholder image set. */
  public boolean hasPlaceholderImage() {
    return mFadeDrawable.getDrawable(PLACEHOLDER_IMAGE_INDEX) != null;
  }

  /** Sets the placeholder image focus point. */
  public void setPlaceholderImageFocusPoint(final PointF focusPoint) {
    Preconditions.checkNotNull(focusPoint);
    getScaleTypeDrawableAtIndex(PLACEHOLDER_IMAGE_INDEX).setFocusPoint(focusPoint);
  }

  /**
   * Sets a new placeholder drawable with old scale type.
   *
   * @param resourceId an identifier of an Android drawable or color resource.
   */
  public void setPlaceholderImage(final int resourceId) {
    setPlaceholderImage(mResources.getDrawable(resourceId));
  }

  /**
   * Sets a new placeholder drawable with scale type.
   *
   * @param resourceId an identifier of an Android drawable or color resource.
   * @param ScalingUtils.ScaleType a new scale type.
   */
  public void setPlaceholderImage(final int resourceId, final ScalingUtils.ScaleType scaleType) {
    setPlaceholderImage(mResources.getDrawable(resourceId), scaleType);
  }

  /** Sets a new failure drawable with old scale type. */
  public void setFailureImage(final @Nullable Drawable drawable) {
    setChildDrawableAtIndex(FAILURE_IMAGE_INDEX, drawable);
  }

  /** Sets a new failure drawable with scale type. */
  public void setFailureImage(final Drawable drawable, final ScalingUtils.ScaleType scaleType) {
    setChildDrawableAtIndex(FAILURE_IMAGE_INDEX, drawable);
    getScaleTypeDrawableAtIndex(FAILURE_IMAGE_INDEX).setScaleType(scaleType);
  }

  /**
   * Sets a new failure drawable with old scale type.
   *
   * @param resourceId an identifier of an Android drawable or color resource.
   */
  public void setFailureImage(final int resourceId) {
    setFailureImage(mResources.getDrawable(resourceId));
  }

  /**
   * Sets a new failure drawable with scale type.
   *
   * @param resourceId an identifier of an Android drawable or color resource.
   * @param ScalingUtils.ScaleType a new scale type.
   */
  public void setFailureImage(final int resourceId, final ScalingUtils.ScaleType scaleType) {
    setFailureImage(mResources.getDrawable(resourceId), scaleType);
  }

  /** Sets a new retry drawable with old scale type. */
  public void setRetryImage(final @Nullable Drawable drawable) {
    setChildDrawableAtIndex(RETRY_IMAGE_INDEX, drawable);
  }

  /** Sets a new retry drawable with scale type. */
  public void setRetryImage(final Drawable drawable, final ScalingUtils.ScaleType scaleType) {
    setChildDrawableAtIndex(RETRY_IMAGE_INDEX, drawable);
    getScaleTypeDrawableAtIndex(RETRY_IMAGE_INDEX).setScaleType(scaleType);
  }

  /**
   * Sets a new retry drawable with old scale type.
   *
   * @param resourceId an identifier of an Android drawable or color resource.
   */
  public void setRetryImage(final int resourceId) {
    setRetryImage(mResources.getDrawable(resourceId));
  }

  /**
   * Sets a new retry drawable with scale type.
   *
   * @param resourceId an identifier of an Android drawable or color resource.
   * @param ScalingUtils.ScaleType a new scale type.
   */
  public void setRetryImage(final int resourceId, final ScalingUtils.ScaleType scaleType) {
    setRetryImage(mResources.getDrawable(resourceId), scaleType);
  }

  /** Sets a new progress bar drawable with old scale type. */
  public void setProgressBarImage(final @Nullable Drawable drawable) {
    setChildDrawableAtIndex(PROGRESS_BAR_IMAGE_INDEX, drawable);
  }

  /** Sets a new progress bar drawable with scale type. */
  public void setProgressBarImage(final Drawable drawable, final ScalingUtils.ScaleType scaleType) {
    setChildDrawableAtIndex(PROGRESS_BAR_IMAGE_INDEX, drawable);
    getScaleTypeDrawableAtIndex(PROGRESS_BAR_IMAGE_INDEX).setScaleType(scaleType);
  }

  /**
   * Sets a new progress bar drawable with old scale type.
   *
   * @param resourceId an identifier of an Android drawable or color resource.
   */
  public void setProgressBarImage(final int resourceId) {
    setProgressBarImage(mResources.getDrawable(resourceId));
  }

  /**
   * Sets a new progress bar drawable with scale type.
   *
   * @param resourceId an identifier of an Android drawable or color resource.
   * @param ScalingUtils.ScaleType a new scale type.
   */
  public void setProgressBarImage(final int resourceId, final ScalingUtils.ScaleType scaleType) {
    setProgressBarImage(mResources.getDrawable(resourceId), scaleType);
  }

  /** Sets the background image if allowed. */
  public void setBackgroundImage(final @Nullable Drawable drawable) {
    setChildDrawableAtIndex(BACKGROUND_IMAGE_INDEX, drawable);
  }

  /**
   * Sets a new overlay image at the specified index.
   *
   * <p>This method will throw if the given index is out of bounds.
   *
   * @param drawable background image
   */
  public void setOverlayImage(final int index, final @Nullable Drawable drawable) {
    // Note that overlays are by definition top-most and therefore the last elements in the array.
    Preconditions.checkArgument(
        index >= 0 && OVERLAY_IMAGES_INDEX + index < mFadeDrawable.getNumberOfLayers(),
        "The given index does not correspond to an overlay image.");
    setChildDrawableAtIndex(OVERLAY_IMAGES_INDEX + index, drawable);
  }

  /** Sets the overlay image if allowed. */
  public void setOverlayImage(final @Nullable Drawable drawable) {
    setOverlayImage(0, drawable);
  }

  /** Sets the rounding params. */
  public void setRoundingParams(final @Nullable RoundingParams roundingParams) {
    mRoundingParams = roundingParams;
    WrappingUtils.updateOverlayColorRounding(mTopLevelDrawable, mRoundingParams);
    for (int i = 0; i < mFadeDrawable.getNumberOfLayers(); i++) {
      WrappingUtils.updateLeafRounding(getParentDrawableAtIndex(i), mRoundingParams, mResources);
    }
  }

  /** Gets the rounding params. */
  @Nullable
  public RoundingParams getRoundingParams() {
    return mRoundingParams;
  }

  @VisibleForTesting
  public boolean hasImage() {
    return mActualImageWrapper.getDrawable() != mEmptyActualImageDrawable;
  }

  public void setOnFadeFinishedListener(final FadeDrawable.OnFadeFinishedListener onFadeFinished) {
    mFadeDrawable.setOnFadeFinishedListener(onFadeFinished);
  }
}
