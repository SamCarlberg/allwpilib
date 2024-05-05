package edu.wpi.first.epilogue.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.first.epilogue.Epilogue;
import java.util.List;
import org.junit.jupiter.api.Test;

class ClassSpecificLoggerTest {
  @Epilogue
  record Point2d(double x, double y, int dim) {

    static class Logger extends ClassSpecificLogger<Point2d> {
      Logger() {
        super(Point2d.class);
      }

      @Override
      protected void update(DataLogger dataLogger, Point2d object) {
        dataLogger.log("x", object.x);
        dataLogger.log("y", object.y);
        dataLogger.log("dim", object.dim);
      }
    }
  }

  @Test
  void testReadPrivate() {
    var point = new Point2d(1, 4, 2);
    var logger = new Point2d.Logger();
    var dataLog = new TestLogger();
    logger.update(dataLog.getSubLogger("Point"), point);

    assertEquals(
        List.of(
            new TestLogger.LogEntry<>("Point/x", 1.0),
            new TestLogger.LogEntry<>("Point/y", 4.0),
            new TestLogger.LogEntry<>("Point/dim", 2)),
        dataLog.getEntries());
  }
}
