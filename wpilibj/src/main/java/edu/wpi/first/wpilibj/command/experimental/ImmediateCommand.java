package edu.wpi.first.wpilibj.command.experimental;

import edu.wpi.first.wpilibj.annotation.Incubating;

/**
 * A command that executes once, using its {@link #initialize()} method.
 */
@Incubating(since = "2020")
public abstract class ImmediateCommand extends CommandBase {

  @Override
  public abstract void initialize();

  @Override
  public void execute() {
    // NOP
  }

  @Override
  public void end() {
    // NOP
  }

  @Override
  public boolean isFinished() {
    return true;
  }
}
