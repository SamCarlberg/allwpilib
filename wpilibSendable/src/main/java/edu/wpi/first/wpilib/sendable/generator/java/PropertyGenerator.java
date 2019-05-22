/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilib.sendable.generator.java;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilib.sendable.generator.Capitalizer;
import edu.wpi.first.wpilib.sendable.schema.SendableProperty;
import edu.wpi.first.wpilib.sendable.schema.SendablePropertyType;

/**
 * Handles generation of property-related code.
 */
@SuppressWarnings("PMD.InsufficientStringBufferDeclaration")
public class PropertyGenerator {
  /**
   * Generates the field declarations for the getter and setter for the given property. Each
   * property declaration is a separate item in the returned list.
   */
  public List<String> generatePropertyFields(SendableProperty property) {
    List<String> list = new ArrayList<>(2);

    StringBuilder builder = new StringBuilder();
    builder.append("  private final ")
        .append(getterType(property))
        .append(' ')
        .append(getterFieldName(property))
        .append(";\n");
    list.add(builder.toString());

    if (property.hasSetter()) {
      builder.setLength(0);
      builder.append("  private final ")
          .append(setterType(property))
          .append(' ')
          .append(setterFieldName(property))
          .append(";\n");
      list.add(builder.toString());
    }
    return list;
  }

  /**
   * Generates the constructor arguments for the getter and setter for the given property. Each
   * property argument is a separate item in the returned list.
   */
  public List<String> generatePropertyArgs(SendableProperty property) {
    List<String> list = new ArrayList<>(2);

    StringBuilder builder = new StringBuilder();
    builder.append(getterType(property))
        .append(' ')
        .append(getterName(property));
    list.add(builder.toString());

    if (property.hasSetter()) {
      builder.setLength(0);
      builder.append(setterType(property))
          .append(' ')
          .append(setterName(property));
      list.add(builder.toString());
    }
    return list;
  }

  /**
   * Generates the field assignments for the getter and setter for the given property. Each
   * assignment is a separate item in the returned list.
   */
  public List<String> generatePropertyFieldAssignments(SendableProperty property) {
    List<String> list = new ArrayList<>(2);

    StringBuilder builder = new StringBuilder();
    builder.append("    ")
        .append(getterFieldName(property))
        .append(" = Objects.requireNonNull(")
        .append(getterName(property))
        .append(", \"")
        .append(getterName(property))
        .append("\");\n");
    list.add(builder.toString());

    if (property.hasSetter()) {
      builder.setLength(0);
      builder.append("    ")
          .append(setterFieldName(property))
          .append(" = Objects.requireNonNull(")
          .append(setterName(property))
          .append(", \"")
          .append(setterName(property))
          .append("\");\n");
      list.add(builder.toString());
    }
    return list;
  }

  /**
   * Generates the sendable code for the given property.
   */
  public String generateSendableImpl(SendableProperty property) {
    StringBuilder builder = new StringBuilder();
    builder.append("    builder.")
        .append(builderPropertyType(property.getType()))
        .append("(\"");
    if (property.isMetadata()) {
      builder.append('.');
    }
    builder.append(propertyName(property))
        .append("\", ")
        .append(getterFieldName(property))
        .append(", ");

    if (property.hasSetter()) {
      builder.append(setterFieldName(property));
    } else {
      builder.append("null");
    }
    builder.append(");\n");
    return builder.toString();
  }

  private String propertyName(SendableProperty property) {
    return new Capitalizer().capitalize(property.getName());
  }

  private String getterName(SendableProperty property) {
    return "get" + propertyName(property);
  }

  private String getterFieldName(SendableProperty property) {
    return "m_" + getterName(property);
  }

  private String setterName(SendableProperty property) {
    return "set" + propertyName(property);
  }

  private String setterFieldName(SendableProperty property) {
    return "m_" + setterName(property);
  }

  @SuppressWarnings("PMD.CyclomaticComplexity")
  private static String getterType(SendableProperty property) {
    switch (property.getType()) {
      case NUMBER:
        return "DoubleSupplier";
      case TEXT:
        return "Supplier<String>";
      case BOOLEAN:
        return "BooleanSupplier";
      case NUMBER_ARRAY:
        return "Supplier<double[]>";
      case TEXT_ARRAY:
        return "Supplier<String[]>";
      case BOOLEAN_ARRAY:
        return "Supplier<boolean[]>";
      case RAW:
        return "Supplier<byte[]>";
      case VALUE:
        return "Supplier<NetworkTableValue>";
      default:
        throw new IllegalArgumentException(
            "Unsupported property type '" + property.getType() + "'");
    }
  }

  @SuppressWarnings("PMD.CyclomaticComplexity")
  private static String setterType(SendableProperty property) {
    switch (property.getType()) {
      case NUMBER:
        return "DoubleConsumer";
      case TEXT:
        return "Consumer<String>";
      case BOOLEAN:
        return "BooleanConsumer";
      case NUMBER_ARRAY:
        return "Consumer<double[]>";
      case TEXT_ARRAY:
        return "Consumer<String[]>";
      case BOOLEAN_ARRAY:
        return "Consumer<boolean[]>";
      case RAW:
        return "Consumer<byte[]>";
      case VALUE:
        return "Consumer<NetworkTableValue>";
      default:
        throw new IllegalArgumentException(
            "Unsupported property type '" + property.getType() + "'");
    }
  }

  @SuppressWarnings("PMD.CyclomaticComplexity")
  private static String builderPropertyType(SendablePropertyType propertyType) {
    switch (propertyType) {
      case NUMBER:
        return "addDoubleProperty";
      case TEXT:
        return "addStringProperty";
      case BOOLEAN:
        return "addBooleanProperty";
      case NUMBER_ARRAY:
        return "addDoubleArrayProperty";
      case TEXT_ARRAY:
        return "addStringArrayProperty";
      case BOOLEAN_ARRAY:
        return "addBooleanArrayProperty";
      case RAW:
        return "addRawProperty";
      case VALUE:
        return "addValueProperty";
      default:
        throw new IllegalArgumentException("Unsupported property type '" + propertyType + "'");
    }
  }

}
