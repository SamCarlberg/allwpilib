package edu.wpi.first.wpilib.sendable.generator.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import edu.wpi.first.wpilib.sendable.schema.SchemaReader;
import edu.wpi.first.wpilib.sendable.schema.SendableSchema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("PMD")
class JavaGeneratorTest {

  private final SchemaReader m_reader = new SchemaReader();

  @ParameterizedTest
  @MethodSource("args")
  void test(String name) {
    SendableSchema schema = schema(name);
    String expected = expected(name);
    assertEquals(expected, new JavaGenerator().generate(schema));
  }

  static Stream<String> args() {
    return Stream.of(
        "all-property-types",
        "all-special",
        "metadata",
        "no-setters"
    );
  }

  private SendableSchema schema(String name) {
    return m_reader.readSchema(
        JavaGeneratorTest.class.getResourceAsStream("/" + name + ".sendable.json")
    );
  }

  private String expected(String name) {
    var in = JavaGeneratorTest.class.getResourceAsStream("/expected/java/" + name + ".java.txt");
    try (var reader = new BufferedReader(new InputStreamReader(in))) {
      return reader.lines()
          .collect(Collectors.joining("\n"))
          + "\n"; // reader discards trailing empty newlines, so we need to manually add it back
    } catch (IOException ex) {
      fail("Could not read expected file", ex);
    }
    return null;
  }
}
