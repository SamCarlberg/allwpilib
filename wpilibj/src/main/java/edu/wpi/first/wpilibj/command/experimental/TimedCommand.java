/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.command.experimental;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.annotation.Incubating;

/**
 * A command that runs for a certain amount of time. Subclasses may override the
 * {@link #isFinished() isFinished()} behavior, but must use {@link #isTimedOut()}
 * alongside another check to ensure a maximum bound on the time the command takes to execute.
 */
@Incubating(since = "2020")
public abstract class TimedCommand extends CommandBase {
  private final double m_timeout;
  private final Timer m_timer = new Timer();

  /**
   * Creates a new timed command.
   *
   * <p>For example, {@code new TimedCommand(5, MILLISECONDS)}</p> will create a command that
   * takes 5 milliseconds to complete.
   *
   * @param timeout how long the command should take to run
   * @param unit    the time unit for the timeout
   */
  public TimedCommand(double timeout, TimeUnit unit) {
    m_timeout = unit.toSeconds(timeout);
  }

  public TimedCommand(String name, double timeout, TimeUnit unit) {
    super(name);
    m_timeout = unit.toSeconds(timeout);
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
