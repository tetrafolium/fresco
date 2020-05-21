/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.common.logging;

import android.util.Log;

/** Logging wrapper with format style support. */
public class FLog {

  public static final int VERBOSE = Log.VERBOSE;

  public static final int DEBUG = Log.DEBUG;

  public static final int INFO = Log.INFO;

  public static final int WARN = Log.WARN;

  public static final int ERROR = Log.ERROR;

  public static final int ASSERT = Log.ASSERT;

  private static LoggingDelegate sHandler = FLogDefaultLoggingDelegate.getInstance();

  /**
   * Sets the logging delegate that overrides the default delegate.
   *
   * @param delegate the delegate to use
   */
  public static void setLoggingDelegate(final LoggingDelegate delegate) {
    if (delegate == null) {
      throw new IllegalArgumentException();
    }
    sHandler = delegate;
  }

  public static boolean isLoggable(final int level) {
    return sHandler.isLoggable(level);
  }

  public static void setMinimumLoggingLevel(final int level) {
    sHandler.setMinimumLoggingLevel(level);
  }

  public static int getMinimumLoggingLevel() {
    return sHandler.getMinimumLoggingLevel();
  }

  public static void v(final String tag, final String msg) {
    if (sHandler.isLoggable(VERBOSE)) {
      sHandler.v(tag, msg);
    }
  }

  public static void v(final String tag, final String msg, final Object arg1) {
    if (sHandler.isLoggable(VERBOSE)) {
      sHandler.v(tag, formatString(msg, arg1));
    }
  }

  public static void v(final String tag, final String msg, final Object arg1, final Object arg2) {
    if (sHandler.isLoggable(VERBOSE)) {
      sHandler.v(tag, formatString(msg, arg1, arg2));
    }
  }

  public static void v(final String tag, final String msg, final Object arg1, final Object arg2, final Object arg3) {
    if (sHandler.isLoggable(VERBOSE)) {
      sHandler.v(tag, formatString(msg, arg1, arg2, arg3));
    }
  }

  public static void v(final String tag, final String msg, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
    if (sHandler.isLoggable(VERBOSE)) {
      sHandler.v(tag, formatString(msg, arg1, arg2, arg3, arg4));
    }
  }

  public static void v(final Class<?> cls, final String msg) {
    if (sHandler.isLoggable(VERBOSE)) {
      sHandler.v(getTag(cls), msg);
    }
  }

  public static void v(final Class<?> cls, final String msg, final Object arg1) {
    if (sHandler.isLoggable(VERBOSE)) {
      sHandler.v(getTag(cls), formatString(msg, arg1));
    }
  }

  public static void v(final Class<?> cls, final String msg, final Object arg1, final Object arg2) {
    if (sHandler.isLoggable(VERBOSE)) {
      sHandler.v(getTag(cls), formatString(msg, arg1, arg2));
    }
  }

  public static void v(final Class<?> cls, final String msg, final Object arg1, final Object arg2, final Object arg3) {
    if (isLoggable(VERBOSE)) {
      v(cls, formatString(msg, arg1, arg2, arg3));
    }
  }

  public static void v(
      final Class<?> cls, final String msg, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
    if (sHandler.isLoggable(VERBOSE)) {
      sHandler.v(getTag(cls), formatString(msg, arg1, arg2, arg3, arg4));
    }
  }

  public static void v(final String tag, final String msg, final Object... args) {
    if (sHandler.isLoggable(VERBOSE)) {
      sHandler.v(tag, formatString(msg, args));
    }
  }

  public static void v(final String tag, final Throwable tr, final String msg, final Object... args) {
    if (sHandler.isLoggable(VERBOSE)) {
      sHandler.v(tag, formatString(msg, args), tr);
    }
  }

  public static void v(final Class<?> cls, final String msg, final Object... args) {
    if (sHandler.isLoggable(VERBOSE)) {
      sHandler.v(getTag(cls), formatString(msg, args));
    }
  }

