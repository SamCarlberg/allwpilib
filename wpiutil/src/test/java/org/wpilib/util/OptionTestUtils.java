package org.wpilib.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/** JUnit-style assertions for smoother interactions with {@link Option}. */
public final class OptionTestUtils {
  private OptionTestUtils() {
    throw new UnsupportedOperationException("This is a utility class for unit tests");
  }

  public static <T> void assertNoValue(Option<T> option) {
    assertNoValue(option, null);
  }

  public static <T> void assertNoValue(Option<T> option, String message) {
    assertEquals(Option.noValue(), option, message);
  }

  public static <T> void assertOptionValue(T expected, Option<T> option) {
    assertOptionValue(expected, option, null);
  }

  public static <T> void assertOptionValue(T expected, Option<T> option, String message) {
    if (option instanceof Option.Value(var actual)) {
      assertEquals(expected, actual, message);
    } else {
      fail("Option was empty when it was expected to contain " + expected);
    }
  }
}
