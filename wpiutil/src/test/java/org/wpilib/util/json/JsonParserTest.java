// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.util.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JsonParserTest {
  @Test
  void testNull() {
    assertNull(JsonParser.parse("null"));
  }

  @Test
  void testBoolean() {
    Object trueNode = JsonParser.parse("true");
    assertTrue((Boolean) trueNode);

    Object falseNode = JsonParser.parse("false");
    assertFalse((Boolean) falseNode);
  }

  @Test
  void testInvalidBooleans() {
    assertThrows(ParseException.class, () -> JsonParser.parse("truefalse"));
    assertThrows(ParseException.class, () -> JsonParser.parse("true1.2"));
  }

  @Test
  void testString() {
    Object strNode = JsonParser.parse("\"string\"");
    assertEquals("string", strNode);
  }

  @Test
  void testInvalidStrings() {
    assertThrows(ParseException.class, () -> JsonParser.parse("\"string\"\"string 2\""));
  }

  @Test
  void testNumber() {
    assertEquals(1.0, JsonParser.parse("1"));
    assertEquals(0.1, JsonParser.parse("0.1"));
    assertEquals(0.1, JsonParser.parse(".1"));
    assertEquals(-0.1, JsonParser.parse("-.1"));
    assertEquals(0.1, JsonParser.parse("1e-1"));
    assertEquals(10.0, JsonParser.parse("1e1"));
    assertEquals(10.0, JsonParser.parse("1E1"));
    assertEquals(10.0, JsonParser.parse("1e+1"));
  }

  @Test
  void testInvalidNumbers() {
    assertThrows(ParseException.class, () -> JsonParser.parse("1.1.1"));
    assertThrows(ParseException.class, () -> JsonParser.parse("1.1.0"));
    assertThrows(ParseException.class, () -> JsonParser.parse("1.1e2.1"));
    assertThrows(ParseException.class, () -> JsonParser.parse("1e+1e"));
    assertThrows(ParseException.class, () -> JsonParser.parse("-"));
    assertThrows(ParseException.class, () -> JsonParser.parse("."));
  }

  @Test
  void testInvalidObjects() {
    // Missing key
    assertThrows(ParseException.class, () -> JsonParser.parse("{ : \"value\" }"));
    // Missing colon
    assertThrows(ParseException.class, () -> JsonParser.parse("{ \"key\" \"value\" }"));
    // Missing value
    assertThrows(ParseException.class, () -> JsonParser.parse("{ \"key\": }"));
    // Missing comma
    assertThrows(ParseException.class, () -> JsonParser.parse("{ \"key1\": \"value1\" \"key2\": \"value2\" }"));
    // Trailing comma
    assertThrows(ParseException.class, () -> JsonParser.parse("{ \"key1\": \"value1\", }"));
    // Key is not a string
    assertThrows(ParseException.class, () -> JsonParser.parse("{ 123: \"value\" }"));
  }

  @Test
  void testInvalidArrays() {
    // Missing comma
    assertThrows(ParseException.class, () -> JsonParser.parse("[1 2]"));
    // Trailing comma
    assertThrows(ParseException.class, () -> JsonParser.parse("[1, 2, ]"));
    // Unexpected character
    assertThrows(ParseException.class, () -> JsonParser.parse("[1, 2; 3]"));
  }

  @Test
  void testUnexpectedEnd() {
    assertThrows(ParseException.class, () -> JsonParser.parse("{\"key\":"));
    assertThrows(ParseException.class, () -> JsonParser.parse("[1, 2,"));
    assertThrows(ParseException.class, () -> JsonParser.parse("\"unterminated"));
    assertThrows(ParseException.class, () -> JsonParser.parse("truef"));
  }

  @Test
  void testParseExceptionPosition() {
    ParseException ex = assertThrows(ParseException.class, () -> JsonParser.parse("truefalse"));
    assertEquals(4, ex.getPosition());

    ex = assertThrows(ParseException.class, () -> JsonParser.parse("{\"key\"  \"value\"}"));
    // position of '\"value\"' after skipWhitespace
    assertEquals(8, ex.getPosition());
  }

  @Test
  @SuppressWarnings("unchecked")
  void testArray() {
    Object arrayNode = JsonParser.parse("[1, 2, 4, true, \"str\"]");
    assertInstanceOf(List.class, arrayNode);
    List<Object> children = (List<Object>) arrayNode;
    assertEquals(5, children.size());
    assertEquals(1.0, children.get(0));
    assertEquals(2.0, children.get(1));
    assertEquals(4.0, children.get(2));
    assertTrue((Boolean) children.get(3));
    assertEquals("str", children.get(4));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testObject() {
    Object objectNode = JsonParser.parse("{ \"key\": \"value\" }");
    assertInstanceOf(Map.class, objectNode);
    Map<String, Object> properties = (Map<String, Object>) objectNode;
    assertEquals(1, properties.size());
    assertEquals("value", properties.get("key"));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testNested() {
    Object node =
        JsonParser.parse(
            """
        {
          "a": [
            1,
            2,
            {
              "b": 3
            }
          ],
          "c": null
        }
        """);
    assertInstanceOf(Map.class, node);
    Map<String, Object> props = (Map<String, Object>) node;
    assertInstanceOf(List.class, props.get("a"));
    assertNull(props.get("c"));
  }

  @Test
  void testUnicode() {
    Object node = JsonParser.parse("\"\\u0041\"");
    assertEquals("A", node);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testInputStream() {
    String json = "{\"key\": \"value\", \"array\": [1, 2, 3], \"bool\": true, \"null\": null}";
    ByteArrayInputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
    Object node = JsonParser.parse(is);
    assertInstanceOf(Map.class, node);
    Map<String, Object> props = (Map<String, Object>) node;
    assertEquals("value", props.get("key"));
    assertEquals(List.of(1.0, 2.0, 3.0), props.get("array"));
    assertTrue((Boolean) props.get("bool"));
    assertNull(props.get("null"));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testLargeInput() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < 10000; i++) {
      if (i > 0) sb.append(",");
      sb.append(i);
    }
    sb.append("]");
    String json = sb.toString();
    ByteArrayInputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
    Object node = JsonParser.parse(is);
    assertInstanceOf(List.class, node);
    List<Object> list = (List<Object>) node;
    assertEquals(10000, list.size());
    assertEquals(0.0, list.get(0));
    assertEquals(9999.0, list.get(9999));
  }
}