  public static void v(final Class<?> cls, final Throwable tr, final String msg, final Object... args) {
    if (sHandler.isLoggable(VERBOSE)) {
      sHandler.v(getTag(cls), formatString(msg, args), tr);
    }
  }

  public static void v(final String tag, final String msg, final Throwable tr) {
    if (sHandler.isLoggable(VERBOSE)) {
      sHandler.v(tag, msg, tr);
    }
  }

  public static void v(final Class<?> cls, final String msg, final Throwable tr) {
    if (sHandler.isLoggable(VERBOSE)) {
      sHandler.v(getTag(cls), msg, tr);
    }
  }

  public static void d(final String tag, final String msg) {
    if (sHandler.isLoggable(DEBUG)) {
      sHandler.d(tag, msg);
    }
  }

  public static void d(final String tag, final String msg, final Object arg1) {
    if (sHandler.isLoggable(DEBUG)) {
      sHandler.d(tag, formatString(msg, arg1));
    }
  }

  public static void d(final String tag, final String msg, final Object arg1, final Object arg2) {
    if (sHandler.isLoggable(DEBUG)) {
      sHandler.d(tag, formatString(msg, arg1, arg2));
    }
  }

  public static void d(final String tag, final String msg, final Object arg1, final Object arg2, final Object arg3) {
    if (sHandler.isLoggable(DEBUG)) {
      sHandler.d(tag, formatString(msg, arg1, arg2, arg3));
    }
  }

  public static void d(final String tag, final String msg, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
    if (sHandler.isLoggable(DEBUG)) {
      sHandler.d(tag, formatString(msg, arg1, arg2, arg3, arg4));
    }
  }

  public static void d(final Class<?> cls, final String msg) {
    if (sHandler.isLoggable(DEBUG)) {
      sHandler.d(getTag(cls), msg);
    }
  }

  public static void d(final Class<?> cls, final String msg, final Object arg1) {
    if (sHandler.isLoggable(DEBUG)) {
      sHandler.d(getTag(cls), formatString(msg, arg1));
    }
  }

  public static void d(final Class<?> cls, final String msg, final Object arg1, final Object arg2) {
    if (sHandler.isLoggable(DEBUG)) {
      sHandler.d(getTag(cls), formatString(msg, arg1, arg2));
    }
  }

  public static void d(final Class<?> cls, final String msg, final Object arg1, final Object arg2, final Object arg3) {
    if (sHandler.isLoggable(DEBUG)) {
      sHandler.d(getTag(cls), formatString(msg, arg1, arg2, arg3));
    }
  }

  public static void d(
      final Class<?> cls, final String msg, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
    if (sHandler.isLoggable(DEBUG)) {
      sHandler.d(getTag(cls), formatString(msg, arg1, arg2, arg3, arg4));
    }
  }

  public static void d(final String tag, final String msg, final Object... args) {
    if (sHandler.isLoggable(DEBUG)) {
      d(tag, formatString(msg, args));
    }
  }

  public static void d(final String tag, final Throwable tr, final String msg, final Object... args) {
    if (sHandler.isLoggable(DEBUG)) {
      d(tag, formatString(msg, args), tr);
    }
  }

  public static void d(final Class<?> cls, final String msg, final Object... args) {
    if (sHandler.isLoggable(DEBUG)) {
      sHandler.d(getTag(cls), formatString(msg, args));
    }
  }

  public static void d(final Class<?> cls, final Throwable tr, final String msg, final Object... args) {
    if (sHandler.isLoggable(DEBUG)) {
      sHandler.d(getTag(cls), formatString(msg, args), tr);
    }
  }

  public static void d(final String tag, final String msg, final Throwable tr) {
    if (sHandler.isLoggable(DEBUG)) {
      sHandler.d(tag, msg, tr);
    }
  }

  public static void d(final Class<?> cls, final String msg, final Throwable tr) {
    if (sHandler.isLoggable(DEBUG)) {
      sHandler.d(getTag(cls), msg, tr);
    }
  }

  public static void i(final String tag, final String msg) {
    if (sHandler.isLoggable(INFO)) {
      sHandler.i(tag, msg);
    }
  }

