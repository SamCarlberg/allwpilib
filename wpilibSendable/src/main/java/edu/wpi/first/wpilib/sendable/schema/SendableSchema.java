/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilib.sendable.schema;

import java.util.List;
import java.util.Objects;

public class SendableSchema {
  private final String m_type;
  private final boolean m_isActuator;
  private final boolean m_hasSafeState;
  private final boolean m_hasUpdateTable;
  private final List<SendableProperty> m_properties;

  /**
   * Creates a new Sendable schema.
   *
   * @param type           the type of the sendable
   * @param isActuator     whether or not the sendable is an actuator
   * @param hasSafeState   whether or not the sendable has a "safe state" to reset to
   * @param hasUpdateTable whether or not the sendable has an arbitrary function to update its table
   * @param properties     properties of the sendable
   */
  public SendableSchema(String type,
                        boolean isActuator,
                        boolean hasSafeState,
                        boolean hasUpdateTable,
                        List<SendableProperty> properties) {
    m_type = Objects.requireNonNull(type, "type");
    m_isActuator = isActuator;
    m_hasSafeState = hasSafeState;
    m_hasUpdateTable = hasUpdateTable;
    m_properties = Objects.requireNonNull(properties, "properties");
  }

  public String getType() {
    return m_type;
  }

  public boolean isActuator() {
    return m_isActuator;
  }

  public boolean hasSafeState() {
    return m_hasSafeState;
  }

  public boolean hasUpdateTable() {
    return m_hasUpdateTable;
  }

  public List<SendableProperty> getProperties() {
    return m_properties;
  }

}
