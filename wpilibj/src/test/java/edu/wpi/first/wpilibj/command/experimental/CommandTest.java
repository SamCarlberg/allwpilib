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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandTest {

  @BeforeEach
  @AfterEach
  void resetScheduler() {
    CommandScheduler.getGlobalCommandScheduler().removeAll();
  }

  @Test
  void testStartAndCancel() {
    CommandScheduler scheduler = CommandScheduler.getGlobalCommandScheduler();
    Command command = new CountingCommand(5);

    command.start();
    assertTrue(scheduler.isScheduled(command), "Command not scheduled after start()");

    // Command execution is tested in CommandSchedulerTest, no point in repeating it here

    command.cancel();
    assertFalse(scheduler.isScheduled(command), "Command still scheduled after cancel()");
  }

}