  public static void i(final String tag, final String msg, final Object arg1) {
    if (sHandler.isLoggable(INFO)) {
      sHandler.i(tag, formatString(msg, arg1));
    }
  }

  public static void i(final String tag, final String msg, final Object arg1, final Object arg2) {
    if (sHandler.isLoggable(INFO)) {
      sHandler.i(tag, formatString(msg, arg1, arg2));
    }
  }

  public static void i(final String tag, final String msg, final Object arg1, final Object arg2, final Object arg3) {
    if (sHandler.isLoggable(INFO)) {
      sHandler.i(tag, formatString(msg, arg1, arg2, arg3));
    }
  }

  public static void i(final String tag, final String msg, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
    if (sHandler.isLoggable(INFO)) {
      sHandler.i(tag, formatString(msg, arg1, arg2, arg3, arg4));
    }
  }

  public static void i(final Class<?> cls, final String msg) {
    if (sHandler.isLoggable(INFO)) {
      sHandler.i(getTag(cls), msg);
    }
  }

  public static void i(final Class<?> cls, final String msg, final Object arg1) {
    if (sHandler.isLoggable(INFO)) {
      sHandler.i(getTag(cls), formatString(msg, arg1));
    }
  }

  public static void i(final Class<?> cls, final String msg, final Object arg1, final Object arg2) {
    if (sHandler.isLoggable(INFO)) {
      sHandler.i(getTag(cls), formatString(msg, arg1, arg2));
    }
  }

  public static void i(final Class<?> cls, final String msg, final Object arg1, final Object arg2, final Object arg3) {
    if (sHandler.isLoggable(INFO)) {
      sHandler.i(getTag(cls), formatString(msg, arg1, arg2, arg3));
    }
  }

  public static void i(
      final Class<?> cls, final String msg, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
    if (sHandler.isLoggable(INFO)) {
      sHandler.i(getTag(cls), formatString(msg, arg1, arg2, arg3, arg4));
    }
  }

  public static void i(final String tag, final String msg, final Object... args) {
    if (sHandler.isLoggable(INFO)) {
      sHandler.i(tag, formatString(msg, args));
    }
  }

  public static void i(final String tag, final Throwable tr, final String msg, final Object... args) {
    if (sHandler.isLoggable(INFO)) {
      sHandler.i(tag, formatString(msg, args), tr);
    }
  }

  public static void i(final Class<?> cls, final String msg, final Object... args) {
    if (sHandler.isLoggable(INFO)) {
      sHandler.i(getTag(cls), formatString(msg, args));
    }
  }

  public static void i(final Class<?> cls, final Throwable tr, final String msg, final Object... args) {
    if (isLoggable(INFO)) {
      sHandler.i(getTag(cls), formatString(msg, args), tr);
    }
  }

  public static void i(final String tag, final String msg, final Throwable tr) {
    if (sHandler.isLoggable(INFO)) {
      sHandler.i(tag, msg, tr);
    }
  }

  public static void i(final Class<?> cls, final String msg, final Throwable tr) {
    if (sHandler.isLoggable(INFO)) {
      sHandler.i(getTag(cls), msg, tr);
    }
  }

  public static void w(final String tag, final String msg) {
    if (sHandler.isLoggable(WARN)) {
      sHandler.w(tag, msg);
    }
  }

  public static void w(final Class<?> cls, final String msg) {
    if (sHandler.isLoggable(WARN)) {
      sHandler.w(getTag(cls), msg);
    }
  }

  public static void w(final String tag, final String msg, final Object... args) {
    if (sHandler.isLoggable(WARN)) {
      sHandler.w(tag, formatString(msg, args));
    }
  }

  public static void w(final String tag, final Throwable tr, final String msg, final Object... args) {
    if (sHandler.isLoggable(WARN)) {
      sHandler.w(tag, formatString(msg, args), tr);
    }
  }

  public static void w(final Class<?> cls, final String msg, final Object... args) {
    if (sHandler.isLoggable(WARN)) {
      sHandler.w(getTag(cls), formatString(msg, args));
    }
  }

