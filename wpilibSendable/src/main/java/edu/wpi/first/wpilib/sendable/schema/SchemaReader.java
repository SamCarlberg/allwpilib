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
