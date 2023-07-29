// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.wpilibj.examples.elevatorprofiledpid;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecondPerSecond;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Per;
import edu.wpi.first.units.Velocity;
import edu.wpi.first.units.Voltage;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;

@SuppressWarnings("PMD.RedundantFieldInitializer")
public class Robot extends TimedRobot {
  private static final double kDt = 0.02;
  private static final Measure<Velocity<Distance>> kMaxVelocity = MetersPerSecond.of(1.75);
  private static final Measure<Velocity<Velocity<Distance>>> kMaxAcceleration =
      MetersPerSecondPerSecond.of(0.75);
  private static final double kP = 1.3;
  private static final double kI = 0.0;
  private static final double kD = 0.7;
  private static final Measure<Voltage> kS = Volts.of(1.1);
  private static final Measure<Voltage> kG = Volts.of(1.2);
  private static final Measure<Per<Voltage, Velocity<Distance>>> kV =
      Volts.of(1.3).per(MetersPerSecond);

  private final Joystick m_joystick = new Joystick(1);
  private final Encoder m_encoder = new Encoder(1, 2);
  private final MotorController m_motor = new PWMSparkMax(1);

  // Create a PID controller whose setpoint's change is subject to maximum
  // velocity and acceleration constraints.
  private final TrapezoidProfile.Constraints m_constraints =
      new TrapezoidProfile.Constraints(kMaxVelocity, kMaxAcceleration);
  private final ProfiledPIDController m_controller =
      new ProfiledPIDController(kP, kI, kD, m_constraints, kDt);
  private final ElevatorFeedforward m_feedforward = new ElevatorFeedforward(kS, kG, kV);

  private static final double kEncoderResolution = 360;
  private static final Measure<Distance> kDrumRadius = Inches.of(1.5);
  public static final Measure<Distance> kEncoderDistancePerPulse =
      kDrumRadius.times(2 * Math.PI).divide(kEncoderResolution);

  @Override
  public void robotInit() {
    m_encoder.setDistancePerPulse(kEncoderDistancePerPulse.in(Meters));
  }

  @Override
  public void teleopPeriodic() {
    if (m_joystick.getRawButtonPressed(2)) {
      m_controller.setGoal(5);
    } else if (m_joystick.getRawButtonPressed(3)) {
      m_controller.setGoal(0);
    }

    // Run controller and update motor output
    m_motor.setVoltage(
        m_controller.calculate(m_encoder.getDistance())
            + m_feedforward.calculate(m_controller.getSetpoint().velocity));
  }
}
