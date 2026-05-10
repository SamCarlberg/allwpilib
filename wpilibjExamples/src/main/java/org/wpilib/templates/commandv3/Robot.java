// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.templates.commandv3;

import org.wpilib.command3.Scheduler;
import org.wpilib.command3.button.CommandNiDsPS5Controller;
import org.wpilib.framework.OpModeRobot;
import org.wpilib.templates.commandv3.constants.DriverConstants;
import org.wpilib.templates.commandv3.mechanisms.ExampleMechanism;

public class Robot extends OpModeRobot {
  final ExampleMechanism exampleMechanism = new ExampleMechanism();
  final CommandNiDsPS5Controller exampleController =
      new CommandNiDsPS5Controller(DriverConstants.DRIVER_CONTROLLER_PORT);

  public Robot() {}

  @Override
  public void robotPeriodic() {
    Scheduler.getDefault().run();
  }
}
