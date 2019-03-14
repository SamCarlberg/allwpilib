package edu.wpi.first.wpilibj.command.experimental;

import edu.wpi.first.wpilibj.annotation.Incubating;

/**
 * A type of trigger that is active while a physical button is pressed and inactive when that
 * button is not pressed. This interface provides several functions for binding commands
 * with method names that better convey the physical state of the button for the binding.
 */
@Incubating(since = "2020")
@FunctionalInterface
public interface Button extends Trigger {

  /**
   * Binds a command to run when the button is pressed by a driver.
   *
   * @param command the command to bind
   * @see #whenActivated(Command)
   */
  default void whenPressed(Command command) {
    whenActivated(command);
  }

  /**
   * Binds a command to continuously run as long as the button is held down. If the command
   * completes naturally before the button is released, it will be restarted. If the button
   * is released while the command is still running, the command will be cancelled and will
   * not run to completion.
   *
   * @param command the command to bind
   * @see #whileActive(Command)
   */
  default void whileHeld(Command command) {
    whileActive(command);
  }

  /**
   * Binds a command to run when the button is released by a driver.
   *
   * @param command the command to bind
   * @see #whenDeactivated(Command)
   */
  default void whenReleased(Command command) {
    whenDeactivated(command);
  }

  /**
   * Binds a command to continuously run as long as the button is not pressed. If the command
   * completes naturally before the button is pressed, it will be restarted. If the button
   * is pressed while the command is still running, the command will be cancelled and will
   * not run to completion.
   *
   * @param command the command to bind
   * @see #whileInactive(Command)
   */
  default void whileReleased(Command command) {
    whileInactive(command);
  }

}
