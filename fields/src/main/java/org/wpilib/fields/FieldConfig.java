// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.fields;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.wpilib.util.json.JsonAttribute;
import org.wpilib.util.json.JsonDeserializer;

public class FieldConfig {
  public static class Corners {
    @JsonAttribute("top-left")
    public double[] m_topLeft;

    @JsonAttribute("bottom-right")
    public double[] m_bottomRight;
  }

  @JsonAttribute("game")
  public String m_game;

  @JsonAttribute("field-image")
  public String m_fieldImage;

  @JsonAttribute("field-corners")
  public Corners m_fieldCorners;

  @JsonAttribute("field-size")
  public double[] m_fieldSize;

  @JsonAttribute("field-unit")
  public String m_fieldUnit;

  public FieldConfig() {}

  public URL getImageUrl() {
    return getClass().getResource(Fields.kBaseResourceDir + m_fieldImage);
  }

  public InputStream getImageAsStream() {
    return getClass().getResourceAsStream(Fields.kBaseResourceDir + m_fieldImage);
  }

  /**
   * Loads a predefined field configuration from a resource file.
   *
   * @param field The predefined field
   * @return The field configuration
   * @throws IOException Throws if the file could not be loaded
   */
  public static FieldConfig loadField(Fields field) throws IOException {
    return loadFromResource(field.m_resourceFile);
  }

  /**
   * Loads a field configuration from a file on disk.
   *
   * @param file The json file to load
   * @return The field configuration
   * @throws IOException Throws if the file could not be loaded
   */
  public static FieldConfig loadFromFile(Path file) throws IOException {
    String json = Files.readString(file);
    return JsonDeserializer.deserialize(json, FieldConfig.class);
  }

  /**
   * Loads a field configuration from a resource file located inside the programs jar file.
   *
   * @param resourcePath The path to the resource file
   * @return The field configuration
   * @throws IOException Throws if the resource could not be loaded
   */
  public static FieldConfig loadFromResource(String resourcePath) throws IOException {
    try (InputStream stream = FieldConfig.class.getResourceAsStream(resourcePath)) {
      String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
      return JsonDeserializer.deserialize(json, FieldConfig.class);
    }
  }
}
