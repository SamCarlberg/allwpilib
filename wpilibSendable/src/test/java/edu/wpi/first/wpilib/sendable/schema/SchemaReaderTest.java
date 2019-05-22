/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilib.sendable.schema;

import java.io.InputStream;
import java.util.List;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SchemaReaderTest {
  @Test
  void testSimpleSchema() {
    SchemaReader reader = new SchemaReader();
    SendableSchema schema = reader.readSchema(resource("simple"));
    assertEquals("Example", schema.getType());
    assertEquals(
        List.of(new SendableProperty("Value", SendablePropertyType.TEXT, true, false)),
        schema.getProperties()
    );
  }

  @Test
  void testOptionalProperties() {
    SchemaReader reader = new SchemaReader();
    SendableSchema schema = reader.readSchema(resource("optional-properties"));
    assertFalse(schema.isActuator());
    assertEquals(
        List.of(new SendableProperty("Property", SendablePropertyType.TEXT, true, false)),
        schema.getProperties()
    );
  }

  @Test
  void testNoSendableTypeSpecified() {
    SchemaReader reader = new SchemaReader();
    assertThrows(
        JsonParseException.class,
        () -> reader.readSchema(resource("no-sendable-type")));
  }

  @Test
  void testNoPropertyTypeSpecified() {
    SchemaReader reader = new SchemaReader();
    assertThrows(
        JsonParseException.class,
        () -> reader.readSchema(resource("no-property-type")));
  }

  @Test
  void testDuplicatePropertyNames() {
    SchemaReader reader = new SchemaReader();
    assertThrows(JsonSyntaxException.class,
        () -> reader.readSchema(resource("duplicate-property-names")));
  }

  @Test
  void testInvalidPropertyName() {
    SchemaReader reader = new SchemaReader();
    assertThrows(JsonSyntaxException.class,
        () -> reader.readSchema(resource("invalid-property-name")));
  }

  private InputStream resource(String name) {
    return SchemaReaderTest.class.getResourceAsStream("/" + name + ".sendable.json");
  }

}
