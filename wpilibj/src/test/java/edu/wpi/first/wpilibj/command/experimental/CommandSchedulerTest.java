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

class CommandSchedulerTest {
  private CommandScheduler m_scheduler;

  @BeforeEach
  void setup() {
    m_scheduler = new CommandScheduler();
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
