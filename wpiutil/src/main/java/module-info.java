@SuppressWarnings("requires-transitive-automatic")
module wpilib.util {
  requires com.fasterxml.jackson.databind;
  requires transitive us.hebi.quickbuf.runtime;

  exports edu.wpi.first.util;
  exports edu.wpi.first.util.cleanup;
  exports edu.wpi.first.util.concurrent;
  exports edu.wpi.first.util.datalog;
  exports edu.wpi.first.util.function;
  exports edu.wpi.first.util.protobuf;
  exports edu.wpi.first.util.sendable;
  exports edu.wpi.first.util.struct;
}
