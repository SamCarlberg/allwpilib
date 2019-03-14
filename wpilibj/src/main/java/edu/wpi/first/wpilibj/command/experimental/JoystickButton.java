/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

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
