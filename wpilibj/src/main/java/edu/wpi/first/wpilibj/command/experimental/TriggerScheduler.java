/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.command.experimental;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.wpi.first.wpilibj.annotation.Incubating;

/**
 * Handles the lifecycle of trigger bindings. Bindings can be added with {@link #addTrigger}, and
 * triggers can be unbound with {@link #unbind}.
 */
@Incubating(since = "2020")
public class TriggerScheduler {
  private final CommandScheduler m_commandScheduler;

  /**
   * The trigger bindings in this scheduler.
   */
  private final List<TriggerBinding> m_triggers = new ArrayList<>();

  private static final TriggerScheduler m_globalScheduler =
      new TriggerScheduler(CommandScheduler.getGlobalCommandScheduler());

  public static TriggerScheduler getGlobalTriggerScheduler() {
    return m_globalScheduler;
  }

  /**
   * Creates a new trigger scheduler object. Commands bound to triggers will be run using the
   * provided command scheduler.
   *
   * @param commandScheduler the command scheduler to use to run bound commands
   */
  public TriggerScheduler(CommandScheduler commandScheduler) {
    m_commandScheduler = Objects.requireNonNull(commandScheduler,
        "Command scheduler cannot be null");
  }

  /**
   * Adds a trigger to this scheduler.
   *
   * <p>Multiple commands can be bound to the same trigger.
   *
   * @param binding the binding to add
   */
  public void addTrigger(TriggerBinding binding) {
    Objects.requireNonNull(binding, "Binding cannot be null");
    m_triggers.add(binding);
  }

  /**
   * Unbinds all commands currently bound to the given trigger.
   *
   * @param trigger the trigger to remove bindings from
   */
  public void unbind(Trigger trigger) {
    m_triggers.removeIf(binding -> binding.getTrigger().equals(trigger));
  }

  /**
   * Removes all trigger bindings.
   */
  public void unbindAll() {
    m_triggers.clear();
  }

  /**
   * Updates all triggers and schedules or terminates their bound commands as appropriate. The
   * command lifecycle is handled by the command scheduler passed to the trigger scheduler's
   * constructor.
   */
  public void run() {
    // Update triggers
    m_triggers.forEach(TriggerBinding::update);

    // Cancel commands from triggers that shouldn't run
    m_triggers.stream()
              .filter(TriggerBinding::shouldCancel)
              .map(TriggerBinding::getCommand)
              .forEach(m_commandScheduler::remove);

    // Add commands that should be started
    m_triggers.stream()
              .filter(TriggerBinding::shouldStart)
              .map(TriggerBinding::getCommand)
              .forEach(m_commandScheduler::add);
  }
}
