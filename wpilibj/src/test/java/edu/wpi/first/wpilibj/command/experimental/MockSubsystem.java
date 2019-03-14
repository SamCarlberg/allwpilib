package edu.wpi.first.wpilibj.command.experimental;

public class MockSubsystem extends Subsystem {
  public MockSubsystem() {
    super(false);
  }

  @Override
  public Command createDefaultCommand() {
    return null;
  }
}
