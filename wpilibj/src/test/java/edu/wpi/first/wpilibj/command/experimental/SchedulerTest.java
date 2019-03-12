package edu.wpi.first.wpilibj.command.experimental;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    trigger.whenActivated(rising);
    trigger.whileActive(active);
    trigger.whenDeactivated(falling);

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
    }

    // Final run should complete the falling edge command and remove it from the scheduler
    m_scheduler.run();
    assertFalse(m_scheduler.isScheduled(rising));
    assertFalse(m_scheduler.isScheduled(active));
    assertFalse(m_scheduler.isScheduled(falling));
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
    CountingCommand a = new CountingCommand(5);
    CountingCommand b = new CountingCommand(5);
    a.requires(subsystem);
    b.requires(subsystem);

    m_scheduler.add(a);
    assertTrue(m_scheduler.isScheduled(a), "Adding a command should schedule it");

    m_scheduler.add(b);
    assertFalse(m_scheduler.isScheduled(a),
        "Adding a command with the same requirements should remove previous command");
    assertTrue(m_scheduler.isScheduled(b), "The new command should have been scheduled");
    assertEquals(0, a.getEndCount(), "Uninitialized command should not have end() called");

    m_scheduler.run();
    assertTrue(m_scheduler.isRunning(b), "New command should be running");
    assertEquals(1, b.getExecCount(), "New command should have executed once");
  }

}
