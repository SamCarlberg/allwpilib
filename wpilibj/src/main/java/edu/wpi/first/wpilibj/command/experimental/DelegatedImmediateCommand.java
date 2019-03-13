package edu.wpi.first.wpilibj.command.experimental;

import java.util.Objects;

/**
 * An {@link ImmediateCommand} that delegates its operation to an external {@link Runnable}.
 */
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
