// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.commands3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import edu.wpi.first.wpilibj.RobotController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(5)
class SchedulerTest {
  private Scheduler scheduler;

  @BeforeEach
  void setup() {
    RobotController.setTimeSource(() -> System.nanoTime() / 1000L);
    scheduler = new Scheduler();
  }

  @Test
  void basic() {
    var enabled = new AtomicBoolean(false);
    var ran = new AtomicBoolean(false);
    var command =
        Command.noReqs(
                (coroutine) -> {
                  do {
                    coroutine.yield();
                  } while (!enabled.get());
                  ran.set(true);
                })
            .make("Basic Command");

    scheduler.schedule(command);
    scheduler.run();
    assertTrue(scheduler.isRunning(command), "Command should be running after being scheduled");

    enabled.set(true);
    scheduler.run();
    if (scheduler.isRunning(command)) {
      fail("Command should no longer be running after awaiting its completion");
    }

    assertTrue(ran.get());
  }

  @Test
  void higherPriorityCancels() {
    var subsystem = new RequireableResource("Subsystem", scheduler);

    var lower = new PriorityCommand(-1000, subsystem);
    var higher = new PriorityCommand(+1000, subsystem);

    scheduler.schedule(lower);
    scheduler.run();
    assertTrue(scheduler.isRunning(lower));

    scheduler.schedule(higher);
    scheduler.run();
    assertTrue(scheduler.isRunning(higher));
    assertFalse(scheduler.isRunning(lower));
  }

  @Test
  void lowerPriorityDoesNotCancel() {
    var subsystem = new RequireableResource("Subsystem", scheduler);

    var lower = new PriorityCommand(-1000, subsystem);
    var higher = new PriorityCommand(+1000, subsystem);

    scheduler.schedule(higher);
    scheduler.run();
    assertTrue(scheduler.isRunning(higher));

    scheduler.schedule(lower);
    scheduler.run();
    if (!scheduler.isRunning(higher)) {
      fail("Higher priority command should still be running");
    }
    if (scheduler.isRunning(lower)) {
      fail("Lower priority command should not be running");
    }
  }

  @Test
  void samePriorityCancels() {
    var subsystem = new RequireableResource("Subsystem", scheduler);

    var first = new PriorityCommand(512, subsystem);
    var second = new PriorityCommand(512, subsystem);

    scheduler.schedule(first);
    scheduler.run();
    assertTrue(scheduler.isRunning(first));

    scheduler.schedule(second);
    scheduler.run();
    assertTrue(scheduler.isRunning(second), "New command should be running");
    assertFalse(scheduler.isRunning(first), "Old command should be canceled");
  }

  @Test
  void atomicity() {
    var resource =
        new RequireableResource("X", scheduler) {
          int x = 0;
        };

    // Launch 100 commands that each call `x++` 500 times.
    // If commands run on different threads, the lack of atomic
    // operations or locks will mean the final number will be
    // less than the expected 50,000
    int numCommands = 100;
    int iterations = 500;

    for (int cmdCount = 0; cmdCount < numCommands; cmdCount++) {
      var command =
          Command.noReqs(
                  (coroutine) -> {
                    for (int i = 0; i < iterations; i++) {
                      coroutine.yield();
                      resource.x++;
                    }
                  })
              .make("CountCommand[" + cmdCount + "]");

      scheduler.schedule(command);
    }

    for (int i = 0; i < iterations; i++) {
      scheduler.run();
    }
    scheduler.run();

    assertEquals(numCommands * iterations, resource.x);
  }

  @Test
  void errorDetection() {
    var resource = new RequireableResource("X", scheduler);

    var command =
        resource
            .run(
                (coroutine) -> {
                  throw new RuntimeException("The exception");
                })
            .make("Bad Behavior");

    scheduler.schedule(command);

    try {

      scheduler.run();
      fail("An exception should have been thrown");
    } catch (RuntimeException e) {
      assertEquals("The exception", e.getMessage());
    } catch (Throwable t) {
      fail("Expected a RuntimeException to be thrown, but got " + t);
    }
  }

