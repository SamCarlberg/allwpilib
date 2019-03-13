package edu.wpi.first.wpilibj.command.experimental;

/**
 * A subsystem is a major independent component, structure, or mechanism of a robot. For example,
 * the drive base of a robot, with all its motors, encoders, and gyros, would be a single
 * subsystem; other examples could be an elevator or arm, with end effectors as their own
 * separate subsystems.
 *
 * <p>All actuators should be part of a subsystem. Most subsystems should have actuators or have
 * other actions that can be taken that influence the physical world in some way. Some robots have
 * LED light strips that can convey information to drivers - these should be in a subsystem to
 * allow commands to change the colors or patterns of the lights, even though nothing truly moves.
 *
 * <p>Subsystems are used within the command-based framework as requirements for
 * {@link Command Commands}. Multiple commands that require the same subsystem cannot run at the
 * same time; instead, starting any command that requires a subsystem will interrupt and stop any
 * running commands that require the same subsystem.
 *
 * <p>The default {@code Subsystem} constructor will register the subsystem with the global
 * scheduler object, which is used by robot programs to run the command framework. Teams wishing
 * to use a custom scheduler implementation or want to manually handle subsystem registration
 * should use the secondary constructor to specify whether or not the subsystem should be
 * registered with the global scheduler.
 *
 * @see Command
 * @see Scheduler
 */
public abstract class Subsystem {

  /**
   * Optional constructor that allows subclasses to bypass registration with the global
   * Scheduler. If {@code register} is {@code false}, the subsystem will not be registered. Most
   * usecases will want to use the global scheduler - in those cases, simply using the default
   * no-argument constructor (or omitting it altogether and letting it be implicitly called) will
   * be enough.
   *
   * @param register flags whether or not the subsystem should be registered
   *
   * @see #Subsystem()
   */
  protected Subsystem(boolean register) {
    if (register) {
      Scheduler.getGlobalScheduler().add(this);
    }
  }

  /**
   * Default constructor. A subclass using this constructor will be automatically registered with
   * the {@link Scheduler#getGlobalScheduler() global Scheduler}.
   *
   * @see #Subsystem(boolean)
   */
  protected Subsystem() {
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
