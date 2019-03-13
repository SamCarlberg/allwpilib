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
  void testAnd(boolean in1, boolean in2, boolean expected) {
    Trigger first = () -> in1;
    Trigger second = () -> in2;
    Trigger and = first.and(second);
    assertEquals(expected, and.get());
  }

  @ParameterizedTest
  @CsvSource({"false,false,false", "true,false,true", "false,true,true", "true,true,true"})
  void testOr(boolean in1, boolean in2, boolean expected) {
    Trigger first = () -> in1;
    Trigger second = () -> in2;
    Trigger or = first.or(second);
    assertEquals(expected, or.get());
  }

  @ParameterizedTest
  @CsvSource({"false,false,false", "true,false,true", "false,true,true", "true,true,false"})
  void testXor(boolean in1, boolean in2, boolean expected) {
    Trigger first = () -> in1;
    Trigger second = () -> in2;
    Trigger xor = first.xor(second);
    assertEquals(expected, xor.get());
  }

}
