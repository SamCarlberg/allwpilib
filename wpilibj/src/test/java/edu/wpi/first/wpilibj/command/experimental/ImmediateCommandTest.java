package edu.wpi.first.wpilibj.command.experimental;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImmediateCommandTest {

  private Scheduler m_scheduler;

  @BeforeEach
  void setup() {
    m_scheduler = new Scheduler();
  }

  @Test
  void test() {
    int[] count = {0};
    ImmediateCommand command = new ImmediateCommand() {
      @Override
      protected void perform() {
        count[0]++;
      }
    };

    // Initial state - adding the command should not run it
    m_scheduler.add(command);
    assertTrue(m_scheduler.isScheduled(command));
    assertEquals(0, count[0]);

    // Running the scheduler should run the command, then remove it
    m_scheduler.run();
    assertEquals(1, count[0]);
    assertFalse(m_scheduler.isScheduled(command));
  }

}
