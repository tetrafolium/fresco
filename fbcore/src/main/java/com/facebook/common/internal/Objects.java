/*
 * Copyright (C) 2014 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.facebook.common.internal;

import static com.facebook.common.internal.Preconditions.checkNotNull;

import java.util.Arrays;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

/**
 * Helper functions that operate on any {@code Object}, and are not already provided in {@link
 * java.util.Objects}.
 *
 * <p>See the Guava User Guide on <a
 * href="https://github.com/google/guava/wiki/CommonObjectUtilitiesExplained">writing {@code Object}
 * methods with {@code Objects}</a>.
 *
 * @author Laurence Gonsalves
 * @since 18.0 (since 2.0 as {@code Objects})
 */
public final class Objects {

  /**
   * Determines whether two possibly-null objects are equal. Returns:
   *
   * <ul>
   *   <li>{@code true} if {@code a} and {@code b} are both null.
   *   <li>{@code true} if {@code a} and {@code b} are both non-null and they are equal according to
   *       {@link Object#equals(Object)}.
   *   <li>{@code false} in all other situations.
   * </ul>
   *
   * <p>This assumes that any non-null objects passed to this function conform to the {@code
   * equals()} contract.
   */
  @CheckReturnValue
  public static boolean equal(final @Nullable Object a, final @Nullable Object b) {
    return a == b || (a != null && a.equals(b));
  }

  /**
   * Generates a hash code for multiple values. The hash code is generated by calling {@link
   * Arrays#hashCode(Object[])}. Note that array arguments to this method, with the exception of a
   * single Object array, do not get any special handling; their hash codes are based on identity
   * and not contents.
   *
   * <p>This is useful for implementing {@link Object#hashCode()}. For example, in an object that
   * has three properties, {@code x}, {@code y}, and {@code z}, one could write:
   *
   * <pre>{@code
   * public int hashCode() {
   *   return Objects.hashCode(getX(), getY(), getZ());
   * }
   * }</pre>
   *
   * <p><b>Warning</b>: When a single object is supplied, the returned hash code does not equal the
   * hash code of that object.
   */
  public static int hashCode(final @Nullable Object... objects) {
    return Arrays.hashCode(objects);
  }

  /**
   * Creates an instance of {@link ToStringHelper}.
   *
   * <p>This is helpful for implementing {@link Object#toString()}. Specification by example:
   *
   * <pre>{@code
   * // Returns "ClassName{}"
   * Objects.toStringHelper(this)
   *     .toString();
   *
   * // Returns "ClassName{x=1}"
   * Objects.toStringHelper(this)
   *     .add("x", 1)
   *     .toString();
   *
   * // Returns "MyObject{x=1}"
   * Objects.toStringHelper("MyObject")
   *     .add("x", 1)
   *     .toString();
   *
   * // Returns "ClassName{x=1, y=foo}"
   * Objects.toStringHelper(this)
   *     .add("x", 1)
   *     .add("y", "foo")
   *     .toString();
   *
   * // Returns "ClassName{x=1}"
   * Objects.toStringHelper(this)
   *     .omitNullValues()
   *     .add("x", 1)
   *     .add("y", null)
   *     .toString();
   * }</pre>
   *
   * <p>Note that in GWT, class names are often obfuscated.
   *
   * @param self the object to generate the string for (typically {@code this}), used only for its
   *     class name
   * @since 18.0 (since 2.0 as {@code Objects.toStringHelper()}).
   */
  public static ToStringHelper toStringHelper(final Object self) {
    return new ToStringHelper(self.getClass().getSimpleName());
  }

  /**
   * Creates an instance of {@link ToStringHelper} in the same manner as {@link
   * #toStringHelper(Object)}, but using the simple name of {@code clazz} instead of using an
   * instance's {@link Object#getClass()}.
   *
   * <p>Note that in GWT, class names are often obfuscated.
   *
   * @param clazz the {@link Class} of the instance
   * @since 18.0 (since 7.0 as {@code Objects.toStringHelper()}).
   */
  public static ToStringHelper toStringHelper(final Class<?> clazz) {
    return new ToStringHelper(clazz.getSimpleName());
  }

