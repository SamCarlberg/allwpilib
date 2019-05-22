package edu.wpi.first.wpilib.sendable.schema;

import java.util.Objects;

public class SendableProperty {
  private final String m_name;
  private final SendablePropertyType m_type;
  private final boolean m_set;
  private final boolean m_metadata;

  /**
   * Creates a new sendable property.
   *
   * @param name     the name of the property
   * @param type     the type of the property
   * @param set      whether or not the property has a 'setter' function
   * @param metadata whether or not the property is metadata
   */
  public SendableProperty(String name,
                          SendablePropertyType type,
                          boolean set,
                          boolean metadata) {
    m_name = Objects.requireNonNull(name, "name");
    m_type = Objects.requireNonNull(type, "type");
    m_set = set;
    m_metadata = metadata;
  }

  public String getName() {
    return m_name;
  }

  public SendablePropertyType getType() {
    return m_type;
  }

  public boolean hasSetter() {
    return m_set;
  }

  public boolean isMetadata() {
    return m_metadata;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    SendableProperty that = (SendableProperty) obj;
    return m_set == that.m_set
        && m_metadata == that.m_metadata
        && m_name.equals(that.m_name)
        && m_type == that.m_type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(m_name, m_type, m_set, m_metadata);
  }

}
