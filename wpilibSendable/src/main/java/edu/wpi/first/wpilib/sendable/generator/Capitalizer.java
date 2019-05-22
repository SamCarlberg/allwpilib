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
