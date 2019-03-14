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
 * Binds a {@link Command} to a {@link Trigger}. Subclasses are responsible for determining when
 * the bound command should start and stop based on the state of the trigger.
 */
@Incubating(since = "2020")
public abstract class TriggerBinding {
  private final Trigger m_trigger;
  private final Command m_command;
  private boolean m_wasActive = false; // NOPMD redundant field initializer
  private boolean m_isActive = false; // NOPMD redundant field initializer

  /**
   * Creates a new trigger binding.
   *
   * @param trigger the trigger to bind to
   * @param command the command to bind
   */
  protected TriggerBinding(Trigger trigger, Command command) {
    m_trigger = Objects.requireNonNull(trigger, "Trigger cannot be null");
    m_command = Objects.requireNonNull(command, "Command cannot be null");
  }

  public final Trigger getTrigger() {
    return m_trigger;
  }

  /**
   * Gets the bound command.
   */
  public final Command getCommand() {
    return m_command;
  }

  /**
   * Updates the state of the binding.
   */
  public final void update() {
    m_wasActive = m_isActive;
    m_isActive = m_trigger.get();
  }

  /**
   * Checks if the trigger was active prior to the most recent update.
   *
   * @return true if the trigger was just active
   */
  protected final boolean wasTriggerActive() {
    return m_wasActive;
  }

  /**
   * Checks if the trigger is currently active after the most recent update.
   *
   * @return true if the trigger is currently active
   */
  protected final boolean isTriggerActive() {
    return m_isActive;
  }

  /**
   * Determines if the command bound to the trigger should start running.
   *
   * @return true if the bound command should start running
   */
  public abstract boolean shouldStart();

  /**
   * Determines if the command bound to the trigger should be cancelled.
   *
   * @return true if the bound command should be cancelled
   */
  public abstract boolean shouldCancel();

  /**
   * A binding type that starts a command when the trigger becomes active and lets the command
   * run to its natural completion.
   */
  public static final class RisingEdge extends TriggerBinding {
    public RisingEdge(Trigger trigger, Command command) {
      super(trigger, command);
    }

    @Override
    public boolean shouldStart() {
      return !wasTriggerActive() && isTriggerActive();
    }

    @Override
    public boolean shouldCancel() {
      return false;
    }
  }

  /**
   * A binding type that starts a command when the trigger becomes inactive and lets the command
   * run to its natural completion.
   */
  public static final class FallingEdge extends TriggerBinding {
    public FallingEdge(Trigger trigger, Command command) {
      super(trigger, command);
    }

    @Override
    public boolean shouldStart() {
      return wasTriggerActive() && !isTriggerActive();
    }

    @Override
    public boolean shouldCancel() {
      return false;
    }
  }

  /**
   * A binding type that continuously runs a command as long as the trigger is active. The command
   * is cancelled once the trigger becomes inactive. If the command naturally completes before then,
   * it will be restarted and run again.
   */
  public static final class WhileActive extends TriggerBinding {
    public WhileActive(Trigger trigger, Command command) {
      super(trigger, command);
    }

    @Override
    public boolean shouldStart() {
      return isTriggerActive();
    }

    @Override
    public boolean shouldCancel() {
      return !isTriggerActive();
    }
  }

  /**
   * A binding type that continuously runs a command as long as the trigger is inactive. The command
   * is cancelled once the trigger becomes active. If the command naturally completes before then,
   * it will be restarted and run again.
   */
  public static final class WhileInactive extends TriggerBinding {
    public WhileInactive(Trigger trigger, Command command) {
      super(trigger, command);
    }

    @Override
    public boolean shouldStart() {
      return !isTriggerActive();
    }

    @Override
    public boolean shouldCancel() {
      return isTriggerActive();
    }
  }

}
