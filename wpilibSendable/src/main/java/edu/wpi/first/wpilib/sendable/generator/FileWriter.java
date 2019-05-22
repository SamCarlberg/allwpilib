package edu.wpi.first.wpilib.sendable.generator;

import java.io.IOException;
import java.nio.file.Path;

import edu.wpi.first.wpilib.sendable.schema.SendableSchema;

public interface FileWriter {

  void write(Path targetDirectory, SendableSchema schema, String contents) throws IOException;

}
