// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.util.struct;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collection;

/**
 * Reusable buffer for serialization/deserialization to/from a raw struct.
 *
 * @param <T> Object type.
 */
public final class StructBuffer<T> {
  private StructBuffer(Struct<T> struct) {
    m_struct = struct;
  }

  /**
   * Returns a StructBuffer for the given struct.
   *
   * @param struct A struct.
   * @param <T> Object type.
   * @return A StructBuffer for the given struct.
   */
  public static <T> StructBuffer<T> create(Struct<T> struct) {
    return new StructBuffer<>(struct);
  }

  /**
   * Gets the struct object of the stored type.
   *
   * @return struct object
   */
  public Struct<T> getStruct() {
    return m_struct;
  }

  /**
   * Gets the type string.
   *
   * @return type string
   */
  public String getTypeString() {
    return m_struct.getTypeString();
  }

  /**
   * Ensures sufficient buffer space is available for the given number of elements. This can only
   * be used with fixed-length structs.
   *
   * @param nelem number of elements
   */
  public void reserve(int nelem) {
    requireCapacity(nelem * m_struct.getSize());
  }

  /**
   * Serializes a value to a ByteBuffer. The returned ByteBuffer is a direct byte buffer with the
   * position set to the end of the serialized data.
   *
   * @param value value
   * @return byte buffer
   */
  public ByteBuffer write(T value) {
    int requiredCapacity = m_struct.getSerializedSize(value);
    requireCapacity(requiredCapacity);

    m_buf.position(0);
    m_struct.pack(m_buf, value);
    return m_buf;
  }

  /**
   * Deserializes a value from a byte array, creating a new object.
   *
   * @param buf byte array
   * @param start starting location within byte array
   * @param len length of serialized data
   * @return new object
   */
  public T read(byte[] buf, int start, int len) {
    return read(ByteBuffer.wrap(buf, start, len));
  }

  /**
   * Deserializes a value from a byte array, creating a new object.
   *
   * @param buf byte array
   * @return new object
   */
  public T read(byte[] buf) {
    return read(buf, 0, buf.length);
  }

  /**
   * Deserializes a value from a ByteBuffer, creating a new object.
   *
   * @param buf byte buffer
   * @return new object
   */
  public T read(ByteBuffer buf) {
    buf.order(ByteOrder.LITTLE_ENDIAN);
    return m_struct.unpack(buf);
  }

  /**
   * Deserializes a value from a byte array into a mutable object.
   *
   * @param out object (will be updated with deserialized contents)
   * @param buf byte array
   * @param start starting location within byte array
   * @param len length of serialized data
   * @throws UnsupportedOperationException if T is immutable
   */
  public void readInto(T out, byte[] buf, int start, int len) {
    readInto(out, ByteBuffer.wrap(buf, start, len));
  }

  /**
   * Deserializes a value from a byte array into a mutable object.
   *
   * @param out object (will be updated with deserialized contents)
   * @param buf byte array
   * @throws UnsupportedOperationException if T is immutable
   */
  public void readInto(T out, byte[] buf) {
    readInto(out, buf, 0, buf.length);
  }

  /**
   * Deserializes a value from a ByteBuffer into a mutable object.
   *
   * @param out object (will be updated with deserialized contents)
   * @param buf byte buffer
   * @throws UnsupportedOperationException if T is immutable
   */
  public void readInto(T out, ByteBuffer buf) {
    m_struct.unpackInto(out, buf);
  }

  /**
   * Serializes a collection of values to a ByteBuffer. The returned ByteBuffer is a direct byte
   * buffer with the position set to the end of the serialized data.
   *
   * @param values values
   * @param fixedLength if the values should be encoded as a fixed-length array. If false, an int32
   *                    field will be prepended
   * @return byte buffer
   */
  public ByteBuffer writeArray(Collection<T> values) {
    int totalCapacity =
        Struct.kSizeInt32 + values.stream().mapToInt(m_struct::getSerializedSize).sum();

    requireCapacity(totalCapacity);

    m_buf.position(0);

    m_buf.putInt(values.size());
    for (T v : values) {
      m_struct.pack(m_buf, v);
    }
    return m_buf;
  }

  /**
   * Serializes an array of values to a ByteBuffer. The returned ByteBuffer is a direct byte buffer
   * with the position set to the end of the serialized data.
   *
   * @param values values
   * @return byte buffer
   */
  public ByteBuffer writeArray(T[] values) {
    int totalCapacity =
        Struct.kSizeInt32 + Arrays.stream(values).mapToInt(m_struct::getSerializedSize).sum();

    requireCapacity(totalCapacity);

    m_buf.position(0);

    m_buf.putInt(values.length);
    for (T v : values) {
      m_struct.pack(m_buf, v);
    }
    return m_buf;
  }

  /**
   * Deserializes an array of values from a byte array, creating an array of new objects.
   *
   * @param buf byte array
   * @param start starting location within byte array
   * @param len length of serialized data
   * @return new object array
   */
  public T[] readArray(byte[] buf, int start, int len) {
    return readArray(ByteBuffer.wrap(buf, start, len));
  }

  /**
   * Deserializes an array of values from a byte array, creating an array of new objects.
   *
   * @param buf byte array
   * @return new object array
   */
  public T[] readArray(byte[] buf) {
    return readArray(buf, 0, buf.length);
  }

  /**
   * Deserializes an array of values from a ByteBuffer, creating an array of new objects.
   *
   * @param buf byte buffer
   * @return new object array
   * @throws java.nio.BufferUnderflowException if the encoded array size at the current buffer
   *   position is greater than the remaining readable capacity of the buffer
   */
  public T[] readArray(ByteBuffer buf) {
    buf.order(ByteOrder.LITTLE_ENDIAN);
    int nelem = buf.getInt();
    @SuppressWarnings("unchecked")
    T[] arr = (T[]) Array.newInstance(m_struct.getTypeClass(), nelem);
    for (int i = 0; i < nelem; i++) {
      arr[i] = m_struct.unpack(buf);
    }
    return arr;
  }

  private void requireCapacity(int capacity) {
    int allocationSize;

    if (m_buf == null) {
      allocationSize = capacity;
    } else if (m_buf.capacity() < capacity) {
      // Current capacity is too small - need to resize
      allocationSize = capacity * 2;
    } else {
      return;
    }

    m_buf = ByteBuffer.allocateDirect(allocationSize).order(ByteOrder.LITTLE_ENDIAN);
  }

  private ByteBuffer m_buf;
  private final Struct<T> m_struct;
}