  @Test
  void runResource() {
    var example =
        new RequireableResource("Counting", scheduler) {
          int x = 0;
        };

    Command countToTen =
        example
            .run(
                (coroutine) -> {
                  example.x = 0;
                  for (int i = 0; i < 10; i++) {
                    coroutine.yield();
                    example.x++;
                  }
                })
            .make("Count To Ten");

    scheduler.schedule(countToTen);
    for (int i = 0; i < 10; i++) {
      scheduler.run();
    }
    scheduler.run();

    assertEquals(10, example.x);
  }

  @Test
  void cancelOnInterruptDoesNotResume() {
    var count = new AtomicInteger(0);

    var resource = new RequireableResource("Resource", scheduler);

    var interrupter =
        Command.withReqs(resource)
            .executing((coroutine) -> {})
            .withPriority(2)
            .make("Interrupter");

    var canceledCommand =
        Command.withReqs(resource)
            .executing(
                (coroutine) -> {
                  count.set(1);
                  coroutine.yield();
                  count.set(2);
                })
            .withPriority(1)
            .make("Cancel By Default");

    scheduler.schedule(canceledCommand);
    scheduler.run();

    scheduler.schedule(interrupter);
    scheduler.run();
    assertEquals(1, count.get()); // the second "set" call should not have run
  }

  @Test
  void scheduleOverDefaultDoesNotRescheduleDefault() {
    var count = new AtomicInteger(0);

    var resource = new RequireableResource("Resource", scheduler);
    var defaultCmd =
        resource
            .run(
                (coroutine) -> {
                  while (true) {
                    count.incrementAndGet();
                    coroutine.yield();
                  }
                })
            .withPriority(-1)
            .make("Default Command");

    var newerCmd = resource.run((coroutine) -> {}).make("Newer Command");
    resource.setDefaultCommand(defaultCmd);
    scheduler.run();
    assertTrue(scheduler.isRunning(defaultCmd), "Default command should be running");

    scheduler.schedule(newerCmd);
    scheduler.run();
    assertFalse(scheduler.isRunning(defaultCmd), "Default command should have been interrupted");
    assertEquals(1, count.get(), "Default command should have run once");

    scheduler.run();
    assertTrue(scheduler.isRunning(defaultCmd));
  }

  @Test
  void cancelAllCancelsAll() {
    var commands = new ArrayList<Command>(10);
    for (int i = 1; i <= 10; i++) {
      commands.add(Command.noReqs(Coroutine::yield).make("Command " + i));
    }
    commands.forEach(scheduler::schedule);
    scheduler.run();
    scheduler.cancelAll();
    for (Command command : commands) {
      if (scheduler.isRunning(command)) {
        fail(command.name() + " was not canceled by cancelAll()");
      }
    }
  }

  @Test
  void cancelAllStartsDefaults() {
    var resources = new ArrayList<RequireableResource>(10);
    for (int i = 1; i <= 10; i++) {
      resources.add(new RequireableResource("System " + i, scheduler));
    }

    var command =
        new CommandBuilder().requiring(resources).executing(Coroutine::yield).make("Big Command");

    // Scheduling the command should evict the on-deck default commands
    scheduler.schedule(command);

    // Then running should get it into the set of running commands
    scheduler.run();

    // Cancelling should clear out the set of running commands
    scheduler.cancelAll();

    // Then ticking the scheduler once to fully remove the command and schedule the defaults
    scheduler.run();

    // Then one more tick to start running the scheduled defaults
    scheduler.run();

    if (scheduler.isRunning(command)) {
      System.err.println(scheduler.getRunningCommands());
      fail(command.name() + " was not canceled by cancelAll()");
    }

    for (var resource : resources) {
      var runningCommands = scheduler.getRunningCommandsFor(resource);
      assertEquals(
          1,
          runningCommands.size(),
          "Resource " + resource + " should have exactly one running command");
      assertInstanceOf(
          IdleCommand.class,
          runningCommands.getFirst(),
          "Resource " + resource + " is not running the default command");
    }
  }

