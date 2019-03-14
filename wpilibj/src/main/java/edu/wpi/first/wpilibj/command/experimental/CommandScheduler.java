/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.command.experimental;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import edu.wpi.first.wpilibj.annotation.Incubating;

/**
 * Handles the lifecycle of commands. A scheduler's {@link #run()} method must be called
 * periodically to update the scheduled commands.
 *
 * <p>Commands can be scheduled with {@link #add(Command)}, and can be cancelled with
 * {@link #remove(Command)}. All commands can be cancelled at once with {@link #removeAll()}.
 *
 * <p>Each {@link Subsystem} registered with a scheduler may only be used by a single command at a
 * time. If a command is scheduled that requires a subsystem in use by another command, that
 * command will be cancelled immediately and the new command will be scheduled. If there comes a
 * time where no commands requiring a registered subsystem are scheduled, that subsystem's
 * {@link Subsystem#createDefaultCommand() default command} will be scheduled to run. A default
 * command will always run if no other command requires the subsystem. If a default command reaches
 * its natural completion (ie {@link Command#isFinished() isFinished()} returns {@code true}), it
 * will be rescheduled and restarted.
 *
 * <p>Commands will run in the order in which they are scheduled.
 *
 * <p>The scheduler is not thread-safe.
 *
 * @see TriggerScheduler
 */
@Incubating(since = "2020")
public class CommandScheduler {
  /**
   * The currently scheduled commands. These may or may not be initialized.
   */
  private final Set<Command> m_commands = new LinkedHashSet<>();

  /**
   * Map each subsystem to its default command.
   */
  private final Map<Subsystem, Command> m_defaultCommands = new HashMap<>();

  /**
   * Map each subsystem to the currently running command that requires it.
   */
  private final Map<Subsystem, Command> m_currentCommands = new HashMap<>();

  /**
   * The currently scheduled commands that have been initialized and are currently running.
   */
  private final Set<Command> m_initializedCommands = new HashSet<>();

  private boolean m_safetyEnabled = true;

  private static final CommandScheduler m_globalScheduler = new CommandScheduler();

  public static CommandScheduler getGlobalCommandScheduler() {
    return m_globalScheduler;
  }

  /**
   * Adds a subsystem to this scheduler. If the subsystem has a default command provided by its
   * {@link Subsystem#createDefaultCommand() createDefaultCommand()} method, that default command
   * will run as long as no other command requiring it is scheduled
   *
   * @param subsystem the subsystem to add
   */
  public void add(Subsystem subsystem) {
    if (m_defaultCommands.containsKey(subsystem)) {
      // Already added
      return;
    }
    Command defaultCommand = subsystem.createDefaultCommand();
    if (defaultCommand != null && !defaultCommand.getRequiredSubsystems().contains(subsystem)) {
      throw new IllegalStateException(
          String.format(
              "Command %s does not require the subsystem %s",
              defaultCommand.getName(),
              subsystem.getName()));
    }
    m_defaultCommands.put(subsystem, defaultCommand);
  }

  /**
   * Schedules a command to run. Has no effect if the same command is added while it is already
   * scheduled; however, a command may be added again if it has already completed execution. Any
   * running commands that require any of the subsystems required by the given command will
   * immediately be terminated and removed from the scheduler.
   *
   * <p>If the command requires an unsafe subsystem and safety is enabled, the command will not
   * be scheduled.
   *
   * @param command the command to schedule
   */
  public void add(Command command) {
    Objects.requireNonNull(command, "Command cannot be null");
    if (m_commands.contains(command)) {
      // This command is already scheduled, don't add it again
      return;
    }

    // Bail if the command uses an unsafe subsystem
    if (isSafetyEnabled() && isUnsafe(command)) {
      return;
    }

    // Terminate running commands that require the same subsystem(s) as the new command
    m_commands.stream()
              .filter(c -> overlaps(command, c))
              .collect(Collectors.toList()) // Avoid concurrent modification exceptions
              .forEach(this::remove);

    m_commands.add(command);
    for (Subsystem subsystem : command.getRequiredSubsystems()) {
      m_currentCommands.put(subsystem, command);
    }
  }

