/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.command.experimental;

public class InjectedSchedulerTrigger implements Trigger {
  private final Scheduler m_scheduler;
  private boolean m_active = false; // NOPMD redundant field initializer

  public InjectedSchedulerTrigger(Scheduler scheduler) {
    m_scheduler = scheduler;
  }

  @Override
  public boolean get() {
    return m_active;
  }

  public void set(boolean active) {
    m_active = active;
  }

  @Override
  public void whileActive(Command command) {
    m_scheduler.addTrigger(new TriggerBinding.WhileActive(this, command));
  }

  @Override
  public void whenActivated(Command command) {
    m_scheduler.addTrigger(new TriggerBinding.RisingEdge(this, command));
  }

  @Override
  public void whenDeactivated(Command command) {
    m_scheduler.addTrigger(new TriggerBinding.FallingEdge(this, command));
  }

  @Override
  public void whileInactive(Command command) {
    m_scheduler.addTrigger(new TriggerBinding.WhileInactive(this, command));
  }
}
