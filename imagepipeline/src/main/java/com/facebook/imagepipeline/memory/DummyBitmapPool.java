/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.memory;

import android.graphics.Bitmap;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.imageutils.BitmapUtil;

public class DummyBitmapPool implements BitmapPool {

  @Override
  public void trim(final MemoryTrimType trimType) {
    // nop
  }

  @Override
  public Bitmap get(final int size) {
    return Bitmap.createBitmap(
        1,
        (int) Math.ceil(size / (double) BitmapUtil.RGB_565_BYTES_PER_PIXEL),
        Bitmap.Config.RGB_565);
  }

  @Override
  public void release(final Bitmap value) {
    Preconditions.checkNotNull(value);
    value.recycle();
  }
}