  /**
   * Checks if two commands require the same subsystem.
   *
   * @param first  the first command
   * @param second the second command
   * @return true if at least one subsystem is required by both commands, false otherwise
   */
  private static boolean overlaps(Command first, Command second) {
    return !Collections.disjoint(first.getRequiredSubsystems(), second.getRequiredSubsystems());
  }

  /**
   * Terminates a scheduled command and removes it. If the command is running, its
   * {@link Command#end() end()} method will be called, otherwise, it is simply removed from the
   * scheduled command queue.
   *
   * @param command the command to remove
   */
  public void remove(Command command) {
    if (m_commands.contains(command)) {
      if (m_initializedCommands.contains(command)) {
        // End the command if it has been initialized
        // Otherwise, it may cause some state to change unexpectedly
        command.end();
        m_initializedCommands.remove(command);
      }
      m_commands.remove(command);
    }
    for (Subsystem subsystem : command.getRequiredSubsystems()) {
      m_currentCommands.put(subsystem, null);
    }
  }

  /**
   * Terminates and removes all scheduled commands from this scheduler.
   */
  public void removeAll() {
    // Copy to a new list to avoid concurrent modification exceptions
    for (Command command : new ArrayList<>(m_commands)) {
      remove(command);
    }
  }

  /**
   * Checks if this scheduler is currently running any commands.
   */
  public boolean hasRunningCommands() {
    return !m_initializedCommands.isEmpty();
  }

  /**
   * Checks if any commands are currently scheduled to run.
   */
  public boolean hasScheduledCommands() {
    return !m_commands.isEmpty();
  }

  /**
   * Checks if a command is currently being run by this scheduler.
   *
   * @param command the command to check
   * @return true if the command is currently running, false otherwise
   */
  public boolean isRunning(Command command) {
    return m_initializedCommands.contains(command);
  }

  /**
   * Checks if a command is currently scheduled to run in this scheduler. A command that is
   * scheduled may not have started running; to check if a command is running, use
   * {@link #isRunning(Command)}.
   *
   * @param command the command to check
   * @return true if the command is scheduled to be run or is currently running, false otherwise
   */
  public boolean isScheduled(Command command) {
    return m_commands.contains(command);
  }

  /**
   * Enables or disabled safety checks. If safety checks are enabled, no commands that require an
   * unsafe subsystem are permitted to run. Any running unsafe commands will be cancelled in the
   * next scheduler update. This behavior is intended to prevent mechanisms from suddenly starting
   * to move when a robot becomes enabled from commands scheduled when the robot was disabled.
   *
   * @param safetyEnabled whether or not safety should be enabled
   * @see Subsystem#isUnsafe()
   */
  public void setSafetyEnabled(boolean safetyEnabled) {
    m_safetyEnabled = safetyEnabled;
  }

  /**
   * Checks if only safe subsystems should be usable. If this is the case, only commands that
   * require safe subsystems will be allowed to run.
   */
  public boolean isSafetyEnabled() {
    return m_safetyEnabled;
  }

  private static boolean isUnsafe(Command command) {
    return command.getRequiredSubsystems().stream().anyMatch(Subsystem::isUnsafe);
  }

  /**
   * Runs the scheduler. This cancels unsafe commands, initializes newly scheduled commands,
   * handles default commands for unused subsystems, runs scheduled commands, and cleans up
   * commands that have finished executing.
   */
  public void run() {
    // Terminate unsafe commands if safety is enabled
    if (isSafetyEnabled()) {
      m_commands.stream()
                .filter(CommandScheduler::isUnsafe)
                .collect(Collectors.toList())
                .forEach(this::remove);
    }
    // Add the default command if the subsystem is not required by any currently scheduled commands
    m_defaultCommands.entrySet()
                     .stream()
                     .filter(e -> e.getValue() != null)
                     .filter(e -> m_currentCommands.get(e.getKey()) == null)
                     .map(Map.Entry::getValue)
                     .forEach(this::add);

    // Initialize commands
    m_commands.stream()
              .filter(command -> !m_initializedCommands.contains(command))
              .peek(Command::initialize)
              .forEach(m_initializedCommands::add);

    // Execute each unfinished command
    m_commands.stream()
              .filter(command -> !command.isFinished())
              .forEach(Command::execute);

    // Remove finished commands
    m_commands.stream()
              .filter(Command::isFinished)
              .collect(Collectors.toList()) // Avoid concurrent modification exceptions
              .forEach(this::remove);
  }
}
