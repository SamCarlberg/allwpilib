// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.util.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class CustomTypeTest {
  static class CustomType {
    private final double m_x;
    private final double m_y;

    @JsonConstructor
    CustomType(@JsonAttribute("x") double x, @JsonAttribute("y") double y) {
      m_x = x;
      m_y = y;
    }

    CustomType() {
      this(0, 0);
    }

    @JsonAttribute("x")
    public double getX() {
      return m_x;
    }

    @JsonAttribute("y")
    public double getY() {
      return m_y;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o instanceof CustomType ct) {
        return Double.compare(ct.m_x, m_x) == 0 && Double.compare(ct.m_y, m_y) == 0;
      }
      return false;
    }

    @Override
    public int hashCode() {
      return Objects.hash(m_x, m_y);
    }

    @Override
    public String toString() {
      return "CustomType{x=" + m_x + ", y=" + m_y + "}";
    }
  }

  @Test
  void testDeserialization() {
    String json = "{\"x\": 1.0, \"y\": 2.0}";
    CustomType result = JsonDeserializer.deserialize(json, CustomType.class);
    assertEquals(new CustomType(1.0, 2.0), result);

    // Test different order
    String json2 = "{\"y\": 3.0, \"x\": 4.0}";
    CustomType result2 = JsonDeserializer.deserialize(json2, CustomType.class);
    assertEquals(new CustomType(4.0, 3.0), result2);
  }

  @Test
  void testSerialization() {
    CustomType obj = new CustomType(5.0, 6.0);
    String json = JsonSerializer.toJson(obj);
    assertTrue(json.contains("\"x\": 5.0"), json);
    assertTrue(json.contains("\"y\": 6.0"), json);

    // Round trip
    CustomType result = JsonDeserializer.deserialize(json, CustomType.class);
    assertEquals(obj, result);
  }

  @Test
  void testNestedSerialization() {
    record Container(@JsonAttribute("inner") CustomType inner, @JsonAttribute("name") String name) {
      @JsonConstructor
      public Container {}
    }

    Container c = new Container(new CustomType(1, 2), "test");
    String json = JsonSerializer.toJson(c);

    Container result = JsonDeserializer.deserialize(json, Container.class);
    assertEquals(c, result);
  }

  @Test
  void complexHierarchyWithRecords() {
    record Point(
        @JsonAttribute("x") double x, @JsonAttribute("y") double y, @JsonAttribute("z") double z) {
      @JsonConstructor
      public Point {}
    }

    record Mesh(@JsonAttribute("points") List<Point> points) {
      @JsonConstructor
      public Mesh {}
    }

    record Triangle(
        @JsonAttribute("A") Point A, @JsonAttribute("B") Point B, @JsonAttribute("C") Point C) {
      @JsonConstructor
      public Triangle {}
    }

    record Model(
        @JsonAttribute("mesh") Mesh mesh, @JsonAttribute("triangles") List<Triangle> triangles) {
      @JsonConstructor
      public Model {}
    }

    Model model =
        new Model(
            new Mesh(List.of(new Point(1, 2, 3), new Point(4, 5, 6))),
            List.of(new Triangle(new Point(0, 0, 0), new Point(1, 1, 1), new Point(2, 2, 2))));
    String json = JsonSerializer.toJson(model);

    Model result = JsonDeserializer.deserialize(json, Model.class);
    assertEquals(model, result);
  }

  @Test
  void complexHierarchyWithClasses() {
    ModelClass model =
        new ModelClass(
            new MeshClass(List.of(new PointClass(1, 2, 3), new PointClass(4, 5, 6))),
            List.of(
                new TriangleClass(
                    new PointClass(0, 0, 0), new PointClass(1, 1, 1), new PointClass(2, 2, 2))));
    String json = JsonSerializer.toJson(model);

    ModelClass result = JsonDeserializer.deserialize(json, ModelClass.class);
    assertEquals(model, result);
  }

  static class PointClass {
    @JsonAttribute("x")
    final double m_x;

    @JsonAttribute("y")
    final double m_y;

    @JsonAttribute("z")
    final double m_z;

    @JsonConstructor
    PointClass(
        @JsonAttribute("x") double x, @JsonAttribute("y") double y, @JsonAttribute("z") double z) {
      m_x = x;
      m_y = y;
      m_z = z;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      PointClass point = (PointClass) o;
      return Double.compare(point.m_x, m_x) == 0
          && Double.compare(point.m_y, m_y) == 0
          && Double.compare(point.m_z, m_z) == 0;
    }

    @Override
    public int hashCode() {
      return Objects.hash(m_x, m_y, m_z);
    }
  }

  static class MeshClass {
    @JsonAttribute("points")
    final List<PointClass> m_points;

    @JsonConstructor
    MeshClass(@JsonAttribute("points") List<PointClass> points) {
      this.m_points = points;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      MeshClass mesh = (MeshClass) o;
      return Objects.equals(m_points, mesh.m_points);
    }

    @Override
    public int hashCode() {
      return Objects.hash(m_points);
    }
  }

  static class TriangleClass {
    @JsonAttribute("A")
    final PointClass m_a;

    @JsonAttribute("B")
    final PointClass m_b;

    @JsonAttribute("C")
    final PointClass m_c;

    @JsonConstructor
    TriangleClass(
        @JsonAttribute("A") PointClass A,
        @JsonAttribute("B") PointClass B,
        @JsonAttribute("C") PointClass C) {
      this.m_a = A;
      this.m_b = B;
      this.m_c = C;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      TriangleClass triangle = (TriangleClass) o;
      return Objects.equals(m_a, triangle.m_a)
          && Objects.equals(m_b, triangle.m_b)
          && Objects.equals(m_c, triangle.m_c);
    }

    @Override
    public int hashCode() {
      return Objects.hash(m_a, m_b, m_c);
    }
  }

  static class ModelClass {
    @JsonAttribute("mesh")
    final MeshClass m_mesh;

    @JsonAttribute("triangles")
    final List<TriangleClass> m_triangles;

    @JsonConstructor
    ModelClass(
        @JsonAttribute("mesh") MeshClass mesh,
        @JsonAttribute("triangles") List<TriangleClass> triangles) {
      this.m_mesh = mesh;
      this.m_triangles = triangles;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      ModelClass model = (ModelClass) o;
      return Objects.equals(m_mesh, model.m_mesh) && Objects.equals(m_triangles, model.m_triangles);
    }

    @Override
    public int hashCode() {
      return Objects.hash(m_mesh, m_triangles);
    }
  }
}
