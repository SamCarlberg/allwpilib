// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

/** An option that contains a value. */
public record Some<T>(T value) implements Option<T> {
  public Some {
    ErrorMessages.requireNonNullParam(value, "value", "Some");
  }

  @Override
  public boolean isPresent() {
    return true;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public void ifPresent(Consumer<? super T> action) {
    action.accept(value);
  }

  @Override
  public T orElse(T fallback) {
    return value;
  }

  @Override
  public T orElseGet(Supplier<? extends T> fallbackSupplier) {
    return value;
  }

  @Override
  public T unwrap() {
    return value;
  }
}
