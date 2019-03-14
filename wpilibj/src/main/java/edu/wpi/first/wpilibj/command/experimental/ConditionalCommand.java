/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.command.experimental;

import edu.wpi.first.wpilibj.annotation.Incubating;

@Incubating(since = "2020")
public abstract class ConditionalCommand extends CommandBase {
  private final Command m_runWhenTrue;
  private final Command m_runWhenFalse;

  private Command m_chosenCommand;

  protected ConditionalCommand(Command runWhenTrue, Command runWhenFalse) {
    this.m_runWhenTrue = runWhenTrue;
    this.m_runWhenFalse = runWhenFalse;

    if (runWhenTrue != null) {
      runWhenTrue.getRequiredSubsystems().forEach(this::requires);
    }
    if (runWhenFalse != null) {
      runWhenFalse.getRequiredSubsystems().forEach(this::requires);
    }
  }

  protected abstract boolean condition();

  @Override
  public void initialize() {
    if (condition()) {
      m_chosenCommand = m_runWhenTrue;
    } else {
      m_chosenCommand = m_runWhenFalse;
    }
  }

  @Override
  public void execute() {
    if (m_chosenCommand != null) {
      m_chosenCommand.execute();
    }
  }

  @Override
  public void end() {
    if (m_chosenCommand != null) {
      m_chosenCommand.end();
    }
  }

  @Override
  public boolean isFinished() {
    return m_chosenCommand == null || m_chosenCommand.isFinished();
  }
}
