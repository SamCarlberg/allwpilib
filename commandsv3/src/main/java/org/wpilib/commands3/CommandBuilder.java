// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.commands3;

import static edu.wpi.first.util.ErrorMessages.requireNonNullParam;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A builder that allows for all functionality of a command to be configured prior to creating it.
 */
public class CommandBuilder {
  private final Set<RequireableResource> requirements = new HashSet<>();
  private Consumer<Coroutine> toRun;
  private Runnable onCancel = () -> {};
  private Runnable onDone = () -> {};
  private int priority = Command.DEFAULT_PRIORITY;
  private Command.RobotDisabledBehavior disabledBehavior =
      Command.RobotDisabledBehavior.CancelWhileDisabled;

  /**
   * Adds a resource as a requirement for the command.
   *
   * @param resource The required resource
   * @return The builder object, for chaining
   * @see Command#requirements()
   */
  public CommandBuilder requiring(RequireableResource resource) {
    requireNonNullParam(resource, "resource", "CommandBuilder.requiring");

    requirements.add(resource);
    return this;
  }

  /**
   * Adds resources as requirements for the command.
   *
   * @param resources The required resources
   * @return The builder object, for chaining
   * @see Command#requirements()
   */
  public CommandBuilder requiring(RequireableResource... resources) {
    requireNonNullParam(resources, "resources", "CommandBuilder.requiring");
    for (int i = 0; i < resources.length; i++) {
      requireNonNullParam(resources[i], "resources[" + i + "]", "CommandBuilder.requiring");
    }

    requirements.addAll(Arrays.asList(resources));
    return this;
  }

  /**
   * Adds resources as requirements for the command.
   *
   * @param resources The required resources
   * @return The builder object, for chaining
   * @see Command#requirements()
   */
  public CommandBuilder requiring(Collection<RequireableResource> resources) {
    requireNonNullParam(resources, "resources", "CommandBuilder.requiring");
    if (resources instanceof List<RequireableResource> l) {
      for (int i = 0; i < l.size(); i++) {
        requireNonNullParam(l.get(i), "resources[" + i + "]", "CommandBuilder.requiring");
      }
    } else {
      // non-indexable collection
      for (var resource : resources) {
        requireNonNullParam(resource, "resources", "CommandBuilder.requiring");
      }
    }

    requirements.addAll(resources);
    return this;
  }

  /**
   * Sets the priority of the command. If not set, {@link Command#DEFAULT_PRIORITY} will be used.
   *
   * @param priority The priority of the command
   * @return The builder object, for chaining
   * @see Command#priority()
   */
  public CommandBuilder withPriority(int priority) {
    this.priority = priority;
    return this;
  }

  /**
   * Marks the command as being able to run while the robot is disabled.
   *
   * @return The builder object, for chaining
   * @see Command#robotDisabledBehavior()
   */
  public CommandBuilder ignoringDisable() {
    return withDisabledBehavior(Command.RobotDisabledBehavior.RunWhileDisabled);
  }

  /**
   * Gives the command the given behavior when the robot is disabled.
   *
   * @param behavior The behavior when the robot is disabled
   * @return The builder object, for chaining
   * @see Command#robotDisabledBehavior()
   * @see #ignoringDisable()
   */
  public CommandBuilder withDisabledBehavior(Command.RobotDisabledBehavior behavior) {
    requireNonNullParam(behavior, "behavior", "CommandBuilder.withDisabledBehavior");

    this.disabledBehavior = behavior;
    return this;
  }

  /**
   * Sets the code that the command will execute when it's being run by the scheduler.
   *
   * @param toRun The command implementation
   * @return The builder object, for chaining
   * @see Command#run(Coroutine)
   */
  public CommandBuilder executing(Consumer<Coroutine> toRun) {
    requireNonNullParam(toRun, "toRun", "CommandBuilder.executing");

    this.toRun = toRun;
    return this;
  }

  /**
   * Sets the code that the command will execute if it's cancelled while running.
   *
   * @param onCancel The cancellation callback
   * @return The builder object, for chaining.
   * @see Command#onCancel()
   */
  public CommandBuilder whenCanceled(Runnable onCancel) {
    requireNonNullParam(onCancel, "onCancel", "CommandBuilder.whenCanceled");

    this.onCancel = onCancel;
    return this;
  }

  /**
   * Sets the code that the command will always execute after finishing -
   * regardless of if the command itself was cancelled or not.
   *
   * @param onDone The finish callback
   * @return The builder object, for chaining.
   */
  public CommandBuilder whenDone(Runnable onDone) {
    requireNonNullParam(onDone, "onDone", "CommandBuilder.whenDone");

    this.onDone = onDone;
    return this;
  }

  /**
   * Creates the command.
   *
   * @return The built command
   * @throws NullPointerException An NPE if either the command {@link #make(String) name} or {@link
   *     #executing(Consumer) implementation} were not configured before calling this method.
   */
  public Command make(String name) {
    requireNonNullParam(name, "name", "CommandBuilder.withName");
    Objects.requireNonNull(toRun, "Command logic was not specified");

    return new Command() {
      @Override
      public void run(Coroutine coroutine) {
        toRun.accept(coroutine);
        onDone.run();
      }

      @Override
      public void onCancel() {
        onCancel.run();
        onDone.run();
      }

      @Override
      public String name() {
        return name;
      }

      @Override
      public Set<RequireableResource> requirements() {
        return requirements;
      }

      @Override
      public int priority() {
        return priority;
      }

      @Override
      public RobotDisabledBehavior robotDisabledBehavior() {
        return disabledBehavior;
      }

      @Override
      public String toString() {
        return name();
      }
    };
  }
}
