package devices;

import org.pmw.tinylog.Logger;

/**
 * This class implements the feedback logic of the Hall effect sensor in the Parallax
 * 900-00360 360 high-speed rotatation servo with feedback. See page 5-6 of
 * https://www.pololu.com/file/0J1395/900-00360-Feedback-360-HS-Servo-v1.2.pdf.
 * It relies on getting the duty cycle of the hall-sensor from custom
 * firmware running on a Digispark ATTINY85 breakout board.
 */
public class ParallaxHallEffectFeedbackSensor {
  public enum WheelSide { LEFT, RIGHT };

  private long rotationCount;
  private int fullCircleUnits = 360;
  private int quadrant2Min;
  private int quadrant3Max;
  private int previousTheta;
  private int theta;
  private final WheelSide wheelSide;
  private final DigisparkFeedbackEncoder digisparkFeedbackEncoder;
  private static final int DUTY_CYCLE_MIN = 29;
  private static final int DUTY_CYCLE_MAX = 971;
  
  
  public ParallaxHallEffectFeedbackSensor(DigisparkFeedbackEncoder digisparkFeedbackEncoder, WheelSide wheelSide) {
    this.digisparkFeedbackEncoder = digisparkFeedbackEncoder;
    this.wheelSide = wheelSide;
    computeQuadrants();
  }

  private void computeQuadrants() {
    quadrant2Min = (int)(fullCircleUnits * .25);
    quadrant3Max = (int)(fullCircleUnits * .75) - 1;
  }

  public long getRotationCount() {
    return rotationCount;
  }

  public void clearRotationCount() {
    rotationCount = 0;
  }

  public long getTickCount() {
    if (rotationCount >= 0) {
      return (rotationCount * fullCircleUnits) + theta;
    } else {
      return ((rotationCount + 1) * fullCircleUnits) - (fullCircleUnits - theta);
    }
  }

  public long getTheta() {
    return theta;
  }

  public boolean update() {
    int dutyCycle = 0;
    if (wheelSide == WheelSide.LEFT) {
      dutyCycle = digisparkFeedbackEncoder.getLeftPctX10();
    } else {
      dutyCycle = digisparkFeedbackEncoder.getRightPctX10();
    }
    // Did we get pulses inside the prescribed timing window?
    if (dutyCycle >= DUTY_CYCLE_MIN && dutyCycle <= DUTY_CYCLE_MAX) {
      theta = (fullCircleUnits - 1) - ((dutyCycle - DUTY_CYCLE_MIN) * fullCircleUnits) / (DUTY_CYCLE_MAX - DUTY_CYCLE_MIN + 1);
      if (theta < 0) {
        theta = 0;
      } else if (theta > (fullCircleUnits - 1)) {
        theta = fullCircleUnits - 1;
      }
      if ((theta < quadrant2Min) && (previousTheta > quadrant3Max)) {
        rotationCount++;
      } else if ((previousTheta < quadrant2Min) && (theta > quadrant3Max)) {
        rotationCount++;
      }
      previousTheta = theta;
      return true;
    } else {
      // No, so tell caller that we can not measure angle this trip
      return false;
    }
  }
}