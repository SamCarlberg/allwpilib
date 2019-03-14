/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.command.experimental;

/**
 * A basic command that keeps track of how many times it has been initialized and executed. The
 * command allows a certain number of calls to execute() before it completes.
 */
@SuppressWarnings("PMD.RedundantFieldInitializer")
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

  /**
   * Gets the number of times this command object has had {@link #end()} called.
   */
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
