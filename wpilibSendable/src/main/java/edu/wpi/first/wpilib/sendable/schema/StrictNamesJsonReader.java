package edu.wpi.first.wpilib.sendable.schema;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

/**
 * An implementation of a JsonReader that prohibits duplicate object names in the same context.
 * For example, this JSON snippet is invalid:
 * <pre>{@code
 * {
 *   "x": 0,
 *   "x", 1
 * }
 * }</pre>
 *
 * <p>Multiple objects in different contexts, however, are permitted:
 * <pre>{@code
 * {
 *   "x": 0,
 *   "nested": {
 *     "x": 1
 *   }
 * }
 * }</pre>
 *
 * <p>Code taken from: <a href="https://stackoverflow.com/a/53824626/3796335">stackoverflow.com/a/53824626/3796335</a>
 */
final class StrictNamesJsonReader extends JsonReader {
  private final Stack<Set<String>> m_nameStack = new Stack<>();
  private Set<String> m_names;

  private StrictNamesJsonReader(final Reader reader) {
    super(reader);
  }

  public static JsonReader of(final Reader reader) {
    return new StrictNamesJsonReader(reader);
  }

  @Override
  public void beginObject() throws IOException {
    super.beginObject();
    m_names = new HashSet<>();
    m_nameStack.add(m_names);
  }

  @Override
  public String nextName() throws IOException {
    final String name = super.nextName();
    if (!m_names.add(name)) {
      throw new JsonSyntaxException(
          "Detected duplicate property name " + name + " in " + m_names + " at " + this);
    }
    return name;
  }

  @Override
  public void endObject() throws IOException {
    super.endObject();
    m_nameStack.pop();
    m_names = !m_nameStack.isEmpty() ? m_nameStack.peek() : null;
  }

}