  @Test
  void cancelDeeplyNestedCompositions() {
    Command root =
        Command.noReqs(
                (co) -> {
                  co.await(
                      Command.noReqs(
                              (co2) -> {
                                co2.await(
                                    Command.noReqs(
                                            (co3) -> {
                                              co3.await(
                                                  Command.noReqs(Coroutine::park)
                                                      .make("Park"));
                                            })
                                        .make("C3"));
                              })
                          .make("C2"));
                })
            .make("Root");

    scheduler.schedule(root);

    scheduler.run();
    assertEquals(4, scheduler.getRunningCommands().size());

    scheduler.cancel(root);
    assertEquals(0, scheduler.getRunningCommands().size());
  }

  @Test
  void compositionsDoNotSelfCancel() {
    var res = new RequireableResource("The Resource", scheduler);
    var group =
        res.run(
                (co) -> {
                  co.await(
                      res.run(
                              (co2) -> {
                                co2.await(
                                    res.run(
                                            (co3) -> {
                                              co3.await(res.run(Coroutine::park).make("Park"));
                                            })
                                        .make("C3"));
                              })
                          .make("C2"));
                })
            .make("Group");

    scheduler.schedule(group);
    scheduler.run();
    assertEquals(4, scheduler.getRunningCommands().size());
    assertTrue(scheduler.isRunning(group));
  }

  @Test
  void compositionsDoNotNeedRequirements() {
    var r1 = new RequireableResource("R1", scheduler);
    var r2 = new RequireableResource("r2", scheduler);

    // the group has no requirements, but can schedule child commands that do
    var group =
        Command.noReqs(
                (co) -> {
                  co.awaitAll(
                      r1.run(Coroutine::park).make("R1 Command"),
                      r2.run(Coroutine::park).make("R2 Command"));
                })
            .make("Composition");

    scheduler.schedule(group);
    scheduler.run(); // start r1 and r2 commands
    assertEquals(3, scheduler.getRunningCommands().size());
  }

  @Test
  void compositionsCannotAwaitConflictingCommands() {
    var res = new RequireableResource("The Resource", scheduler);

    var group =
        Command.noReqs(
                (co) -> {
                  co.awaitAll(
                      res.run(Coroutine::park).make("First"),
                      res.run(Coroutine::park).make("Second"));
                })
            .make("Group");

    scheduler.schedule(group);

    // Running should attempt to schedule multiple conflicting commands
    try {
      scheduler.run();
      fail("An exception should have been thrown");
    } catch (IllegalArgumentException iae) {
        assertEquals(
            "Command Second requires resources that are already used by First. Both require The Resource",
            iae.getMessage());
    } catch (Exception e) {
      fail("Unexpected exception: " + e);
    }
  }

  @Test
  void doesNotRunOnCancelWhenInterruptingOnDeck() {
    var ran = new AtomicBoolean(false);

    var resource = new RequireableResource("The Resource", scheduler);
    var cmd = resource.run(Coroutine::yield).whenCanceled(() -> ran.set(true)).make("cmd");
    var interrupter = resource.run(Coroutine::yield).make("Interrupter");
    scheduler.schedule(cmd);
    scheduler.schedule(interrupter);
    scheduler.run();

    assertFalse(ran.get(), "onCancel ran when it shouldn't have!");
  }

  @Test
  void doesNotRunOnCancelWhenCancellingOnDeck() {
    var ran = new AtomicBoolean(false);

    var resource = new RequireableResource("The Resource", scheduler);
    var cmd = resource.run(Coroutine::yield).whenCanceled(() -> ran.set(true)).make("cmd");
    scheduler.schedule(cmd);
    // cancelling before calling .run()
    scheduler.cancel(cmd);
    scheduler.run();

    assertFalse(ran.get(), "onCancel ran when it shouldn't have!");
  }

  @Test
  void runsOnCancelWhenInterruptingCommand() {
    var ran = new AtomicBoolean(false);

    var resource = new RequireableResource("The Resource", scheduler);
    var cmd = resource.run(Coroutine::park).whenCanceled(() -> ran.set(true)).make("cmd");
    var interrupter = resource.run(Coroutine::park).make("Interrupter");
    scheduler.schedule(cmd);
    scheduler.run();
    scheduler.schedule(interrupter);
    scheduler.run();

    assertTrue(ran.get(), "onCancel should have run!");
  }

