// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.wpilibj.examples.mecanumcontrollercommand;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecondPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Second;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.MecanumDriveKinematics;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Velocity;

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
    public static final int kFrontLeftMotorPort = 0;
    public static final int kRearLeftMotorPort = 1;
    public static final int kFrontRightMotorPort = 2;
    public static final int kRearRightMotorPort = 3;

    public static final int[] kFrontLeftEncoderPorts = new int[] {0, 1};
    public static final int[] kRearLeftEncoderPorts = new int[] {2, 3};
    public static final int[] kFrontRightEncoderPorts = new int[] {4, 5};
    public static final int[] kRearRightEncoderPorts = new int[] {6, 7};

    public static final boolean kFrontLeftEncoderReversed = false;
    public static final boolean kRearLeftEncoderReversed = true;
    public static final boolean kFrontRightEncoderReversed = false;
    public static final boolean kRearRightEncoderReversed = true;

    // Distance between centers of right and left wheels on robot and the center of the robot.
    public static final Measure<Distance> kHalfTrackWidth = Meters.of(0.25);
    // Distance between centers of front and back wheels on robot and the center of the robot.
    public static final Measure<Distance> kHalfWheelBase = Meters.of(0.35);

    public static final MecanumDriveKinematics kDriveKinematics =
        new MecanumDriveKinematics(
            new Translation2d(kHalfWheelBase.in(Meters), kHalfTrackWidth.in(Meters)),
            new Translation2d(kHalfWheelBase.in(Meters), -kHalfTrackWidth.in(Meters)),
            new Translation2d(-kHalfWheelBase.in(Meters), kHalfTrackWidth.in(Meters)),
            new Translation2d(-kHalfWheelBase.in(Meters), -kHalfTrackWidth.in(Meters)));

    public static final int kEncoderCPR = 1024;
    public static final Measure<Distance> kWheelDiameter = Inches.of(6);
    public static final Measure<Distance> kEncoderDistancePerPulse =
        // Assumes the encoders are directly mounted on the wheel shafts
        kWheelDiameter.times(Math.PI).divide(kEncoderCPR);

    // These are example values only - DO NOT USE THESE FOR YOUR OWN ROBOT!
    // These characterization values MUST be determined either experimentally or theoretically
    // for *your* robot's drive.
    // The SysId tool provides a convenient method for obtaining these values for your robot.
    public static final SimpleMotorFeedforward<Distance> kFeedforward =
        new SimpleMotorFeedforward<>(1, 0.8, 0.15);

    // Example value only - as above, this must be tuned for your drive!
    public static final double kPFrontLeftVel = 0.5;
    public static final double kPRearLeftVel = 0.5;
    public static final double kPFrontRightVel = 0.5;
    public static final double kPRearRightVel = 0.5;
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

    public static final double kPXController = 0.5;
    public static final double kPYController = 0.5;
    public static final double kPThetaController = 0.5;

    // Constraint for the motion profiled robot angle controller
    public static final TrapezoidProfile.Constraints kThetaControllerConstraints =
        new TrapezoidProfile.Constraints(
            kMaxAngularSpeed.in(RadiansPerSecond),
            kMaxAngularAcceleration.in(RadiansPerSecond.per(Second)));
  }
}
