package edu.wpi.first.wpilibj3.command.async;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Time;

public class HardwareResource {
  private final String name;

  private final AsyncScheduler registeredScheduler;

  public HardwareResource(String name) {
    this(name, AsyncScheduler.getInstance());
  }

  public HardwareResource(String name, AsyncScheduler scheduler) {
    this.name = name;
    this.registeredScheduler = scheduler;
    scheduler.registerResource(this);
  }

  public String getName() {
    return name;
  }

  /**
   * Sets the default command to run on the resource when no other command is scheduled. The default
   * command's priority is effectively the minimum allowable priority for any command requiring a
   * resource. For this reason, it's recommended that a default command have a priority no greater
   * than {@link AsyncCommand#DEFAULT_PRIORITY} to prevent it from blocking other, non-default
   * commands from running.
   *
   * <p>The default command is initially an idle command that merely parks the execution thread.
   * This command has the lowest possible priority so as to allow any other command to run.
   *
   * @param defaultCommand the new default command
   */
  public void setDefaultCommand(AsyncCommand defaultCommand) {
    registeredScheduler.setDefaultCommand(this, defaultCommand);
  }

  public AsyncCommand getDefaultCommand() {
    return registeredScheduler.getDefaultCommand(this);
  }

  /** Gets the currently executing command on this resource. */
  public AsyncCommand getCurrentCommand() {
    return registeredScheduler.getCommandUsing(this);
  }

  public AsyncCommandBuilder run(ThrowingRunnable command) {
    return new AsyncCommandBuilder().requiring(this).executing(command);
  }

  public AsyncCommand idle() {
    return new IdleCommand(this);
  }

  public AsyncCommand idle(Measure<Time> duration) {
    return new IdleCommand(this, duration);
  }

  public AsyncCommandBuilder runSequence(AsyncCommand... commands) {
    // All commands most only have this resource as a requirement
    for (AsyncCommand command : commands) {
      if (command.requirements().size() != 1 || !command.requires(this)) {
        throw new IllegalArgumentException("Command " + command.name() + " can only require " + getName());
      }
    }

    return new AsyncCommandBuilder().requiring(this).executing(() -> {
      for (AsyncCommand command : commands) {
        command.run();
      }
    });
  }

  @Override
  public String toString() {
    return name;
  }
}