/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.command.experimental;

public class InjectedTrigger implements Trigger {
  private boolean m_active = false; // NOPMD redundant field initializer

  @Override
  public boolean get() {
    return m_active;
  }

  public void set(boolean active) {
    m_active = active;
  }
}
