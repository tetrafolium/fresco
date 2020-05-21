/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.fresco.vito.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import com.facebook.drawee.drawable.Rounded;
import com.facebook.drawee.drawable.RoundedBitmapDrawable;
import com.facebook.drawee.drawable.RoundedColorDrawable;
import com.facebook.drawee.drawable.RoundedNinePatchDrawable;
import com.facebook.fresco.vito.options.BorderOptions;
import com.facebook.fresco.vito.options.RoundingOptions;
import javax.annotation.Nullable;

/**
 * A class that contains helper methods for rounding a bitmap or different kind of Drawables. It
 * handles the conversion to the specific types of drawables.
 *
 * <p>Different combinations are:<br>
 * - {@link Bitmap} -> already rounded -> no border -> circular -> {@link BitmapDrawable}<br>
 * - {@link Bitmap} -> already rounded -> border -> circular -> {@link CircularBorderBitmapDrawable}
 * <br>
 * - {@link Bitmap} -> already rounder or not -> rounded corners -> {@link RoundedBitmapDrawable}
 * <br>
 * - {@link BitmapDrawable} -> {@link RoundedBitmapDrawable}<br>
 * - {@link ColorDrawable} -> {@link RoundedColorDrawable}<br>
 * - {@link NinePatchDrawable} -> {@link RoundedNinePatchDrawable}<br>
 */
public class RoundingUtils {

  private boolean mAlreadyRounded;

  public RoundingUtils() {
    this(false);
  }

  public RoundingUtils(final boolean alreadyRounded) {
    mAlreadyRounded = alreadyRounded;
  }

  public void setAlreadyRounded(final boolean alreadyRounded) {
    mAlreadyRounded = alreadyRounded;
  }

  /**
   * Creates a drawable with the {@link RoundingOptions} and {@link BorderOptions} applied to it.
   *
   * @param bitmap a bitmap to be wrapped in the final {@link BitmapDrawable}
   * @param borderOptions border options for the given image
   * @param roundingOptions rounding options for the given image
   * @return a drawable with the applied effect
   */
  public Drawable roundedDrawable(
      final Resources resources,
      final Bitmap bitmap,
      final @Nullable BorderOptions borderOptions,
      final @Nullable RoundingOptions roundingOptions) {
    if (borderOptions != null && borderOptions.width > 0) {
      return roundedDrawableWithBorder(resources, bitmap, borderOptions, roundingOptions);
    } else {
      return roundedDrawableWithoutBorder(resources, bitmap, roundingOptions);
    }
  }

  /**
   * Creates a drawable with the {@link RoundingOptions} and {@link BorderOptions} applied to it.
   *
   * @param drawable the image to transform
   * @param borderOptions border options for the given image
   * @param roundingOptions rounding options for the given image
   * @return a drawable with the applied effect
   */
  public Drawable roundedDrawable(
      final Resources resources,
      final Drawable drawable,
      final @Nullable BorderOptions borderOptions,
      final @Nullable RoundingOptions roundingOptions) {
    if (borderOptions != null && borderOptions.width > 0) {
      return roundedDrawableWithBorder(resources, drawable, borderOptions, roundingOptions);
    } else {
      return roundedDrawableWithoutBorder(resources, drawable, roundingOptions);
    }
  }

  private Drawable roundedDrawableWithoutBorder(
      final Resources resources, final Bitmap bitmap, final @Nullable RoundingOptions roundingOptions) {
    if ((roundingOptions == null) || (mAlreadyRounded && roundingOptions.isCircular())) {
      return new BitmapDrawable(resources, bitmap);
    } else {
      return applyRounding(getRoundedDrawable(resources, bitmap), null, roundingOptions);
    }
  }

  private Drawable roundedDrawableWithBorder(
      final Resources resources,
      final Bitmap bitmap,
      final BorderOptions borderOptions,
      final @Nullable RoundingOptions roundingOptions) {
    if (roundingOptions == null) {
      return squareDrawableWithBorder(getRoundedDrawable(resources, bitmap), borderOptions);
    } else {
      if (mAlreadyRounded && roundingOptions.isCircular()) {
        // Circular rounding is performed on the bitmap, so we only need to draw a circular border
        return circularNativeDrawableWithBorder(resources, bitmap, borderOptions);
      } else {
        return applyRounding(getRoundedDrawable(resources, bitmap), borderOptions, roundingOptions);
      }
    }
  }

