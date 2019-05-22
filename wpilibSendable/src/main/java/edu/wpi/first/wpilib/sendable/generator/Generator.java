package edu.wpi.first.wpilib.sendable.generator;

import edu.wpi.first.wpilib.sendable.schema.SendableSchema;

public interface Generator {

  String generate(SendableSchema schema);

}
