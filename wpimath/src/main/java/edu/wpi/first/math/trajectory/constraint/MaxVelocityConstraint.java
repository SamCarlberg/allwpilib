// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.math.trajectory.constraint;

import static edu.wpi.first.units.Units.MetersPerSecond;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Velocity;

/**
 * Represents a constraint that enforces a max velocity. This can be composed with the {@link
 * EllipticalRegionConstraint} or {@link RectangularRegionConstraint} to enforce a max velocity in a
 * region.
 */
public class MaxVelocityConstraint implements TrajectoryConstraint {
  private final double m_maxVelocity;

  /**
   * Constructs a new MaxVelocityConstraint.
   *
   * @param maxVelocityMetersPerSecond The max velocity.
   */
  public MaxVelocityConstraint(double maxVelocityMetersPerSecond) {
    m_maxVelocity = maxVelocityMetersPerSecond;
  }

  /**
   * Constructs a new MaxVelocityConstraint.
   *
   * @param maxVelocity The max velocity.
   */
  public MaxVelocityConstraint(Measure<Velocity<Distance>> maxVelocity) {
    this(maxVelocity.in(MetersPerSecond));
  }

  @Override
  public double getMaxVelocityMetersPerSecond(
      Pose2d poseMeters, double curvatureRadPerMeter, double velocityMetersPerSecond) {
    return m_maxVelocity;
  }

  @Override
  public TrajectoryConstraint.MinMax getMinMaxAccelerationMetersPerSecondSq(
      Pose2d poseMeters, double curvatureRadPerMeter, double velocityMetersPerSecond) {
    return new MinMax();
  }
}
