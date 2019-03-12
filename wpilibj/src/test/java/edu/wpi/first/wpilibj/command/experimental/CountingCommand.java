package edu.wpi.first.wpilibj.command.experimental;

import java.util.Collections;
import java.util.Set;

/**
 * A basic command that keeps track of how many times it has been initialized and executed. The
 * command allows a certain number of calls to execute() before it completes.
 */
final class CountingCommand extends CommandBase {

  private int m_count = 0;
  private int m_initCount = 0;
  private int m_endCount = 0;
  private final int m_maxCount;

  CountingCommand(int maxCount) {
    this.m_maxCount = maxCount;
  }

  /**
   * Gets the number of times this command object has been initialized with {@link #initialize()}.
   */
  int getInitCount() {
    return m_initCount;
  }

  /**
   * Gets the number of times {@link #execute()} has been called since the last initialization.
   */
  int getExecCount() {
    return m_count;
  }

  int getEndCount() {
    return m_endCount;
  }

  @Override
  public void initialize() {
    m_count = 0;
    m_initCount++;
  }

  @Override
  public void execute() {
    m_count++;
  }

  @Override
  public void end() {
    m_endCount++;
  }

  @Override
  public boolean isFinished() {
    return m_count >= m_maxCount;
  }
}
