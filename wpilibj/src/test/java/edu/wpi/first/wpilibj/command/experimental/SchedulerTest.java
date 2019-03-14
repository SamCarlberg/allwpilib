/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.command.experimental;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchedulerTest {
  private Scheduler m_scheduler;

  @BeforeEach
  void setup() {
    m_scheduler = new Scheduler();
  }

  @Test
  void simpleTest() {
    int maxCount = 1;
    CountingCommand command = new CountingCommand(maxCount);
    m_scheduler.add(command);

    // Nothing should be running after adding the command
    assertEquals(0, command.getInitCount());
    assertFalse(m_scheduler.hasRunningCommands());

    m_scheduler.run();

    assertEquals(1, command.getInitCount());
    assertEquals(1, command.getExecCount(), "execute() should only have been called once");
    assertFalse(m_scheduler.hasRunningCommands(), "Completed commands should have been removed");

    // Run again. This should cause the scheduler to note that the command is finished and remove it
    m_scheduler.run();
    assertFalse(m_scheduler.hasRunningCommands(), "The command should no longer be scheduled");
  }

  @Test
  void testTriggers() {
    InjectedSchedulerTrigger trigger = new InjectedSchedulerTrigger(m_scheduler);
    CountingCommand rising = new CountingCommand(3);
    CountingCommand active = new CountingCommand(1000);
    CountingCommand falling = new CountingCommand(3);
    CountingCommand inactive = new CountingCommand(5);

    trigger.whenActivated(rising);
    trigger.whileActive(active);
    trigger.whenDeactivated(falling);
    trigger.whileInactive(inactive);

    trigger.set(true);

    // Rising edge and whileActive bindings
    for (int i = 1; i <= 3; i++) {
      m_scheduler.run();
      assertEquals(i, rising.getExecCount());
      assertEquals(i, active.getExecCount());
      assertEquals(0, falling.getExecCount());
    }

    assertFalse(m_scheduler.isScheduled(rising));
    assertTrue(m_scheduler.isScheduled(active));
    assertFalse(m_scheduler.isScheduled(falling));

    // Rising edge should be complete, whileActive should run
    m_scheduler.run();
    assertEquals(3, rising.getExecCount());
    assertFalse(m_scheduler.isScheduled(rising));
    assertEquals(4, active.getExecCount());
    assertFalse(m_scheduler.isScheduled(falling));

    // Falling edge
    trigger.set(false);

    // Falling edge command should run to completion; the other commands should not run
    for (int i = 1; i <= 3; i++) {
      m_scheduler.run();
      assertFalse(m_scheduler.isScheduled(rising));
      assertFalse(m_scheduler.isScheduled(active));
      assertEquals(i, falling.getExecCount());
      assertEquals(i, inactive.getExecCount());
    }

    // Final run should complete the falling edge command and remove it from the scheduler
    m_scheduler.run();
    assertFalse(m_scheduler.isScheduled(rising));
    assertFalse(m_scheduler.isScheduled(active));
    assertFalse(m_scheduler.isScheduled(falling));
    assertTrue(m_scheduler.isScheduled(inactive));

    // Run once more to run the inactive command to completion
    m_scheduler.run();
    assertEquals(5, inactive.getExecCount());
    assertEquals(1, inactive.getEndCount());
    assertFalse(m_scheduler.isScheduled(inactive));
    assertFalse(m_scheduler.hasRunningCommands());
  }

  @Test
  void testTriggerRestartsWhileActive() {
    InjectedSchedulerTrigger trigger = new InjectedSchedulerTrigger(m_scheduler);
    CountingCommand command = new CountingCommand(1);
    trigger.whileActive(command);
    trigger.set(true);

    // Initial state: should not have run
    assertEquals(0, command.getInitCount());
    assertEquals(0, command.getExecCount());

    // Run the command - this will init, exec, and end all in one pass
    m_scheduler.run();
    assertEquals(1, command.getInitCount());
    assertEquals(1, command.getExecCount());

    // Command should be restarted and reinitialized
    m_scheduler.run();
    assertEquals(2, command.getInitCount(), "Command should have been restarted");
    assertEquals(1, command.getExecCount());

    // Disabling the trigger should cause the command to be removed from the scheduler
    trigger.set(false);
    m_scheduler.run();
    assertFalse(m_scheduler.isScheduled(command), "Bound command should no longer be scheduled");
    assertEquals(2, command.getInitCount());
    assertEquals(1, command.getExecCount());
  }

  @Test
  void testSubsystemInterrupts() {
    Subsystem subsystem = new MockSubsystem();
    subsystem.disableSafety();
    CountingCommand first = new CountingCommand(5);
    CountingCommand second = new CountingCommand(5);
    first.requires(subsystem);
    second.requires(subsystem);

    m_scheduler.add(first);
    assertTrue(m_scheduler.isScheduled(first), "Adding a command should schedule it");

    m_scheduler.add(second);
    assertFalse(m_scheduler.isScheduled(first),
        "Adding a command with the same requirements should remove previous command");
    assertTrue(m_scheduler.isScheduled(second), "The new command should have been scheduled");
    assertEquals(0, first.getEndCount(), "Uninitialized command should not have end() called");

    m_scheduler.run();
    assertTrue(m_scheduler.isRunning(second), "New command should be running");
    assertEquals(1, second.getExecCount(), "New command should have executed once");
  }

  @Test
  void testDefaultCommands() {
    CountingCommand defaultCommand = new CountingCommand(2);
    Subsystem subsystem = new Subsystem(false) {
      @Override
      protected Command createDefaultCommand() {
        defaultCommand.requires(this);
        return defaultCommand;
      }
    };
    subsystem.disableSafety();
    CountingCommand newCommand = new CountingCommand(10);
    newCommand.requires(subsystem);

    // Run with just the default command
    m_scheduler.add(subsystem);
    m_scheduler.run();
    assertEquals(1, defaultCommand.getExecCount(), "Default command should have run");

    // Add a new command that overrides the default command
    m_scheduler.add(newCommand);
    m_scheduler.run();
    assertEquals(1, defaultCommand.getEndCount(), "Default command should not have run");
    assertEquals(1, newCommand.getExecCount(), "New command should have run");

    // Remove the new command. The default command should be reinitialized and run again
    m_scheduler.remove(newCommand);
    m_scheduler.run();
    assertEquals(2, defaultCommand.getInitCount(), "Default command should have been restarted");
    assertEquals(1, defaultCommand.getExecCount(), "Default command should have run");

    // Run the default command to its completion
    m_scheduler.run();
    assertEquals(2, defaultCommand.getExecCount(), "Default command should have run");
    assertEquals(2, defaultCommand.getEndCount(), "Default command should have run to completion");

    // Run again - this should restart the default command from the beginning
    m_scheduler.run();
    assertEquals(3, defaultCommand.getInitCount(), "Default command should have been restarted");
    assertEquals(1, defaultCommand.getExecCount(), "Default command should have run");
  }

  @Test
  void testAddSubsystemWithDefaultCommandNotRequiringIt() {
    Subsystem subsystem = new Subsystem(false) {
      @Override
      protected Command createDefaultCommand() {
        return new CountingCommand(0);
      }
    };
    assertThrows(IllegalStateException.class, () -> m_scheduler.add(subsystem));
  }

  @Test
  void testRemoveAll() {
    Command first = new CountingCommand(5);
    Command second = new CountingCommand(5);
    m_scheduler.add(first);
    m_scheduler.add(second);

    m_scheduler.run();
    assertTrue(m_scheduler.hasRunningCommands(), "Commands should be running");

    m_scheduler.removeAll();
    assertFalse(m_scheduler.hasRunningCommands(), "No commands should be running");
    assertFalse(m_scheduler.isScheduled(first), "First command was not removed");
    assertFalse(m_scheduler.isScheduled(second), "Second command was not removed");
  }

  @Test
  void testUnsafeCommands() {
    Subsystem subsystem = new MockSubsystem();
    CountingCommand command = new CountingCommand(10);
    command.requires(subsystem);
    m_scheduler.add(subsystem);

    // Unsafe command should not be added
    m_scheduler.add(command);
    assertFalse(m_scheduler.isScheduled(command),
        "Command with unsafe requirements should not be scheduled");

    // Disabling safety should let the command be added and executed
    m_scheduler.setSafetyEnabled(false);
    m_scheduler.add(command);
    m_scheduler.run();
    assertEquals(1, command.getExecCount(), "Command should have been scheduled and executed");

    // Re-enabling safety should remove the unsafe command when run() is called
    m_scheduler.setSafetyEnabled(true);
    assertTrue(m_scheduler.isScheduled(command),
        "Unsafe command should remain scheduled until run() is called");
    m_scheduler.run();
    assertFalse(m_scheduler.isScheduled(command), "Command should not still be scheduled");
    assertEquals(1, command.getExecCount(), "Command should not have continued to run");
    assertEquals(1, command.getEndCount(), "Command should have terminated");
  }

}
