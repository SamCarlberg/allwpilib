package edu.wpi.first.wpilibj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an element as an incubating experimental feature. Any element marked with this annotation
 * may be changed at any point. Any element marked with this annotation is guaranteed to remain in
 * WPILib for at least the season in which it was introduced, but may be moved, renamed, or removed
 * from WPILib prior to any successive kickoff.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({
    ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR,
    ElementType.FIELD,
    ElementType.METHOD,
    ElementType.MODULE,
    ElementType.PACKAGE,
    ElementType.TYPE,
    ElementType.TYPE_USE
})
public @interface Incubating {
  /**
   * The FRC season year in which the feature was introduced.
   */
  String since();
}
