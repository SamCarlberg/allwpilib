// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.util.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Handles serialization of objects to JSON text. Any collections in the object tree will be
 * represented as arrays. Cyclic object graphs are not supported.
 */
public final class JsonSerializer {
  private JsonSerializer() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
  }

  /**
   * Serializes an object to JSON text. Any collections in the object tree will be represented as
   * arrays. Cyclic object graphs are not supported.
   *
   * @param object The object to serialize.
   * @return The JSON representation of the object.
   * @throws StackOverflowError If the object graph contains cycles.
   */
  @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
  public static String toJson(Object object) {
    if (object == null) {
      return "null";
    }
    if (object instanceof String str) {
      return "\"" + escape(str) + "\"";
    }
    if (object instanceof Number || object instanceof Boolean) {
      return object.toString();
    }
    if (object instanceof Collection<?> collection) {
      return "["
          + collection.stream().map(JsonSerializer::toJson).collect(Collectors.joining(", "))
          + "]";
    }
    if (object instanceof Map<?, ?> map) {
      return "{"
          + map.entrySet().stream()
              .map(e -> "\"" + escape(String.valueOf(e.getKey())) + "\": " + toJson(e.getValue()))
              .collect(Collectors.joining(", "))
          + "}";
    }
    if (object.getClass().isArray()) {
      return "["
          + IntStream.range(0, Array.getLength(object))
              .mapToObj(i -> toJson(Array.get(object, i)))
              .collect(Collectors.joining(", "))
          + "]";
    }

    Class<?> clazz = object.getClass();
    Map<String, Object> props = new LinkedHashMap<>();

    getAllMethods(clazz)
        .forEach(
            method -> {
              JsonAttribute attr = method.getAnnotation(JsonAttribute.class);
              if (method.getParameterCount() != 0) {
                throw new IllegalArgumentException(
                    "@JsonAttribute methods cannot have parameters: "
                        + method.getDeclaringClass().getName()
                        + "."
                        + method.getName());
              }

              if (props.containsKey(attr.value())) {
                return;
              }

              method.setAccessible(true);

              try {
                Object val = method.invoke(object);
                if (val != null) {
                  props.put(attr.value(), val);
                }
              } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Failed to invoke method " + method.getName(), e);
              }
            });

    getAllFields(clazz)
        .forEach(
            field -> {
              JsonAttribute attr = field.getAnnotation(JsonAttribute.class);
              if (props.containsKey(attr.value())) {
                return;
              }

              field.setAccessible(true);

              try {
                Object val = field.get(object);
                if (val != null) {
                  props.put(attr.value(), val);
                }
              } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field " + field.getName(), e);
              }
            });

    return toJson(props);
  }

  private static List<Method> getAllMethods(Class<?> clazz) {
    List<Method> methods = new ArrayList<>();
    for (Method method : clazz.getDeclaredMethods()) {
      if (method.isAnnotationPresent(JsonAttribute.class)) {
        methods.add(method);
      }
    }
    if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
      methods.addAll(getAllMethods(clazz.getSuperclass()));
    }
    for (Class<?> iface : clazz.getInterfaces()) {
      methods.addAll(getAllMethods(iface));
    }
    return methods;
  }

  private static List<Field> getAllFields(Class<?> clazz) {
    List<Field> fields = new ArrayList<>();
    for (Field field : clazz.getDeclaredFields()) {
      if (field.isAnnotationPresent(JsonAttribute.class)) {
        fields.add(field);
      }
    }
    if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
      fields.addAll(getAllFields(clazz.getSuperclass()));
    }
    return fields;
  }

  private static String escape(String s) {
    StringBuilder sb = new StringBuilder();

    // For efficiency, we loop with a switch instead of chaining seven .replace() calls
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      switch (c) {
        case '\\' -> sb.append("\\\\");
        case '"' -> sb.append("\\\"");
        case '\b' -> sb.append("\\b");
        case '\f' -> sb.append("\\f");
        case '\n' -> sb.append("\\n");
        case '\r' -> sb.append("\\r");
        case '\t' -> sb.append("\\t");
        default -> sb.append(c);
      }
    }

    return sb.toString();
  }
}
