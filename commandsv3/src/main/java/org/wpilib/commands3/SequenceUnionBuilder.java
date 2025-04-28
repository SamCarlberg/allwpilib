// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.commands3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

/**
 * A builder class to configure and then create a {@link SequenceUnion}. Like {@link CommandBuilder},
 * the final command is created by calling the terminal {@link #make(String)} method, or with
 * an automatically generated name using {@link #make()}.
 */
public class SequenceUnionBuilder {
  private final List<Command> steps = new ArrayList<>();
  private BooleanSupplier endCondition = null;

  public SequenceUnionBuilder andThen(Command first, Command... others) {
    steps.add(first);
    steps.addAll(Arrays.asList(others));
    return this;
  }

  /**
   * Adds an end condition to the command group. If this condition is met before all required
   * commands have completed, the group will exit early. If multiple end conditions are added
   * (e.g. {@code .until(() -> conditionA()).until(() -> conditionB())}), then the last end
   * condition added will be used and any previously configured condition will be overridden.
   *
   * @param endCondition The end condition for the group
   * @return The builder object, for chaining
   */
  public SequenceUnionBuilder until(BooleanSupplier endCondition) {
    this.endCondition = endCondition;
    return this;
  }

  /**
   * Creates the sequence command, giving it the specified name.
   *
   * @param name The name of the sequence command
   * @return The built command
   */
  public Command make(String name) {
    var seq = new SequenceUnion(name, steps);
    if (endCondition != null) {
      // No custom end condition, return the group as is
      return seq;
    }

    // We have a custom end condition, so we need to wrap the group in a race
    return ParallelUnion.builder()
               .optional(seq, Command.waitUntil(endCondition).make("Until Condition"))
               .make(name);
  }

  /**
   * Creates the sequence command, giving it an automatically generated name based on the commands
   * in the sequence.
   *
   * @return The built command
   */
  public Command make() {
    return make(steps.stream().map(Command::name).collect(Collectors.joining(" -> ")));
  }
}
