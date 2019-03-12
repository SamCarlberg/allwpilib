package edu.wpi.first.wpilibj.command.experimental;

public interface Subsystem {
  /**
   * Creates a command that should be used to control this subsystem
   *
   * @return a default command object, or {@code null} if this subsystem does not require a default
   *         command to be constantly running
   */
  Command createDefaultCommand();

  default String getName() {
    return getClass().getSimpleName();
  }

}
