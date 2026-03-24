// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.util.json;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.UnusedFormalParameter")
class ExceptionTest {
  @Test
  void testParseTypeMismatch() {
    String json = "{\"x\": \"not a number\"}";
    assertThrows(IllegalArgumentException.class, () -> JsonDeserializer.deserialize(json, CustomType.class));
  }

  @Test
  void testParseMissingConstructor() {
    class NoCtor {
      @JsonAttribute("x")
      int m_x;
    }

    assertThrows(IllegalArgumentException.class, () -> JsonDeserializer.deserialize("{\"x\": 1}", NoCtor.class));
  }

  @Test
  void testParseMultipleConstructors() {
    class MultiCtor {
      @JsonConstructor
      MultiCtor(@JsonAttribute("x") int x) {}

      @JsonConstructor
      MultiCtor(@JsonAttribute("y") int y, int z) {} // CHECKSTYLE.OFF UnusedFormalParameter
    }

    assertThrows(
        IllegalArgumentException.class, () -> JsonDeserializer.deserialize("{\"x\": 1}", MultiCtor.class));
  }

  @Test
  void testParseMissingAttribute() {
    class MissingAttr {
      @JsonConstructor
      MissingAttr(int x) {} // CHECKSTYLE.OFF UnusedFormalParameter
    }

    assertThrows(
        IllegalArgumentException.class, () -> JsonDeserializer.deserialize("{\"x\": 1}", MissingAttr.class));
  }

  @Test
  void testParseMissingProperty() {
    String json = "{\"x\": 1}"; // Missing 'y'
    assertThrows(IllegalArgumentException.class, () -> JsonDeserializer.deserialize(json, CustomType.class));
  }

  @Test
  void testParserUnexpectedEnd() {
    assertThrows(RuntimeException.class, () -> JsonDeserializer.deserializeRaw(""));
    assertThrows(RuntimeException.class, () -> JsonDeserializer.deserializeRaw("   "));
  }

  @Test
  void testParserUnexpectedChar() {
    assertThrows(ParseException.class, () -> JsonDeserializer.deserializeRaw("!"));
  }

  @Test
  void testParserExpectedStringKey() {
    assertThrows(ParseException.class, () -> JsonDeserializer.deserializeRaw("{1: 2}"));
  }

  @Test
  void testParserExpectedColon() {
    assertThrows(ParseException.class, () -> JsonDeserializer.deserializeRaw("{\"key\" 1}"));
  }

  @Test
  void testParserExpectedCommaOrBraceInObject() {
    assertThrows(ParseException.class, () -> JsonDeserializer.deserializeRaw("{\"key\": 1 \"other\": 2}"));
  }

  @Test
  void testParserExpectedCommaOrBracketInArray() {
    assertThrows(ParseException.class, () -> JsonDeserializer.deserializeRaw("[1 2]"));
  }

  @Test
  void testParserUnterminatedString() {
    assertThrows(ParseException.class, () -> JsonDeserializer.deserializeRaw("\"unterminated"));
  }

  @Test
  void testParserUnterminatedEscape() {
    assertThrows(ParseException.class, () -> JsonDeserializer.deserializeRaw("\"\\"));
  }

  @Test
  void testParserInvalidUnicode() {
    assertThrows(ParseException.class, () -> JsonDeserializer.deserializeRaw("\"\\u123\""));
    assertThrows(ParseException.class, () -> JsonDeserializer.deserializeRaw("\"\\u123G\""));
  }

  @Test
  void testParserInvalidNumber() {
    assertThrows(ParseException.class, () -> JsonDeserializer.deserializeRaw("1.2.3"));
  }

  @Test
  void testParserExpectedBoolean() {
    // 't' but not 'true'
    assertThrows(ParseException.class, () -> JsonDeserializer.deserializeRaw("tr"));
    // 'f' but not 'false'
    assertThrows(ParseException.class, () -> JsonDeserializer.deserializeRaw("fa"));
  }

  @Test
  void testParserExpectedNull() {
    // 'n' but not 'null'
    assertThrows(ParseException.class, () -> JsonDeserializer.deserializeRaw("nu"));
  }

  // Helper class for tests
  public static class CustomType {
    @JsonAttribute("x")
    private final double m_x;

    @JsonAttribute("y")
    private final double m_y;

    @JsonConstructor
    CustomType(@JsonAttribute("x") double x, @JsonAttribute("y") double y) {
      m_x = x;
      m_y = y;
    }
  }
}
