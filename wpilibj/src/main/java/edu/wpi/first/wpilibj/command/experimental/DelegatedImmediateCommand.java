/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.command.experimental;

import java.util.Objects;

import edu.wpi.first.wpilibj.annotation.Incubating;

/**
 * An {@link ImmediateCommand} that delegates its operation to an external {@link Runnable}.
 */
@Incubating(since = "2020")
public class DelegatedImmediateCommand extends ImmediateCommand {
  private final String m_name;
  private final Runnable m_func;

  /**
   * Creates a new delegated immediate command.
   *
   * @param name the name of the command
   * @param func the function to delegate to
   */
  public DelegatedImmediateCommand(String name, Runnable func) {
    m_name = Objects.requireNonNull(name, "Name cannot be null");
    m_func = Objects.requireNonNull(func, "Delegate function cannot be null");
  }

  /**
   * Creates a new delegated immediate command.
   *
   * @param func the function to delegate to
   */
  public DelegatedImmediateCommand(Runnable func) {
    this("DelegatedImmediateCommand[" + func + "]", func);
  }

  @Override
  protected void perform() {
    m_func.run();
  }

  @Override
  public String getName() {
    return m_name;
  }
}