  /**
   * Creates an instance of {@link ToStringHelper} in the same manner as {@link
   * #toStringHelper(Object)}, but using {@code className} instead of using an instance's {@link
   * Object#getClass()}.
   *
   * @param className the name of the instance type
   * @since 18.0 (since 7.0 as {@code Objects.toStringHelper()}).
   */
  public static ToStringHelper toStringHelper(final String className) {
    return new ToStringHelper(className);
  }

  /**
   * Returns the first of two given parameters that is not {@code null}, if either is, or otherwise
   * throws a {@link NullPointerException}.
   *
   * <p><b>Note:</b> if {@code first} is represented as an {@link Optional}, this can be
   * accomplished with {@linkplain Optional#or(Object) first.or(second)}. That approach also allows
   * for lazy evaluation of the fallback instance, using {@linkplain Optional#or(Supplier)
   * first.or(Supplier)}.
   *
   * @return {@code first} if {@code first} is not {@code null}, or {@code second} if {@code first}
   *     is {@code null} and {@code second} is not {@code null}
   * @throws NullPointerException if both {@code first} and {@code second} were {@code null}
   * @since 3.0
   */
  public static <T> T firstNonNull(final @Nullable T first, final @Nullable T second) {
    return first != null ? first : checkNotNull(second);
  }

  /**
   * Support class for {@link Objects#toStringHelper}.
   *
   * @author Jason Lee
   * @since 18.0 (since 2.0 as {@code Objects.ToStringHelper}).
   */
  public static final class ToStringHelper {
    private final String className;
    private final ValueHolder holderHead = new ValueHolder();
    private ValueHolder holderTail = holderHead;
    private boolean omitNullValues = false;

    /** Use {@link Objects#toStringHelper(Object)} to create an instance. */
    private ToStringHelper(final String className) {
      this.className = checkNotNull(className);
    }

    /**
     * Configures the {@link ToStringHelper} so {@link #toString()} will ignore properties with null
     * value. The order of calling this method, relative to the {@code add()}/{@code addValue()}
     * methods, is not significant.
     *
     * @since 18.0 (since 12.0 as {@code Objects.ToStringHelper.omitNullValues()}).
     */
    public ToStringHelper omitNullValues() {
      omitNullValues = true;
      return this;
    }

    /**
     * Adds a name/value pair to the formatted output in {@code name=value} format. If {@code value}
     * is {@code null}, the string {@code "null"} is used, unless {@link #omitNullValues()} is
     * called, in which case this name/value pair will not be added.
     */
    public ToStringHelper add(final String name, final @Nullable Object value) {
      return addHolder(name, value);
    }

    /**
     * Adds a name/value pair to the formatted output in {@code name=value} format.
     *
     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.add()}).
     */
    public ToStringHelper add(final String name, final boolean value) {
      return addHolder(name, String.valueOf(value));
    }

    /**
     * Adds a name/value pair to the formatted output in {@code name=value} format.
     *
     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.add()}).
     */
    public ToStringHelper add(final String name, final char value) {
      return addHolder(name, String.valueOf(value));
    }

    /**
     * Adds a name/value pair to the formatted output in {@code name=value} format.
     *
     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.add()}).
     */
    public ToStringHelper add(final String name, final double value) {
      return addHolder(name, String.valueOf(value));
    }

    /**
     * Adds a name/value pair to the formatted output in {@code name=value} format.
     *
     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.add()}).
     */
    public ToStringHelper add(final String name, final float value) {
      return addHolder(name, String.valueOf(value));
    }

    /**
     * Adds a name/value pair to the formatted output in {@code name=value} format.
     *
     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.add()}).
     */
    public ToStringHelper add(final String name, final int value) {
      return addHolder(name, String.valueOf(value));
    }

    /**
     * Adds a name/value pair to the formatted output in {@code name=value} format.
     *
     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.add()}).
     */
    public ToStringHelper add(final String name, final long value) {
      return addHolder(name, String.valueOf(value));
    }

