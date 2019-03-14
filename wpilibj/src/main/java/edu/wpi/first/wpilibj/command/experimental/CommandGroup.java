/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.command.experimental;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.wpi.first.wpilibj.annotation.Incubating;

/**
 * A type of command that itself runs groups of commands. The nested commands run in sequential
 * blocks; each block is composed of one or more commands. All commands in a block run in parallel;
 * once all commands in a block have completed, the next block of commands will start running. This
 * continues until all commands have completed or until the command group is interrupted or
 * cancelled.
 *
 * <pre>{@code
 * public class MyGroup extends CommandGroup {
 *   public MyGroup() {
 *     // These two commands will run in parallel
 *     addBlock(new FirstCommand(), new SecondCommand());
 *
 *     // Wait 5 seconds after the first block is complete
 *     addTimedBlock(5, TimeUnit.SECONDS);
 *
 *     // This command will run after a 5 second delay
 *     addBlock(new ThirdCommand());
 *   }
 * }
 * }</pre>
 */
@Incubating(since = "2020")
public class CommandGroup extends CommandBase {
  private final Scheduler m_scheduler = new Scheduler();
  private final List<CommandBlock> m_blocks = new ArrayList<>();
  private int m_currentBlockIndex = -1;
  private CommandBlock m_currentBlock;

  /**
   * A block of commands that run in parallel.
   */
  private static final class CommandBlock {
    private final List<Command> m_commands;

    CommandBlock(Command first, Command... next) {
      m_commands = new ArrayList<>();
      m_commands.add(first);
      Collections.addAll(m_commands, next);
    }

    List<Command> getCommands() {
      return m_commands;
    }
  }

  private void addBlock(CommandBlock block) {
    for (Command command : block.getCommands()) {
      command.getRequiredSubsystems().forEach(this::requires);
    }
    m_blocks.add(block);
  }

  /**
   * Adds a block of commands to run in parallel. These commands will start running after the
   * previous block completes; that is, when all commands in the previous block have finished
   * according to their individual {@link #isFinished()} methods.
   *
   * <p>If a single block should take at least some minimum amount of time, add a
   * {@link WaitCommand} to the block for the desired duration. The WaitCommand will prevent the
   * block from completing until that duration has elapsed. A helper function
   * {@link #addTimedBlock(double, Command...)} exists for this reason.
   *
   * <p>If multiple commands in a single block require the same subsystem, the last such command
   * will win out - all prior commands will not be run. This restriction does not exist for
   * commands in different blocks. For this reason, it is highly recommended to <i>not</i> attempt
   * to use multiple commands that require the same subsystem in the same block.
   *
   * <p>This method should only be called in the subclass constructor.
   *
   * @param first the mandatory command in the block
   * @param next  optional extra commands to run in the block
   */
  protected void addBlock(Command first, Command... next) {
    addBlock(new CommandBlock(first, next));
  }

  /**
   * Adds a block that lasts at least the given amount of time before completion.
   *
   * @param time     the minimum length of time the block should take to execute, in seconds
   * @param commands the commands in the block
   * @see #addBlock(Command, Command...)
   */
  protected void addTimedBlock(double time, Command... commands) {
    addBlock(new WaitCommand(time), commands);
  }

  /**
   * Schedules the next block of commands to run. If no more blocks are available, the current
   * block is set no {@code null} so that {@link #isFinished()} will return {@code true}.
   */
  private void scheduleNextBlock() {
    m_currentBlockIndex++;
    if (m_currentBlockIndex < m_blocks.size()) {
      m_currentBlock = m_blocks.get(m_currentBlockIndex);
      m_currentBlock.getCommands().forEach(m_scheduler::add);
    } else {
      // No more blocks to schedule, we're done
      m_currentBlock = null;
    }
  }

  @Override
  public void initialize() {
    // Start block 0 (ie the first block)
    m_currentBlockIndex = -1;
    scheduleNextBlock();
  }

  @Override
  public void execute() {
    m_scheduler.run();
    if (!m_scheduler.hasRunningCommands()) {
      scheduleNextBlock();
    }
  }

  @Override
  public void end() {
    m_scheduler.removeAll();
    m_currentBlock = null;
  }

  @Override
  public boolean isFinished() {
    return m_currentBlock == null;
  }

}
