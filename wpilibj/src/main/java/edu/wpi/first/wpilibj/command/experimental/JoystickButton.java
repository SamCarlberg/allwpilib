package edu.wpi.first.wpilibj.command.experimental;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.annotation.Incubating;

/**
 * A trigger that is active when a button on a joystick is pressed, and is inactive when that
 * button is released.
 */
@Incubating(since = "2020")
public class JoystickButton implements Button {

  private final GenericHID m_joystick;
  private final int m_buttonIndex;

  /**
   * Creates a new joystick button.
   *
   * @param joystick    the joystick or controller to get the button for
   * @param buttonIndex the index of the button on the controller.
   *                    Note that button indexes start at 1
   */
  public JoystickButton(GenericHID joystick, int buttonIndex) {
    m_joystick = joystick;
    m_buttonIndex = buttonIndex;
  }

  @Override
  public boolean get() {
    return m_joystick.getRawButton(m_buttonIndex);
  }
}
