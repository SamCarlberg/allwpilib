/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.command.experimental;

import edu.wpi.first.wpilibj.annotation.Incubating;

/**
 * A {@code TimeUnit} represents time durations at a given unit of granularity and provides utility
 * methods to convert across units, and to perform timing and delay operations in these units. A
 * {@code TimeUnit} does not maintain time information, but only helps organize and use time
 * representations that may be maintained separately across various contexts.
 *
 * <p>A {@code TimeUnit} is mainly used to inform time-based methods and constructors how a given
 * timing parameter should be interpreted. Note however, that there is no guarantee that a
 * particular timeout implementation will be able to notice the passage of time at the same
 * granularity as the given {@code TimeUnit}.
 */
@Incubating(since = "2020")
public enum TimeUnit {
  /**
   * A time unit representing one millionth of a second.
   */
  MICROSECONDS {
    @Override
    double toMicros(double duration) {
      return duration;
    }

    @Override
    double toMillis(double duration) {
      return duration / 1e3;
    }

    @Override
    double toSeconds(double duration) {
      return duration / 1e6;
    }
  },

  /**
   * A time unit representing one thousandth of a second.
   */
  MILLISECONDS {
    @Override
    double toMicros(double duration) {
      return duration * 1e3;
    }

    @Override
    double toMillis(double duration) {
      return duration;
    }

    @Override
    double toSeconds(double duration) {
      return duration / 1e3;
    }
  },

  /**
   * A time unit representing one second.
   */
  SECONDS {
    @Override
    double toMicros(double duration) {
      return duration / 1e6;
    }

    @Override
    double toMillis(double duration) {
      return duration / 1e3;
    }

    @Override
    double toSeconds(double duration) {
      return duration;
    }
  };

  /**
   * Converts a duration of the native unit to microseconds.
   *
   * @param duration the duration to convert
   * @return the microseconds equivalent of the duration
   */
  abstract double toMicros(double duration);

  /**
   * Converts a duration of the native unit to milliseconds.
   *
   * @param duration the duration to convert
   * @return the milliseconds equivalent of the duration
   */
  abstract double toMillis(double duration);

  /**
   * Converts a duration of the native unit to seconds.
   *
   * @param duration the duration to convert
   * @return the seconds equivalent of the duration
   */
  abstract double toSeconds(double duration);
}
