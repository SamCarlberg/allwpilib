// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.util.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;

/** Parses JSON text into Java objects. */
public final class JsonParser {
  private final String m_json;
  private int m_pos;

  private JsonParser(String json) {
    this.m_json = json;
    this.m_pos = 0;
  }

  /**
   * Parses a JSON string into a Java object.
   *
   * @param json The JSON string to parse
   * @return The parsed Java object
   * @throws ParseException If the JSON string is invalid
   */
  public static Object parse(String json) {
    if (json == null) {
      throw new IllegalArgumentException("JSON string cannot be null");
    }
    JsonParser parser = new JsonParser(json);
    Object node = parser.parseValue();
    parser.skipWhitespace();
    if (parser.m_pos < json.length()) {
      throw new ParseException(
          "Invalid JSON. Expected end of input at " + parser.m_pos, parser.m_pos);
    }
    return node;
  }

  private Object parseValue() {
    skipWhitespace();
    if (m_pos >= m_json.length()) {
      throw new ParseException("Unexpected end of input at position " + m_pos, m_pos);
    }

    char c = m_json.charAt(m_pos);
    Object node =
        switch (c) {
          case '{' -> parseObject();
          case '[' -> parseArray();
          case '"' -> parseString();
          case 't', 'f' -> parseBoolean();
          case 'n' -> parseNull();
          default -> {
            if (Character.isDigit(c) || c == '-' || c == '.') {
              yield parseNumber();
            } else {
              throw new ParseException(
                  "Unexpected character: " + c + " at position " + m_pos, m_pos);
            }
          }
        };
    skipWhitespace();
    return node;
  }

  private Map<String, Object> parseObject() {
    m_pos++; // skip '{'

    // Use a linked hashmap to preserve key order
    SequencedMap<String, Object> properties = new LinkedHashMap<>();
    skipWhitespace();
    if (m_pos < m_json.length() && m_json.charAt(m_pos) == '}') {
      m_pos++;
      return properties;
    }

    while (true) {
      skipWhitespace();
      if (m_pos >= m_json.length() || m_json.charAt(m_pos) != '"') {
        throw new ParseException("Expected string key in object at position " + m_pos, m_pos);
      }
      final String key = parseString();

      skipWhitespace();
      if (m_pos >= m_json.length() || m_json.charAt(m_pos) != ':') {
        throw new ParseException("Expected ':' after key in object at position " + m_pos, m_pos);
      }
      m_pos++;

      Object value = parseValue();
      properties.put(key, value);

      if (m_pos < m_json.length() && m_json.charAt(m_pos) == '}') {
        m_pos++;
        break;
      }
      if (m_pos >= m_json.length() || m_json.charAt(m_pos) != ',') {
        throw new ParseException("Expected ',' or '}' in object at position " + m_pos, m_pos);
      }
      m_pos++;
    }
    return properties;
  }

  private List<Object> parseArray() {
    m_pos++; // skip '['
    List<Object> children = new ArrayList<>();
    skipWhitespace();
    if (m_pos < m_json.length() && m_json.charAt(m_pos) == ']') {
      m_pos++;
      return children;
    }

    while (true) {
      children.add(parseValue());
      if (m_pos < m_json.length() && m_json.charAt(m_pos) == ']') {
        m_pos++;
        break;
      }
      if (m_pos >= m_json.length() || m_json.charAt(m_pos) != ',') {
        throw new ParseException("Expected ',' or ']' in array at position " + m_pos, m_pos);
      }
      m_pos++;
    }
    return children;
  }

  private String parseString() {
    m_pos++; // skip '"'
    StringBuilder sb = new StringBuilder();
    while (m_pos < m_json.length()) {
      char c = m_json.charAt(m_pos);
      if (c == '"') {
        m_pos++;
        return sb.toString();
      } else if (c == '\\') {
        m_pos++;
        if (m_pos >= m_json.length()) {
          throw new ParseException("Unterminated escape sequence", m_pos);
        }
        char escaped = m_json.charAt(m_pos);
        switch (escaped) {
          case '"' -> sb.append('"');
          case '\\' -> sb.append('\\');
          case '/' -> sb.append('/');
          case 'b' -> sb.append('\b');
          case 'f' -> sb.append('\f');
          case 'n' -> sb.append('\n');
          case 'r' -> sb.append('\r');
          case 't' -> sb.append('\t');
          case 'u' -> {
            if (m_pos + 4 >= m_json.length()) {
              throw new ParseException("Invalid unicode escape", m_pos);
            }
            String hex = m_json.substring(m_pos + 1, m_pos + 5);
            try {
              sb.append((char) Integer.parseInt(hex, 16));
            } catch (NumberFormatException e) {
              throw new ParseException("Invalid unicode escape: " + hex, m_pos);
            }
            m_pos += 4;
          }
          default -> sb.append(escaped);
        }
      } else {
        sb.append(c);
      }
      m_pos++;
    }
    throw new ParseException("Unterminated string starting at " + m_pos, m_pos);
  }

  private double parseNumber() {
    int start = m_pos;
    // Consume characters that can be part of a number
    // We'll be a bit liberal here and let Double.parseDouble handle the actual validation
    while (m_pos < m_json.length()) {
      char c = m_json.charAt(m_pos);
      if (Character.isDigit(c) || c == '.' || c == '-' || c == '+' || c == 'e' || c == 'E') {
        m_pos++;
      } else {
        break;
      }
    }
    String numStr = m_json.substring(start, m_pos);
    try {
      return Double.parseDouble(numStr);
    } catch (NumberFormatException e) {
      throw new ParseException("Invalid number format: " + numStr + " at position " + start, start);
    }
  }

  private boolean parseBoolean() {
    if (m_json.startsWith("true", m_pos)) {
      m_pos += 4;
      return true;
    } else if (m_json.startsWith("false", m_pos)) {
      m_pos += 5;
      return false;
    }
    throw new ParseException("Expected boolean at position " + m_pos, m_pos);
  }

  private Object parseNull() {
    if (m_json.startsWith("null", m_pos)) {
      m_pos += 4;
      return null;
    }
    throw new ParseException("Expected null at position " + m_pos, m_pos);
  }

  private void skipWhitespace() {
    while (m_pos < m_json.length() && Character.isWhitespace(m_json.charAt(m_pos))) {
      m_pos++;
    }
  }
}
