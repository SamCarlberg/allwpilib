// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.wpilibj.examples.elevatorsimulation;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Kilograms;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecondPerSecond;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Mass;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Per;
import edu.wpi.first.units.Velocity;
import edu.wpi.first.units.Voltage;

public class Constants {
  public static final int kMotorPort = 0;
  public static final int kEncoderAChannel = 0;
  public static final int kEncoderBChannel = 1;
  public static final int kJoystickPort = 0;

  public static final double kElevatorKp = 5;
  public static final double kElevatorKi = 0;
  public static final double kElevatorKd = 0;

  public static final Measure<Voltage> kElevatorkS = Volts.zero();
  public static final Measure<Voltage> kElevatorkG = Volts.of(0.762);
  public static final Measure<Per<Voltage, Velocity<Distance>>> kElevatorkV =
      Volts.of(0.762).per(MetersPerSecond);
  public static final Measure<Per<Voltage, Velocity<Velocity<Distance>>>> kElevatorkA =
      Volts.per(MetersPerSecondPerSecond).zero();

  public static final double kElevatorGearing = 10.0;
  public static final Measure<Distance> kElevatorDrumRadius = Inches.of(2.0);
  public static final Measure<Mass> kCarriageMass = Kilograms.of(4.0);

  public static final Measure<Distance> kSetpoint = Meters.of(0.75);
  // Encoder is reset to measure 0 at the bottom, so minimum height is 0.
  public static final Measure<Distance> kMinElevatorHeight = Meters.zero();
  public static final Measure<Distance> kMaxElevatorHeight = Meters.of(1.25);

  // distance per pulse = (distance per revolution) / (pulses per revolution)
  //  = (Pi * D) / ppr
  public static final Measure<Distance> kElevatorEncoderDistPerPulse =
      kElevatorDrumRadius.times(2.0 * Math.PI).divide(4096);
}
