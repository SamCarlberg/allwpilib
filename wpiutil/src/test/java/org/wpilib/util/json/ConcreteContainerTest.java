// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.util.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;

class ConcreteContainerTest {
  @Test
  void loadHashMap() {
    String json =
        """
        { "a": 1, "b": 2, "c": 3 }
        """;
    var map = JsonDeserializer.deserialize(json, HashMap.class);
    assertEquals(Map.of("a", 1.0, "b", 2.0, "c", 3.0), map);
  }

  @Test
  void loadTreeMap() {
    String json =
        """
        { "a": 1, "b": 2, "c": 3 }
        """;
    var map = JsonDeserializer.deserialize(json, TreeMap.class);
    assertEquals(Map.of("a", 1.0, "b", 2.0, "c", 3.0), map);
  }
}