  @Test
  void doesNotRunOnCancelWhenCompleting() {
    var ran = new AtomicBoolean(false);

    var resource = new RequireableResource("The Resource", scheduler);
    var cmd = resource.run(Coroutine::yield).whenCanceled(() -> ran.set(true)).make("cmd");
    scheduler.schedule(cmd);
    scheduler.run();
    scheduler.run();

    assertFalse(scheduler.isScheduledOrRunning(cmd));
    assertFalse(ran.get(), "onCancel ran when it shouldn't have!");
  }

  @Test
  void runsOnCancelWhenCancelling() {
    var ran = new AtomicBoolean(false);

    var resource = new RequireableResource("The Resource", scheduler);
    var cmd = resource.run(Coroutine::yield).whenCanceled(() -> ran.set(true)).make("cmd");
    scheduler.schedule(cmd);
    scheduler.run();
    scheduler.cancel(cmd);

    assertTrue(ran.get(), "onCancel should have run!");
  }

  @Test
  void runsOnCancelWhenCancellingParent() {
    var ran = new AtomicBoolean(false);

    var resource = new RequireableResource("The Resource", scheduler);
    var cmd = resource.run(Coroutine::yield).whenCanceled(() -> ran.set(true)).make("cmd");

    var group = new SequenceUnion("Seq", Collections.singletonList(cmd));
    scheduler.schedule(group);
    scheduler.run();
    scheduler.cancel(group);

    assertTrue(ran.get(), "onCancel should have run!");
  }

  @Test
  void sideloadThrowingException() {
    scheduler.sideload(
        co -> {
          throw new RuntimeException("Bang!");
        });

    // An exception raised in a sideload function should bubble up
    assertEquals("Bang!", assertThrowsExactly(RuntimeException.class, scheduler::run).getMessage());
  }

  @Test
  void nestedResources() {
    var superstructure =
        new RequireableResource("Superstructure", scheduler) {
          private final RequireableResource elevator =
              new RequireableResource("Elevator", scheduler);
          private final RequireableResource arm = new RequireableResource("Arm", scheduler);

          public Command superCommand() {
            return run(co -> {
                  co.await(elevator.run(Coroutine::park).make("Elevator Subcommand"));
                  co.await(arm.run(Coroutine::park).make("Arm Subcommand"));
                })
                .make("Super Command");
          }
        };

    scheduler.schedule(superstructure.superCommand());
    scheduler.run();
    assertEquals(
        List.of(superstructure.arm.getDefaultCommand()),
        superstructure.arm.getRunningCommands(),
        "Arm should only be running its default command");

    // Scheduling something that requires an in-use inner resource cancels the outer command
    scheduler.schedule(superstructure.elevator.run(Coroutine::park).make("Conflict"));

    scheduler.run(); // schedules the default superstructure command
    scheduler.run(); // starts running the default superstructure command
    assertEquals(List.of(superstructure.getDefaultCommand()), superstructure.getRunningCommands());
  }

