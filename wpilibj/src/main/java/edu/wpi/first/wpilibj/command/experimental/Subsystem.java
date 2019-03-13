package edu.wpi.first.wpilibj.command.experimental;

public abstract class Subsystem {

  protected Subsystem(boolean register) {
    if (register) {
      Scheduler.getGlobalScheduler().add(this);
    }
  }

  public Subsystem() {
    this(true);
  }

  /**
   * Creates a command that should be used to control this subsystem.
   *
   * @return a default command object, or {@code null} if this subsystem does not require a default
   *         command to be constantly running
   */
  protected abstract Command createDefaultCommand();

  public String getName() {
    return getClass().getSimpleName();
  }

}
