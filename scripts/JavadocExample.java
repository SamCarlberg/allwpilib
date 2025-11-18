package org.wpilib.command3;

import java.lang.Object;
import java.lang.Object;
import java.lang.Object;

/**
 * <h1>Starting off </h1>
 *
 * <ol>
 *   <li>First
 *   <li>Second
 *   <li>Third
 * </ol>
 *
 * <h1>After the list</h1>
 *
 * Some more text
 *
 * <pre>{@code
 * void sampleCode(int a, int b) {
 *   if (a > b) {
 *     return a + b;
 *   } else {
 *     return a - b;
 *   }
 * }
 * }</pre>
 *
 * Here's a link to {@link #toString() to string }    and  xys   {@link #hashCode() hash  code  } {@link #equals(Object)} right in the middle of a goddamn line.
 * I'm continuing the paragraph here and should stay in the same paragraph in the resulting markdown.
 *
 * Here's the start of a new thought that actually belongs to the sentences above because there's no {@code <p>} tag saying no
 *
 * <table>
 *   <thead><tr><th>Fibonacci Number</th><th>Value</th></tr></thead>
 *   <tbody>
 *     <tr><td>1</td><td>1</td></tr>
 *     <tr><td>2</td><td>1</td></tr>
 *     <tr><td>3</td><td>2</td></tr>
 *     <tr><td>4</td><td>3</td></tr>
 *     <tr><td>5</td><td>5</td></tr>
 *     <tr><td>6</td><td>8</td></tr>
 *     <tr><td>7</td><td>13</td></tr>
 *     <tr><td>8</td><td>21</td></tr>
 *     <tr><td>9</td><td>34</td></tr>
 *     <tr><td>10</td><td>55</td></tr>
 *     <tr><td>11</td><td><a href=https://en.wikipedia.org/wiki/Fibonacci_Sequence>Wikipedia</a></td></tr>
 *   </tbody>
 * </table>
 */
public class JavadocExample {
  /**
   * Provide string description of video mode.
   *
   * <p>The returned string is "{width}x{height} {format} {fps} fps".
   *
   * You need to use {@link #thisHasGenericParams(java.lang.Object, java.lang.Object, java.lang.Object)} first
   * or the {@link JavadocExample example type}
   */
  public void thisHasRawBraces() {

  }

  /**
   * [a]T x [b] = [C]
   */
  public void thisHasRawSquareBrackets() {

  }

  /**
   * {@code [a] T x [b] = [C]}
   * <pre>{@code
   * [ a ] T x [ b ] = [ C ]
   * }</pre>
   *
   */
  public void thisHasSquareBracketsInCodeBlocks() {

  }

  /**
   *This method has generic type parameters.
   * @param t a tee
   * @param u a yew
   * @param v a vew
   * @return something?
   * @param <T> the type of tea
   * @param <U> the type of yew.
   * @param <V> the type of vee
   * @throws java.lang.RuntimeException
   */
  public <T, U, V> T thisHasGenericParams(T t, U u, V v) {
    return t;
  }

  /**
   * The param description for "b" should not be merged into the description for "a".
   *
   * {@link #thisHasParamsWithSquareBracketDescriptions(int, int) (int, int)}
   *
   * @param a a value in [0, 1]
   * @param b a value in [0, 1]
   */
  public void thisHasParamsWithSquareBracketDescriptions(int a, int b) {}

  /**
   * Here's a place, or something.
   *
   * <a href=https://en.wikipedia.org/wiki/Fibonacci_number>
   *   A link on many
   *   lines</a>
   *
   * <p>This link should be square-brackets-and-parens <a href=https://example.com>https://example.com</a>
   *
   * {@link java.lang.String strings
   * are useful
   * constructs}
   *
   * @param x something
   * @see <a href=https://en.wikipedia.org/wiki/Fibonacci_number>An external link</a>
   * @see <a href=https://en.wikipedia.org/wiki/Fibonacci_number>
   *   A different external link</a>
   * @throws java.lang.RuntimeException When you fail
   * @param <X> the type of X
   */
  public <X> void thisLinksWithSee(int x) {}
}
