// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.wpilib.hardware.led;

import static org.wpilib.util.ErrorMessages.requireNonNullParam;

import org.wpilib.LEDReader;
import org.wpilib.LEDWriter;
import org.wpilib.color.Color;
import org.wpilib.color.Color8Bit;
import org.wpilib.hardware.hal.AddressableLEDJNI;
import org.wpilib.hardware.hal.HAL;
import org.wpilib.hardware.hal.PWMJNI;

/**
 * A class for driving addressable LEDs, such as WS2812B, WS2815, and NeoPixels.
 *
 * <p>By default, the timing supports WS2812B and WS2815 LEDs, but is configurable using {@link
 * #setBitTiming(int, int, int, int)}
 *
 * <p>Some LEDs use a different color order than the default GRB. The color order is configurable
 * using {@link #setColorOrder(ColorOrder)}.
 *
 * <p>Only 1 LED driver is currently supported by the roboRIO. However, multiple LED strips can be
 * connected in series and controlled from the single driver.
 */
public class AddressableLED implements AutoCloseable {
  /** Order that color data is sent over the wire. */
  public enum ColorOrder {
    /** RGB order. */
    kRGB(AddressableLEDJNI.COLOR_ORDER_RGB),
    /** RBG order. */
    kRBG(AddressableLEDJNI.COLOR_ORDER_RBG),
    /** BGR order. */
    kBGR(AddressableLEDJNI.COLOR_ORDER_BGR),
    /** BRG order. */
    kBRG(AddressableLEDJNI.COLOR_ORDER_BRG),
    /** GBR order. */
    kGBR(AddressableLEDJNI.COLOR_ORDER_GBR),
    /** GRB order. This is the default order. */
    kGRB(AddressableLEDJNI.COLOR_ORDER_GRB);

    /** The native value for this ColorOrder. */
    public final int value;

    ColorOrder(int value) {
      this.value = value;
    }

    /**
     * Gets a color order from an int value.
     *
     * @param value int value
     * @return color order
     */
    public ColorOrder fromValue(int value) {
      return switch (value) {
        case AddressableLEDJNI.COLOR_ORDER_RBG -> kRBG;
        case AddressableLEDJNI.COLOR_ORDER_BGR -> kBGR;
        case AddressableLEDJNI.COLOR_ORDER_BRG -> kBRG;
        case AddressableLEDJNI.COLOR_ORDER_GRB -> kGRB;
        case AddressableLEDJNI.COLOR_ORDER_GBR -> kGBR;
        case AddressableLEDJNI.COLOR_ORDER_RGB -> kRGB;
        default -> kGRB;
      };
    }
  }

  private final int m_pwmHandle;
  private final int m_handle;

  /**
   * Constructs a new driver for a specific port.
   *
   * @param port the output port to use (Must be a PWM header, not on MXP)
   */
  public AddressableLED(int port) {
    m_pwmHandle = PWMJNI.initializePWMPort(port);
    m_handle = AddressableLEDJNI.initialize(m_pwmHandle);
    HAL.reportUsage("IO", port, "AddressableLED");
  }

  @Override
  public void close() {
    if (m_handle != 0) {
      AddressableLEDJNI.free(m_handle);
    }
    if (m_pwmHandle != 0) {
      PWMJNI.freePWMPort(m_pwmHandle);
    }
  }

  /**
   * Sets the color order for this AddressableLED. The default order is GRB.
   *
   * <p>This will take effect on the next call to {@link #setData(AddressableLEDBuffer)}.
   *
   * @param order the color order
   */
  public void setColorOrder(ColorOrder order) {
    AddressableLEDJNI.setColorOrder(m_handle, order.value);
  }

  /**
   * Sets the length of the LED strip.
   *
   * <p>Calling this is an expensive call, so it's best to call it once, then just update data.
   *
   * <p>The max length is 5460 LEDs.
   *
   * @param length the strip length
   */
  public void setLength(int length) {
    AddressableLEDJNI.setLength(m_handle, length);
  }

