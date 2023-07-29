// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.wpilibj.examples.drivedistanceoffboard;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecondPerSecond;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Per;
import edu.wpi.first.units.Velocity;
import edu.wpi.first.units.Voltage;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static final class DriveConstants {
    public static final int kLeftMotor1Port = 0;
    public static final int kLeftMotor2Port = 1;
    public static final int kRightMotor1Port = 2;
    public static final int kRightMotor2Port = 3;

    // These are example values only - DO NOT USE THESE FOR YOUR OWN ROBOT!
    // These characterization values MUST be determined either experimentally or theoretically
    // for *your* robot's drive.
    // The SysId tool provides a convenient method for obtaining these values for your robot.
    public static final Measure<Voltage> kS = Volts.of(1);
    public static final Measure<Per<Voltage, Velocity<Distance>>> kV =
        Volts.of(0.8).per(MetersPerSecond);
    public static final Measure<Per<Voltage, Velocity<Velocity<Distance>>>> kA =
        Volts.of(0.15).per(MetersPerSecondPerSecond);

    public static final double kp = 1;

    public static final Measure<Velocity<Distance>> kMaxSpeed = MetersPerSecond.of(3);
    public static final Measure<Velocity<Velocity<Distance>>> kMaxAcceleration =
        MetersPerSecondPerSecond.of(10);
  }

  public static final class OIConstants {
    public static final int kDriverControllerPort = 0;
  }
}
