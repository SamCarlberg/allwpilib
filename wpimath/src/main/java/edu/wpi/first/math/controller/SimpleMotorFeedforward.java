// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.math.controller;

import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.MutableMeasure;
import edu.wpi.first.units.Per;
import edu.wpi.first.units.Time;
import edu.wpi.first.units.Unit;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.Velocity;
import edu.wpi.first.units.Voltage;

/**
 * A helper class that computes feedforward outputs for a simple permanent-magnet DC motor.
 *
 * @param <I> the type of the input units that velocity and acceleration are in.
 */
public class SimpleMotorFeedforward<I extends Unit<I>> {
  public final double ks;
  public final double kv;
  public final double ka;

  private final MutableMeasure<Voltage> m_output = MutableMeasure.zero(Volts);

  /**
   * Creates a new SimpleMotorFeedforward with the specified gains. Units of the gain values will
   * dictate units of the computed feedforward.
   *
   * @param ks The static gain.
   * @param kv The velocity gain.
   * @param ka The acceleration gain.
   */
  public SimpleMotorFeedforward(double ks, double kv, double ka) {
    this.ks = ks;
    this.kv = kv;
    this.ka = ka;
  }

  /**
   * Creates a new SimpleMotorFeedforward with the specified gains.
   *
   * @param ks The static gain.
   * @param kv The velocity gain.
   * @param ka The acceleration gain.
   */
  public SimpleMotorFeedforward(
      Measure<Voltage> ks,
      Measure<Per<Voltage, Velocity<I>>> kv,
      Measure<Per<Voltage, Velocity<Velocity<I>>>> ka) {
    this(ks.in(Volts), kv.baseUnitMagnitude(), ka.baseUnitMagnitude());
  }

  /**
   * Creates a new SimpleMotorFeedforward with the specified gains. Acceleration gain is defaulted
   * to zero. Units of the gain values will dictate units of the computed feedforward.
   *
   * @param ks The static gain.
   * @param kv The velocity gain.
   */
  public SimpleMotorFeedforward(double ks, double kv) {
    this(ks, kv, 0);
  }

  /**
   * Creates a new SimpleMotorFeedforward with the specified gains. Acceleration gain is defaulted
   * to zero.
   *
   * @param ks The static gain.
   * @param kv The velocity gain.
   */
  public SimpleMotorFeedforward(
      Measure<Voltage> ks,
      Measure<Per<Voltage, Velocity<I>>> kv) {
    this(ks, kv, Units.<Per<Voltage, Velocity<Velocity<I>>>>anonymous().zero());
  }

  /**
   * Calculates the feedforward from the gains and setpoints.
   *
   * @param velocity The velocity setpoint.
   * @param acceleration The acceleration setpoint.
   * @return The computed feedforward.
   */
  public double calculate(double velocity, double acceleration) {
    return ks * Math.signum(velocity) + kv * velocity + ka * acceleration;
  }

  /**
   * Calculates the feedforward from the gains and setpoints.
   *
   * @param velocity The velocity setpoint.
   * @param acceleration The acceleration setpoint.
   * @return The computed feedforward.
   */
  public Measure<Voltage> calculate(
      Measure<Velocity<I>> velocity,
      Measure<Velocity<Velocity<I>>> acceleration) {
    m_output.mut_replace(
        calculate(velocity.baseUnitMagnitude(), acceleration.baseUnitMagnitude()),
        Volts
    );

    return m_output;
  }

  /**
   * Calculates the feedforward from the gains and setpoints.
   *
   * @param currentVelocity The current velocity setpoint.
   * @param nextVelocity The next velocity setpoint.
   * @param dtSeconds Time between velocity setpoints in seconds.
   * @return The computed feedforward.
   */
  public double calculate(double currentVelocity, double nextVelocity, double dtSeconds) {
    var plant = LinearSystemId.identifyVelocitySystem(this.kv, this.ka);
    var feedforward = new LinearPlantInversionFeedforward<>(plant, dtSeconds);

    var r = Matrix.mat(Nat.N1(), Nat.N1()).fill(currentVelocity);
    var nextR = Matrix.mat(Nat.N1(), Nat.N1()).fill(nextVelocity);

    return ks * Math.signum(currentVelocity) + feedforward.calculate(r, nextR).get(0, 0);
  }

