package edu.wpi.first.wpilibj.command.experimental;

import edu.wpi.first.wpilibj.annotation.Incubating;

/**
 * A {@code Trigger} represents the state of some input, usually a button on a controller. Triggers
 * may also be represented by the state of a digital input, or any custom function that can have
 * two states. {@link Command Commands} can be bound to run when a trigger is active, inactive,
 * or makes a transition between the two states.
 *
 * <h3>Binding commands to a trigger</h3>
 * <pre>{@code
 * Trigger trigger = ...;
 * trigger.whenActivated(new RunWhenPressed());
 * trigger.whileActive(new RunWhilePressed());
 * trigger.whenDeactivated(new RunWhenReleased());
 * trigger.whileInactive(new RunWhileReleased());
 * }</pre>
 *
 * <p>Triggers are <i>composable</i> and may be combined with the logical functions
 * {@link #and(Trigger)}, {@link #or(Trigger)}, {@link #xor(Trigger)}, and {@link #not(Trigger)}.
 * This makes it easy to bind a command that, for example, runs only when two buttons are pressed at
 * the same time, or when one button is pressed when another is not.
 *
 * <h3>Composing triggers</h3>
 * <pre>{@code
 * Trigger buttonA = ...;
 * Trigger buttonB = ...;
 * Trigger aAndB = buttonA.and(buttonB);
 * Trigger aOrB = buttonA.or(buttonB);
 * Trigger aButNotB = buttonA.and(not(buttonB));
 * }</pre>
 *
 * <p>Since {@code Trigger} is a functional interface, it can be used as a target for lambda
 * expressions and method references, making it easy to create custom triggers without needing to
 * create a custom class.
 *
 * <h3>Creating a custom trigger</h3>
 * <pre>{@code
 * // Binding to a limit switch
 * DigitalInput myLimitSwitch = new DigitalInput(0);
 * Trigger trigger = myLimitSwitch::get;
 *
 * // Trigger when a joystick is over halfway pushed
 * Joystick myJoystick = new Joystick(0);
 * Trigger trigger = () -> myJoystick.getRawAxis(0) > 0.5;
 * }</pre>
 */
@Incubating(since = "2020")
@FunctionalInterface
public interface Trigger {

  /**
   * Gets the current state of this trigger.
   *
   * @return true if this trigger is active, false if it is inactive
   */
  boolean get();

  /**
   * Negates a trigger.
   *
   * @param trigger the trigger to negate
   * @return a trigger that always returns the opposite of the one given
   * @see Trigger#not() Trigger.not()
   */
  static Trigger not(Trigger trigger) {
    return trigger.not();
  }

  /**
   * Returns a trigger that always returns the opposite of this one.
   */
  default Trigger not() {
    return () -> !get();
  }

  /**
   * Creates a trigger that is only active while both this trigger and another are active
   * simultaneously.
   *
   * @param other the other trigger
   */
  default Trigger and(Trigger other) {
    return () -> this.get() && other.get();
  }

  /**
   * Creates a trigger that is only active while either this trigger or another is active.
   *
   * @param other the other trigger
   */
  default Trigger or(Trigger other) {
    return () -> this.get() || other.get();
  }

  /**
   * Creates a trigger that is only active when either this trigger or another, but not both,
   * is active.
   *
   * @param other the other trigger
   */
  default Trigger xor(Trigger other) {
    return () -> this.get() ^ other.get();
  }

  /**
   * Runs a command as long as this trigger is active. The command will be cancelled when this
   * trigger is deactivated. If the command completes naturally before then, it will be restarted.
   *
   * @param command the command to bind
   */
  default void whileActive(Command command) {
    Scheduler.getGlobalScheduler()
             .addTrigger(this, Scheduler.TriggerBindingType.kActive, command);
  }

  /**
   * Starts a command when this trigger becomes active ("rising edge"). The command will then run
   * to its natural completion, regardless of whether or not this trigger is deactivated in the
   * meanwhile.
   *
   * @param command the command to bind
   */
  default void whenActivated(Command command) {
    Scheduler.getGlobalScheduler()
             .addTrigger(this, Scheduler.TriggerBindingType.kRisingEdge, command);
  }

  /**
   * Starts a command when this trigger becomes inactive ("falling edge"). The command will then
   * run to its natural completion.
   *
   * @param command the command to bind
   */
  default void whenDeactivated(Command command) {
    Scheduler.getGlobalScheduler()
             .addTrigger(this, Scheduler.TriggerBindingType.kFallingEdge, command);
  }

  /**
   * Runs a command as long as this trigger is inactive. The command will be cancelled when this
   * trigger is activated. If the command completes naturally before then, it will be restarted.
   *
   * @param command the command to bind
   */
  default void whileInactive(Command command) {
    Scheduler.getGlobalScheduler()
             .addTrigger(this, Scheduler.TriggerBindingType.kInactive, command);
  }

}
