package edu.wpi.first.wpilibj.command.experimental;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.annotation.Incubating;

/**
 * A command that runs for a certain amount of time. Timeout is specified in seconds. Subclasses
 * may override the {@link #isFinished() isFinished()} behavior, but must use {@link #isTimedOut()}
 * alongside another check to ensure a maximum bound on the time the command takes to execute.
 */
@Incubating(since = "2020")
public abstract class TimedCommand extends CommandBase {

  private final double m_timeout;
  private final Timer m_timer;

  /**
   * Creates a new timed command.
   *
   * @param timeout how long the command should take to run, in seconds
   */
  public TimedCommand(double timeout) {
    m_timeout = timeout;
    m_timer = new Timer();
  }

  @Override
  public void initialize() {
    m_timer.reset();
    m_timer.start();
  }

  @Override
  public boolean isFinished() {
    return isTimedOut();
  }

  /**
   * Checks if this command has run for the length of time specified in the constructor.
   */
  public boolean isTimedOut() {
    return m_timer.hasPeriodPassed(m_timeout);
  }
}
