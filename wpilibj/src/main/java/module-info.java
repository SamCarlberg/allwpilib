module wpilib.wpilibj {
  requires ejml.core;
  requires ejml.simple;
  requires transitive wpilib.math;
  requires transitive wpilib.units;
  requires transitive wpilib.util;

  exports edu.wpi.first.wpilibj;
  exports edu.wpi.first.wpilibj.counter;
  exports edu.wpi.first.wpilibj.drive;
  exports edu.wpi.first.wpilibj.event;
  exports edu.wpi.first.wpilibj.internal;
  exports edu.wpi.first.wpilibj.livewindow;
  exports edu.wpi.first.wpilibj.motorcontrol;
  exports edu.wpi.first.wpilibj.shuffleboard;
  exports edu.wpi.first.wpilibj.simulation;
  exports edu.wpi.first.wpilibj.smartdashboard;
  exports edu.wpi.first.wpilibj.sysid;
  exports edu.wpi.first.wpilibj.util;
}