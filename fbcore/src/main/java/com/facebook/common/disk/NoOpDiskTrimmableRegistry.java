/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.common.disk;

import javax.annotation.Nullable;

/** Implementation of {@link DiskTrimmableRegistry} that does not do anything. */
public class NoOpDiskTrimmableRegistry implements DiskTrimmableRegistry {
  private static @Nullable NoOpDiskTrimmableRegistry sInstance = null;

  private NoOpDiskTrimmableRegistry() { }

  public static synchronized NoOpDiskTrimmableRegistry getInstance() {
    if (sInstance == null) {
      sInstance = new NoOpDiskTrimmableRegistry();
    }
    return sInstance;
  }

  @Override
  public void registerDiskTrimmable(final DiskTrimmable trimmable) { }

  @Override
  public void unregisterDiskTrimmable(final DiskTrimmable trimmable) { }
}
