// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.util.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Place on a field or getter method for JSON serialization, and on constructor parameters for JSON
 * deserialization. Note that constructor parameters only need to be a subset of the serialized
 * attributes - any attributes present in JSON text that do not have corresponding constructor
 * parameters will be ignored.
 *
 * <p>This example class will serialize as {@code {"foo": "foo-value", "bar": "foo-value-bar"} },
 * but deserializing will only use the "foo" attribute. This can be useful for including extra
 * information in serialized JSON that isn't necessary for the Java objects.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonAttribute {
  /**
   * The name of the JSON attribute to map to.
   *
   * @return The attribute name.
   */
  String value();
}
