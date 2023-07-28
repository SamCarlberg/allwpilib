// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.wpilibj.examples.statespacedifferentialdrivesimulation;

import static edu.wpi.first.units.Units.Feet;
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecondPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Volts;
import static edu.wpi.first.units.Units.VoltsPerMeterPerSecond;
import static edu.wpi.first.units.Units.VoltsPerMeterPerSecondSquared;
import static edu.wpi.first.units.Units.VoltsPerRadianPerSecond;
import static edu.wpi.first.units.Units.VoltsPerRadianPerSecondSquared;

import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.math.system.LinearSystem;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.units.Angle;
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

    public static final int[] kLeftEncoderPorts = new int[] {0, 1};
    public static final int[] kRightEncoderPorts = new int[] {2, 3};
    public static final boolean kLeftEncoderReversed = false;
    public static final boolean kRightEncoderReversed = true;

    public static final Measure<Distance> kTrackwidth = Feet.of(2).plus(Inches.of(3));
    public static final DifferentialDriveKinematics kDriveKinematics =
        new DifferentialDriveKinematics(kTrackwidth.in(Meters));

    public static final int kEncoderCPR = 1024;
    public static final Measure<Distance> kWheelDiameter = Inches.of(6);
    public static final Measure<Distance> kWheelRadius = kWheelDiameter.divide(2);
    public static final Measure<Distance> kEncoderDistancePerPulse =
        // Assumes the encoders are directly mounted on the wheel shafts
        kWheelDiameter.times(Math.PI).divide(kEncoderCPR);

    public static final boolean kGyroReversed = true;

    // These are example values only - DO NOT USE THESE FOR YOUR OWN ROBOT!
    // These characterization values MUST be determined either experimentally or theoretically
    // for *your* robot's drive.
    // The Robot Characterization Toolsuite provides a convenient tool for obtaining these
    // values for your robot.
    public static final Measure<Voltage> kS_linear = Volts.of(0.22);
    public static final Measure<Per<Voltage, Velocity<Distance>>> kV_linear =
        Volts.of(1.98).per(MetersPerSecond);
    public static final Measure<Per<Voltage, Velocity<Velocity<Distance>>>> kA_linear =
        Volts.of(0.2).per(MetersPerSecondPerSecond);

    // These are example values only - DO NOT USE THESE FOR YOUR OWN ROBOT!
    // These characterization values MUST be determined either experimentally or theoretically
    // for *your* robot's drive.
    // These two values are "angular" kV and kA
    public static final Measure<Per<Voltage, Velocity<Angle>>> kV_angular =
        Volts.of(1.5).per(RadiansPerSecond);
    public static final Measure<Per<Voltage, Velocity<Velocity<Angle>>>> kA_angular =
        Volts.of(0.3).per(RadiansPerSecond.per(Second));

    public static final LinearSystem<N2, N2, N2> kDrivetrainPlant =
        LinearSystemId.identifyDrivetrainSystem(
            kV_linear.in(VoltsPerMeterPerSecond),
            kA_linear.in(VoltsPerMeterPerSecondSquared),
            kV_angular.in(VoltsPerRadianPerSecond),
            kA_angular.in(VoltsPerRadianPerSecondSquared));

    // Example values only -- use what's on your physical robot!
    public static final DCMotor kDriveGearbox = DCMotor.getCIM(2);
    public static final double kDriveGearing = 8;

    // Example value only - as above, this must be tuned for your drive!
    public static final double kPDriveVel = 8.5;
  }

  public static final class OIConstants {
    public static final int kDriverControllerPort = 0;
  }

  public static final class AutoConstants {
    public static final Measure<Velocity<Distance>> kMaxSpeed = MetersPerSecond.of(3);
    public static final Measure<Velocity<Velocity<Distance>>> kMaxAcceleration =
        MetersPerSecondPerSecond.of(3);

    // Reasonable baseline values for a RAMSETE follower in units of meters and seconds
    public static final double kRamseteB = 2;
    public static final double kRamseteZeta = 0.7;
  }
}
