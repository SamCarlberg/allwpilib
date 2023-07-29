// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.wpilibj.examples.statespaceelevator;

import static edu.wpi.first.units.Units.Feet;
import static edu.wpi.first.units.Units.FeetPerSecond;
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.InchesPerSecond;
import static edu.wpi.first.units.Units.Kilograms;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.Milliseconds;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.math.Nat;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.controller.LinearQuadraticRegulator;
import edu.wpi.first.math.estimator.KalmanFilter;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.math.system.LinearSystem;
import edu.wpi.first.math.system.LinearSystemLoop;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Mass;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Time;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;

/**
 * This is a sample program to demonstrate how to use a state-space controller to control an
 * elevator.
 */
public class Robot extends TimedRobot {
  private static final int kMotorPort = 0;
  private static final int kEncoderAChannel = 0;
  private static final int kEncoderBChannel = 1;
  private static final int kJoystickPort = 0;
  private static final Measure<Distance> kHighGoalPosition = Feet.of(3);
  private static final Measure<Distance> kLowGoalPosition = Feet.of(0);

  private static final Measure<Mass> kCarriageMass = Kilograms.of(4.5);

  private static final Measure<Distance> kDrumDiameter = Inches.of(3);

  // Reduction between motors and encoder, as output over input. If the elevator spins slower than
  // the motors, this number should be greater than one.
  private static final double kElevatorGearing = 6.0;

  private final TrapezoidProfile m_profile =
      new TrapezoidProfile(
          new TrapezoidProfile.Constraints(
              FeetPerSecond.of(3),
              FeetPerSecond.per(Second).of(6))); // Max elevator speed and acceleration.
  private TrapezoidProfile.State m_lastProfiledReference = new TrapezoidProfile.State();

  // Nominal time between loops. 0.020 for TimedRobot, but can be lower if using notifiers.
  private static final Measure<Time> kUpdatePeriod = Milliseconds.of(20);

  /* The plant holds a state-space model of our elevator. This system has the following properties:

  States: [position, velocity], in meters and meters per second.
  Inputs (what we can "put in"): [voltage], in volts.
  Outputs (what we can measure): [position], in meters.

  This elevator is driven by two NEO motors.
   */
  private final LinearSystem<N2, N1, N1> m_elevatorPlant =
      LinearSystemId.createElevatorSystem(
          DCMotor.getNEO(2), kCarriageMass, kDrumDiameter, kElevatorGearing);

  // The observer fuses our encoder data and voltage inputs to reject noise.
  private final KalmanFilter<N2, N1, N1> m_observer =
      new KalmanFilter<>(
          Nat.N2(),
          Nat.N1(),
          m_elevatorPlant,
          VecBuilder.fill(
              Meters.convertFrom(2, Inches),
              MetersPerSecond.convertFrom(40, InchesPerSecond)), // How accurate we
          // think our model is, in meters and meters/second.
          VecBuilder.fill(0.001), // How accurate we think our encoder position
          // data is. In this case we very highly trust our encoder position reading.
          kUpdatePeriod.in(Seconds));

  // A LQR uses feedback to create voltage commands.
  private final LinearQuadraticRegulator<N2, N1, N1> m_controller =
      new LinearQuadraticRegulator<>(
          m_elevatorPlant,
          VecBuilder.fill(Meters.convertFrom(1, Inches), Meters.convertFrom(10.0, Inches)),
          // qelms. Position and velocity error tolerances, in meters and meters per second.
          // Decrease this to more heavily penalize state excursion, or make the controller behave
          // more aggressively. In this example we weight position much more highly than velocity,
          // but this can be tuned to balance the two.
          VecBuilder.fill(12.0), // relms. Control effort (voltage) tolerance. Decrease this to more
          // heavily penalize control effort, or make the controller less aggressive. 12 is a good
          // starting point because that is the (approximate) maximum voltage of a battery.
          kUpdatePeriod.in(Seconds));

  // The state-space loop combines a controller, observer, feedforward and plant for easy control.
  private final LinearSystemLoop<N2, N1, N1> m_loop =
      new LinearSystemLoop<>(
          m_elevatorPlant, m_controller, m_observer, Volts.of(12), kUpdatePeriod);

  // An encoder set up to measure elevator height in meters.
  private final Encoder m_encoder = new Encoder(kEncoderAChannel, kEncoderBChannel);

  private final MotorController m_motor = new PWMSparkMax(kMotorPort);

  // A joystick to read the trigger from.
  private final Joystick m_joystick = new Joystick(kJoystickPort);

  @Override
  public void robotInit() {
    // Circumference = pi * d, so distance per click = pi * d / counts
    // Use meters for compatibility with WPILib's state-space models, which expect SI units
    m_encoder.setDistancePerPulse(kDrumDiameter.times(Math.PI).divide(4096).in(Meters));
  }

  @Override
  public void teleopInit() {
    // Reset our loop to make sure it's in a known state.
    m_loop.reset(VecBuilder.fill(m_encoder.getDistance(), m_encoder.getRate()));

    // Reset our last reference to the current state.
    m_lastProfiledReference =
        new TrapezoidProfile.State(m_encoder.getDistance(), m_encoder.getRate());
  }

  @Override
  public void teleopPeriodic() {
    // Sets the target position of our arm. This is similar to setting the setpoint of a
    // PID controller.
    TrapezoidProfile.State goal;
    if (m_joystick.getTrigger()) {
      // the trigger is pressed, so we go to the high goal.
      goal = new TrapezoidProfile.State(kHighGoalPosition, MetersPerSecond.zero());
    } else {
      // Otherwise, we go to the low goal
      goal = new TrapezoidProfile.State(kLowGoalPosition, MetersPerSecond.zero());
    }
    // Step our TrapezoidalProfile forward 20ms and set it as our next reference
    m_lastProfiledReference =
        m_profile.calculate(kUpdatePeriod.in(Seconds), goal, m_lastProfiledReference);
    m_loop.setNextR(m_lastProfiledReference.position, m_lastProfiledReference.velocity);

    // Correct our Kalman filter's state vector estimate with encoder data.
    m_loop.correct(VecBuilder.fill(m_encoder.getDistance()));

    // Update our LQR to generate new voltage commands and use the voltages to predict the next
    // state with out Kalman filter.
    m_loop.predict(kUpdatePeriod.in(Seconds));

    // Send the new calculated voltage to the motors.
    // voltage = duty cycle * battery voltage, so
    // duty cycle = voltage / battery voltage
    double nextVoltage = m_loop.getU(0);
    m_motor.setVoltage(nextVoltage);
  }
}