  /**
   * Calculates the feedforward from the gains and setpoints.
   *
   * @param currentVelocity The current velocity setpoint.
   * @param nextVelocity The next velocity setpoint.
   * @param dt Time between velocity setpoints.
   * @return The computed feedforward.
   */
  public Measure<Voltage> calculate(
      Measure<Velocity<I>> currentVelocity,
      Measure<Velocity<I>> nextVelocity,
      Measure<Time> dt) {
    m_output.mut_replace(
        calculate(
            currentVelocity.baseUnitMagnitude(),
            nextVelocity.baseUnitMagnitude(),
            dt.in(Seconds)
        ),
        Volts
    );

    return m_output;
  }

  // Rearranging the main equation from the calculate() method yields the
  // formulas for the methods below:

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
  public Measure<Voltage> calculate(Measure<Velocity<I>> velocity) {
    return calculate(velocity, Units.<Velocity<Velocity<I>>>anonymous().zero());
  }

  /**
   * Calculates the maximum achievable velocity given a maximum voltage supply and an acceleration.
   * Useful for ensuring that velocity and acceleration constraints for a trapezoidal profile are
   * simultaneously achievable - enter the acceleration constraint, and this will give you a
   * simultaneously-achievable velocity constraint.
   *
   * @param maxVoltage The maximum voltage that can be supplied to the motor.
   * @param acceleration The acceleration of the motor.
   * @return The maximum possible velocity at the given acceleration.
   */
  public double maxAchievableVelocity(double maxVoltage, double acceleration) {
    // Assume max velocity is positive
    return (maxVoltage - ks - acceleration * ka) / kv;
  }

  /**
   * Calculates the minimum achievable velocity given a maximum voltage supply and an acceleration.
   * Useful for ensuring that velocity and acceleration constraints for a trapezoidal profile are
   * simultaneously achievable - enter the acceleration constraint, and this will give you a
   * simultaneously-achievable velocity constraint.
   *
   * @param maxVoltage The maximum voltage that can be supplied to the motor.
   * @param acceleration The acceleration of the motor.
   * @return The minimum possible velocity at the given acceleration.
   */
  public double minAchievableVelocity(double maxVoltage, double acceleration) {
    // Assume min velocity is negative, ks flips sign
    return (-maxVoltage + ks - acceleration * ka) / kv;
  }

  /**
   * Calculates the maximum achievable acceleration given a maximum voltage supply and a velocity.
   * Useful for ensuring that velocity and acceleration constraints for a trapezoidal profile are
   * simultaneously achievable - enter the velocity constraint, and this will give you a
   * simultaneously-achievable acceleration constraint.
   *
   * @param maxVoltage The maximum voltage that can be supplied to the motor.
   * @param velocity The velocity of the motor.
   * @return The maximum possible acceleration at the given velocity.
   */
  public double maxAchievableAcceleration(double maxVoltage, double velocity) {
    return (maxVoltage - ks * Math.signum(velocity) - velocity * kv) / ka;
  }

  /**
   * Calculates the minimum achievable acceleration given a maximum voltage supply and a velocity.
   * Useful for ensuring that velocity and acceleration constraints for a trapezoidal profile are
   * simultaneously achievable - enter the velocity constraint, and this will give you a
   * simultaneously-achievable acceleration constraint.
   *
   * @param maxVoltage The maximum voltage that can be supplied to the motor.
   * @param velocity The velocity of the motor.
   * @return The minimum possible acceleration at the given velocity.
   */
  public double minAchievableAcceleration(double maxVoltage, double velocity) {
    return maxAchievableAcceleration(-maxVoltage, velocity);
  }
}
