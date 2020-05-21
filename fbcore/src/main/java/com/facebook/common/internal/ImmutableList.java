/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.common.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A dummy representation of an immutable set. This can be used temporarily as a type until we have
 * an actual non-guava implementation.
 */
public class ImmutableList<E> extends ArrayList<E> {

  private ImmutableList(final int capacity) {
    super(capacity);
  }

  private ImmutableList(final List<E> list) {
    super(list);
  }

  public static <E> ImmutableList<E> copyOf(final List<E> list) {
    return new ImmutableList<>(list);
  }

  public static <E> ImmutableList<E> of(final E... elements) {
    final ImmutableList<E> list = new ImmutableList<>(elements.length);
    Collections.addAll(list, elements);
    return list;
  }
}
