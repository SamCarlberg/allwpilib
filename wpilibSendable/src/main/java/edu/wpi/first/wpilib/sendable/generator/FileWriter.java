/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilib.sendable.generator;

import java.io.IOException;
import java.nio.file.Path;

import edu.wpi.first.wpilib.sendable.schema.SendableSchema;

public interface FileWriter {

  void write(Path targetDirectory, SendableSchema schema, String contents) throws IOException;

}
