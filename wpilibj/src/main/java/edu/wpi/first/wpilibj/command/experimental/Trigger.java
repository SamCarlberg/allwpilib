package edu.wpi.first.wpilibj.command.experimental;

import edu.wpi.first.wpilibj.annotation.Incubating;

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
   */
  default void whileActive(Command command) {
    Scheduler.getGlobalScheduler().addTrigger(this, Scheduler.TriggerBindingType.kActive, command);
  }

  /**
   * Starts a command when this trigger becomes active ("rising edge"). The command will then run
   * to its natural completion, regardless of whether or not this trigger is deactivated in the
   * meanwhile.
   */
  default void whenActivated(Command command) {
    Scheduler.getGlobalScheduler().addTrigger(this, Scheduler.TriggerBindingType.kRisingEdge, command);
  }

  /**
   * Starts a command when this trigger becomes inactive ("falling edge"). The command will then
   * run to its natural completion.
   */
  default void whenDeactivated(Command command) {
    Scheduler.getGlobalScheduler().addTrigger(this, Scheduler.TriggerBindingType.kFallingEdge, command);
  }

}
