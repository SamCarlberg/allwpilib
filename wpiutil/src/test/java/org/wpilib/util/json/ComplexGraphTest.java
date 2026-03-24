// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.util.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
class ComplexGraphTest {
  public record Node(
      @JsonAttribute("name") String name, @JsonAttribute("children") List<Node> children) {
    @JsonConstructor
    public Node {}
  }

  @Test
  void testDeeplyNestedObjects() {
    String json = "{\"name\": \"root\", \"children\": [{\"name\": \"child1\", \"children\": []}]}";

    Node root = JsonDeserializer.deserialize(json, Node.class);
    assertEquals("root", root.name());
    assertEquals(1, root.children().size());

    Object child = root.children().get(0);
    assertInstanceOf(Node.class, child);
    Node childNode = (Node) child;
    assertEquals("child1", childNode.name());
  }

  @Test
  void testNestedMapsAndLists() {
    String json = "{\"a\": [1, 2, {\"b\": 3}], \"c\": {\"d\": [4, 5]}}";
    Map<String, Object> map = (Map<String, Object>) JsonDeserializer.deserializeRaw(json);

    assertEquals(2, map.size());
    List<Object> a = (List<Object>) map.get("a");
    assertEquals(3, a.size());
    assertEquals(1.0, a.get(0));
    Map<String, Object> nestedB = (Map<String, Object>) a.get(2);
    assertEquals(3.0, nestedB.get("b"));

    Map<String, Object> c = (Map<String, Object>) map.get("c");
    List<Object> d = (List<Object>) c.get("d");
    assertEquals(4.0, d.get(0));
    assertEquals(5.0, d.get(1));
  }

  public static class Container {
    @JsonAttribute("items")
    private final List<String> m_items;

    @JsonAttribute("meta")
    private final Map<String, Double> m_meta;

    @JsonConstructor
    Container(
        @JsonAttribute("items") List<String> items,
        @JsonAttribute("meta") Map<String, Double> meta) {
      m_items = items;
      m_meta = meta;
    }
  }

  @Test
  void testSerializationOfComplexGraph() {
    List<String> items = List.of("apple", "banana");
    Map<String, Double> meta = Map.of("count", 2.0, "price", 0.99);
    Container container = new Container(items, meta);

    String json = JsonSerializer.toJson(container);
    // Map order is not guaranteed by Map.of, but JsonSerializer uses LinkedHashMap for its internal
    // processing of objects.
    // However, here it uses Map.of which doesn't guarantee order.
    assertTrue(json.contains("\"items\": [\"apple\", \"banana\"]"), json);
    assertTrue(json.contains("\"meta\": {"), json);
    assertTrue(json.contains("\"count\": 2.0"), json);
    assertTrue(json.contains("\"price\": 0.99"), json);
  }
}
