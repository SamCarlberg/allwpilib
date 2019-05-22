/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilib.sendable.generator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CapitalizerTest {
  @Test
  void testProperInput() {
    String input = "AlreadyInTheCorrectFormat";
    String output = new Capitalizer().capitalize(input);
    assertEquals(input, output);
  }

  @Test
  void testSingleLowercaseWord() {
    String output = new Capitalizer().capitalize("word");
    assertEquals("Word", output);
  }

  @Test
  void testMultipleWords() {
    String output = new Capitalizer().capitalize("foo bar");
    assertEquals("FooBar", output);
  }

  @Test
  void testLeadingSpaces() {
    String output = new Capitalizer().capitalize("  foo");
    assertEquals("Foo", output);
  }

  @Test
  void testMultipleSpaces() {
    String output = new Capitalizer().capitalize("foo   bar");
    assertEquals("FooBar", output);
  }

}
