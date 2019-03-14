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
