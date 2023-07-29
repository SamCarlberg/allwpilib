// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.math.controller;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecondPerSecond;
import static edu.wpi.first.units.Units.Volts;
import static edu.wpi.first.units.Units.VoltsPerMeterPerSecond;
import static edu.wpi.first.units.Units.VoltsPerMeterPerSecondSquared;

import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.MutableMeasure;
import edu.wpi.first.units.Per;
import edu.wpi.first.units.Velocity;
import edu.wpi.first.units.Voltage;

/**
 * A helper class that computes feedforward outputs for a simple elevator (modeled as a motor acting
 * against the force of gravity).
 */
public class ElevatorFeedforward {
  public final double ks;
  public final double kg;
  public final double kv;
  public final double ka;

  private final MutableMeasure<Voltage> m_output = MutableMeasure.zero(Volts);

  /**
   * Creates a new ElevatorFeedforward with the specified gains. Units of the gain values will
   * dictate units of the computed feedforward.
   *
   * @param ks The static gain.
   * @param kg The gravity gain.
   * @param kv The velocity gain.
   * @param ka The acceleration gain.
   */
  public ElevatorFeedforward(double ks, double kg, double kv, double ka) {
    this.ks = ks;
    this.kg = kg;
    this.kv = kv;
    this.ka = ka;
  }

  /**
   * Creates a new ElevatorFeedforward with the specified gains.
   *
   * @param ks The static gain.
   * @param kg The gravity gain.
   * @param kv The velocity gain.
   * @param ka The acceleration gain.
   */
  public ElevatorFeedforward(
      Measure<Voltage> ks,
      Measure<Voltage> kg,
      Measure<Per<Voltage, Velocity<Distance>>> kv,
      Measure<Per<Voltage, Velocity<Velocity<Distance>>>> ka) {
    this(
        ks.in(Volts),
        kg.in(Volts),
        kv.in(VoltsPerMeterPerSecond),
        ka.in(VoltsPerMeterPerSecondSquared));
  }

  /**
   * Creates a new ElevatorFeedforward with the specified gains. Acceleration gain is defaulted to
   * zero. Units of the gain values will dictate units of the computed feedforward.
   *
   * @param ks The static gain.
   * @param kg The gravity gain.
   * @param kv The velocity gain.
   */
  public ElevatorFeedforward(double ks, double kg, double kv) {
    this(ks, kg, kv, 0);
  }

  public ElevatorFeedforward(
      Measure<Voltage> ks,
      Measure<Voltage> kg,
      Measure<Per<Voltage, Velocity<Distance>>> kv) {
    this(ks, kg, kv, VoltsPerMeterPerSecondSquared.zero());
  }

  /**
   * Calculates the feedforward from the gains and setpoints.
   *
   * @param velocity The velocity setpoint.
   * @param acceleration The acceleration setpoint.
   * @return The computed feedforward.
   */
  public double calculate(double velocity, double acceleration) {
    return ks * Math.signum(velocity) + kg + kv * velocity + ka * acceleration;
  }

  /**
   * Calculates the feedforward from the gains and setpoints.
   *
   * @param velocity The velocity setpoint.
   * @param acceleration The acceleration setpoint.
   * @return The computed feedforward.
   */
  public Measure<Voltage> calculate(
      Measure<Velocity<Distance>> velocity,
      Measure<Velocity<Velocity<Distance>>> acceleration) {
    double rawVolts = calculate(
        velocity.in(MetersPerSecond),
        acceleration.in(MetersPerSecondPerSecond));
    m_output.mut_replace(rawVolts, Volts);
    return m_output;
  }

  /**
   * Calculates the feedforward from the gains and velocity setpoint (acceleration is assumed to be
   * zero).
   *
   * @param velocity The velocity setpoint.
   * @return The computed feedforward.
   */
  public double calculate(double velocity) {
    return calculate(velocity, 0);
  }

  /**
   * Calculates the feedforward from the gains and velocity setpoint (acceleration is assumed to be
   * zero).
   *
   * @param velocity The velocity setpoint.
   * @return The computed feedforward.
   */
  public Measure<Voltage> calculate(Measure<Velocity<Distance>> velocity) {
    return calculate(velocity, MetersPerSecondPerSecond.zero());
  }

  // Rearranging the main equation from the calculate() method yields the
  // formulas for the methods below:

  /**
   * Calculates the maximum achievable velocity given a maximum voltage supply and an acceleration.
   * Useful for ensuring that velocity and acceleration constraints for a trapezoidal profile are
   * simultaneously achievable - enter the acceleration constraint, and this will give you a
   * simultaneously-achievable velocity constraint.
   *
   * @param maxVoltage The maximum voltage that can be supplied to the elevator.
   * @param acceleration The acceleration of the elevator.
   * @return The maximum possible velocity at the given acceleration.
   */
  public double maxAchievableVelocity(double maxVoltage, double acceleration) {
    // Assume max velocity is positive
    return (maxVoltage - ks - kg - acceleration * ka) / kv;
  }

  /**
   * Calculates the minimum achievable velocity given a maximum voltage supply and an acceleration.
   * Useful for ensuring that velocity and acceleration constraints for a trapezoidal profile are
   * simultaneously achievable - enter the acceleration constraint, and this will give you a
   * simultaneously-achievable velocity constraint.
   *
   * @param maxVoltage The maximum voltage that can be supplied to the elevator.
   * @param acceleration The acceleration of the elevator.
   * @return The minimum possible velocity at the given acceleration.
   */
  public double minAchievableVelocity(double maxVoltage, double acceleration) {
    // Assume min velocity is negative, ks flips sign
    return (-maxVoltage + ks - kg - acceleration * ka) / kv;
  }

  /**
   * Calculates the maximum achievable acceleration given a maximum voltage supply and a velocity.
   * Useful for ensuring that velocity and acceleration constraints for a trapezoidal profile are
   * simultaneously achievable - enter the velocity constraint, and this will give you a
   * simultaneously-achievable acceleration constraint.
   *
   * @param maxVoltage The maximum voltage that can be supplied to the elevator.
   * @param velocity The velocity of the elevator.
   * @return The maximum possible acceleration at the given velocity.
   */
  public double maxAchievableAcceleration(double maxVoltage, double velocity) {
    return (maxVoltage - ks * Math.signum(velocity) - kg - velocity * kv) / ka;
  }

  /**
   * Calculates the minimum achievable acceleration given a maximum voltage supply and a velocity.
   * Useful for ensuring that velocity and acceleration constraints for a trapezoidal profile are
   * simultaneously achievable - enter the velocity constraint, and this will give you a
   * simultaneously-achievable acceleration constraint.
   *
   * @param maxVoltage The maximum voltage that can be supplied to the elevator.
   * @param velocity The velocity of the elevator.
   * @return The minimum possible acceleration at the given velocity.
   */
  public double minAchievableAcceleration(double maxVoltage, double velocity) {
    return maxAchievableAcceleration(-maxVoltage, velocity);
  }
}
