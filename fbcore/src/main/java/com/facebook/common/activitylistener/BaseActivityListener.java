/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.common.activitylistener;

import android.app.Activity;

public class BaseActivityListener implements ActivityListener {

  @Override
  public void onActivityCreate(final Activity activity) { }

  @Override
  public void onStop(final Activity activity) { }

  @Override
  public void onStart(final Activity activity) { }

  @Override
  public void onDestroy(final Activity activity) { }

  @Override
  public int getPriority() {
    return ActivityListener.MIN_PRIORITY;
  }

  @Override
  public void onPause(final Activity activity) { }

  @Override
  public void onResume(final Activity activity) { }
}