  /**
   * Sets the LED output data.
   *
   * <p>If the output is enabled, this will start writing the next data cycle. It is safe to call,
   * even while output is enabled.
   *
   * @param buffer the buffer to write
   */
  public void setData(AddressableLEDBuffer buffer) {
    AddressableLEDJNI.setData(m_handle, buffer.m_buffer);
  }

  /**
   * Sets the bit timing.
   *
   * <p>By default, the driver is set up to drive WS2812B and WS2815, so nothing needs to be set for
   * those.
   *
   * @param highTime0 high time for 0 bit in nanoseconds (default 400 ns)
   * @param lowTime0 low time for 0 bit in nanoseconds (default 900 ns)
   * @param highTime1 high time for 1 bit in nanoseconds (default 900 ns)
   * @param lowTime1 low time for 1 bit in nanoseconds (default 600 ns)
   */
  public void setBitTiming(int highTime0, int lowTime0, int highTime1, int lowTime1) {
    AddressableLEDJNI.setBitTiming(m_handle, highTime0, lowTime0, highTime1, lowTime1);
  }

  /**
   * Sets the sync time.
   *
   * <p>The sync time is the time to hold output so LEDs enable. Default set for WS2812B and WS2815.
   *
   * @param syncTime the sync time in microseconds (default 280 μs)
   */
  public void setSyncTime(int syncTime) {
    AddressableLEDJNI.setSyncTime(m_handle, syncTime);
  }

  /**
   * Starts the output.
   *
   * <p>The output writes continuously.
   */
  public void start() {
    AddressableLEDJNI.start(m_handle);
  }

  /** Stops the output. */
  public void stop() {
    AddressableLEDJNI.stop(m_handle);
  }

  /** Buffer storage for Addressable LEDs. */
  public static class AddressableLEDBuffer implements LEDReader, LEDWriter {
    byte[] m_buffer;

    /**
     * Constructs a new LED buffer with the specified length.
     *
     * @param length The length of the buffer in pixels
     */
    public AddressableLEDBuffer(int length) {
      m_buffer = new byte[length * 4];
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
     * directly to an {@link AddressableLED}, but are useful tools for logically separating
     * different sections of an LED strip for independent control.
     *
     * @param startingIndex the first index in this buffer that the view should encompass
     *     (inclusive)
     * @param endingIndex the last index in this buffer that the view should encompass (inclusive)
     * @return the view object
     */
    public AddressableLEDBufferView createView(int startingIndex, int endingIndex) {
      return new AddressableLEDBufferView(this, startingIndex, endingIndex);
    }
  }

  /**
   * A view of another addressable LED buffer. Views CANNOT be written directly to an LED strip; the
   * backing buffer must be written instead. However, views provide an easy way to split a large LED
   * strip into smaller sections (which may be reversed from the orientation of the LED strip as a
   * whole) that can be animated individually without modifying LEDs outside those sections.
   */
  public static class AddressableLEDBufferView implements LEDReader, LEDWriter {
    private final LEDReader m_backingReader;
    private final LEDWriter m_backingWriter;
    private final int m_startingIndex;
    private final int m_endingIndex;
    private final int m_length;

    /**
     * Creates a new view of a buffer. A view will be reversed if the starting index is after the
     * ending index; writing front-to-back in the view will write in the back-to-front direction on
     * the underlying buffer.
     *
     * @param backingBuffer the backing buffer to view
     * @param startingIndex the index of the LED in the backing buffer that the view should start
     *     from
     * @param endingIndex the index of the LED in the backing buffer that the view should end on
     * @param <B> the type of the buffer object to create a view for
     */
    public <B extends LEDReader & LEDWriter> AddressableLEDBufferView(
        B backingBuffer, int startingIndex, int endingIndex) {
      this(
          requireNonNullParam(backingBuffer, "backingBuffer", "AddressableLEDBufferView"),
          backingBuffer,
          startingIndex,
          endingIndex);
    }