  private Drawable roundedDrawableWithoutBorder(
      final Resources resources, final Drawable drawable, final @Nullable RoundingOptions roundingOptions) {
    if (roundingOptions != null) {
      return applyRounding(getRoundedDrawable(resources, drawable), null, roundingOptions);
    }
    return drawable;
  }

  private Drawable roundedDrawableWithBorder(
      final Resources resources,
      final Drawable drawable,
      final BorderOptions borderOptions,
      final @Nullable RoundingOptions roundingOptions) {
    if (roundingOptions == null) {
      return squareDrawableWithBorder(getRoundedDrawable(resources, drawable), borderOptions);
    } else {
      if (mAlreadyRounded && roundingOptions.isCircular() && drawable instanceof BitmapDrawable) {
        // Circular rounding is performed on the bitmap, so we only need to draw a circular border
        return circularNativeDrawableWithBorder(
            resources, ((BitmapDrawable) drawable).getBitmap(), borderOptions);
      } else {
        return applyRounding(
            getRoundedDrawable(resources, drawable), borderOptions, roundingOptions);
      }
    }
  }

  private static <T extends Drawable & Rounded> T getRoundedDrawable(
      final Resources resources, final Bitmap bitmap) {
    return (T) new RoundedBitmapDrawable(resources, bitmap);
  }

  private <T extends Drawable & Rounded> T getRoundedDrawable(
      final Resources resources, final Drawable drawable) {
    T roundingDrawable;
    if (drawable instanceof BitmapDrawable) {
      roundingDrawable = getRoundedDrawable(resources, ((BitmapDrawable) drawable).getBitmap());
    } else if (drawable instanceof NinePatchDrawable) {
      roundingDrawable = (T) new RoundedNinePatchDrawable((NinePatchDrawable) drawable);
    } else if (drawable instanceof ColorDrawable) {
      roundingDrawable = (T) RoundedColorDrawable.fromColorDrawable((ColorDrawable) drawable);
    } else {
      throw new UnsupportedOperationException(
          "Rounding of the drawable type not supported: " + drawable);
    }
    return roundingDrawable;
  }

  private <T extends Drawable & Rounded> Drawable applyRounding(
      final T drawable, final @Nullable BorderOptions borderOptions, final RoundingOptions roundingOptions) {
    if (!roundingOptions.isCircular()) {
      return roundedCornerDrawable(drawable, borderOptions, roundingOptions);
    } else {
      return circularDrawable(drawable, borderOptions);
    }
  }

  private <T extends Drawable & Rounded> Drawable squareDrawableWithBorder(
      final T drawable, final BorderOptions borderOptions) {
    // We use the same rounded corner drawable to draw the border without applying rounding
    return roundedCornerDrawable(drawable, borderOptions, null);
  }

  private static Drawable circularNativeDrawableWithBorder(
      final Resources resources, final Bitmap bitmap, final BorderOptions borderOptions) {
    CircularBorderBitmapDrawable drawable = new CircularBorderBitmapDrawable(resources, bitmap);
    drawable.setBorder(borderOptions);
    return drawable;
  }

  private static <T extends Drawable & Rounded> Drawable circularDrawable(
      final T drawable, final @Nullable BorderOptions borderOptions) {
    drawable.setCircle(true);
    if (borderOptions != null) {
      applyBorders(drawable, borderOptions);
    }
    return drawable;
  }

  private static <T extends Drawable & Rounded> Drawable roundedCornerDrawable(
      final T drawable,
      final @Nullable BorderOptions borderOptions,
      final @Nullable RoundingOptions roundingOptions) {
    if (borderOptions != null) {
      applyBorders(drawable, borderOptions);
    }
    if (roundingOptions != null) {
      float[] radii = roundingOptions.getCornerRadii();
      if (radii != null) {
        drawable.setRadii(radii);
      } else {
        drawable.setRadius(roundingOptions.getCornerRadius());
      }
    }
    return drawable;
  }

  /**
   * Applies the border according to {@link BorderOptions}
   *
   * @param drawable the drawable where the borders are applied
   * @param borderOptions {@link BorderOptions}
   */
  private static <T extends Drawable & Rounded> void applyBorders(
      final T drawable, final BorderOptions borderOptions) {
    drawable.setBorder(borderOptions.color, borderOptions.width);
    drawable.setPadding(borderOptions.padding);
  }
}
