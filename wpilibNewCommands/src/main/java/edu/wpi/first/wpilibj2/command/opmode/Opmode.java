// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.wpilibj2.command.opmode;

import edu.wpi.first.util.ErrorMessages;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import java.util.function.Consumer;

/**
 * An opmode is a fixed control configuration for your robot. Intended to be used with
 * {@link OpmodeCommandRobot}, an opmode can be selected for each robot control period (autonomous,
 * teleop, test, and disabled), which will run when the robot enters that period. Any configurations
 * applied by the opmode will be applied when it loads, and loading a new opmode (either by changing
 * the selection or changing to a different control period) will wipe the configuration applied by
 * the old opmode to allow the new opmode to load without potential conflicts.
 *
 * <p>Opmodes can safely configure trigger bindings (to controller buttons or custom triggers),
 * default commands, and schedule new commands to execute when the opmode loads. Any other
 * configuration will <i>not</i> be automatically wiped, and will need to be manually handled.</p>
 *
 * @param group The group that the opmode belongs to.
 * @param name The name of the opmode.
 * @param setup The opmode setup function. This may be null.
 */
public record Opmode(OpmodeGroup group, String name, Consumer<Opmode> setup) {
  public Opmode {
    ErrorMessages.requireNonNullParam(group, "group", "Opmode");
    ErrorMessages.requireNonNullParam(name, "name", "Opmode");
  }

  /**
   * Runs the setup function, if present.
   */
  void runSetup() {
    if (setup != null) {
      setup.accept(this);
    }
  }

  /**
   * Schedules a command to run when the opmode begins.
   *
   * @param command The command to run
   */
  public void onStart(Command command) {
    CommandScheduler.getInstance().schedule(command);
  }
}
