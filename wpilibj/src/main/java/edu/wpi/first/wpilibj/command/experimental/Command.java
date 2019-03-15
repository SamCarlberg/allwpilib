/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.command.experimental;

import java.util.Set;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.annotation.Incubating;

/**
 * Performs some task using one or more subsystems.
 *
 * @see CommandScheduler
 * @see Subsystem
 */
@Incubating(since = "2020")
public interface Command extends Sendable {
  /**
   * Gets the name of this command. Defaults to the name of the implementing class.
   */
  @Override
  default String getName() {
    return getClass().getSimpleName();
  }

  /**
   * Initializes this command. This will only run once during the lifecycle of the command object.
   */
  void initialize();

  /**
   * Executes this command. This is called periodically until {@link #isFinished()} returns
   * {@code true}.
   */
  void execute();

  /**
   * Ends this command and resets the systems it uses. This method is only called once during the
   * lifecycle of a command. This resets the state of the command such that it can be reinitialized
   * and reused later in the program.
   */
  void end();

  /**
   * Determines whether or not this command has completed execution.
   *
   * @return true if this command has finished, false if it still needs to execute
   */
  boolean isFinished();

  /**
   * Gets the subsystems that this command uses. Each subsystem may only be used by one
   * command at a time.
   *
   * @return a set of the subsystems used by this command
   */
  Set<Subsystem> getRequiredSubsystems();

  /**
   * Starts this command. By default, the command will be run using the global command scheduler.
   * A command that has been started with this method can be cancelled with {@link #cancel()}.
   * Calling {@code start()} on a command that is already running will have no effect.
   *
   * @see CommandScheduler#getGlobalCommandScheduler()
   */
  default void start() {
    CommandScheduler.getGlobalCommandScheduler().add(this);
  }

  /**
   * Cancels this command after it has been started. Cancelling a command that is not running will
   * have no effect.
   */
  default void cancel() {
    CommandScheduler.getGlobalCommandScheduler().remove(this);
  }

}
