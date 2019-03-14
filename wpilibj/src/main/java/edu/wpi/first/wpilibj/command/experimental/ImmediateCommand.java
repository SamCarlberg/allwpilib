package edu.wpi.first.wpilibj.command.experimental;

import edu.wpi.first.wpilibj.annotation.Incubating;

/**
 * A command that executes once, using its {@link #initialize() initialize()} method.
 */
@Incubating(since = "2020")
public abstract class ImmediateCommand extends CommandBase {

  /**
   * Performs the action of this command. This is run exactly once in the lifecycle of the command,
   * in the {@link #initialize()} method.
   */
  protected abstract void perform();

  @Override
  public final void initialize() {
    perform();
  }

  @Override
  public final void execute() {
    // NOP
  }

  @Override
  public void end() {
    // NOP, but available for subclasses to override
  }

  @Override
  public final boolean isFinished() {
    return true;
  }
}
