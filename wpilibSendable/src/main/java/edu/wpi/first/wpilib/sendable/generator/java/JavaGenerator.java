package edu.wpi.first.wpilib.sendable.generator.java;

import java.util.List;
import java.util.stream.Collectors;

import edu.wpi.first.wpilib.sendable.generator.Generator;
import edu.wpi.first.wpilib.sendable.schema.SendableProperty;
import edu.wpi.first.wpilib.sendable.schema.SendableSchema;

@SuppressWarnings({"PMD.GodClass", "PMD.InsufficientStringBufferDeclaration"})
public class JavaGenerator implements Generator {

  private static final String kTemplate =
      "/*----------------------------------------------------------------------------*/\n"
          + "/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */\n"
          + "/* Open Source Software - may be modified and shared by FRC teams. The code   */\n"
          + "/* must be accompanied by the FIRST BSD license file in the root directory of */\n"
          + "/* the project.                                                               */\n"
          + "/*----------------------------------------------------------------------------*/\n\n"
          + "package edu.wpi.first.wpilibj.sendable;\n\n"
          + "import java.util.Objects;\n"
          + "import java.util.function.*;\n\n"
          + "import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;\n"
          + "import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder.BooleanConsumer;\n\n"
          + "public class ${TYPE} {\n"
          + "${SPECIAL_FIELDS}"
          + "${PROPERTY_FIELDS}\n"
          + "  public ${TYPE}(${SPECIAL_ARGS}${PROPERTY_ARGS}) {\n"
          + "${SET_SPECIAL_FIELDS}"
          + "${SET_PROPERTY_FIELDS}"
          + "  }\n\n"
          + "  public void initSendable(SendableBuilder builder) {\n"
          + "${SENDABLE_INIT}"
          + "  }\n"
          + "}\n";

  private final PropertyGenerator m_propertyGenerator = new PropertyGenerator();

  @Override
  public String generate(SendableSchema schema) {
    String propFields = schema.getProperties()
        .stream()
        .map(m_propertyGenerator::generatePropertyFields)
        .flatMap(List::stream)
        .collect(Collectors.joining());
    String propArgs = schema.getProperties()
        .stream()
        .map(m_propertyGenerator::generatePropertyArgs)
        .flatMap(List::stream)
        .collect(Collectors.joining(", "));
    String propFieldAssignments = schema.getProperties()
        .stream()
        .map(m_propertyGenerator::generatePropertyFieldAssignments)
        .flatMap(List::stream)
        .collect(Collectors.joining());

    return kTemplate.replace("${TYPE}", createClassName(schema))
        .replace("${SPECIAL_FIELDS}", createSpecialFields(schema))
        .replace("${PROPERTY_FIELDS}", propFields)
        .replace("${SPECIAL_ARGS}", createSpecialArgs(schema))
        .replace("${PROPERTY_ARGS}", propArgs)
        .replace("${SET_SPECIAL_FIELDS}", createSpecialFieldAssignments(schema))
        .replace("${SET_PROPERTY_FIELDS}", propFieldAssignments)
        .replace("${SENDABLE_INIT}", createSendableInit(schema));
  }

  public static String createClassName(SendableSchema schema) {
    return "Sendable" + schema.getType().replaceAll("\\s+", "");
  }

  private static String createSpecialArgs(SendableSchema schema) {
    if (!schema.hasSafeState() && !schema.hasUpdateTable()) {
      return "";
    }

    StringBuilder builder = new StringBuilder();
    if (schema.hasSafeState()) {
      builder.append("Runnable safeState");
    }
    if (schema.hasUpdateTable()) {
      if (builder.length() > 0) {
        builder.append(", ");
      }
      builder.append("Runnable updateTable");
    }
    if (!schema.getProperties().isEmpty()) {
      builder.append(", ");
    }
    return builder.toString();
  }

  private static String createSpecialFields(SendableSchema schema) {
    StringBuilder builder = new StringBuilder();
    if (schema.hasSafeState()) {
      builder.append("  private final Runnable m_safeState;\n");
    }
    if (schema.hasUpdateTable()) {
      builder.append("  private final Runnable m_updateTable;\n");
    }
    return builder.toString();
  }

  private static String createSpecialFieldAssignments(SendableSchema schema) {
    StringBuilder builder = new StringBuilder();
    if (schema.hasSafeState()) {
      builder.append("    m_safeState = Objects.requireNonNull(safeState, \"safeState\");\n");
    }
    if (schema.hasUpdateTable()) {
      builder.append("    m_updateTable = Objects.requireNonNull(updateTable, \"updateTable\");\n");
    }
    return builder.toString();
  }

  private String createSendableInit(SendableSchema schema) {
    StringBuilder builder = new StringBuilder();
    builder.append("    builder.setSmartDashboardType(\"")
        .append(schema.getType())
        .append("\");\n");
    if (schema.isActuator()) {
      builder.append("    builder.setActuator(true);\n");
    }
    if (schema.hasSafeState()) {
      builder.append("    builder.setSafeState(m_safeState);\n");
    }
    if (schema.hasUpdateTable()) {
      builder.append("    builder.setUpdateTable(m_updateTable);\n");
    }

    for (SendableProperty property : schema.getProperties()) {
      builder.append(m_propertyGenerator.generateSendableImpl(property));
    }
    return builder.toString();
  }

}
