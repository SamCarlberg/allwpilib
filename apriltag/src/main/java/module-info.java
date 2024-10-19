module wpilib.apriltag {
  requires transitive wpilib.opencv;
  requires wpilib.math;
  requires wpilib.util;
  requires com.fasterxml.jackson.annotation;
  requires com.fasterxml.jackson.databind;

  exports edu.wpi.first.apriltag;
  exports edu.wpi.first.apriltag.jni;
}
