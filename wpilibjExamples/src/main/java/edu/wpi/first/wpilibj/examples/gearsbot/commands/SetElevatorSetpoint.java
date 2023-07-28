// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.wpilibj.examples.gearsbot.commands;

import static edu.wpi.first.units.Units.Meters;

import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Measure;
import edu.wpi.first.wpilibj.examples.gearsbot.subsystems.Elevator;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Move the elevator to a given location. This command finishes when it is within the tolerance, but
 * leaves the PID loop running to maintain the position. Other commands using the elevator should
 * make sure they disable PID!
 */
public class SetElevatorSetpoint extends Command {
  private final Elevator m_elevator;
  private final Measure<Distance> m_setpoint;

  /**
   * Create a new SetElevatorSetpoint command.
   *
   * @param setpoint The setpoint to set the elevator to
   * @param elevator The elevator to use
   */
  public SetElevatorSetpoint(Measure<Distance> setpoint, Elevator elevator) {
    m_elevator = elevator;
    m_setpoint = setpoint;
    addRequirements(m_elevator);
  }

  // Called just before this Command runs the first time
  @Override
  public void initialize() {
    m_elevator.setSetpoint(m_setpoint.in(Meters));
    m_elevator.enable();
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  public boolean isFinished() {
    return m_elevator.getController().atSetpoint();
  }
}
