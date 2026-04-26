// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class OptionTest {
  @Test
  void noValueReturnsSameObject() {
    assertSame(Option.noValue(), Option.noValue());
  }

  @Test
  void ofAcceptsNull() {
    assertSame(Option.noValue(), Option.of(null));
  }

  @Test
  void noValueMapReturnsNoValue() {
    assertSame(Option.noValue(), Option.noValue().map(x -> "new value"));
  }

  @Test
  void valuedMapReturnsNewValue() {
    assertEquals(Option.of("new value"), Option.of("new").map(x -> x + " value"));
  }

  @Test
  void noValueFlatMapReturnsNoValue() {
    assertSame(Option.noValue(), Option.noValue().flatMap(x -> Option.of("new value")));
  }

  @Test
  void valuedFlatMapReturnsNewValue() {
    assertEquals(Option.of("new value"), Option.of("new").flatMap(x -> Option.of(x + " value")));
  }

  @Test
  void valuedMapToNullReturnsNoValue() {
    assertSame(Option.noValue(), Option.of("value").map(x -> null));
  }

  @Test
  void valuedFlatMapToNoValueReturnsNoValue() {
    assertSame(Option.noValue(), Option.of("value").flatMap(x -> Option.noValue()));
  }
}
