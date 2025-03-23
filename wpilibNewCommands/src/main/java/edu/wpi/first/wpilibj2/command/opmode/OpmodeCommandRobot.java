// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.wpilibj2.command.opmode;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import java.util.function.Consumer;

/**
 * Runs a command-based robot using {@link Opmode Opmodes}. Only one opmode is active at a time, and
 * the framework provides groups for autonomous, teleoperated, and test robot modes. When an opmode
 * starts, all pre-existing trigger bindings and default commands are wiped; only the bindings and
 * default commands belonging to the loaded opmode will be active. This allows for operator control
 * to be configured with clear delineations; for example, a test opmode may want controller bindings
 * for running SysID tests, while a teleop mode may want to use those same controller buttons to
 * perform scoring actions during a match.
 *
 * <p>Opmodes may be selected on a dashboard. When the corresponding robot mode begins, whatever
 * opmode is selected for that mode will be loaded. Changing the selection on the dashboard while
 * the robot is in the corresponding mode will immediately load the new opmode, overriding whatever
 * had been running at the time. This means that modes can be changed while the robot is running;
 * this is intended for use in a team workspace to make it easier to test different opmodes.</p>
 *
 * <pre><code>
 *   public class Robot extends OpmodeCommandRobot {
 *     public Robot() {
 *       // The default autonomous opmode, which will run if no selection is made
 *       defaultAutonomous(mode -> {
 *         elevator.setDefaultCommand(elevator.lower());
 *         mode.onStart(drive.driveStraight().withTimeout(5));
 *       });
 *
 *       // A fancy autonomous mode that needs to be selected on the dashboard in order to run
 *       autonomous("Fancy", mode -> {
 *         elevator.setDefaultCommand(elevator.holdPosition());
 *         mode.onStart(
 *           drive.driveToAprilTag(6)
 *             .andThen(elevator.raise()));
 *       });
 *
 *       // The default teleop opmode. We don't create any other opmdoes for teleop, so this will
 *       // always be loaded during teleop
 *       defaultTeleop(mode -> {
 *         // Default commands
 *         drive.setDefaultCommand(drive.driveWithJoysticks());
 *         elevator.setDefaultCommand(elevator.holdPosition());
 *
 *         // Controller bindings
 *         controller.x().whileTrue(elevator.lower());
 *         controller.y().whileTrue(elevator.raise());
 *       });
 *
 *       test("System Checks", mode -> {
 *         controller.leftBumper().whileTrue(drive.individualModuleChecks());
 *         controller.leftTrigger().whileTrue(drive.coordinatedModuleChecks());
 *         controller.x().whileTrue(elevator.setpointChecks());
 *       });
 *
 *       test("Tuning", mode -> {
 *         controller.x().whileTrue(elevator.runSysIdRoutine());
 *       });
 *     }
 *   }
 * </code></pre>
 */
public class OpmodeCommandRobot extends TimedRobot {
  private final OpmodeGroup m_autons = new OpmodeGroup();
  private final OpmodeGroup m_teleops = new OpmodeGroup();
  private final OpmodeGroup m_tests = new OpmodeGroup();
  private final OpmodeGroup m_disables = new OpmodeGroup();

  public OpmodeCommandRobot() {
    SmartDashboard.putData("Autonomous Modes", m_autons);
    SmartDashboard.putData("Teleoperated Modes", m_teleops);
    SmartDashboard.putData("Test Modes", m_tests);
    SmartDashboard.putData("Disabled Modes", m_disables);

    m_autons.onChange(m -> {
      if (isAutonomousEnabled()) {
        enterMode(m);
      }
    });

    m_teleops.onChange(m -> {
      if (isTeleopEnabled()) {
        enterMode(m);
      }
    });

    m_tests.onChange(m -> {
      if (isTestEnabled()) {
        enterMode(m);
      }
    });

    m_disables.onChange(m -> {
      if (isDisabled()) {
        enterMode(m);
      }
    });
  }

  /**
   * Enters the currently selected autonomous opmode, or the default opmode if no selection has been
   * made. If no opmode is selected <i>and</i> no default opmode has been set, then the autonomous
   * period will do nothing.
   */
  @Override
  public void autonomousInit() {
    Opmode mode = m_autons.getSelected();
    enterMode(mode);
    if (mode == null) {
      DriverStation.reportWarning(
          "No autonomous opmode was selected! A default autonomous mode can be set using `defaultAutonomous`",
          false);
    }
  }

