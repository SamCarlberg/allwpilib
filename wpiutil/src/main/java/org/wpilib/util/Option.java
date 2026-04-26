// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.util;

import java.util.function.Function;

/**
 * Represents a value that may or may not be present. Options are useful for handling data that may
 * or may not be present without risking potential NullPointerExceptions or NoSuchElementExceptions.
 *
 * <p>This interface is intended to be used with pattern-matching {@code if} and {@code switch}
 * statements, a Java language feature introduced in Java 21. See the <a
 * href="https://docs.oracle.com/en/java/javase/21/language/record-patterns.html">official Java
 * documentation</a> for more information on this language feature.
 *
 * <pre>{@code
 * // If statements:
 * Option<String> potentialText = fetchData(); // Pretend this function exists for sake of example
 * if (potentialText instanceof Option.Value(String data)) {
 *   System.out.println("Got text: " + data);
 * } else {
 *   System.err.println("Didn't get any data");
 * }
 *
 * // Switch statements:
 * switch (potentialText) {
 *   case Option.Value(String data) -> System.out.println("Got data: " + data);
 *   case Option.NoValue() -> System.err.println("Didn't get any data");
 * }
 * }</pre>
 *
 * @param <T> The type of the values that can potentially be stored in the option
 * @see <a
 *     href="https://docs.oracle.com/en/java/javase/21/language/record-patterns.html">https://docs.oracle.com/en/java/javase/21/language/record-patterns.html</a>
 */
public sealed interface Option<T> {
  /**
   * Creates a new option wrapping a value. If the value is null, a {@link #noValue() no-value}
   * option is returned.
   *
   * @param value The value to wrap. May be null.
   * @param <T> The type of the values that can potentially be stored in the option
   * @return An option for the value.
   */
  static <T> Option<T> of(T value) {
    if (value == null) {
      return noValue();
    } else {
      return new Value<>(value);
    }
  }

  /**
   * Creates an option representing no value.
   *
   * @param <T> The type of values that could have potentially been stored in the option
   * @return A no-value option
   */
  @SuppressWarnings("unchecked")
  static <T> NoValue<T> noValue() {
    return NoValue.NO_VALUE;
  }

  /**
   * Applies a mapping function to the value in this option, returning a new option containing the
   * result of the mapping function. The function may return {@code null}; if so, an empty option is
   * returned. Applying a mapping function to an empty option always returns another empty option.
   * This function is equivalent to the following if-else statement:
   *
   * <pre>{@code
   * Option<?> option = ...;
   * Option<?> mapped = switch (option) {
   *   case Option.Value(var value) -> Option.maybe(mappingFunction.apply(value));
   *   case NoValue() -> Option.none();
   * };
   * }</pre>
   *
   * @param mapper The mapping function to apply.
   * @param <R> The result type of the mapping function
   * @return An option containing the result of the mapping operation, or an empty option if the
   *     mapping function returned null
   */
  <R> Option<R> map(Function<? super T, ? extends R> mapper);

  /**
   * Like {@link #map(Function)}, but where the function may return another {@code Option} object
   * instead of a raw value. Useful to avoid stackups of nested {@code Option<Option<Option<...>>>}
   * types.
   *
   * @param mapper The mapping function to apply
   * @param <R> The result type of the mapping function
   * @return An option containing the result of the mapping operation
   */
  <R> Option<R> flatMap(Function<? super T, ? extends Option<? extends R>> mapper);

  /**
   * An option type that contains a value.
   *
   * @param value The value of the option. This is never {@code null}
   * @param <T> The type of the value
   */
  record Value<T>(T value) implements Option<T> {
    /**
     * Canonical constructor. Prefer {@link Option#of(Object)} instead of constructing values
     * directly.
     *
     * @param value The value of the option. Cannot be {@code null}
     */
    public Value {
      ErrorMessages.requireNonNullParam(value, "value", "Value");
    }

    @Override
    public <R> Option<R> map(Function<? super T, ? extends R> mapper) {
      return Option.of(mapper.apply(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> Option<R> flatMap(Function<? super T, ? extends Option<? extends R>> mapper) {
      return (Option<R>) mapper.apply(value);
    }
  }

  /**
   * An option type that does not contain a value.
   *
   * @param <T> The type of the optional value
   */
  record NoValue<T>() implements Option<T> {
    // This type has no state and is immutable, so all instances are functionally identical.
    // Using a singleton cuts down on unnecessary object allocation
    @SuppressWarnings("rawtypes")
    static final NoValue NO_VALUE = new NoValue();

    @Override
    public <R> Option<R> map(Function<? super T, ? extends R> mapper) {
      return noValue();
    }

    @Override
    public <R> Option<R> flatMap(Function<? super T, ? extends Option<? extends R>> mapper) {
      return noValue();
    }
  }
}