  @Test
  void protobuf() {
    var res = new RequireableResource("The Resource", scheduler);
    var parkCommand = res.run(Coroutine::park).make("Park");
    var c3Command = res.run((co) -> co.await(parkCommand)).make("C3");
    var c2Command = res.run((co) -> co.await(c3Command)).make("C2");
    var group = res.run((co) -> co.await(c2Command)).make("Group");

    scheduler.schedule(group);
    scheduler.run();

    var scheduledCommand1 = Command.noReqs(Coroutine::park).make("Command 1");
    var scheduledCommand2 = Command.noReqs(Coroutine::park).make("Command 2");
    scheduler.schedule(scheduledCommand1);
    scheduler.schedule(scheduledCommand2);

    var message = Scheduler.proto.createMessage();
    Scheduler.proto.pack(message, scheduler);
    var messageJson = message.toString();
    assertEquals(
        """
        {
          "lastTimeMs": %s,
          "queuedCommands": [{
            "priority": 0,
            "id": %s,
            "name": "Command 1"
          }, {
            "priority": 0,
            "id": %s,
            "name": "Command 2"
          }],
          "runningCommands": [{
            "lastTimeMs": %s,
            "totalTimeMs": %s,
            "priority": 0,
            "id": %s,
            "name": "Group",
            "requirements": [{
              "name": "The Resource"
            }]
          }, {
            "lastTimeMs": %s,
            "totalTimeMs": %s,
            "priority": 0,
            "id": %s,
            "parentId": %s,
            "name": "C2",
            "requirements": [{
              "name": "The Resource"
            }]
          }, {
            "lastTimeMs": %s,
            "totalTimeMs": %s,
            "priority": 0,
            "id": %s,
            "parentId": %s,
            "name": "C3",
            "requirements": [{
              "name": "The Resource"
            }]
          }, {
            "lastTimeMs": %s,
            "totalTimeMs": %s,
            "priority": 0,
            "id": %s,
            "parentId": %s,
            "name": "Park",
            "requirements": [{
              "name": "The Resource"
            }]
          }]
        }"""
            .formatted(
                // Scheduler data
                scheduler.lastRuntimeMs(),

                // On deck commands
                scheduler.runId(scheduledCommand1),
                scheduler.runId(scheduledCommand2),

                // Running commands
                scheduler.lastRuntimeMs(group),
                scheduler.totalRuntimeMs(group),
                scheduler.runId(group), // id
                // top-level command, no parent ID

                scheduler.lastRuntimeMs(c2Command),
                scheduler.totalRuntimeMs(c2Command),
                scheduler.runId(c2Command), // id
                scheduler.runId(group), // parent

                scheduler.lastRuntimeMs(c3Command),
                scheduler.totalRuntimeMs(c3Command),
                scheduler.runId(c3Command), // id
                scheduler.runId(c2Command), // parent

                scheduler.lastRuntimeMs(parkCommand),
                scheduler.totalRuntimeMs(parkCommand),
                scheduler.runId(parkCommand), // id
                scheduler.runId(c3Command) // parent
                ),
        messageJson);
  }

  @Test
  void siblingsInCompositionCanShareRequirements() {
    var resource = new RequireableResource("The Resource", scheduler);
    var firstRan = new AtomicBoolean(false);
    var secondRan = new AtomicBoolean(false);

    var first = resource.run(c -> {
      firstRan.set(true);
      c.park();
    }).make("First");

    var second = resource.run(c -> {
      secondRan.set(true);
      c.park();
    }).make("Second");

    var group = Command.noReqs(co -> {
      co.fork(first);
      co.fork(second);
      co.park();
    }).make("Group");

    scheduler.schedule(group);
    scheduler.run();

    assertTrue(firstRan.get(), "First child should have run to a yield point");
    assertTrue(secondRan.get(), "Second child should have run to a yield point");
    assertFalse(scheduler.isScheduledOrRunning(first), "First child should have been interrupted");
    assertTrue(scheduler.isRunning(second), "Second child should still be running");
    assertTrue(scheduler.isRunning(group), "Group should still be running");
  }

  @Test
  void nestedOneShotCompositionsAllRunInOneCycle() {
    var runs = new AtomicInteger(0);
    Supplier<Command> makeOneShot = () -> Command.noReqs(_c -> runs.incrementAndGet()).make("One Shot");
    var command = Command.noReqs(co -> {
      co.fork(makeOneShot.get());
      co.fork(makeOneShot.get());
      co.fork(Command.noReqs(inner -> inner.fork(makeOneShot.get())).make("Inner"));
      co.fork(
          Command.noReqs(co2 -> {
            co2.fork(makeOneShot.get());
            co2.fork(
                Command.noReqs(co3 -> {
                  co3.fork(makeOneShot.get());
                }).make("3"));
          }).make("2"));
    }).make("Command");

    scheduler.schedule(command);
    scheduler.run();
    assertEquals(5, runs.get(), "All oneshot commands should have run");
    assertFalse(scheduler.isRunning(command), "Command should have exited after one cycle");
  }

  record PriorityCommand(int priority, RequireableResource... subsystems) implements Command {
    @Override
    public void run(Coroutine coroutine) {
      coroutine.park();
    }

    @Override
    public Set<RequireableResource> requirements() {
      return Set.of(subsystems);
    }

    @Override
    public String name() {
      return toString();
    }

    @Override
    public String toString() {
      return "PriorityCommand[priority=" + priority + ", subsystems=" + Set.of(subsystems) + "]";
    }
  }
}
