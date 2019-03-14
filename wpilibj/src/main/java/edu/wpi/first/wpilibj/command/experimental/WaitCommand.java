/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.command.experimental;

import edu.wpi.first.wpilibj.annotation.Incubating;

/**
 * Waits until a certain time period has elapsed.
 */
@Incubating(since = "2020")
public class WaitCommand extends TimedCommand {
  /**
   * Creates a new wait command.
   *
   * @param timeout how long the command should wait for, in seconds
   */
  public WaitCommand(double timeout) {
    super(timeout);
  }

  @Override
  public void execute() {
    // NOP
  }

  @Override
  public void end() {
    // NOP
  }
}
