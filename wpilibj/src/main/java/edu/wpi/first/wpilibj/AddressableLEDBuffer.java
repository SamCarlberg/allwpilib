// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.wpilibj;

import edu.wpi.first.util.struct.Struct;
import edu.wpi.first.util.struct.StructSerializable;
import edu.wpi.first.wpilibj.util.Color8Bit;
import java.nio.ByteBuffer;

/** Buffer storage for Addressable LEDs. */
public class AddressableLEDBuffer implements LEDReader, LEDWriter, StructSerializable {
  byte[] m_buffer;

  // Will be lazily initialized if needed
  public static final AddressableLEDBufferStruct struct = new AddressableLEDBufferStruct();

  /**
   * Constructs a new LED buffer with the specified length.
   *
   * @param length The length of the buffer in pixels
   */
  public AddressableLEDBuffer(int length) {
    m_buffer = new byte[length * 4];
  }

  /**
   * Gets raw access to the full data buffer that will be sent to the addressable LED strip. Data
   * is structured in repeating [B, G, R, 0] four-byte groups. If you want to change the colors in
   * the buffer, use {@link #setRGB(int, int, int, int)} instead of modifying the buffer directly;
   * similarly, for reading colors, use {@link #getRed(int)}, {@link #getGreen(int)}, and
   * {@link #getBlue(int)}. This method is primarily intended for serialization.
   *
   * @return the raw data buffer
   */
  public byte[] getRawBuffer() {
    return m_buffer;
  }

  /**
   * Sets a specific led in the buffer.
   *
   * @param index the index to write
   * @param r the r value [0-255]
   * @param g the g value [0-255]
   * @param b the b value [0-255]
   */
  @Override
  public void setRGB(int index, int r, int g, int b) {
    m_buffer[index * 4] = (byte) b;
    m_buffer[(index * 4) + 1] = (byte) g;
    m_buffer[(index * 4) + 2] = (byte) r;
    m_buffer[(index * 4) + 3] = 0;
  }

  /**
   * Gets the buffer length.
   *
   * @return the buffer length
   */
  @Override
  public int getLength() {
    return m_buffer.length / 4;
  }

  /**
   * Gets the red channel of the color at the specified index.
   *
   * @param index the index of the LED to read
   * @return the value of the red channel, from [0, 255]
   */
  @Override
  public int getRed(int index) {
    return m_buffer[index * 4 + 2] & 0xFF;
  }

  /**
   * Gets the green channel of the color at the specified index.
   *
   * @param index the index of the LED to read
   * @return the value of the green channel, from [0, 255]
   */
  @Override
  public int getGreen(int index) {
    return m_buffer[index * 4 + 1] & 0xFF;
  }

  /**
   * Gets the blue channel of the color at the specified index.
   *
   * @param index the index of the LED to read
   * @return the value of the blue channel, from [0, 255]
   */
  @Override
  public int getBlue(int index) {
    return m_buffer[index * 4] & 0xFF;
  }

  /**
   * Creates a view of a subsection of this data buffer, starting from (and including) {@code
   * startingIndex} and ending on (and including) {@code endingIndex}. Views cannot be written
   * directly to an {@link AddressableLED}, but are useful tools for logically separating different
   * sections of an LED strip for independent control.
   *
   * @param startingIndex the first index in this buffer that the view should encompass (inclusive)
   * @param endingIndex the last index in this buffer that the view should encompass (inclusive)
   * @return the view object
   */
  public AddressableLEDBufferView createView(int startingIndex, int endingIndex) {
    return new AddressableLEDBufferView(this, startingIndex, endingIndex);
  }

  public static final class BGRStruct implements Struct<Color8Bit> {
    public static final BGRStruct kInstance = new BGRStruct();

    @Override
    public Class<Color8Bit> getTypeClass() {
      return Color8Bit.class;
    }

    @Override
    public String getTypeName() {
      return "ColorBGR";
    }

    @Override
    public int getSize() {
      return kSizeInt8 * 3;
    }

    @Override
    public String getSchema() {
      return "uint8 blue;uint8 green;unit8 red";
    }

    @Override
    public Color8Bit unpack(ByteBuffer bb) {
      int blue = bb.get() & 0xFF;
      int green = bb.get() & 0xFF;
      int red = bb.get() & 0xFF;

      return new Color8Bit(red, green, blue);
    }

    @Override
    public void pack(ByteBuffer bb, Color8Bit value) {
      bb.put((byte) value.blue);
      bb.put((byte) value.green);
      bb.put((byte) value.red);
    }
  }

  public static final class AddressableLEDBufferStruct implements Struct<AddressableLEDBuffer> {
    @Override
    public Class<AddressableLEDBuffer> getTypeClass() {
      return AddressableLEDBuffer.class;
    }

    @Override
    public String getTypeName() {
      return "AddressableLEDBuffer";
    }

    @Override
    public int getSize() {
      throw new UnsupportedOperationException("Addressable LED buffers do not have a fixed size");
    }

    @Override
    public int getSerializedSize(AddressableLEDBuffer buffer) {
      // note: assumes the buffer is well-formed and has a length that's a multiple of four
      return (buffer.m_buffer.length / 4) * 3;
    }

    @Override
    public String getSchema() {
      return "ColorBGR data[varying]";
    }

    @Override
    public Struct<?>[] getNested() {
      return new Struct<?>[] { BGRStruct.kInstance };
    }

    @Override
    public AddressableLEDBuffer unpack(ByteBuffer bb) {
      int size = bb.getInt(); // number of bytes, 3x number of pixels

      AddressableLEDBuffer buffer = new AddressableLEDBuffer(size / 3);
      for (int i = 0; i < buffer.m_buffer.length; i++) {
        if (i % 4 == 3) {
          // Padding bytes on 3, 7, 11, 15, etc. (1 less than a multiple of 4)
          buffer.m_buffer[i] = 0;
        } else {
          buffer.m_buffer[i] = bb.get();
        }
      }

      return buffer;
    }

    @Override
    public void pack(ByteBuffer bb, AddressableLEDBuffer buffer) {
      // Packed data does not include the padding bytes; multiply by 3/4
      bb.putInt((buffer.m_buffer.length / 4) * 3);

      byte[] bgr0 = buffer.getRawBuffer();

      for (int i = 0; i < bgr0.length; i += 4) {
        bb.put(bgr0[i + 0]); // Blue
        bb.put(bgr0[i + 1]); // Green
        bb.put(bgr0[i + 2]); // Red
        // bb.put(bgr0[i + 3]); // Deliberately NOT including the padding byte
      }
    }
  }
}
