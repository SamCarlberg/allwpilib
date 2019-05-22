/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilib.sendable.generator.java;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import edu.wpi.first.wpilib.sendable.generator.FileWriter;
import edu.wpi.first.wpilib.sendable.schema.SendableSchema;

public class JavaFileWriter implements FileWriter {
  @Override
  public void write(Path targetDirectory, SendableSchema schema, String contents)
      throws IOException {
    String fileName = JavaGenerator.createClassName(schema) + ".java";
    Path targetFile = targetDirectory.resolve(fileName);
    if (!Files.isDirectory(targetDirectory) || !Files.exists(targetDirectory)) {
      Files.createDirectories(targetDirectory);
    }
    Files.deleteIfExists(targetFile);
    Files.writeString(
        targetFile,
        contents,
        StandardCharsets.UTF_8,
        StandardOpenOption.WRITE,
        StandardOpenOption.CREATE
    );
  }
}
