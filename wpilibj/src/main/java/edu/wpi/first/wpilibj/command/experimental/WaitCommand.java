package edu.wpi.first.wpilibj.command.experimental;

import java.util.concurrent.TimeUnit;

import edu.wpi.first.wpilibj.Timer;

/**
 * Waits until a certain time period has elapsed.
 */
public class WaitCommand extends CommandBase {

  private final Timer m_timer = new Timer();
  private final double m_period; // seconds

  public WaitCommand(long timeout, TimeUnit unit) {
    m_period = unit.toMicros(timeout) / 1e6;
  }

  @Override
  public void initialize() {
    m_timer.reset();
    m_timer.start();
  }

  @Override
  public void execute() {
    // NOP
  }

  @Override
  public void end() {
    // NOP
  }

  @Override
  public boolean isFinished() {
    return m_timer.hasPeriodPassed(m_period);
  }
}
