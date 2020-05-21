/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.common.references;

import com.facebook.common.internal.Preconditions;
import javax.annotation.Nullable;

public class RefCountCloseableReference<T> extends CloseableReference<T> {

  private RefCountCloseableReference(
      final SharedReference<T> sharedReference, final LeakHandler leakHandler, final @Nullable Throwable stacktrace) {
    super(sharedReference, leakHandler, stacktrace);
  }

  /*package*/ RefCountCloseableReference(
      final T t,
      final ResourceReleaser<T> resourceReleaser,
      final LeakHandler leakHandler,
      final @Nullable Throwable stacktrace) {
    super(t, resourceReleaser, leakHandler, stacktrace);
  }

  @Override
  public CloseableReference<T> clone() {
    Preconditions.checkState(isValid());
    return new RefCountCloseableReference<T>(mSharedReference, mLeakHandler, mStacktrace);
  }
}
