/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.memory;

import static org.mockito.ArgumentMatchers.*;

import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.SharedReference;
import java.io.Closeable;
import org.mockito.*;

/** Utilities for testing {@link CloseableReference}. */
public class CloseableReferences {
  private static class CloseableReferenceMatcher<T extends Closeable>
      implements ArgumentMatcher<CloseableReference<T>> {

    private final CloseableReference<T> mCloseableReference;

    public CloseableReferenceMatcher(final CloseableReference<T> closeableReference) {
      mCloseableReference = closeableReference;
    }

    @Override
    public boolean matches(final CloseableReference argument) {
      return mCloseableReference.getUnderlyingReferenceTestOnly()
          == argument.getUnderlyingReferenceTestOnly();
    }
  }

  /**
   * Returns a Mockito ArgumentMatcher that checks that its argument has the same underlying {@link
   * SharedReference}
   */
  public static <T extends Closeable> CloseableReference<T> eqUnderlying(
      final CloseableReference<T> closeableReference) {
    return argThat(new CloseableReferenceMatcher<T>(closeableReference));
  }
}
