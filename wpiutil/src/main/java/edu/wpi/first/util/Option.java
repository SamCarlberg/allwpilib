package edu.wpi.first.util;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An option either has a value ({@link Some}) or is empty ({@link None}). Use pattern matching
 * if/else or switch statements on an {@code Option} object to concisely check if a result is
 * present.
 *
 * <pre>{@code
 * public Option<Item> getItem() {
 *   if (itemIsAvailable())
 *     return Option.some(getNextItem());
 *   else
 *     return Option.none();
 * }
 *
 * // somewhere else:
 * switch (getItem()) {
 *   case Some<Item>(var item) -> useItem(item);
 *   case None _ -> doNothing();
 * }
 * }</pre>
 *
 * @param <T> the type of the optional values
 */
public sealed interface Option<T> permits Some, None {
  /**
   * Creates an option containing a value. The value cannot be null.
   *
   * @param value the value
   * @param <T> the type of the optional value
   * @return the option
   */
  static <T> Option<T> some(T value) {
    ErrorMessages.requireNonNullParam(value, "value", "Option.some()");
    return new Some<>(value);
  }

  /**
   * Creates an option containing no value.
   *
   * @param <T> the type of the optional value
   * @return the empty option
   */
  @SuppressWarnings("unchecked")
  static <T> Option<T> none() {
    return (None<T>) None.NONE;
  }

  // The following methods are provided for an OO-based API that looks similar to java.util.Optional

  /**
   * Checks if this option contains a value.
   *
   * @return true if the option has a value, false if it does not
   */
  boolean isPresent();

  /**
   * Checks if this option does not contain a value.
   *
   * @return true if the option has no value, false if it does
   */
  boolean isEmpty();

  /**
   * If this option contains a value, pass it to the action to perform a task with it.
   *
   * @param action the action to perform on the contained value
   */
  void ifPresent(Consumer<? super T> action);

  /**
   * Returns the value contained in this option, or a fallback value if the option is empty.
   *
   * @param fallback the fallback value to use if the option is empty
   * @return the value contained in the option, or the fallback value if the option is empty
   */
  T orElse(T fallback);

  /**
   * Returns the value contained in this option, or the output of a lazily-evaluated fallback
   * function if the option is empty.
   *
   * @param fallbackSupplier a function that yields fallback values to be used if the option is
   *     empty
   * @return the value contained or the option, or a value returned by the supplier if the option is
   *     empty
   */
  T orElseGet(Supplier<? extends T> fallbackSupplier);

  /**
   * Returns the value contained in this option. If this option does not contain a value, a {@link
   * NoSuchElementException} will be raised instead. This method should only be called if the option
   * has already been confirmed to contain a value with {@link #isPresent()} or {@link #isEmpty()}
   * or if you are otherwise confident that the option is certain to contain a value.
   *
   * @return the value contained in the option
   * @throws NoSuchElementException if the option contains nothing
   */
  T unwrap() throws NoSuchElementException;

  /**
   * Wraps a {@code java.util.Optional<T>} as an {@code Option<T>}. This is for backwards
   * compatibility because {@code java.util.Optional} cannot be used with destructuring operations.
   *
   * @param optional the optional value to wrap
   * @param <T> the type of the optional values
   * @return an {@code Option} corresponding to the given {@code Optional}
   */
  @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalIsPresent"})
  static <T> Option<T> wrapOptional(Optional<T> optional) {
    if (optional.isPresent()) {
      return some(optional.get());
    } else {
      return none();
    }
  }
}
