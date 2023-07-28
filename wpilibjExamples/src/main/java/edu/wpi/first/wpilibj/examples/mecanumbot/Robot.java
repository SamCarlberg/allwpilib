// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.wpilibj.examples.mecanumbot;

import static edu.wpi.first.units.Units.FeetPerSecond;
import static edu.wpi.first.units.Units.Milliseconds;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Seconds;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.MutableMeasure;
import edu.wpi.first.units.Time;
import edu.wpi.first.units.Velocity;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;

public class Robot extends TimedRobot {
  private static final Measure<Time> kPeriod = Milliseconds.of(20);

  private final XboxController m_controller = new XboxController(0);
  private final Drivetrain m_mecanum = new Drivetrain();

  private static final Measure<Time> kSlewPeriod = Seconds.of(3);

  // Slew rate limiters to make joystick inputs more gentle; 1/3 sec from 0 to 1.
  private final SlewRateLimiter m_xspeedLimiter = new SlewRateLimiter(kSlewPeriod.in(Seconds));
  private final SlewRateLimiter m_yspeedLimiter = new SlewRateLimiter(kSlewPeriod.in(Seconds));
  private final SlewRateLimiter m_rotLimiter = new SlewRateLimiter(kSlewPeriod.in(Seconds));

  // Track x, y, and angular speed in mutable measures to avoid creating new objects every time
  // driveWithJoystick is called. This helps reduce garbage collector activity and avoid loop
  // time overruns.
  private final MutableMeasure<Velocity<Distance>> m_xSpeed = MutableMeasure.zero(FeetPerSecond);
  private final MutableMeasure<Velocity<Distance>> m_ySpeed = MutableMeasure.zero(FeetPerSecond);
  private final MutableMeasure<Velocity<Angle>> m_rotSpeed =
      MutableMeasure.zero(RotationsPerSecond);

  @Override
  public void autonomousPeriodic() {
    driveWithJoystick(false);
    m_mecanum.updateOdometry();
  }

  @Override
  public void teleopPeriodic() {
    driveWithJoystick(true);
  }

  private void driveWithJoystick(boolean fieldRelative) {
    // Get the x speed. We are inverting this because Xbox controllers return
    // negative values when we push forward.
    m_xSpeed.mut_replace(
        -m_xspeedLimiter.calculate(m_controller.getLeftY()) * Drivetrain.kMaxSpeed.magnitude(),
        Drivetrain.kMaxSpeed.unit());

    // Get the y speed or sideways/strafe speed. We are inverting this because
    // we want a positive value when we pull to the left. Xbox controllers
    // return positive values when you pull to the right by default.
    m_xSpeed.mut_replace(
        -m_yspeedLimiter.calculate(m_controller.getLeftX()) * Drivetrain.kMaxSpeed.magnitude(),
        Drivetrain.kMaxSpeed.unit());

    // Get the rate of angular rotation. We are inverting this because we want a
    // positive value when we pull to the left (remember, CCW is positive in
    // mathematics). Xbox controllers return positive values when you pull to
    // the right by default.
    m_rotSpeed.mut_replace(
        -m_rotLimiter.calculate(m_controller.getRightX()) * Drivetrain.kMaxAngularSpeed.magnitude(),
        Drivetrain.kMaxAngularSpeed.unit());

    m_mecanum.drive(m_xSpeed, m_ySpeed, m_rotSpeed, fieldRelative, kPeriod);
  }
}
