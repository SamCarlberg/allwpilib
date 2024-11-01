// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.epilogue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Placed on a subclass of {@code ClassSpecificLogger}. Epilogue will detect it at compile time and
 * allow logging of data types compatible with the logger.
 *
 * <p>Custom loggers have the following requirements, which are checked at compile time:
 * <ul>
 *   <li>The class declared in the annotation must match the generic type</li>
 *   <li>The class must have a public no-argument constructor</li>
 *   <li>The class must declare a {@code public static final kInstance} field</li>
 * </ul>
 * </p>
 *
 * <pre><code>
 * {@literal @}CustomLoggerFor(VendorMotorType.class)
 *  public class ExampleMotorLogger extends ClassSpecificLogger&lt;VendorMotorType&gt; {
 *    public static final ExampleMotorLogger kInstance = new ExampleMotorLogger();
 *
 *    public ExampleMotorLogger() {
 *      super(VendorMotorType.class);
 *    }
 *
 *   {@literal @}Override
 *    public void tryUpdate(DataLogger dataLogger, VendorMotorType object) {
 *      // ... custom logging code ...
 *    }
 *  }
 * </code></pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CustomLoggerFor {
  /**
   * The class or classes of objects able to be logged with the annotated logger. If multiple
   * classes are provided, they must all inherit from the generic type parameter passed to the
   * {@code extends ClassSpecificLogger} clause in the class declaration.
   *
   * @return the supported data types
   */
  Class<?>[] value();
}
