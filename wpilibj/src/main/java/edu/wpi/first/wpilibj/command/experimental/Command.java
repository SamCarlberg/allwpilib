/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.command.experimental;

import java.util.Set;

import edu.wpi.first.wpilibj.annotation.Incubating;

/**
 * Performs some task using one or more subsystems.
 *
 * <h3>Lifecycle of a Command</h3>
 * Commands are initialized, run, and terminated by the {@link Scheduler}. After adding a command
 * to the scheduler, its {@link #initialize()} method will be run in the next update cycle. Then,
 * its {@link #isFinished()} method is called to see if the command should be executed - if not,
 * its {@link #end()} method is called and it will be removed from the scheduler. Otherwise, its
 * {@link #execute()} method is called. The condition of the command will be checked again by the
 * scheduler in its next update. A command that has completed execution may be rescheduled and
 * reused.
 */
@Incubating(since = "2020")
public interface Command {

  /**
   * Gets the name of this command. Defaults to the name of the implementing class.
   */
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

}
