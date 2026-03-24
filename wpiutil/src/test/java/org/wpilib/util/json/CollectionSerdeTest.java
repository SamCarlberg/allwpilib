// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.util.json;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import org.junit.jupiter.api.Test;

class CollectionSerdeTest {
  @Test
  @SuppressWarnings("unchecked")
  void testSetSerde() {
    Set<String> set = Set.of("a", "b", "c");
    String json = JsonSerializer.toJson(set);
    Set<String> deserialized = JsonDeserializer.deserialize(json, HashSet.class);
    assertEquals(set, deserialized);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testQueueSerde() {
    Queue<Integer> queue = new ArrayDeque<>(List.of(1, 2, 3));
    String json = JsonSerializer.toJson(queue);
    Queue<Double> deserialized = JsonDeserializer.deserialize(json, ArrayDeque.class);
    assertEquals(List.of(1.0, 2.0, 3.0), List.copyOf(deserialized));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testStackSerde() {
    Stack<Integer> stack = new Stack<>();
    stack.push(1);
    stack.push(2);
    String json = JsonSerializer.toJson(stack);
    Stack<Double> deserialized = JsonDeserializer.deserialize(json, Stack.class);
    assertEquals(List.of(1.0, 2.0), List.copyOf(deserialized));
  }

  @Test
  void testDoubleArraySerde() {
    double[] array = {1.1, 2.2, 3.3};
    String json = JsonSerializer.toJson(array);
    assertEquals("[1.1, 2.2, 3.3]", json);
    double[] deserialized = JsonDeserializer.deserialize(json, double[].class);
    assertArrayEquals(array, deserialized);
  }

  @Test
  void testObjectArraySerde() {
    String[] array = {"a", "b", "c"};
    String json = JsonSerializer.toJson(array);
    assertEquals("[\"a\", \"b\", \"c\"]", json);
    String[] deserialized = JsonDeserializer.deserialize(json, String[].class);
    assertArrayEquals(array, deserialized);
  }

  @Test
  void testEmptyArraySerde() {
    int[] array = {};
    String json = JsonSerializer.toJson(array);
    assertEquals("[]", json);
    int[] deserialized = JsonDeserializer.deserialize(json, int[].class);
    assertArrayEquals(array, deserialized);
  }

  @Test
  void testMultiDimensionalArraySerde() {
    int[][] array = {{1, 2}, {3, 4}};
    String json = JsonSerializer.toJson(array);
    assertEquals("[[1, 2], [3, 4]]", json);
    int[][] deserialized = JsonDeserializer.deserialize(json, int[][].class);
    assertArrayEquals(array[0], deserialized[0]);
    assertArrayEquals(array[1], deserialized[1]);
  }

  @Test
  void testArrayWithNullsSerde() {
    String[] array = {"a", null, "c"};
    String json = JsonSerializer.toJson(array);
    assertEquals("[\"a\", null, \"c\"]", json);
    String[] deserialized = JsonDeserializer.deserialize(json, String[].class);
    assertArrayEquals(array, deserialized);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testCollectionWithNullsSerde() {
    // List.of doesn't support nulls, use ArrayList
    List<String> listWithNulls = new ArrayList<>();
    listWithNulls.add("a");
    listWithNulls.add(null);
    listWithNulls.add("c");
    String json = JsonSerializer.toJson(listWithNulls);
    List<String> deserialized = JsonDeserializer.deserialize(json, ArrayList.class);
    assertEquals(listWithNulls, deserialized);
  }
}
