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
    m_scheduler.addTrigger(this, Scheduler.TriggerBindingType.kActive, command);
  }

  @Override
  public void whenActivated(Command command) {
    m_scheduler.addTrigger(this, Scheduler.TriggerBindingType.kRisingEdge, command);
  }

  @Override
  public void whenDeactivated(Command command) {
    m_scheduler.addTrigger(this, Scheduler.TriggerBindingType.kFallingEdge, command);
  }

  @Override
  public void whileInactive(Command command) {
    m_scheduler.addTrigger(this, Scheduler.TriggerBindingType.kInactive, command);
  }
}
