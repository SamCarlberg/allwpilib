// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.wpilibj.commandsv3.button;

import edu.wpi.first.wpilibj.PS5Controller;
import edu.wpi.first.wpilibj.commandsv3.Scheduler;
import edu.wpi.first.wpilibj.event.EventLoop;

/**
 * A version of {@link PS5Controller} with {@link Trigger} factories for command-based.
 *
 * @see PS5Controller
 */
@SuppressWarnings("MethodName")
public class CommandPS5Controller extends CommandGenericHID {
  private final PS5Controller m_hid;

  /**
   * Construct an instance of a device.
   *
   * @param port The port index on the Driver Station that the device is plugged into.
   */
  public CommandPS5Controller(int port) {
    super(port);
    m_hid = new PS5Controller(port);
  }

  /**
   * Get the underlying GenericHID object.
   *
   * @return the wrapped GenericHID object
   */
  @Override
  public PS5Controller getHID() {
    return m_hid;
  }

  /**
   * Constructs an event instance around the L2 button's digital signal.
   *
   * @return an event instance representing the L2 button's digital signal attached to the {@link
   *     Scheduler#getDefaultButtonLoop() default scheduler button loop}.
   */
  public Trigger L2() {
    return L2(Scheduler.getInstance().getDefaultButtonLoop());
  }

  /**
   * Constructs an event instance around the L2 button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the L2 button's digital signal attached to the given
   *     loop.
   */
  public Trigger L2(EventLoop loop) {
    return m_hid.L2(loop).castTo(Trigger::new);
  }

  /**
   * Constructs an event instance around the R2 button's digital signal.
   *
   * @return an event instance representing the R2 button's digital signal attached to the {@link
   *     Scheduler#getDefaultButtonLoop() default scheduler button loop}.
   */
  public Trigger R2() {
    return R2(Scheduler.getInstance().getDefaultButtonLoop());
  }

  /**
   * Constructs an event instance around the R2 button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the R2 button's digital signal attached to the given
   *     loop.
   */
  public Trigger R2(EventLoop loop) {
    return m_hid.R2(loop).castTo(Trigger::new);
  }

  /**
   * Constructs an event instance around the L1 button's digital signal.
   *
   * @return an event instance representing the L1 button's digital signal attached to the {@link
   *     Scheduler#getDefaultButtonLoop() default scheduler button loop}.
   */
  public Trigger L1() {
    return L1(Scheduler.getInstance().getDefaultButtonLoop());
  }

  /**
   * Constructs an event instance around the L1 button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the L1 button's digital signal attached to the given
   *     loop.
   */
  public Trigger L1(EventLoop loop) {
    return m_hid.L1(loop).castTo(Trigger::new);
  }

  /**
   * Constructs an event instance around the R1 button's digital signal.
   *
   * @return an event instance representing the R1 button's digital signal attached to the {@link
   *     Scheduler#getDefaultButtonLoop() default scheduler button loop}.
   */
  public Trigger R1() {
    return R1(Scheduler.getInstance().getDefaultButtonLoop());
  }

  /**
   * Constructs an event instance around the R1 button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the R1 button's digital signal attached to the given
   *     loop.
   */
  public Trigger R1(EventLoop loop) {
    return m_hid.R1(loop).castTo(Trigger::new);
  }

  /**
   * Constructs an event instance around the L3 button's digital signal.
   *
   * @return an event instance representing the L3 button's digital signal attached to the {@link
   *     Scheduler#getDefaultButtonLoop() default scheduler button loop}.
   */
  public Trigger L3() {
    return L3(Scheduler.getInstance().getDefaultButtonLoop());
  }

  /**
   * Constructs an event instance around the L3 button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the L3 button's digital signal attached to the given
   *     loop.
   */
  public Trigger L3(EventLoop loop) {
    return m_hid.L3(loop).castTo(Trigger::new);
  }

  /**
   * Constructs an event instance around the R3 button's digital signal.
   *
   * @return an event instance representing the R3 button's digital signal attached to the {@link
   *     Scheduler#getDefaultButtonLoop() default scheduler button loop}.
   */
  public Trigger R3() {
    return R3(Scheduler.getInstance().getDefaultButtonLoop());
  }

  /**
   * Constructs an event instance around the R3 button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the R3 button's digital signal attached to the given
   *     loop.
   */
  public Trigger R3(EventLoop loop) {
    return m_hid.R3(loop).castTo(Trigger::new);
  }

  /**
   * Constructs an event instance around the square button's digital signal.
   *
   * @return an event instance representing the square button's digital signal attached to the
   *     {@link Scheduler#getDefaultButtonLoop() default scheduler button loop}.
   */
  public Trigger square() {
    return square(Scheduler.getInstance().getDefaultButtonLoop());
  }

  /**
   * Constructs an event instance around the square button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the square button's digital signal attached to the given
   *     loop.
   */
  public Trigger square(EventLoop loop) {
    return m_hid.square(loop).castTo(Trigger::new);
  }

