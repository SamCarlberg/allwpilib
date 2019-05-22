package edu.wpi.first.wpilib.sendable.schema;

import java.util.Locale;

public enum SendablePropertyType {
  NUMBER("Number"),
  TEXT("Text"),
  BOOLEAN("Boolean"),
  NUMBER_ARRAY("Number Array"),
  TEXT_ARRAY("Text Array"),
  BOOLEAN_ARRAY("Boolean Array"),
  RAW("Raw"),
  VALUE("Value"),
  ;

  private final String m_scheme;

  SendablePropertyType(String scheme) {
    m_scheme = scheme;
  }

  public String getScheme() {
    return m_scheme;
  }

  /**
   * Gets the property type for the given scheme. Capitalization is ignored.
   *
   * @param scheme the scheme for the property
   * @return the property type for the given scheme
   * @throws IllegalArgumentException if the scheme is unknown or invalid
   */
  @SuppressWarnings("PMD.CyclomaticComplexity")
  public static SendablePropertyType forScheme(String scheme) {
    switch (scheme.toLowerCase(Locale.US)) {
      case "number":
        return NUMBER;
      case "text":
        return TEXT;
      case "boolean":
        return BOOLEAN;
      case "number array":
        return NUMBER_ARRAY;
      case "text array":
        return TEXT_ARRAY;
      case "boolean array":
        return BOOLEAN_ARRAY;
      case "raw":
        return RAW;
      case "value":
        return VALUE;
      default:
        throw new IllegalArgumentException("Unknown property scheme '" + scheme + "'");
    }
  }
}
