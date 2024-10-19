module wpilib.apriltag {
  requires opencv.java;
  requires wpilib.math;
  requires wpilib.util;

  exports edu.wpi.first.apriltag;
  exports edu.wpi.first.apriltag.jni;
}
