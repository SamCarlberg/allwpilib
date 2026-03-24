// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.util.json;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Handles deserialization of JSON text to objects. This class natively works with standard Java
 * types and collections; specficially, {@code ArrayList} for JSON arrays and {@code LinkedHashMap}
 * for JSON objects. If you want to deserialize JSON data to a specific type, use {@link
 * #deserialize(String, Class)}. The target class must have exactly one constructor with the {@link
 * JsonConstructor @JsonConstructor} annotation.
 */
public final class JsonDeserializer {
  private static final Map<Class<?>, Function<Number, Object>> NUMERIC_CONVERSIONS;

  static {
    NUMERIC_CONVERSIONS = new LinkedHashMap<>();
    NUMERIC_CONVERSIONS.put(double.class, Number::doubleValue);
    NUMERIC_CONVERSIONS.put(Double.class, Number::doubleValue);
    NUMERIC_CONVERSIONS.put(int.class, Number::intValue);
    NUMERIC_CONVERSIONS.put(Integer.class, Number::intValue);
    NUMERIC_CONVERSIONS.put(long.class, Number::longValue);
    NUMERIC_CONVERSIONS.put(Long.class, Number::longValue);
    NUMERIC_CONVERSIONS.put(float.class, Number::floatValue);
    NUMERIC_CONVERSIONS.put(Float.class, Number::floatValue);
    NUMERIC_CONVERSIONS.put(short.class, Number::shortValue);
    NUMERIC_CONVERSIONS.put(Short.class, Number::shortValue);
    NUMERIC_CONVERSIONS.put(byte.class, Number::byteValue);
    NUMERIC_CONVERSIONS.put(Byte.class, Number::byteValue);
  }

  private JsonDeserializer() {
    throw new UnsupportedOperationException(
        "JsonDeserializer is a utility class and cannot be instantiated");
  }

  /**
   * Parses a JSON string into raw Java types. This can be used if you don't need to marshal data to
   * a specific type, or if you plan on doing the marshaling yourself.
   *
   * @param json The JSON string to parse.
   * @return The parsed JSON value (String, Double, Boolean, List, Map, or null).
   * @throws ParseException If the JSON string is invalid or cannot be parsed.
   */
  public static Object deserializeRaw(String json) {
    return JsonParser.parse(json);
  }

  /**
   * Parses a JSON input stream into raw Java types. This can be used if you don't need to marshal
   * data to a specific type, or if you plan on doing the marshaling yourself.
   *
   * @param inputStream The JSON input stream to parse.
   * @return The parsed JSON value (String, Double, Boolean, List, Map, or null).
   * @throws ParseException If the JSON in the input stream is invalid or if the input stream cannot
   *     be read
   */
  public static Object deserializeRaw(InputStream inputStream) {
    return JsonParser.parse(inputStream);
  }

  /**
   * Parses a JSON string into an object of the specified class. JSON primitive values are converted
   * to their Java counterparts, arrays are transformed into List objects, and nested objects are
   * recursively parsed into nested maps.
   *
   * @param json The JSON string to parse.
   * @param clazz The class to parse the JSON into.
   * @param <T> The type of the parsed object.
   * @return The parsed JSON object.
   * @throws ParseException If the JSON string is invalid or cannot be parsed.
   * @throws ClassCastException If the parsed JSON object cannot be cast to the specified class.
   */
  public static <T> T deserialize(String json, Class<T> clazz) {
    Object node = JsonParser.parse(json);
    return parseNodeAs(node, clazz);
  }

  /**
   * Parses a JSON input stream into an object of the specified class. JSON primitive values are
   * converted to their Java counterparts, arrays are transformed into List objects, and nested
   * objects are recursively parsed into nested maps.
   *
   * @param inputStream The JSON input stream to parse.
   * @param clazz The class to parse the JSON into.
   * @param <T> The type of the parsed object.
   * @return The parsed JSON object.
   * @throws ParseException If the JSON in the input stream is invalid or if the input stream cannot
   *     be read.
   * @throws ClassCastException If the parsed JSON object cannot be cast to the specified class.
   */
  public static <T> T deserialize(InputStream inputStream, Class<T> clazz) {
    Object node = JsonParser.parse(inputStream);
    return parseNodeAs(node, clazz);
  }

  @SuppressWarnings("unchecked")
  private static <T> T parseNodeAs(Object node, Type type) {
    Class<?> clazz;
    if (type instanceof Class<?>) {
      clazz = (Class<?>) type;
    } else if (type instanceof ParameterizedType pt) {
      clazz = (Class<?>) pt.getRawType();
    } else {
      throw new IllegalArgumentException("Unsupported type: " + type);
    }

    return switch (node) {
      case null -> null;
      case Number num -> loadNumber(node, num, clazz);
      case Boolean bool -> loadBoolean(bool);
      case String str -> loadString(str);
      case List<?> list when clazz.isArray() -> loadArray(list, clazz);
      case List<?> list -> loadCollection(type, list, clazz);
      case Map<?, ?> map when Map.class.isAssignableFrom(clazz) ->
          loadMap(type, map, (Class<? extends Map<?, ?>>) clazz);
      default -> {
        if (!(node instanceof Map)) {
          throw new IllegalArgumentException(
              "Expected object for "
                  + clazz.getName()
                  + ", got "
                  + node.getClass().getSimpleName());
        }
        yield loadObject((Map<String, Object>) node, clazz);
      }
    };
  }

  @SuppressWarnings("unchecked")
  private static <T> T loadBoolean(Object node) {
    if (node instanceof Boolean bool) {
      return (T) bool;
    }
    throw new IllegalArgumentException("Expected boolean, got " + node.getClass().getSimpleName());
  }

