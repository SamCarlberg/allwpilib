/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.command.experimental;

import java.util.HashSet;
import java.util.Set;

import edu.wpi.first.wpilibj.annotation.Incubating;

@Incubating(since = "2020")
public abstract class CommandBase implements Command {
  private final Set<Subsystem> m_requirements = new HashSet<>();

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
}
