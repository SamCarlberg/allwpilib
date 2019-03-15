/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.command.experimental;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.annotation.Incubating;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

/**
 * Base class for commands that implements the requirements for {@code Sendable}.
 */
@Incubating(since = "2020")
public abstract class CommandBase extends SendableBase implements Command {
  private final Set<Subsystem> m_requirements = new HashSet<>();

  /**
   * Creates a new command. The name of the command will default to the name of the subclass
   * (e.g. {@code class MyCommand extends CommandBase} will be named "MyCommand"), but can be
   * changed with {@link #setName(String)}.
   */
  protected CommandBase() {
    super(false);
    setName(Command.super.getName());
  }

  /**
   * Creates a new command with the given name.
   *
   * @param name the name of the command
   */
  protected CommandBase(String name) {
    super(false);
    setName(Objects.requireNonNull(name, "Name cannot be null"));
  }

  /**
   * Adds the given subsystem as a requirement of this command. This should be called in the
   * constructor of the command object.
   *
   * @param subsystem a required subsystem.
   */
  protected final void requires(Subsystem subsystem) {
    m_requirements.add(subsystem);
  }

  @Override
  public final Set<Subsystem> getRequiredSubsystems() {
    return m_requirements;
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.setSmartDashboardType("Command");
    builder.addStringProperty(".name", this::getName, null);
    builder.addBooleanProperty(
        "running",
        () -> CommandScheduler.getGlobalCommandScheduler().isRunning(this),
        run -> {
          if (run) {
            start();
          } else {
            cancel();
          }
        });
  }
}
