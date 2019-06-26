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
      if (pct > 0) {
        float scale = trim.getMaxPulseWidthMs() - trim.getMidPulseWidthMs();
        leftWheelServo.setPulseWidthMs(trim.getMidPulseWidthMs() + (scale * pct / 100));
      } else if (pct < 0) {
        float scale = trim.getMidPulseWidthMs() - trim.getMinPulseWidthMs();
        leftWheelServo.setPulseWidthMs(trim.getMidPulseWidthMs() + (scale * pct / 100));
      } else {
        leftWheelServo.setPulseWidthMs(trim.getMidPulseWidthMs());
      }
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
      if (pct > 0) {
        float scale = trim.getMaxPulseWidthMs() - trim.getMidPulseWidthMs();
        rightWheelServo.setPulseWidthMs(trim.getMidPulseWidthMs() + (scale * pct / 100));
      } else if (pct < 0) {
        float scale = trim.getMidPulseWidthMs() - trim.getMinPulseWidthMs();
        rightWheelServo.setPulseWidthMs(trim.getMidPulseWidthMs() + (scale * pct / 100));
      } else {
        rightWheelServo.setPulseWidthMs(trim.getMidPulseWidthMs());
      }
    } catch (RuntimeIOException e) {
      Logger.error("Error setting right wheel drive percentage: " + e.getMessage());
    }
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