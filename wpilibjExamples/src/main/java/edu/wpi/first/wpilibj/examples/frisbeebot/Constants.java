// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.wpilibj.examples.frisbeebot;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Per;
import edu.wpi.first.units.Time;
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

    public static final int[] kLeftEncoderPorts = new int[] {0, 1};
    public static final int[] kRightEncoderPorts = new int[] {2, 3};
    public static final boolean kLeftEncoderReversed = false;
    public static final boolean kRightEncoderReversed = true;

    public static final int kEncoderCPR = 1024;
    public static final Measure<Distance> kWheelDiameter = Inches.of(6);
    public static final Measure<Distance> kEncoderDistancePerPulse =
        // Assumes the encoders are directly mounted on the wheel shafts
        kWheelDiameter.times(Math.PI).divide(kEncoderCPR);
  }

  public static final class ShooterConstants {
    public static final int[] kEncoderPorts = new int[] {4, 5};
    public static final boolean kEncoderReversed = false;
    public static final int kEncoderCPR = 1024;
    public static final Measure<Angle> kEncoderDistancePerPulse =
        // Distance units will be rotations
        Rotations.one().divide(kEncoderCPR);

    public static final int kShooterMotorPort = 4;
    public static final int kFeederMotorPort = 5;

    public static final Measure<Velocity<Angle>> kShooterFreeSpeed = RotationsPerSecond.of(5300);
    public static final Measure<Velocity<Angle>> kShooterTargetSpeed = RotationsPerSecond.of(4000);
    public static final Measure<Velocity<Angle>> kShooterSpeedTolerance = RotationsPerSecond.of(50);

    // These are not real PID gains, and will have to be tuned for your specific robot.
    public static final double kP = 1;
    public static final double kI = 0;
    public static final double kD = 0;

    // On a real robot the feedforward constants should be empirically determined; these are
    // reasonable guesses.
    public static final Measure<Voltage> kS = Volts.of(0.05);
    public static final Measure<Per<Voltage, Velocity<Angle>>> kV =
        // Should have value 12V at free speed...
        Volts.per(kShooterFreeSpeed.unit()).of(12 / kShooterFreeSpeed.magnitude());

    public static final double kFeederSpeed = 0.5;
  }

  public static final class AutoConstants {
    public static final Measure<Time> kAutoTimeout = Seconds.of(12);
    public static final Measure<Time> kAutoShootTime = Seconds.of(7);
  }

  public static final class OIConstants {
    public static final int kDriverControllerPort = 0;
  }
}