    /**
     * Adds an unnamed value to the formatted output.
     *
     * <p>It is strongly encouraged to use {@link #add(String, Object)} instead and give value a
     * readable name.
     */
    public ToStringHelper addValue(final @Nullable Object value) {
      return addHolder(value);
    }

    /**
     * Adds an unnamed value to the formatted output.
     *
     * <p>It is strongly encouraged to use {@link #add(String, boolean)} instead and give value a
     * readable name.
     *
     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.addValue()}).
     */
    public ToStringHelper addValue(final boolean value) {
      return addHolder(String.valueOf(value));
    }

    /**
     * Adds an unnamed value to the formatted output.
     *
     * <p>It is strongly encouraged to use {@link #add(String, char)} instead and give value a
     * readable name.
     *
     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.addValue()}).
     */
    public ToStringHelper addValue(final char value) {
      return addHolder(String.valueOf(value));
    }

    /**
     * Adds an unnamed value to the formatted output.
     *
     * <p>It is strongly encouraged to use {@link #add(String, double)} instead and give value a
     * readable name.
     *
     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.addValue()}).
     */
    public ToStringHelper addValue(final double value) {
      return addHolder(String.valueOf(value));
    }

    /**
     * Adds an unnamed value to the formatted output.
     *
     * <p>It is strongly encouraged to use {@link #add(String, float)} instead and give value a
     * readable name.
     *
     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.addValue()}).
     */
    public ToStringHelper addValue(final float value) {
      return addHolder(String.valueOf(value));
    }

    /**
     * Adds an unnamed value to the formatted output.
     *
     * <p>It is strongly encouraged to use {@link #add(String, int)} instead and give value a
     * readable name.
     *
     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.addValue()}).
     */
    public ToStringHelper addValue(final int value) {
      return addHolder(String.valueOf(value));
    }

    /**
     * Adds an unnamed value to the formatted output.
     *
     * <p>It is strongly encouraged to use {@link #add(String, long)} instead and give value a
     * readable name.
     *
     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.addValue()}).
     */
    public ToStringHelper addValue(final long value) {
      return addHolder(String.valueOf(value));
    }

    /**
     * Returns a string in the format specified by {@link Objects#toStringHelper(Object)}.
     *
     * <p>After calling this method, you can keep adding more properties to later call toString()
     * again and get a more complete representation of the same object; but properties cannot be
     * removed, so this only allows limited reuse of the helper instance. The helper allows
     * duplication of properties (multiple name/value pairs with the same name can be added).
     */
    @Override
    public String toString() {
      // create a copy to keep it consistent in case value changes
      boolean omitNullValuesSnapshot = omitNullValues;
      String nextSeparator = "";
      StringBuilder builder = new StringBuilder(32).append(className).append('{');
      for (ValueHolder valueHolder = holderHead.next;
          valueHolder != null;
          valueHolder = valueHolder.next) {
        Object value = valueHolder.value;
        if (!omitNullValuesSnapshot || value != null) {
          builder.append(nextSeparator);
          nextSeparator = ", ";

          if (valueHolder.name != null) {
            builder.append(valueHolder.name).append('=');
          }
          if (value != null && value.getClass().isArray()) {
            Object[] objectArray = {value};
            String arrayString = Arrays.deepToString(objectArray);
            builder.append(arrayString, 1, arrayString.length() - 1);
          } else {
            builder.append(value);
          }
        }
      }
      return builder.append('}').toString();
    }

    private ValueHolder addHolder() {
      ValueHolder valueHolder = new ValueHolder();
      holderTail = holderTail.next = valueHolder;
      return valueHolder;
    }

    private ToStringHelper addHolder(final @Nullable Object value) {
      ValueHolder valueHolder = addHolder();
      valueHolder.value = value;
      return this;
    }

    private ToStringHelper addHolder(final String name, final @Nullable Object value) {
      ValueHolder valueHolder = addHolder();
      valueHolder.value = value;
      valueHolder.name = checkNotNull(name);
      return this;
    }

    private static final class ValueHolder {
      @Nullable String name;
      @Nullable Object value;
      @Nullable ValueHolder next;
    }
  }

  private Objects() { }
}
