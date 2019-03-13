package edu.wpi.first.wpilibj.command.experimental;

public abstract class Subsystem {

  public Subsystem() {
    Scheduler.getGlobalScheduler().add(this);
  }

  /**
   * Creates a command that should be used to control this subsystem
   *
   * @return a default command object, or {@code null} if this subsystem does not require a default
   *         command to be constantly running
   */
  protected abstract Command createDefaultCommand();

  public String getName() {
    return getClass().getSimpleName();
  }

}
