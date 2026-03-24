// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.util.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a constructor as the JSON deserialization constructor for a class. Only one constructor per
 * class may have this annotation. Every parameter on the constructor must have a {@link
 * JsonAttribute @JsonAttribute} annotation describing what JSON attribute to pass to it. Any
 * attribute in a JSON object that does not have a corresponding parameter will be ignored.
 */
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonConstructor {}
