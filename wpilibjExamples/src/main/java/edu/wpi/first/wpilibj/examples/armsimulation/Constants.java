// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.wpilibj.examples.armsimulation;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Kilograms;
import static edu.wpi.first.units.Units.Rotations;

import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Mass;
import edu.wpi.first.units.Measure;

public class Constants {
  public static final int kMotorPort = 0;
  public static final int kEncoderAChannel = 0;
  public static final int kEncoderBChannel = 1;
  public static final int kJoystickPort = 0;

  public static final String kArmPositionKey = "ArmPosition";
  public static final String kArmPKey = "ArmP";

  // The P gain for the PID controller that drives this arm.
  public static final double kDefaultArmKp = 50.0;
  public static final Measure<Angle> kDefaultArmSetpoint = Degrees.of(75);

  // distance per pulse = 1 rotation / (pulses per rotation)
  //  = 1 / (4096 pulses)
  public static final Measure<Angle> kArmEncoderDistPerPulse = Rotations.one().divide(4096);

  public static final double kArmReduction = 200;
  public static final Measure<Mass> kArmMass = Kilograms.of(8.0);
  public static final Measure<Distance> kArmLength = Inches.of(30);
  public static final Measure<Angle> kMinAngle = Degrees.of(-75);
  public static final Measure<Angle> kMaxAngle = Degrees.of(255);
}