  /**
   * Constructs an event instance around the cross button's digital signal.
   *
   * @return an event instance representing the cross button's digital signal attached to the {@link
   *     Scheduler#getDefaultButtonLoop() default scheduler button loop}.
   */
  public Trigger cross() {
    return cross(Scheduler.getInstance().getDefaultButtonLoop());
  }

  /**
   * Constructs an event instance around the cross button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the cross button's digital signal attached to the given
   *     loop.
   */
  public Trigger cross(EventLoop loop) {
    return m_hid.cross(loop).castTo(Trigger::new);
  }

  /**
   * Constructs an event instance around the triangle button's digital signal.
   *
   * @return an event instance representing the triangle button's digital signal attached to the
   *     {@link Scheduler#getDefaultButtonLoop() default scheduler button loop}.
   */
  public Trigger triangle() {
    return triangle(Scheduler.getInstance().getDefaultButtonLoop());
  }

  /**
   * Constructs an event instance around the triangle button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the triangle button's digital signal attached to the
   *     given loop.
   */
  public Trigger triangle(EventLoop loop) {
    return m_hid.triangle(loop).castTo(Trigger::new);
  }

  /**
   * Constructs an event instance around the circle button's digital signal.
   *
   * @return an event instance representing the circle button's digital signal attached to the
   *     {@link Scheduler#getDefaultButtonLoop() default scheduler button loop}.
   */
  public Trigger circle() {
    return circle(Scheduler.getInstance().getDefaultButtonLoop());
  }

  /**
   * Constructs an event instance around the circle button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the circle button's digital signal attached to the given
   *     loop.
   */
  public Trigger circle(EventLoop loop) {
    return m_hid.circle(loop).castTo(Trigger::new);
  }

  /**
   * Constructs an event instance around the create button's digital signal.
   *
   * @return an event instance representing the create button's digital signal attached to the
   *     {@link Scheduler#getDefaultButtonLoop() default scheduler button loop}.
   */
  public Trigger create() {
    return create(Scheduler.getInstance().getDefaultButtonLoop());
  }

  /**
   * Constructs an event instance around the create button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the create button's digital signal attached to the given
   *     loop.
   */
  public Trigger create(EventLoop loop) {
    return m_hid.create(loop).castTo(Trigger::new);
  }

  /**
   * Constructs an event instance around the PS button's digital signal.
   *
   * @return an event instance representing the PS button's digital signal attached to the {@link
   *     Scheduler#getDefaultButtonLoop() default scheduler button loop}.
   */
  public Trigger PS() {
    return PS(Scheduler.getInstance().getDefaultButtonLoop());
  }

  /**
   * Constructs an event instance around the PS button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the PS button's digital signal attached to the given
   *     loop.
   */
  public Trigger PS(EventLoop loop) {
    return m_hid.PS(loop).castTo(Trigger::new);
  }

  /**
   * Constructs an event instance around the options button's digital signal.
   *
   * @return an event instance representing the options button's digital signal attached to the
   *     {@link Scheduler#getDefaultButtonLoop() default scheduler button loop}.
   */
  public Trigger options() {
    return options(Scheduler.getInstance().getDefaultButtonLoop());
  }

  /**
   * Constructs an event instance around the options button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the options button's digital signal attached to the
   *     given loop.
   */
  public Trigger options(EventLoop loop) {
    return m_hid.options(loop).castTo(Trigger::new);
  }

  /**
   * Constructs an event instance around the touchpad's digital signal.
   *
   * @return an event instance representing the touchpad's digital signal attached to the {@link
   *     Scheduler#getDefaultButtonLoop() default scheduler button loop}.
   */
  public Trigger touchpad() {
    return touchpad(Scheduler.getInstance().getDefaultButtonLoop());
  }

  /**
   * Constructs an event instance around the touchpad's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the touchpad's digital signal attached to the given
   *     loop.
   */
  public Trigger touchpad(EventLoop loop) {
    return m_hid.touchpad(loop).castTo(Trigger::new);
  }

  /**
   * Get the X axis value of left side of the controller.
   *
   * @return the axis value.
   */
  public double getLeftX() {
    return m_hid.getLeftX();
  }

  /**
   * Get the X axis value of right side of the controller.
   *
   * @return the axis value.
   */
  public double getRightX() {
    return m_hid.getRightX();
  }

  /**
   * Get the Y axis value of left side of the controller.
   *
   * @return the axis value.
   */
  public double getLeftY() {
    return m_hid.getLeftY();
  }

  /**
   * Get the Y axis value of right side of the controller.
   *
   * @return the axis value.
   */
  public double getRightY() {
    return m_hid.getRightY();
  }

  /**
   * Get the L2 axis value of the controller. Note that this axis is bound to the range of [0, 1] as
   * opposed to the usual [-1, 1].
   *
   * @return the axis value.
   */
  public double getL2Axis() {
    return m_hid.getL2Axis();
  }

  /**
   * Get the R2 axis value of the controller. Note that this axis is bound to the range of [0, 1] as
   * opposed to the usual [-1, 1].
   *
   * @return the axis value.
   */
  public double getR2Axis() {
    return m_hid.getR2Axis();
  }
}