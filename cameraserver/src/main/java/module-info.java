module wpilib.cameraserver {
  requires transitive wpilib.opencv;
  requires wpilib.cscore;
  requires wpilib.ntcore;
  requires wpilib.util;

  exports edu.wpi.first.cameraserver;
  exports edu.wpi.first.vision;
}
