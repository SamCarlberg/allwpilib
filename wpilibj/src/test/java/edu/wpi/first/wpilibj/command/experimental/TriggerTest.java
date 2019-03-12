package edu.wpi.first.wpilibj.command.experimental;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TriggerTest {
  @ParameterizedTest
  @CsvSource({"true,false", "false,true"})
  void testNot(boolean input, boolean expected) {
    Trigger trigger = () -> input;
    Trigger not = trigger.not();
    assertEquals(expected, not.get());
  }

  @ParameterizedTest
  @CsvSource({"false,false,false", "true,false,false", "false,true,false", "true,true,true"})
  void testAnd(boolean a, boolean b, boolean expected) {
    Trigger first = () -> a;
    Trigger second = () -> b;
    Trigger and = first.and(second);
    assertEquals(expected, and.get());
  }

  @ParameterizedTest
  @CsvSource({"false,false,false", "true,false,true", "false,true,true", "true,true,true"})
  void testOr(boolean a, boolean b, boolean expected) {
    Trigger first = () -> a;
    Trigger second = () -> b;
    Trigger or = first.or(second);
    assertEquals(expected, or.get());
  }

  @ParameterizedTest
  @CsvSource({"false,false,false", "true,false,true", "false,true,true", "true,true,false"})
  void testXor(boolean a, boolean b, boolean expected) {
    Trigger first = () -> a;
    Trigger second = () -> b;
    Trigger xor = first.xor(second);
    assertEquals(expected, xor.get());
  }

}
