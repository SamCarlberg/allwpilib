// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.util;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An option without a value. All instances of this class are effectively identical.
 *
 * @param <T> the type of the values that can be contained
 */
public final class None<T> implements Option<T> {
  /** The singleton {@code None} instance. */
  static final None<?> NONE = new None<>();

  private None() {
    // private ctor - use the predefined constant!
  }

  @Override
  public boolean isPresent() {
    return false;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public void ifPresent(Consumer<? super T> action) {
    // Not present, do nothing
  }

  @Override
  public T orElse(T fallback) {
    return fallback;
  }

  @Override
  public T orElseGet(Supplier<? extends T> fallbackSupplier) {
    return fallbackSupplier.get();
  }

  @Override
  public T unwrap() throws NoSuchElementException {
    throw new NoSuchElementException();
  }
}
