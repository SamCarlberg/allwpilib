// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.wpilibj.examples.armbotoffboard;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Volts;

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

    public static final int kEncoderCPR = 1024;
    public static final Measure<Distance> kWheelDiameter = Inches.of(6);
    public static final Measure<Distance> kEncoderDistancePerPulse =
        // Assumes the encoders are directly mounted on the wheel shafts
        kWheelDiameter.times(Math.PI).divide(kEncoderCPR);
  }

  public static final class ArmConstants {
    public static final int kMotorPort = 4;

    public static final double kP = 1;

    // These are fake gains; in actuality these must be determined individually for each robot
    public static final Measure<Voltage> kS = Volts.of(1);
    public static final Measure<Voltage> kG = Volts.of(1);
    public static final Measure<Per<Voltage, Velocity<Angle>>> kV =
        Volts.of(0.5).per(RadiansPerSecond);
    public static final Measure<Per<Voltage, Velocity<Velocity<Angle>>>> kA =
        Volts.of(0.1).per(RadiansPerSecond.per(Second));

    public static final Measure<Velocity<Angle>> kMaxVelocity = RadiansPerSecond.of(3);
    public static final Measure<Velocity<Velocity<Angle>>> kMaxAcceleration =
        RadiansPerSecond.per(Second).of(10);

    // The offset of the arm from the horizontal in its neutral position,
    // measured from the horizontal
    public static final Measure<Angle> kArmOffset = Radians.of(0.5);
  }

  public static final class OIConstants {
    public static final int kDriverControllerPort = 0;
  }
}
