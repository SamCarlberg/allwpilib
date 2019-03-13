package edu.wpi.first.wpilibj.command.experimental;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import edu.wpi.first.wpilibj.annotation.Incubating;

/**
 * Handles the lifecycle of commands. A scheduler's {@link #run()} method must be called
 * periodically to update the scheduled commands.
 *
 * <p>Commands added to a scheduler are executed in the order in which they are added.
 *
 * <p>The scheduler is not thread-safe.
 */
@Incubating(since = "2020")
public class Scheduler {
  /**
   * Default global instance.
   */
  private static final Scheduler kGlobalScheduler = new Scheduler();

  /**
   * Gets the default global scheduler object.
   */
  public static Scheduler getGlobalScheduler() {
    return kGlobalScheduler;
  }

  /**
   * The currently scheduled commands. These may or may not be initialized.
   */
  private final Set<Command> m_commands = new LinkedHashSet<>();

  /**
   * Map each subsystem to its default command.
   */
  private final Map<Subsystem, Command> m_defaultCommands = new HashMap<>();

  /**
   * The currently scheduled commands that have been initialized and are currently running.
   */
  private final Set<Command> m_initializedCommands = new HashSet<>();

  /**
   * The trigger bindings in this scheduler.
   */
  private final List<TriggerBinding> m_triggers = new ArrayList<>();

  public enum TriggerBindingType {
    /**
     * Binds a command to run as long as a trigger is active.
     */
    kActive,
    /**
     * Binds a command to be started when a trigger becomes active.
     */
    kRisingEdge,
    /**
     * Binds a command to be started when a trigger becomes inactive.
     */
    kFallingEdge,
  }

  private static final class TriggerBinding {
    private final Trigger m_trigger;
    private final TriggerBindingType m_type;
    private final Command m_command;
    private boolean m_wasActive = false; // NOPMD redundant field initializer
    private boolean m_isActive = false; // NOPMD redundant field initializer

    TriggerBinding(Trigger trigger, TriggerBindingType type, Command command) {
      m_trigger = trigger;
      m_type = type;
      m_command = command;
    }

    public Command getCommand() {
      return m_command;
    }

    /**
     * Updates the state of the binding.
     */
    public void update() {
      m_wasActive = m_isActive;
      m_isActive = m_trigger.get();
    }

    /**
     * Checks if the bound command should be cancelled by the scheduler.
     */
    public boolean shouldCancel() {
      if (m_type == TriggerBindingType.kActive) {
        // Cancel the command if the trigger is no longer active
        return !m_isActive;
      } else {
        // Rising and falling edge triggers let the command run to completion,
        // so the command should never be cancelled
        return false;
      }
    }

    /**
     * Checks if the command bound to the trigger should be started by the scheduler.
     */
    public boolean shouldStart() {
      switch (m_type) {
        case kActive:
          // Technically, this should be
          // return m_isActive && !scheduler.isRunning(m_command)
          // But since starting a command that is already running has no effect, this is logically
          // equivalent and removes the need to keep a reference to the scheduler object
          return m_isActive;
        case kRisingEdge:
          return !m_wasActive && m_isActive;
        case kFallingEdge:
          return m_wasActive && !m_isActive;
        default:
          return false;
      }
    }
  }

  /**
   * Adds a trigger to this scheduler. The scheduler's {@link #run()} method will start or stop
   * the bound command as determined by the binding type and the state of the trigger.
   *
   * <p>Multiple commands can be bound to the same trigger.
   *
   * @param trigger the trigger to bind a command to
   * @param type    the type of binding
   * @param command the command to be bound to the trigger
   */
  public void addTrigger(Trigger trigger, TriggerBindingType type, Command command) {
    Objects.requireNonNull(trigger, "Trigger cannot be null");
    Objects.requireNonNull(type, "Binding type cannot be null");
    Objects.requireNonNull(command, "Command cannot be null");
    m_triggers.add(new TriggerBinding(trigger, type, command));
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
   * scheduled; however, a command may be added again if it has already completed execution.
   *
   * @param command the command to schedule
   */
  public void add(Command command) {
    Objects.requireNonNull(command, "Command cannot be null");
    if (m_commands.contains(command)) {
      // This command is already scheduled, don't add it again
      return;
    }

    // Terminate running commands that require the same subsystem(s) as the new command
    m_commands.stream()
              .filter(c -> overlaps(command, c))
              .collect(Collectors.toList()) // Avoid concurrent modification exceptions
              .forEach(this::remove);

    m_commands.add(command);
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
   * Runs all scheduled commands.
   */
  public void run() {
    // Update triggers
    m_triggers.forEach(TriggerBinding::update);

    // Cancel commands from triggers that shouldn't run
    m_triggers.stream()
              .filter(TriggerBinding::shouldCancel)
              .map(TriggerBinding::getCommand)
              .forEach(this::remove);

    // Add commands that should be started
    m_triggers.stream()
              .filter(TriggerBinding::shouldStart)
              .map(TriggerBinding::getCommand)
              .forEach(this::add);

    // Add the default command if the subsystem is not required by any currently scheduled commands
    m_defaultCommands.forEach((subsystem, defaultCommand) -> {
      boolean isUnused = m_commands.stream()
                                   .filter(c -> c != defaultCommand)
                                   .map(Command::getRequiredSubsystems)
                                   .noneMatch(c -> c.contains(subsystem));
      if (isUnused) {
        add(defaultCommand);
      }
    });

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