  public static void w(final Class<?> cls, final Throwable tr, final String msg, final Object... args) {
    if (isLoggable(WARN)) {
      w(cls, formatString(msg, args), tr);
    }
  }

  public static void w(final String tag, final String msg, final Throwable tr) {
    if (sHandler.isLoggable(WARN)) {
      sHandler.w(tag, msg, tr);
    }
  }

  public static void w(final Class<?> cls, final String msg, final Throwable tr) {
    if (sHandler.isLoggable(WARN)) {
      sHandler.w(getTag(cls), msg, tr);
    }
  }

  public static void e(final String tag, final String msg) {
    if (sHandler.isLoggable(ERROR)) {
      sHandler.e(tag, msg);
    }
  }

  public static void e(final Class<?> cls, final String msg) {
    if (sHandler.isLoggable(ERROR)) {
      sHandler.e(getTag(cls), msg);
    }
  }

  public static void e(final String tag, final String msg, final Object... args) {
    if (sHandler.isLoggable(ERROR)) {
      sHandler.e(tag, formatString(msg, args));
    }
  }

  public static void e(final String tag, final Throwable tr, final String msg, final Object... args) {
    if (sHandler.isLoggable(ERROR)) {
      sHandler.e(tag, formatString(msg, args), tr);
    }
  }

  public static void e(final Class<?> cls, final String msg, final Object... args) {
    if (sHandler.isLoggable(ERROR)) {
      sHandler.e(getTag(cls), formatString(msg, args));
    }
  }

  public static void e(final Class<?> cls, final Throwable tr, final String msg, final Object... args) {
    if (sHandler.isLoggable(ERROR)) {
      sHandler.e(getTag(cls), formatString(msg, args), tr);
    }
  }

  public static void e(final String tag, final String msg, final Throwable tr) {
    if (sHandler.isLoggable(ERROR)) {
      sHandler.e(tag, msg, tr);
    }
  }

  public static void e(final Class<?> cls, final String msg, final Throwable tr) {
    if (sHandler.isLoggable(ERROR)) {
      sHandler.e(getTag(cls), msg, tr);
    }
  }

  public static void wtf(final String tag, final String msg) {
    if (sHandler.isLoggable(ERROR)) {
      sHandler.wtf(tag, msg);
    }
  }

  public static void wtf(final Class<?> cls, final String msg) {
    if (sHandler.isLoggable(ERROR)) {
      sHandler.wtf(getTag(cls), msg);
    }
  }

  public static void wtf(final String tag, final String msg, final Object... args) {
    if (sHandler.isLoggable(ERROR)) {
      sHandler.wtf(tag, formatString(msg, args));
    }
  }

  public static void wtf(final String tag, final Throwable tr, final String msg, final Object... args) {
    if (sHandler.isLoggable(ERROR)) {
      sHandler.wtf(tag, formatString(msg, args), tr);
    }
  }

  public static void wtf(final Class<?> cls, final String msg, final Object... args) {
    if (sHandler.isLoggable(ERROR)) {
      sHandler.wtf(getTag(cls), formatString(msg, args));
    }
  }

  public static void wtf(final Class<?> cls, final Throwable tr, final String msg, final Object... args) {
    if (sHandler.isLoggable(ERROR)) {
      sHandler.wtf(getTag(cls), formatString(msg, args), tr);
    }
  }

  public static void wtf(final String tag, final String msg, final Throwable tr) {
    if (sHandler.isLoggable(ERROR)) {
      sHandler.wtf(tag, msg, tr);
    }
  }

  public static void wtf(final Class<?> cls, final String msg, final Throwable tr) {
    if (sHandler.isLoggable(ERROR)) {
      sHandler.wtf(getTag(cls), msg, tr);
    }
  }

  private static String formatString(final String str, final Object... args) {
    return String.format(null, str, args);
  }

  private static String getTag(final Class<?> clazz) {
    return clazz.getSimpleName();
  }
}
