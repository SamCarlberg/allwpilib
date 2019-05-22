/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilib.sendable.generator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import edu.wpi.first.wpilib.sendable.generator.java.JavaFileWriter;
import edu.wpi.first.wpilib.sendable.generator.java.JavaGenerator;
import edu.wpi.first.wpilib.sendable.schema.SchemaReader;
import edu.wpi.first.wpilib.sendable.schema.SendableSchema;

public final class Main {
  private Main() {
  }

  /**
   * Main method. The first argument must be the generation type ("java", "cpp", or "h"). The second
   * argument must be an absolute path to the directory into which code should be generated.
   */
  public static void main(String[] args) throws IOException {
    String type = args[0];
    String destinationDir = args[1];
    Path target = Paths.get(destinationDir);

    SchemaLister lister = new SchemaLister();
    Map<SendableSchema, String> generated;
    FileWriter writer;

    switch (type) {
      case "java":
        writer = new JavaFileWriter();
        generated = lister.generate(new SchemaReader(), new JavaGenerator());
        break;
      case "cpp":
        throw new UnsupportedOperationException("C++ code generation is not yet implemented");
      case "h":
        throw new UnsupportedOperationException("C++ header generation is not yet implemented");
      default:
        throw new IllegalArgumentException("Unknown generation target type '" + type + "'");
    }

    for (Map.Entry<SendableSchema, String> entry : generated.entrySet()) {
      SendableSchema schema = entry.getKey();
      String contents = entry.getValue();
      writer.write(target, schema, contents);
    }
  }

}
