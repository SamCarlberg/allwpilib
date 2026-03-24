// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.util.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JsonSerializerCustomTypeTest {
  public record Point(@JsonAttribute("x") double x, @JsonAttribute("y") double y) {
    @JsonConstructor
    public Point {}
  }

  public record Container(@JsonAttribute("points") List<Point> points) {
    @JsonConstructor
    public Container {}
  }

  @Test
  void testNestedCustomTypeInList() {
    String json = "{\"points\": [{\"x\": 1, \"y\": 1}, {\"x\": 2, \"y\": 2}]}";
    Container container = JsonDeserializer.deserialize(json, Container.class);

    assertEquals(2, container.points().size());
    assertInstanceOf(Point.class, container.points().get(0));
    assertEquals(1.0, container.points().get(0).x());
    assertEquals(1.0, container.points().get(0).y());
    assertEquals(2.0, container.points().get(1).x());
    assertEquals(2.0, container.points().get(1).y());
  }

  @Test
  void testNestedCustomTypeInMap() {
    record MapContainer(@JsonAttribute("points") Map<String, Point> points) {
      @JsonConstructor
      public MapContainer {}
    }

    String json = "{\"points\": {\"p1\": {\"x\": 1, \"y\": 1}, \"p2\": {\"x\": 2, \"y\": 2}}}";
    MapContainer container = JsonDeserializer.deserialize(json, MapContainer.class);

    assertEquals(2, container.points().size());
    assertInstanceOf(Point.class, container.points().get("p1"));
    assertEquals(1.0, container.points().get("p1").x());
    assertEquals(2.0, container.points().get("p2").x());
  }
}
