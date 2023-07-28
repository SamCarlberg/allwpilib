// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.wpilibj.examples.swervecontrollercommand;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecondPerSecond;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Per;
import edu.wpi.first.units.Velocity;
import edu.wpi.first.units.Voltage;
import edu.wpi.first.wpilibj.TimedRobot;

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
    public static final int kFrontLeftDriveMotorPort = 0;
    public static final int kRearLeftDriveMotorPort = 2;
    public static final int kFrontRightDriveMotorPort = 4;
    public static final int kRearRightDriveMotorPort = 6;

    public static final int kFrontLeftTurningMotorPort = 1;
    public static final int kRearLeftTurningMotorPort = 3;
    public static final int kFrontRightTurningMotorPort = 5;
    public static final int kRearRightTurningMotorPort = 7;

    public static final int[] kFrontLeftTurningEncoderPorts = new int[] {0, 1};
    public static final int[] kRearLeftTurningEncoderPorts = new int[] {2, 3};
    public static final int[] kFrontRightTurningEncoderPorts = new int[] {4, 5};
    public static final int[] kRearRightTurningEncoderPorts = new int[] {6, 7};

    public static final boolean kFrontLeftTurningEncoderReversed = false;
    public static final boolean kRearLeftTurningEncoderReversed = true;
    public static final boolean kFrontRightTurningEncoderReversed = false;
    public static final boolean kRearRightTurningEncoderReversed = true;

    public static final int[] kFrontLeftDriveEncoderPorts = new int[] {8, 9};
    public static final int[] kRearLeftDriveEncoderPorts = new int[] {10, 11};
    public static final int[] kFrontRightDriveEncoderPorts = new int[] {12, 13};
    public static final int[] kRearRightDriveEncoderPorts = new int[] {14, 15};

    public static final boolean kFrontLeftDriveEncoderReversed = false;
    public static final boolean kRearLeftDriveEncoderReversed = true;
    public static final boolean kFrontRightDriveEncoderReversed = false;
    public static final boolean kRearRightDriveEncoderReversed = true;

    // If you call DriveSubsystem.drive() with a different period make sure to update this.
    public static final double kDrivePeriod = TimedRobot.kDefaultPeriod;

    public static final Measure<Distance> kHalfTrackWidth = Meters.of(0.25);
    // Distance between centers of right and left wheels on robot
    public static final Measure<Distance> kHalfWheelBase = Meters.of(0.35);
    // Distance between front and back wheels on robot
    public static final SwerveDriveKinematics kDriveKinematics =
        new SwerveDriveKinematics(
            new Translation2d(kHalfWheelBase.in(Meters), kHalfTrackWidth.in(Meters)),
            new Translation2d(kHalfWheelBase.in(Meters), -kHalfTrackWidth.in(Meters)),
            new Translation2d(-kHalfWheelBase.in(Meters), kHalfTrackWidth.in(Meters)),
            new Translation2d(-kHalfWheelBase.in(Meters), -kHalfTrackWidth.in(Meters)));
    public static final boolean kGyroReversed = false;

    // These are example values only - DO NOT USE THESE FOR YOUR OWN ROBOT!
    // These characterization values MUST be determined either experimentally or theoretically
    // for *your* robot's drive.
    // The SysId tool provides a convenient method for obtaining these values for your robot.
    public static final Measure<Voltage> kS = Volts.of(1);
    public static final Measure<Per<Voltage, Velocity<Distance>>> kV =
        Volts.of(0.8).per(MetersPerSecond);
    public static final Measure<Per<Voltage, Velocity<Velocity<Distance>>>> kA =
        Volts.of(0.15).per(MetersPerSecondPerSecond);

    public static final Measure<Velocity<Distance>> kMaxSpeed = MetersPerSecond.of(3);
  }

  public static final class ModuleConstants {
    public static final Measure<Velocity<Angle>> kMaxModuleAngularSpeed =
        RotationsPerSecond.of(1);
    public static final Measure<Velocity<Velocity<Angle>>> kMaxModuleAngularAcceleration =
        RotationsPerSecond.per(Second).of(1);

    public static final int kEncoderCPR = 1024;
    public static final Measure<Distance> kWheelDiameter = Inches.of(6);
    public static final Measure<Distance> kDriveEncoderDistancePerPulse =
        // Assumes the encoders are directly mounted on the wheel shafts
        kWheelDiameter.times(Math.PI).divide(kEncoderCPR);

    public static final Measure<Angle> kTurningEncoderDistancePerPulse =
        // Assumes the encoders are on a 1:1 reduction with the module shaft.
        Rotations.one().divide(kEncoderCPR);

    public static final double kPModuleTurningController = 1;

    public static final double kPModuleDriveController = 1;
  }

  public static final class OIConstants {
    public static final int kDriverControllerPort = 0;
  }

  public static final class AutoConstants {
    public static final Measure<Velocity<Distance>> kMaxSpeed = MetersPerSecond.of(3);
    public static final Measure<Velocity<Velocity<Distance>>> kMaxAcceleration =
        MetersPerSecondPerSecond.of(3);
    public static final Measure<Velocity<Angle>> kMaxAngularSpeed = RotationsPerSecond.of(0.5);
    public static final Measure<Velocity<Velocity<Angle>>> kMaxAngularAcceleration =
        RotationsPerSecond.per(Second).of(0.5);

    public static final double kPXController = 1;
    public static final double kPYController = 1;
    public static final double kPThetaController = 1;

    // Constraint for the motion profiled robot angle controller
    public static final TrapezoidProfile.Constraints kThetaControllerConstraints =
        new TrapezoidProfile.Constraints(kMaxAngularSpeed, kMaxAngularAcceleration);
  }
}
