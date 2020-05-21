/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.systrace;

import android.os.Build;
import android.os.Trace;
import com.facebook.imagepipelinebase.BuildConfig;

public class DefaultFrescoSystrace implements FrescoSystrace.Systrace {

  @Override
  public void beginSection(final String name) {
    if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
      Trace.beginSection(name);
    }
  }

  @Override
  public FrescoSystrace.ArgsBuilder beginSectionWithArgs(final String name) {
    if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
      return new DefaultArgsBuilder(name);
    }

    return FrescoSystrace.NO_OP_ARGS_BUILDER;
  }

  @Override
  public void endSection() {
    if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
      Trace.endSection();
    }
  }

  @Override
  public boolean isTracing() {
    return BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
  }

  /**
   * Handles adding args to a systrace section by naively appending them to the section name. This
   * functionality has a more intelligent implementation when using a tracer that writes directly to
   * ftrace instead of using Android's Trace class.
   */
  private static final class DefaultArgsBuilder implements FrescoSystrace.ArgsBuilder {

    private final StringBuilder mStringBuilder;

    public DefaultArgsBuilder(final String name) {
      mStringBuilder = new StringBuilder(name);
    }

    @Override
    public void flush() {
      // 127 is the max name length according to
      // https://developer.android.com/reference/android/os/Trace.html
      if (mStringBuilder.length() > 127) {
        mStringBuilder.setLength(127);
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        Trace.beginSection(mStringBuilder.toString());
      }
    }

    @Override
    public FrescoSystrace.ArgsBuilder arg(final String key, final Object value) {
      mStringBuilder
          .append(';')
          .append(key)
          .append('=')
          .append(value == null ? "null" : value.toString());
      return this;
    }

    @Override
    public FrescoSystrace.ArgsBuilder arg(final String key, final int value) {
      mStringBuilder.append(';').append(key).append('=').append(Integer.toString(value));
      return this;
    }

    @Override
    public FrescoSystrace.ArgsBuilder arg(final String key, final long value) {
      mStringBuilder.append(';').append(key).append('=').append(Long.toString(value));
      return this;
    }

    @Override
    public FrescoSystrace.ArgsBuilder arg(final String key, final double value) {
      mStringBuilder.append(';').append(key).append('=').append(Double.toString(value));
      return this;
    }
  }
}
