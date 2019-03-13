package edu.wpi.first.wpilibj.command.experimental;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.annotation.Incubating;

@Incubating(since = "2020")
public class JoystickButton implements Trigger {

  private final GenericHID m_joystick;
  private final int m_buttonNum;

  public JoystickButton(GenericHID joystick, int buttonNumber) {
    m_joystick = joystick;
    m_buttonNum = buttonNumber;
  }

  @Override
  public boolean get() {
    return m_joystick.getRawButton(m_buttonNum);
  }
}