  @SuppressWarnings("unchecked")
  private static <T> T loadNumber(Object node, Number num, Class<?> clazz) {
    Function<Number, Object> conv = NUMERIC_CONVERSIONS.get(clazz);
    if (conv != null) {
      return (T) conv.apply(num);
    }
    throw new IllegalArgumentException("Expected number, got " + node.getClass().getSimpleName());
  }

  @SuppressWarnings("unchecked")
  private static <T> T loadString(Object node) {
    if (node instanceof String str) {
      return (T) str;
    }
    throw new IllegalArgumentException("Expected string, got " + node.getClass().getSimpleName());
  }

  @SuppressWarnings("unchecked")
  private static <T> T loadArray(List<?> list, Class<?> clazz) {
    Class<?> componentType = clazz.getComponentType();
    Object result = Array.newInstance(componentType, list.size());
    for (int i = 0; i < list.size(); i++) {
      Array.set(result, i, parseNodeAs(list.get(i), componentType));
    }
    return (T) result;
  }

  @SuppressWarnings("unchecked")
  private static <T> T loadCollection(Type type, List<?> list, Class<?> clazz) {
    Type elementType;
    if (Objects.requireNonNull(type) instanceof ParameterizedType pt) {
      elementType = pt.getActualTypeArguments()[0];
    } else {
      elementType = null;
    }

    Collection<Object> result;
    if (clazz.isAssignableFrom(ArrayList.class)) {
      // Default for `Collection`, `List`, etc: Use arraylist for size reasons
      result = new ArrayList<>(list.size());
    } else if (Collection.class.isAssignableFrom(clazz)) {
      try {
        result = (Collection<Object>) clazz.getConstructor().newInstance();
      } catch (ReflectiveOperationException e) {
        throw new RuntimeException("Failed to instantiate collection class: " + clazz.getName(), e);
      }
    } else {
      throw new IllegalArgumentException("Expected collection, got " + clazz.getSimpleName());
    }

    if (elementType == null) {
      result.addAll(list);
    } else {
      for (Object item : list) {
        result.add(parseNodeAs(item, elementType));
      }
    }
    return (T) result;
  }

  @SuppressWarnings("unchecked")
  private static <T> T loadMap(Type type, Map<?, ?> map, Class<? extends Map<?, ?>> clazz) {
    Type valueType;
    if (Objects.requireNonNull(type) instanceof ParameterizedType pt) {
      valueType = pt.getActualTypeArguments()[1];
    } else {
      valueType = null;
    }

    if (clazz.isAssignableFrom(LinkedHashMap.class)) {
      // Have maps default to LinkedHashMap for predictable order
      Map<String, Object> result = new LinkedHashMap<>(map.size());
      if (valueType == null) {
        result.putAll((Map<? extends String, ?>) map);
      } else {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
          result.put(String.valueOf(entry.getKey()), parseNodeAs(entry.getValue(), valueType));
        }
      }
      return (T) result;
    } else {
      // Generic fallback: assume the class has a public no-argument constructor and instantiate it
      // reflectively
      try {
        Map<String, Object> result = (Map<String, Object>) clazz.getConstructor().newInstance();
        if (valueType == null) {
          result.putAll((Map<? extends String, ?>) map);
        } else {
          for (Map.Entry<?, ?> entry : map.entrySet()) {
            result.put(String.valueOf(entry.getKey()), parseNodeAs(entry.getValue(), valueType));
          }
        }
        return (T) result;
      } catch (ReflectiveOperationException e) {
        throw new IllegalArgumentException(
            "Failed to instantiate map class: " + clazz.getName(), e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> T loadObject(Map<String, Object> node, Class<?> clazz) {
    Constructor<?> ctor = getJsonConstructor(clazz);

    Parameter[] parameters = ctor.getParameters();
    Object[] args = new Object[parameters.length];

    if ((clazz.isMemberClass() || clazz.isLocalClass())
        && !java.lang.reflect.Modifier.isStatic(clazz.getModifiers())) {
      throw new IllegalArgumentException(
          "Nonstatic inner classes cannot be instantiated: " + clazz.getName());
    }

    for (int i = 0; i < parameters.length; i++) {
      Parameter param = parameters[i];
      JsonAttribute attr = param.getAnnotation(JsonAttribute.class);
      if (attr == null) {
        throw new IllegalArgumentException(
            "Parameter "
                + param.getName()
                + " in "
                + clazz.getName()
                + " constructor is missing @JsonAttribute");
      }

      String name = attr.value();
      Object valNode = node.get(name);
      if (valNode == null && !node.containsKey(name)) {
        throw new IllegalArgumentException(
            "Missing property '" + name + "' for " + clazz.getName());
      }

      Type paramType = param.getParameterizedType();
      if (clazz.isRecord()) {
        for (RecordComponent component : clazz.getRecordComponents()) {
          if (component.getName().equals(param.getName())) {
            paramType = component.getGenericType();
            break;
          }
        }
      }
      args[i] = parseNodeAs(valNode, paramType);
    }

    try {
      return (T) ctor.newInstance(args);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException("Failed to instantiate " + clazz.getName(), e);
    }
  }

  @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
  private static Constructor<?> getJsonConstructor(Class<?> clazz) {
    Constructor<?> ctor = null;
    for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
      if (constructor.isAnnotationPresent(JsonConstructor.class)) {
        if (ctor == null) {
          ctor = constructor;
        } else {
          throw new IllegalArgumentException(
              "Only one constructor can be annotated with @JsonConstructor");
        }
      }
    }

    if (ctor == null) {
      throw new IllegalArgumentException("No @JsonConstructor found for " + clazz.getName());
    }

    ctor.setAccessible(true);
    return ctor;
  }
}
