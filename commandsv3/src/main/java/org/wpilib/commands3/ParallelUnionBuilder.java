// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.commands3;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

/**
 * A builder class to configure and then create a {@link ParallelUnion}. Like
 * {@link CommandBuilder}, the final command is created by calling the terminal
 * {@link #make(String)} method, or with an automatically generated name using
 * {@link #make()}.
 */
public class ParallelUnionBuilder {
  private final Set<Command> commands = new LinkedHashSet<>();
  private final Set<Command> requiredCommands = new LinkedHashSet<>();
  private BooleanSupplier endCondition = null;

  /**
   * Adds one or more optional commands to the group. They will not be required to complete for
   * the parallel group to exit, and will be canceled once all required commands have finished.
   *
   * @param commands The optional commands to add to the group
   * @return The builder object, for chaining
   */
  public ParallelUnionBuilder optional(Command... commands) {
    this.commands.addAll(Arrays.asList(commands));
    return this;
  }

  /**
   * Adds one or more required commands to the group. All required commands will need to complete
   * for the group to exit.
   *
   * @param commands The required commands to add to the group
   * @return The builder object, for chaining
   */
  public ParallelUnionBuilder requiring(Command... commands) {
    requiredCommands.addAll(Arrays.asList(commands));
    this.commands.addAll(requiredCommands);
    return this;
  }

  /**
   * Forces the group to be a pure race, where the group will finish after the first command in
   * the group completes. All other commands in the group will be canceled.
   *
   * @return The builder object, for chaining
   */
  public ParallelUnionBuilder racing() {
    requiredCommands.clear();
    return this;
  }

  /**
   * Forces the group to require all its commands to complete, overriding any configured race or
   * deadline behaviors. The group will only exit once every command has completed.
   *
   * @return The builder object, for chaining
   */
  public ParallelUnionBuilder requireAll() {
    requiredCommands.clear();
    requiredCommands.addAll(commands);
    return this;
  }

  /**
   * Adds an end condition to the command group. If this condition is met before all required
   * commands have completed, the group will exit early. If multiple end conditions are added
   * (e.g. {@code .until(() -> conditionA()).until(() -> conditionB())}), then the last end
   * condition added will be used and any previously configured condition will be overridden.
   *
   * @param condition The end condition for the group
   * @return The builder object, for chaining
   */
  public ParallelUnionBuilder until(BooleanSupplier condition) {
    endCondition = condition;
    return this;
  }

  /**
   * Creates the group, using the provided named. The group will require everything that the
   * commands in the group require, and will have a priority level equal to the highest priority
   * among those commands.
   *
   * @param name The name of the parallel group
   * @return The built group
   */
  public ParallelUnion make(String name) {
    var group = new ParallelUnion(name, commands, requiredCommands);
    if (endCondition == null) {
      // No custom end condition, return the group as is
      return group;
    }

    // We have a custom end condition, so we need to wrap the group in a race
    return ParallelUnion.builder()
               .optional(group, Command.waitUntil(endCondition).make("Until Condition"))
               .make(name);
  }

  /**
   * Creates the group, giving it a name based on the commands within the group.
   *
   * @return The built group
   */
  public ParallelUnion make() {
    // eg "(CommandA & CommandB & CommandC)"
    String required =
        requiredCommands.stream().map(Command::name).collect(Collectors.joining(" & ", "(", ")"));

    var optionalCommands = new LinkedHashSet<>(commands);
    optionalCommands.removeAll(requiredCommands);
    // eg "(CommandA | CommandB | CommandC)"
    String optional =
        optionalCommands.stream().map(Command::name).collect(Collectors.joining(" | ", "(", ")"));

    if (requiredCommands.isEmpty()) {
      // No required commands, pure race
      return make(optional);
    } else if (optionalCommands.isEmpty()) {
      // Everything required
      return make(required);
    } else {
      // Some form of deadline
      // eg "[(CommandA & CommandB) * (CommandX | CommandY | ...)]"
      String name = "[%s * %s]".formatted(required, optional);
      return make(name);
    }
  }
}
