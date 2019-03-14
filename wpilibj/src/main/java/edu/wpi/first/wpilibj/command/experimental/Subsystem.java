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
 * <p>Subsystems with actuators or moving parts are considered <i>unsafe</i>. If a command is
 * scheduled for a subsystem while the robot is disabled, nothing will move - until the robot is
 * enabled, causing wheels or other mechanisms to suddenly start moving. This poses a safety risk,
 * since if a person is standing in the way of a robot that suddenly starts driving at them, or
 * has fingers in a pinch point, there is a chance of injury. Subsystems that do <i>not</i> have
 * any actuators or control the motion of a mechanism may disable safety checks with
 * {@link #disableSafety()}, allowing commands to use the subsystem while the robot is disabled.
 * The aforementioned LED strip subsystem is an example of a subsystem that may disable safety.
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

  private boolean m_isUnsafe = true;

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

  /**
   * Explicitly disables safety. This should <b>ONLY</b> be used with subsystems that do not
   * control any actuators; for example, a subsystem for a string of LED lights may have its
   * safety disabled since it cannot cause any unexpected motion of the robot. A drivebase
   * subsystem should always have safety enabled, since it would otherwise be able to suddenly
   * move when enabling the robot if a command was scheduled for it in disabled mode.
   *
   * <p><strong>ONLY USE THIS METHOD ON A SUBSYSTEM WITHOUT ACTUATORS</strong>
   */
  protected final void disableSafety() {
    m_isUnsafe = false;
  }

  /**
   * Checks if this subsystem is unsafe. An unsafe subsystem is one that contains actuators that
   * may suddenly move when the robot is enabled if a command was scheduled for the subsystem
   * when the robot was disabled (since actuators are only controllable when the robot is enabled,
   * the command would be scheduled but only cause things to move when the robot becomes enabled,
   * which poses a safety risk if humans are in the way).
   */
  public final boolean isUnsafe() {
    return m_isUnsafe;
  }

  /**
   * Gets the name of this subsystem. Defaults to the name of the class.
   */
  public String getName() {
    return getClass().getSimpleName();
  }

}
