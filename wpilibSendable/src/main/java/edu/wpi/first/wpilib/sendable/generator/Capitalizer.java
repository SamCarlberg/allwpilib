/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilib.sendable.generator;

public class Capitalizer {
  /**
   * Capitalizes each word in the given input, removing whitespace. For example:
   * <pre>{@code
   * capitalize("foo bar") -> "FooBar"
   * capitalize("x y z") -> "XYZ"
   * }</pre>
   *
   * @param input the input to capitalize
   * @return the capitalized string
   */
  public String capitalize(String input) {
    StringBuilder builder = new StringBuilder();

    boolean capitalizeNext = false;
    for (int i = 0; i < input.toCharArray().length; i++) {
      char ch = input.charAt(i);
      if (Character.isWhitespace(ch)) {
        capitalizeNext = true;
        continue;
      }
      if (i == 0) {
        capitalizeNext = true;
      }
      if (capitalizeNext) {
        builder.append(Character.toUpperCase(ch));
        capitalizeNext = false;
      } else {
        builder.append(ch);
      }
    }
    return builder.toString();
  }
}
