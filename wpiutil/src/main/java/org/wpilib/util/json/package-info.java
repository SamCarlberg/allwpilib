// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

/**
 * Contains a minimal JSON serialization and deserialization library for WPILib. This package is
 * built to have a minimal impact on robot program startup time (as opposed to Jackson, which may
 * take hundreds of milliseconds to load hundreds of internal classes to parse a simple AprilTag
 * field layout). This package was built for serialization and deserialization of fairly simple
 * objects like Pose2d and Rotation2d and, to remain lightweight, intentionally omits advanced
 * features that other libraries provide.
 */
package org.wpilib.util.json;
