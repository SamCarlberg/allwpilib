/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.command.experimental;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TriggerSchedulerTest {
  private CommandScheduler m_commandScheduler;
  private TriggerScheduler m_triggerScheduler;

  @BeforeEach
  void setup() {
    m_commandScheduler = CommandScheduler.getGlobalCommandScheduler();
    m_triggerScheduler = TriggerScheduler.getGlobalTriggerScheduler();
  }

  @AfterEach
  void tearDown() {
    CommandScheduler.getGlobalCommandScheduler().removeAll();
    TriggerScheduler.getGlobalTriggerScheduler().unbindAll();
  }

  private void runSchedulers() {
    m_triggerScheduler.run();
    m_commandScheduler.run();
  }

  @Test
  void testTriggers() {
    InjectedTrigger trigger = new InjectedTrigger();
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
      runSchedulers();
      assertEquals(i, rising.getExecCount());
      assertEquals(i, active.getExecCount());
      assertEquals(0, falling.getExecCount());
    }

    assertFalse(m_commandScheduler.isScheduled(rising));
    assertTrue(m_commandScheduler.isScheduled(active));
    assertFalse(m_commandScheduler.isScheduled(falling));

    // Rising edge should be complete, whileActive should run
    runSchedulers();
    assertEquals(3, rising.getExecCount());
    assertFalse(m_commandScheduler.isScheduled(rising));
    assertEquals(4, active.getExecCount());
    assertFalse(m_commandScheduler.isScheduled(falling));

    // Falling edge
    trigger.set(false);

    // Falling edge command should run to completion; the other commands should not run
    for (int i = 1; i <= 3; i++) {
      runSchedulers();
      assertFalse(m_commandScheduler.isScheduled(rising));
      assertFalse(m_commandScheduler.isScheduled(active));
      assertEquals(i, falling.getExecCount());
      assertEquals(i, inactive.getExecCount());
    }

    // Final run should complete the falling edge command and remove it from the scheduler
    runSchedulers();
    assertFalse(m_commandScheduler.isScheduled(rising));
    assertFalse(m_commandScheduler.isScheduled(active));
    assertFalse(m_commandScheduler.isScheduled(falling));
    assertTrue(m_commandScheduler.isScheduled(inactive));

    // Run once more to run the inactive command to completion
    runSchedulers();
    assertEquals(5, inactive.getExecCount());
    assertEquals(1, inactive.getEndCount());
    assertFalse(m_commandScheduler.isScheduled(inactive));
    assertFalse(m_commandScheduler.hasRunningCommands());
  }

  @Test
  void testTriggerRestartsWhileActive() {
    InjectedTrigger trigger = new InjectedTrigger();
    CountingCommand command = new CountingCommand(1);
    trigger.whileActive(command);
    trigger.set(true);

    // Initial state: should not have run
    assertEquals(0, command.getInitCount());
    assertEquals(0, command.getExecCount());

    // Run the command - this will init, exec, and end all in one pass
    runSchedulers();
    assertEquals(1, command.getInitCount());
    assertEquals(1, command.getExecCount());

    // Command should be restarted and reinitialized
    runSchedulers();
    assertEquals(2, command.getInitCount(), "Command should have been restarted");
    assertEquals(1, command.getExecCount());

    // Disabling the trigger should cause the command to be removed from the scheduler
    trigger.set(false);
    runSchedulers();
    assertFalse(m_commandScheduler.isScheduled(command),
        "Bound command should no longer be scheduled");
    assertEquals(2, command.getInitCount());
    assertEquals(1, command.getExecCount());
  }
}