  /**
   * Enters the currently selected teleop opmode, or the default opmode if no selection has been
   * made. If no opmode is selected <i>and</i> no default opmode has been set, then the teleop
   * period will do nothing.
   */
  @Override
  public void teleopInit() {
    Opmode mode = m_teleops.getSelected();
    enterMode(mode);
    if (mode == null) {
      DriverStation.reportWarning("No teleoperated opmode was selected! A default teleoperated mode can be set using `defaultTeleop`", false);
    }
  }

  /**
   * Enters the currently selected test opmode, or the default opmode if no selection has been
   * made. If no opmode is selected <i>and</i> no default opmode has been set, then the test
   * period will do nothing.
   */
  @Override
  public void testInit() {
    Opmode mode = m_tests.getSelected();
    enterMode(mode);
    if (mode == null) {
      DriverStation.reportWarning("No test opmode was selected! A default test mode can be set using `defaultTest`", false);
    }
  }

  /**
   * Enters the currently selected disabled opmode, or the default opmode if no selection has been
   * made. If no opmode is selected <i>and</i> no default opmode has been set, then the disabled
   * period will do nothing.
   */
  @Override
  public void disabledInit() {
    enterMode(m_disables.getSelected());
    // TODO: Should we warn here? No robot control is possible
  }

  /**
   * Resets all configured triggers and default commands. This method is called automatically when
   * an opmode changes to guarantee the new opmode won't have conflicts with any existing bindings
   * or default commands. Any running commands will also be canceled.
   */
  protected void resetConfigurations() {
    var scheduler = CommandScheduler.getInstance();
    // Cancel all running commands, clear all triggers and button bindings, and remove all default
    // commands.  In essence, wipe everything so we can start from a completely clean slate when
    scheduler.cancelAll();
    scheduler.getDefaultButtonLoop().clear();

    // Note: this only sets the default commands to null; the subsystems remain registered
    scheduler.getSubsystems().forEach(scheduler::removeDefaultCommand);
  }

  /**
   * Loads an opmode, overriding any existing bindings and default commands.
   *
   * @param mode The opmode to enter.
   */
  protected void enterMode(Opmode mode) {
    resetConfigurations();

    if (mode != null) {
      mode.runSetup();
    }
  }


  // Opmode factories

  /**
   * Creates a new autonomous opmode, or changes the configuration for an already existing
   * autonomous with the given name. The code in the initializer will be executed if the opmode is
   * selected when the autonomous period begins. Initializers should include all trigger bindings,
   * default command configurations, and routine scheduling, since all existing configurations are
   * deleted when the opmode starts.
   *
   * @param name The name of the opmode
   * @param initializer The opmode initializer
   * @return The autonomous opmode
   */
  protected Opmode autonomous(String name, Consumer<Opmode> initializer) {
    var opmode = new Opmode(m_autons, name, initializer);
    m_autons.addOption(name, opmode);
    return opmode;
  }

  /**
   * Creates an autonomous opmode to use if nothing was manually selected at the time the autonomous
   * period begins, or changes the configuration for an existing default mode if one was already
   * created.
   *
   * @param initializer The opmode initializer
   * @return The default autonomous opmode
   */
  protected Opmode defaultAutonomous(Consumer<Opmode> initializer) {
    String name = "Default Autonomous Mode";
    var opmode = new Opmode(m_autons, name, initializer);
    m_autons.setDefaultOption(name, opmode);
    return opmode;
  }

  protected Opmode teleoperated(String name, Consumer<Opmode> initializer) {
    var opmode = new Opmode(m_teleops, name, initializer);
    m_teleops.addOption(name, opmode);
    return opmode;
  }

  protected Opmode defaultTeleoperated(Consumer<Opmode> initializer) {
    String name = "Default Teleoperated Mode";
    var opmode = new Opmode(m_teleops, name, initializer);
    m_teleops.setDefaultOption(name, opmode);
    return opmode;
  }

  protected Opmode test(String name, Consumer<Opmode> initializer) {
    var opmode = new Opmode(m_tests, name, initializer);
    m_tests.addOption(name, opmode);
    return opmode;
  }

  protected Opmode defaultTest(Consumer<Opmode> initializer) {
    String name = "Default Test Mode";
    var opmode = new Opmode(m_tests, name, initializer);
    m_tests.setDefaultOption(name, opmode);
    return opmode;
  }

  protected Opmode disabled(String name, Consumer<Opmode> initializer) {
    var opmode = new Opmode(m_disables, name, initializer);
    m_disables.addOption(name, opmode);
    return opmode;
  }

  protected Opmode defaultDisabled(Consumer<Opmode> initializer) {
    String name = "Default Disabled Mode";
    var opmode = new Opmode(m_disables, name, initializer);
    m_disables.setDefaultOption(name, opmode);
    return opmode;
  }
}
