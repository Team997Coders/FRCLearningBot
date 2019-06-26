package subsystems;

import commands.*;
import devices.DigisparkFeedbackEncoder;
import devices.ParallaxHallEffectFeedbackSensor;

import com.diozero.devices.Servo;
import com.diozero.devices.Servo.Trim;
import com.diozero.util.RuntimeIOException;

import org.pmw.tinylog.Logger;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

import oi.OperatorInterface;

public class DriveTrain extends Subsystem {
  private final Servo leftWheelServo;
  private final Servo rightWheelServo;
  private final DigisparkFeedbackEncoder digisparkFeedbackEncoder;
  private final ParallaxHallEffectFeedbackSensor leftWheelFeedbackSensor;
  private final ParallaxHallEffectFeedbackSensor rightWheelFeedbackSensor;
  private final Trim trim;
  private final OperatorInterface operatorInterface;
  private Command defaultCommand;

  public DriveTrain(Trim trim, 
      Servo leftWheelServo, 
      Servo rightWheelServo,
      DigisparkFeedbackEncoder digisparkFeedbackEncoder, 
      ParallaxHallEffectFeedbackSensor leftWheelFeedbackSensor,
      ParallaxHallEffectFeedbackSensor rightWheelFeedbackSensor,
      OperatorInterface operatorInterface) {
    super("DriveTrain");
    this.trim = trim;
    this.leftWheelServo = leftWheelServo;
    this.rightWheelServo = rightWheelServo;
    this.digisparkFeedbackEncoder = digisparkFeedbackEncoder;
    this.leftWheelFeedbackSensor = leftWheelFeedbackSensor;
    this.rightWheelFeedbackSensor = rightWheelFeedbackSensor;
    this.operatorInterface = operatorInterface;
  }

  /**
   * Make the left wheel move.
   * @param pct   A percentage of power from -100 to 100.
   */
  public void setLeftWheelDrive(int pct) {
    try {
      setWheelDrive(pct, leftWheelServo);
    } catch (RuntimeIOException e) {
      Logger.error("Error setting left wheel drive percentage: " + e.getMessage());
    }
  }

  /**
   * Make the right wheel move.
   * @param pct   A percentage of power from -100 to 100.
   */
  public void setRightWheelDrive(int pct) {
    try {
      setWheelDrive(pct, rightWheelServo);
    } catch (RuntimeIOException e) {
      Logger.error("Error setting right wheel drive percentage: " + e.getMessage());
    }
  }

  /**
   * Centralized method to make drive train servos move at a given
   * rate with is a percentage of max.
   * @param pct     A percentage of power from -100 to 100
   * @param servo   The servo to set
   */
  private void setWheelDrive(int pct, Servo servo) {
    if (pct > 0) {
      float scale = trim.getMaxPulseWidthMs() - trim.getMidPulseWidthMs();
      servo.setPulseWidthMs(trim.getMidPulseWidthMs() + (scale * pct / 100));
    } else if (pct < 0) {
      float scale = trim.getMidPulseWidthMs() - trim.getMinPulseWidthMs();
      servo.setPulseWidthMs(trim.getMidPulseWidthMs() + (scale * pct / 100));
    } else {
      servo.setPulseWidthMs(trim.getMidPulseWidthMs());
    }
  }

  /**
   * Get the percentage of power being applied to the right wheel.
   * @return  An integer between -100 and 100.
   */
  public int getRightWheelDrive() {
    return getWheelDrive(rightWheelServo);
  }

  /**
   * Get the percentage of power being applied to the left wheel.
   * @return  An integer between -100 and 100.
   */
  public int getLeftWheelDrive() {
    return -getWheelDrive(leftWheelServo);
    /*
    if (pct > 0) {
      return pct - 100;
    } else if (pct < 0) {
      return 100 + pct;
    } else {
      return 0;
    }
    */
  }

  private int getWheelDrive(Servo servo) {
    float pulseWidthMs = servo.getPulseWidthMs();
    float pct = 0;
    if (pulseWidthMs > trim.getMidPulseWidthMs()) {
      float scale = trim.getMaxPulseWidthMs() - trim.getMidPulseWidthMs();
      pct = ((pulseWidthMs - trim.getMidPulseWidthMs()) / scale) * 100;
    } else if (pulseWidthMs < trim.getMidPulseWidthMs()) {
      float scale = trim.getMidPulseWidthMs() - trim.getMinPulseWidthMs();
      pct = ((pulseWidthMs - trim.getMidPulseWidthMs() ) / scale) * 100;
    }
    return (int)Math.round(pct);
  }

  public long getLeftWheelTickCount() {
    return leftWheelFeedbackSensor.getTickCount();
  }

  public long getRightWheelTickCount() {
    return rightWheelFeedbackSensor.getTickCount();
  }

  public void updateFeedbackSensors() {
    digisparkFeedbackEncoder.update();
    leftWheelFeedbackSensor.update();
    rightWheelFeedbackSensor.update();
  }

  public void stop() {
    leftWheelServo.setPulseWidthMs(trim.getMidPulseWidthMs());
    rightWheelServo.setPulseWidthMs(trim.getMidPulseWidthMs());
  }

  protected void initDefaultCommand() {
    defaultCommand = new ArcadeDrive(this, operatorInterface);
    setDefaultCommand(defaultCommand);
  }
}