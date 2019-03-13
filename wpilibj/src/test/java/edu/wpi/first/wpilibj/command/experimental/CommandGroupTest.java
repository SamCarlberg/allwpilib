package edu.wpi.first.wpilibj.command.experimental;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandGroupTest {

  @Test
  void testMultipleBlocks() {
    final int maxCountBlock1 = 6;
    final int maxCountBlock2 = 4;

    CountingCommand b0c0 = new CountingCommand(maxCountBlock1);
    CountingCommand b0c1 = new CountingCommand(1);
    CountingCommand b1c0 = new CountingCommand(maxCountBlock2);
    CountingCommand b1c1 = new CountingCommand(maxCountBlock2 - 1);

    CommandGroup group = new CommandGroup();
    // First block: should take 6 iterations to complete
    group.addBlock(b0c0, b0c1);
    // Second block: should take 4 iterations to complete
    group.addBlock(b1c0, b1c1);

    group.initialize();
    int execCount = 0;

    // Run the first block to completion, but this should not start execution of the second
    while (execCount < maxCountBlock1) {
      group.execute();
      execCount++;
    }
    assertAll(
        () -> assertEquals(maxCountBlock1, b0c0.getExecCount(),
            "The first block should have run to completion"),
        () -> assertEquals(1, b0c1.getExecCount()),
        () -> assertEquals(0, b1c0.getExecCount(),
            "The second block should not have started execution"),
        () -> assertEquals(0, b1c1.getExecCount(),
            "The second block should not have started execution")
    );

    // Finish executing the group
    while (!group.isFinished()) {
      group.execute();
      execCount++;
    }
    group.end();

    assertEquals(maxCountBlock1 + maxCountBlock2, execCount,
        "Wrong number of group executions");

    assertAll("All blocks should have run",
        () -> assertEquals(maxCountBlock1, b0c0.getExecCount()),
        () -> assertEquals(1, b0c1.getExecCount()),
        () -> assertEquals(maxCountBlock2, b1c0.getExecCount()),
        () -> assertEquals(maxCountBlock2 - 1, b1c1.getExecCount())
    );

  }

}
