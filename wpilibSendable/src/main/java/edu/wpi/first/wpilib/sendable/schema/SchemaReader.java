/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilib.sendable.schema;

import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SchemaReader {
  private final Gson m_gson;

  /**
   * Creates a new schema reader.
   */
  public SchemaReader() {
    m_gson = new GsonBuilder()
        .registerTypeAdapter(SendableSchema.class, new SendableSchemaDeserializer())
        .create();
  }

  /**
   * Parses a Sendable schema from a JSON input stream.
   *
   * @param inputStream a stream of the JSON data
   * @return the schema
   */
  public SendableSchema readSchema(InputStream inputStream) {
    return m_gson.fromJson(
        StrictNamesJsonReader.of(new InputStreamReader(inputStream)),
        SendableSchema.class
    );
  }

}
