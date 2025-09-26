package org.wpilib.commands3;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class StateMachineTest extends CommandTestBase {
  @Test
  void firstStateIsInitial() {
    Mechanism mech = new Mechanism("Mechanism", m_scheduler);
    Command command1 = mech.run(Coroutine::park).named("Command 1");
    Command command2 = mech.run(Coroutine::park).named("Command 2");

    StateMachine stateMachine = new StateMachine("State Machine");
    var state1 = stateMachine.addState(command1);
    var state2 = stateMachine.addState(command2);
    state1.switchTo(state2).whenComplete();

    m_scheduler.schedule(stateMachine);
    m_scheduler.run();
    assertTrue(m_scheduler.isRunning(stateMachine), "State Machine should be running");
    assertTrue(m_scheduler.isRunning(command1), "Command 1 should be running as the initial state");
    assertFalse(m_scheduler.isRunning(command2), "Command 2 should not be running");
  }

  @Test
  void initialStateCanBeOverridden() {
    Mechanism mech = new Mechanism("Mechanism", m_scheduler);
    Command command1 = mech.run(Coroutine::park).named("Command 1");
    Command command2 = mech.run(Coroutine::park).named("Command 2");

    StateMachine stateMachine = new StateMachine("State Machine");
    var state1 = stateMachine.addState(command1);
    var state2 = stateMachine.addState(command2);
    stateMachine.setInitialState(state2);
    state2.switchTo(state1).whenComplete();

    m_scheduler.schedule(stateMachine);
    m_scheduler.run();
    assertTrue(m_scheduler.isRunning(stateMachine), "State Machine should be running");
    assertTrue(m_scheduler.isRunning(command2), "Command 2 should be running as the initial state");
    assertFalse(m_scheduler.isRunning(command1), "Command 1 should not be running");
  }

  @Test
  void transitions() {
    AtomicBoolean signalA = new AtomicBoolean(false);
    AtomicBoolean signalB = new AtomicBoolean(false);

    Mechanism mech = new Mechanism("Mechanism", m_scheduler);
    var command1 = mech.run(Coroutine::park).named("Command 1");
    var command2 = mech.run(Coroutine::park).named("Command 2");
    var command3 = mech.run(Coroutine::park).named("Command 3");

    StateMachine stateMachine = new StateMachine("State Machine");

    var state1 = stateMachine.addState(command1);
    var state2 = stateMachine.addState(command2);
    var state3 = stateMachine.addState(command3);

    state1.switchTo(state2).when(signalA::get);
    state2.switchTo(state3).when(signalB::get);

    m_scheduler.schedule(stateMachine);
    m_scheduler.run();
    assertAll(
        () -> assertTrue(m_scheduler.isRunning(stateMachine), "State Machine should be running"),
        () -> assertTrue(m_scheduler.isRunning(command1), "Command 1 should be running"),
        () -> assertFalse(m_scheduler.isRunning(command2), "Command 2 should not be running"),
        () -> assertFalse(m_scheduler.isRunning(command3), "Command 3 should not be running"));

    signalA.set(true);
    m_scheduler.run();
    assertAll(
        () -> assertTrue(m_scheduler.isRunning(stateMachine), "State Machine should be running"),
        () -> assertFalse(m_scheduler.isRunning(command1), "Command 1 should not be running"),
        () -> assertTrue(m_scheduler.isRunning(command2), "Command 2 should be running"),
        () -> assertFalse(m_scheduler.isRunning(command3), "Command 3 should not be running"));

    signalB.set(true);
    m_scheduler.run();
    assertAll(
        () -> assertTrue(m_scheduler.isRunning(stateMachine), "State Machine should be running"),
        () -> assertFalse(m_scheduler.isRunning(command1), "Command 1 should not be running"),
        () -> assertFalse(m_scheduler.isRunning(command2), "Command 2 should not be running"),
        () -> assertTrue(m_scheduler.isRunning(command3), "Command 3 should be running"));
  }
}
