// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.wpilibj.examples.hatchbottraditional.commands;

import static edu.wpi.first.units.Units.Inches;

import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Measure;
import edu.wpi.first.wpilibj.examples.hatchbottraditional.subsystems.DriveSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

public class DriveDistance extends Command {
  private final DriveSubsystem m_drive;
  private final Measure<Distance> m_distance;
  private final double m_speed;

  /**
   * Creates a new DriveDistance.
   *
   * @param distance The distance the robot will drive
   * @param speed The speed at which the robot will drive, between -1 and +1
   * @param drive The drive subsystem on which this command will run
   */
  public DriveDistance(Measure<Distance> distance, double speed, DriveSubsystem drive) {
    m_distance = distance;
    m_speed = speed;
    m_drive = drive;
    addRequirements(m_drive);
  }

  @Override
  public void initialize() {
    m_drive.resetEncoders();
    m_drive.arcadeDrive(m_speed, 0);
  }

  @Override
  public void execute() {
    m_drive.arcadeDrive(m_speed, 0);
  }

  @Override
  public void end(boolean interrupted) {
    m_drive.arcadeDrive(0, 0);
  }

  @Override
  public boolean isFinished() {
    return Math.abs(m_drive.getAverageEncoderDistance().in(Inches)) >= m_distance.in(Inches);
  }
}
