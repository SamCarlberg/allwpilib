// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.util.json;

/** An exception raised when parsing invalid JSON text. */
public class ParseException extends RuntimeException {
  private final int m_position;

  /**
   * Constructs a ParseException with a message and the character position in the JSON text.
   *
   * @param message The error message.
   * @param position The character position in the JSON text.
   */
  public ParseException(String message, int position) {
    super(message);
    m_position = position;
  }

  /**
   * Gets the character position in the JSON text where the error occurred.
   *
   * @return The character position in the JSON text.
   */
  public int getPosition() {
    return m_position;
  }
}
