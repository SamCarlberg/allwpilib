// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.math.util;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Feet;
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Kilograms;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Milliseconds;
import static edu.wpi.first.units.Units.Pounds;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.Seconds;

/** Utility class that converts between commonly used units in FRC. */
public final class Units {
  /** Utility class, so constructor is private. */
  private Units() {
    throw new UnsupportedOperationException("This is a utility class!");
  }

  /**
   * Converts given meters to feet.
   *
   * @param meters The meters to convert to feet.
   * @return Feet converted from meters.
   */
  public static double metersToFeet(double meters) {
    return Feet.convertFrom(meters, Meters);
  }

  /**
   * Converts given feet to meters.
   *
   * @param feet The feet to convert to meters.
   * @return Meters converted from feet.
   */
  public static double feetToMeters(double feet) {
    return Meters.convertFrom(feet, Feet);
  }

  /**
   * Converts given meters to inches.
   *
   * @param meters The meters to convert to inches.
   * @return Inches converted from meters.
   */
  public static double metersToInches(double meters) {
    return Inches.convertFrom(meters, Meters);
  }

  /**
   * Converts given inches to meters.
   *
   * @param inches The inches to convert to meters.
   * @return Meters converted from inches.
   */
  public static double inchesToMeters(double inches) {
    return Meters.convertFrom(inches, Inches);
  }

  /**
   * Converts given degrees to radians.
   *
   * @param degrees The degrees to convert to radians.
   * @return Radians converted from degrees.
   */
  public static double degreesToRadians(double degrees) {
    return Radians.convertFrom(degrees, Degrees);
  }

  /**
   * Converts given radians to degrees.
   *
   * @param radians The radians to convert to degrees.
   * @return Degrees converted from radians.
   */
  public static double radiansToDegrees(double radians) {
    return Degrees.convertFrom(radians, Radians);
  }

  /**
   * Converts given radians to rotations.
   *
   * @param radians The radians to convert.
   * @return rotations Converted from radians.
   */
  public static double radiansToRotations(double radians) {
    return Rotations.convertFrom(radians, Radians);
  }

  /**
   * Converts given degrees to rotations.
   *
   * @param degrees The degrees to convert.
   * @return rotations Converted from degrees.
   */
  public static double degreesToRotations(double degrees) {
    return Rotations.convertFrom(degrees, Degrees);
  }

  /**
   * Converts given rotations to degrees.
   *
   * @param rotations The rotations to convert.
   * @return degrees Converted from rotations.
   */
  public static double rotationsToDegrees(double rotations) {
    return Degrees.convertFrom(rotations, Rotations);
  }

  /**
   * Converts given rotations to radians.
   *
   * @param rotations The rotations to convert.
   * @return radians Converted from rotations.
   */
  public static double rotationsToRadians(double rotations) {
    return Radians.convertFrom(rotations, Rotations);
  }

  /**
   * Converts rotations per minute to radians per second.
   *
   * @param rpm The rotations per minute to convert to radians per second.
   * @return Radians per second converted from rotations per minute.
   */
  public static double rotationsPerMinuteToRadiansPerSecond(double rpm) {
    return RadiansPerSecond.convertFrom(rpm, RPM);
  }

  /**
   * Converts radians per second to rotations per minute.
   *
   * @param radiansPerSecond The radians per second to convert to from rotations per minute.
   * @return Rotations per minute converted from radians per second.
   */
  public static double radiansPerSecondToRotationsPerMinute(double radiansPerSecond) {
    return RPM.convertFrom(radiansPerSecond, RadiansPerSecond);
  }

  /**
   * Converts given milliseconds to seconds.
   *
   * @param milliseconds The milliseconds to convert to seconds.
   * @return Seconds converted from milliseconds.
   */
  public static double millisecondsToSeconds(double milliseconds) {
    return Seconds.convertFrom(milliseconds, Milliseconds);
  }

  /**
   * Converts given seconds to milliseconds.
   *
   * @param seconds The seconds to convert to milliseconds.
   * @return Milliseconds converted from seconds.
   */
  public static double secondsToMilliseconds(double seconds) {
    return Milliseconds.convertFrom(seconds, Seconds);
  }

  /**
   * Converts kilograms into lbs (pound-mass).
   *
   * @param kilograms The kilograms to convert to lbs (pound-mass).
   * @return Lbs (pound-mass) converted from kilograms.
   */
  public static double kilogramsToLbs(double kilograms) {
    return Pounds.convertFrom(kilograms, Kilograms);
  }

  /**
   * Converts lbs (pound-mass) into kilograms.
   *
   * @param lbs The lbs (pound-mass) to convert to kilograms.
   * @return Kilograms converted from lbs (pound-mass).
   */
  public static double lbsToKilograms(double lbs) {
    return Kilograms.convertFrom(lbs, Pounds);
  }
}