    /**
     * Creates a new view of a buffer. A view will be reversed if the starting index is after the
     * ending index; writing front-to-back in the view will write in the back-to-front direction on
     * the underlying buffer.
     *
     * @param backingReader the backing LED data reader
     * @param backingWriter the backing LED data writer
     * @param startingIndex the index of the LED in the backing buffer that the view should start
     *     from
     * @param endingIndex the index of the LED in the backing buffer that the view should end on
     */
    public AddressableLEDBufferView(
        LEDReader backingReader, LEDWriter backingWriter, int startingIndex, int endingIndex) {
      requireNonNullParam(backingReader, "backingReader", "AddressableLEDBufferView");
      requireNonNullParam(backingWriter, "backingWriter", "AddressableLEDBufferView");

      if (startingIndex < 0 || startingIndex >= backingReader.getLength()) {
        throw new IndexOutOfBoundsException("Start index out of range: " + startingIndex);
      }
      if (endingIndex < 0 || endingIndex >= backingReader.getLength()) {
        throw new IndexOutOfBoundsException("End index out of range: " + endingIndex);
      }

      m_backingReader = backingReader;
      m_backingWriter = backingWriter;

      m_startingIndex = startingIndex;
      m_endingIndex = endingIndex;
      m_length = Math.abs(endingIndex - startingIndex) + 1;
    }

    /**
     * Creates a view that operates on the same range as this one, but goes in reverse order. This
     * is useful for serpentine runs of LED strips connected front-to-end; simply reverse the view
     * for reversed sections and animations will move in the same physical direction along both
     * strips.
     *
     * @return the reversed view
     */
    public AddressableLEDBufferView reversed() {
      return new AddressableLEDBufferView(this, m_length - 1, 0);
    }

    @Override
    public int getLength() {
      return m_length;
    }

    @Override
    public void setRGB(int index, int r, int g, int b) {
      m_backingWriter.setRGB(nativeIndex(index), r, g, b);
    }

    @Override
    public Color getLED(int index) {
      // override to delegate to the backing buffer to avoid 3x native index lookups & bounds checks
      return m_backingReader.getLED(nativeIndex(index));
    }

    @Override
    public Color8Bit getLED8Bit(int index) {
      // override to delegate to the backing buffer to avoid 3x native index lookups & bounds checks
      return m_backingReader.getLED8Bit(nativeIndex(index));
    }

    @Override
    public int getRed(int index) {
      return m_backingReader.getRed(nativeIndex(index));
    }

    @Override
    public int getGreen(int index) {
      return m_backingReader.getGreen(nativeIndex(index));
    }

    @Override
    public int getBlue(int index) {
      return m_backingReader.getBlue(nativeIndex(index));
    }

    /**
     * Checks if this view is reversed with respect to its backing buffer.
     *
     * @return true if the view is reversed, false otherwise
     */
    public boolean isReversed() {
      return m_endingIndex < m_startingIndex;
    }

    /**
     * Converts a view-local index in the range [start, end] to a global index in the range [0,
     * length].
     *
     * @param viewIndex the view-local index
     * @return the corresponding global index
     * @throws IndexOutOfBoundsException if the view index is not contained within the bounds of
     *     this view
     */
    private int nativeIndex(int viewIndex) {
      if (isReversed()) {
        //  0  1  2  3   4  5  6  7   8  9  10
        //  ↓  ↓  ↓  ↓   ↓  ↓  ↓  ↓   ↓  ↓  ↓
        // [_, _, _, _, (d, c, b, a), _, _, _]
        //               ↑  ↑  ↑  ↑
        //               3  2  1  0
        if (viewIndex < 0 || viewIndex > m_startingIndex) {
          throw new IndexOutOfBoundsException(viewIndex);
        }
        return m_startingIndex - viewIndex;
      } else {
        //  0  1  2  3   4  5  6  7   8  9  10
        //  ↓  ↓  ↓  ↓   ↓  ↓  ↓  ↓   ↓  ↓  ↓
        // [_, _, _, _, (a, b, c, d), _, _, _]
        //               ↑  ↑  ↑  ↑
        //               0  1  2  3
        if (viewIndex < 0 || viewIndex > m_endingIndex) {
          throw new IndexOutOfBoundsException(viewIndex);
        }
        return m_startingIndex + viewIndex;
      }
    }
  }
}
