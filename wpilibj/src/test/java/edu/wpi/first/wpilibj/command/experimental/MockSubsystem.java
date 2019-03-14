/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.command.experimental;

public class MockSubsystem extends Subsystem {
  public MockSubsystem() {
    super(false);
  }

  @Override
  public Command createDefaultCommand() {
    return null;
  }
}
