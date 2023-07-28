// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.wpilibj.examples.frisbeebot.subsystems;

import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.units.Angle;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.examples.frisbeebot.Constants.ShooterConstants;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj2.command.PIDSubsystem;

public class ShooterSubsystem extends PIDSubsystem {
  private final PWMSparkMax m_shooterMotor = new PWMSparkMax(ShooterConstants.kShooterMotorPort);
  private final PWMSparkMax m_feederMotor = new PWMSparkMax(ShooterConstants.kFeederMotorPort);
  private final Encoder m_shooterEncoder =
      new Encoder(
          ShooterConstants.kEncoderPorts[0],
          ShooterConstants.kEncoderPorts[1],
          ShooterConstants.kEncoderReversed);
  private final SimpleMotorFeedforward<Angle> m_shooterFeedforward =
      new SimpleMotorFeedforward<>(
          ShooterConstants.kS, ShooterConstants.kV);

  /** The shooter subsystem for the robot. */
  public ShooterSubsystem() {
    super(new PIDController(ShooterConstants.kP, ShooterConstants.kI, ShooterConstants.kD));
    getController().setTolerance(ShooterConstants.kShooterSpeedTolerance.in(RotationsPerSecond));
    m_shooterEncoder.setDistancePerPulse(ShooterConstants.kEncoderDistancePerPulse.in(Rotations));
    setSetpoint(ShooterConstants.kShooterTargetSpeed.in(RotationsPerSecond));
  }

  @Override
  public void useOutput(double output, double setpoint) {
    m_shooterMotor.setVoltage(output + m_shooterFeedforward.calculate(setpoint));
  }

  @Override
  public double getMeasurement() {
    return m_shooterEncoder.getRate();
  }

  public boolean atSetpoint() {
    return m_controller.atSetpoint();
  }

  public void runFeeder() {
    m_feederMotor.set(ShooterConstants.kFeederSpeed);
  }

  public void stopFeeder() {
    m_feederMotor.set(0);
  }
}
