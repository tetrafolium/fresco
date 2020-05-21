/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.common.logging;

import android.util.Log;
import java.io.PrintWriter;
import java.io.StringWriter;

/** Default implementation of {@link LoggingDelegate}. */
public class FLogDefaultLoggingDelegate implements LoggingDelegate {

  public static final FLogDefaultLoggingDelegate sInstance = new FLogDefaultLoggingDelegate();

  private String mApplicationTag = "unknown";
  private int mMinimumLoggingLevel = Log.WARN;

  public static FLogDefaultLoggingDelegate getInstance() {
    return sInstance;
  }

  private FLogDefaultLoggingDelegate() { }

  /**
   * Sets an application tag that is used for checking if a log line is loggable and also to prefix
   * to all log lines.
   *
   * @param tag the tag
   */
  public void setApplicationTag(final String tag) {
    mApplicationTag = tag;
  }

  @Override
  public void setMinimumLoggingLevel(final int level) {
    mMinimumLoggingLevel = level;
  }

  @Override
  public int getMinimumLoggingLevel() {
    return mMinimumLoggingLevel;
  }

  @Override
  public boolean isLoggable(final int level) {
    return mMinimumLoggingLevel <= level;
  }

  @Override
  public void v(final String tag, final String msg) {
    println(Log.VERBOSE, tag, msg);
  }

  @Override
  public void v(final String tag, final String msg, final Throwable tr) {
    println(Log.VERBOSE, tag, msg, tr);
  }

  @Override
  public void d(final String tag, final String msg) {
    println(Log.DEBUG, tag, msg);
  }

  @Override
  public void d(final String tag, final String msg, final Throwable tr) {
    println(Log.DEBUG, tag, msg, tr);
  }

  @Override
  public void i(final String tag, final String msg) {
    println(Log.INFO, tag, msg);
  }

  @Override
  public void i(final String tag, final String msg, final Throwable tr) {
    println(Log.INFO, tag, msg, tr);
  }

  @Override
  public void w(final String tag, final String msg) {
    println(Log.WARN, tag, msg);
  }

  @Override
  public void w(final String tag, final String msg, final Throwable tr) {
    println(Log.WARN, tag, msg, tr);
  }

  @Override
  public void e(final String tag, final String msg) {
    println(Log.ERROR, tag, msg);
  }

  @Override
  public void e(final String tag, final String msg, final Throwable tr) {
    println(Log.ERROR, tag, msg, tr);
  }

  /**
   * Note: this gets forwarded to {@code android.util.Log.e} as {@code android.util.Log.wtf} might
   * crash the app.
   */
  @Override
  public void wtf(final String tag, final String msg) {
    println(Log.ERROR, tag, msg);
  }

  /**
   * Note: this gets forwarded to {@code android.util.Log.e} as {@code android.util.Log.wtf} might
   * crash the app.
   */
  @Override
  public void wtf(final String tag, final String msg, final Throwable tr) {
    println(Log.ERROR, tag, msg, tr);
  }

  @Override
  public void log(final int priority, final String tag, final String msg) {
    println(priority, tag, msg);
  }

  private void println(final int priority, final String tag, final String msg) {
    Log.println(priority, prefixTag(tag), msg);
  }

  private void println(final int priority, final String tag, final String msg, final Throwable tr) {
    Log.println(priority, prefixTag(tag), getMsg(msg, tr));
  }

  private String prefixTag(final String tag) {
    if (mApplicationTag != null) {
      return mApplicationTag + ":" + tag;
    } else {
      return tag;
    }
  }

  private static String getMsg(final String msg, final Throwable tr) {
    return msg + '\n' + getStackTraceString(tr);
  }

  private static String getStackTraceString(final Throwable tr) {
    if (tr == null) {
      return "";
    }
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    tr.printStackTrace(pw);
    return sw.toString();
  }
}
