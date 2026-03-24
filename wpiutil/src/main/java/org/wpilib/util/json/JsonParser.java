// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.util.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;

/** Parses JSON text into Java objects. */
public final class JsonParser {
  private final Reader m_reader;
  // Make the buffer the size of a hardware page (4KB)
  private final char[] m_buffer = new char[4096];
  private int m_bufferPos;
  private int m_bufferLen;
  private int m_pos;

  private JsonParser(Reader reader) {
    this.m_reader = reader;
    this.m_bufferPos = 0;
    this.m_bufferLen = 0;
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
    JsonParser parser = new JsonParser(new StringReader(json));
    Object node = parser.parseValue();
    parser.skipWhitespace();
    if (parser.hasNext()) {
      throw new ParseException(
          "Invalid JSON. Expected end of input at " + parser.m_pos, parser.m_pos);
    }
    return node;
  }

  /**
   * Parses a JSON input stream into a Java object.
   *
   * @param is The JSON input stream to parse
   * @return The parsed Java object
   * @throws ParseException If the JSON in the input stream is invalid or if the input stream cannot
   *   be read
   */
  public static Object parse(InputStream is) {
    if (is == null) {
      throw new IllegalArgumentException("Input stream cannot be null");
    }

    try (var reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
      JsonParser parser = new JsonParser(reader);
      Object node = parser.parseValue();
      parser.skipWhitespace();
      if (parser.hasNext()) {
        throw new ParseException(
            "Invalid JSON. Expected end of input at " + parser.m_pos, parser.m_pos);
      }
      return node;
    } catch (IOException e) {
      throw new ParseException("Could not read JSON text from input stream", e, 0);
    }
  }

  private boolean hasNext() {
    if (m_bufferPos < m_bufferLen) {
      return true;
    }
    fillBuffer();
    return m_bufferPos < m_bufferLen;
  }

  private char peek() {
    if (!hasNext()) {
      throw new ParseException("Unexpected end of input at position " + m_pos, m_pos);
    }
    return m_buffer[m_bufferPos];
  }

  private char next() {
    char c = peek();
    m_bufferPos++;
    m_pos++;
    return c;
  }

  private boolean matches(String s) {
    for (int i = 0; i < s.length(); i++) {
      if (!hasNext() || peek() != s.charAt(i)) {
        return false;
      }
      next();
    }
    return true;
  }

  private void fillBuffer() {
    try {
      m_bufferLen = m_reader.read(m_buffer);
      m_bufferPos = 0;
    } catch (IOException e) {
      throw new ParseException("IO error: " + e.getMessage(), m_pos);
    }
  }

  private Object parseValue() {
    skipWhitespace();
    char c = peek();
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
    next(); // skip '{'

    // Use a linked hashmap to preserve key order
    SequencedMap<String, Object> properties = new LinkedHashMap<>();
    skipWhitespace();
    if (hasNext() && peek() == '}') {
      next();
      return properties;
    }

    while (true) {
      skipWhitespace();
      if (!hasNext() || peek() != '"') {
        throw new ParseException("Expected string key in object at position " + m_pos, m_pos);
      }
      final String key = parseString();

      skipWhitespace();
      if (!hasNext() || peek() != ':') {
        throw new ParseException("Expected ':' after key in object at position " + m_pos, m_pos);
      }
      next();

      Object value = parseValue();
      properties.put(key, value);

      skipWhitespace();
      if (hasNext() && peek() == '}') {
        next();
        break;
      }
      if (!hasNext() || peek() != ',') {
        throw new ParseException("Expected ',' or '}' in object at position " + m_pos, m_pos);
      }
      next();
    }
    return properties;
  }

  private List<Object> parseArray() {
    next(); // skip '['
    List<Object> children = new ArrayList<>();
    skipWhitespace();
    if (hasNext() && peek() == ']') {
      next();
      return children;
    }

    while (true) {
      children.add(parseValue());
      skipWhitespace();
      if (hasNext() && peek() == ']') {
        next();
        break;
      }
      if (!hasNext() || peek() != ',') {
        throw new ParseException("Expected ',' or ']' in array at position " + m_pos, m_pos);
      }
      next();
    }
    return children;
  }

  private String parseString() {
    next(); // skip '"'
    StringBuilder sb = new StringBuilder();
    while (hasNext()) {
      char c = next();
      if (c == '"') {
        return sb.toString();
      } else if (c == '\\') {
        if (!hasNext()) {
          throw new ParseException("Unterminated escape sequence", m_pos);
        }
        char escaped = next();
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
            StringBuilder hexSb = new StringBuilder();
            for (int i = 0; i < 4; i++) {
              if (!hasNext()) {
                throw new ParseException("Invalid unicode escape", m_pos);
              }
              hexSb.append(next());
            }
            String hex = hexSb.toString();
            try {
              sb.append((char) Integer.parseInt(hex, 16));
            } catch (NumberFormatException e) {
              throw new ParseException("Invalid unicode escape: " + hex, m_pos);
            }
          }
          default -> sb.append(escaped);
        }
      } else {
        sb.append(c);
      }
    }
    throw new ParseException("Unterminated string starting at " + m_pos, m_pos);
  }

  private double parseNumber() {
    int start = m_pos;
    StringBuilder sb = new StringBuilder();
    while (hasNext()) {
      char c = peek();
      if (Character.isDigit(c) || c == '.' || c == '-' || c == '+' || c == 'e' || c == 'E') {
        sb.append(next());
      } else {
        break;
      }
    }
    String numStr = sb.toString();
    try {
      return Double.parseDouble(numStr);
    } catch (NumberFormatException e) {
      throw new ParseException("Invalid number format: " + numStr + " at position " + start, start);
    }
  }

  private boolean parseBoolean() {
    if (peek() == 't') {
      if (matches("true")) {
        return true;
      }
    } else if (peek() == 'f') {
      if (matches("false")) {
        return false;
      }
    }
    throw new ParseException("Expected boolean at position " + m_pos, m_pos);
  }

  private Object parseNull() {
    if (matches("null")) {
      return null;
    }
    throw new ParseException("Expected null at position " + m_pos, m_pos);
  }

  private void skipWhitespace() {
    while (hasNext() && Character.isWhitespace(peek())) {
      next();
    }
  }
}
