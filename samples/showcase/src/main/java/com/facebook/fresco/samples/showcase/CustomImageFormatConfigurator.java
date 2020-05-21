/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.fresco.samples.showcase;

import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import com.facebook.drawee.backends.pipeline.DraweeConfig;
import com.facebook.fresco.samples.showcase.imageformat.color.ColorImageExample;
import com.facebook.fresco.samples.showcase.imageformat.keyframes.KeyframesDecoderExample;
import com.facebook.fresco.samples.showcase.imageformat.svg.SvgDecoderExample;
import com.facebook.imagepipeline.decoder.ImageDecoderConfig;

/** Helper class to add custom decoders and drawable factories if enabled. */
public class CustomImageFormatConfigurator {

  private static final String IMAGE_FORMAT_PREFS = "fresco_image_format_prefs";
  private static final String IMAGE_FORMAT_COLOR_KEY = "color";
  private static final String IMAGE_FORMAT_SVG_KEY = "svg";

  @Nullable
  public static ImageDecoderConfig createImageDecoderConfig(final Context context) {
    ImageDecoderConfig.Builder config = ImageDecoderConfig.newBuilder();
    if (isGlobalColorDecoderEnabled(context)) {
      config.addDecodingCapability(
          ColorImageExample.IMAGE_FORMAT_COLOR,
          ColorImageExample.createFormatChecker(),
          ColorImageExample.createDecoder());
    }
    if (isSvgEnabled(context)) {
      config.addDecodingCapability(
          SvgDecoderExample.SVG_FORMAT,
          new SvgDecoderExample.SvgFormatChecker(),
          new SvgDecoderExample.SvgDecoder());
    }
    if (isKeyframesEnabled()) {
      config.addDecodingCapability(
          KeyframesDecoderExample.IMAGE_FORMAT_KEYFRAMES,
          KeyframesDecoderExample.createFormatChecker(),
          KeyframesDecoderExample.createDecoder());
    }
    return config.build();
  }

  public static void addCustomDrawableFactories(
      final Context context, final DraweeConfig.Builder draweeConfigBuilder) {
    // We always add the color drawable factory so that it can be used for image decoder overrides,
    // see ImageFormatOverrideExample.
    draweeConfigBuilder.addCustomDrawableFactory(ColorImageExample.createDrawableFactory());
    if (isSvgEnabled(context)) {
      draweeConfigBuilder.addCustomDrawableFactory(new SvgDecoderExample.SvgDrawableFactory());
    }
    if (isKeyframesEnabled()) {
      draweeConfigBuilder.addCustomDrawableFactory(KeyframesDecoderExample.createDrawableFactory());
    }
  }

  public static boolean isGlobalColorDecoderEnabled(final Context context) {
    return getBoolean(context, IMAGE_FORMAT_COLOR_KEY, false);
  }

  public static void setGlobalColorDecoderEnabled(final Context context, final boolean colorEnabled) {
    setBoolean(context, IMAGE_FORMAT_COLOR_KEY, colorEnabled);
  }

  public static boolean isSvgEnabled(final Context context) {
    return getBoolean(context, IMAGE_FORMAT_SVG_KEY, false);
  }

  public static void setSvgEnabled(final Context context, final boolean svgEnabled) {
    setBoolean(context, IMAGE_FORMAT_SVG_KEY, svgEnabled);
  }

  public static boolean isKeyframesEnabled() {
    return Build.VERSION.SDK_INT >= 15;
  }

  private static boolean getBoolean(final Context context, final String key, final boolean defaultValue) {
    return context
        .getSharedPreferences(IMAGE_FORMAT_PREFS, Context.MODE_PRIVATE)
        .getBoolean(key, defaultValue);
  }

  private static void setBoolean(final Context context, final String key, final boolean value) {
    context
        .getSharedPreferences(IMAGE_FORMAT_PREFS, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(key, value)
        .apply();
  }
}
