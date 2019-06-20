package devices;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.diozero.api.DigitalInputDevice;
import com.diozero.api.DigitalInputEvent;
import com.diozero.api.GpioEventTrigger;
import com.diozero.api.GpioPullUpDown;
import com.diozero.api.InputEventListener;
import com.diozero.util.RuntimeIOException;

import org.pmw.tinylog.Logger;

/**
 * This class implements the feedback logic of the Hall effect sensor in the Parallax
 * 900-00360 360 high-speed rotatation servo with feedback. See page 5-6 of
 * https://www.pololu.com/file/0J1395/900-00360-Feedback-360-HS-Servo-v1.2.pdf
 */
public class ParallaxHallEffectFeedbackSensor 
    extends DigitalInputDevice 
    implements InputEventListener<DigitalInputEvent> {
  private long tHigh;
  private long tLow;
  private long lastNanos;
  private boolean lastValue;
  private long rotationCount;
  private int fullCircleUnits = 360;
  private int quadrant2Min;
  private int quadrant3Max;
  private int previousTheta;
  private int theta;
  private static final int DUTY_CYCLE_MIN = 29;
  private static final int DUTY_CYCLE_MAX = 971;
  private Lock lock;
  
  public ParallaxHallEffectFeedbackSensor(int gpio) throws RuntimeIOException {
    super(gpio, GpioPullUpDown.NONE, GpioEventTrigger.BOTH);
    lock = new ReentrantLock();
    computeQuadrants();
    addListener(this);
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
    // Fetch timing values locally so we do not block valueChanged event processing
    long tHigh = 0;
    long tLow = 0;
    long tCycle = 0;
    lock.lock();
    try {
      tHigh = this.tHigh;
      tLow = this.tLow;
    } finally {
      lock.unlock();
    }

    tCycle = tHigh + tLow;
    if (gpio == 27) {
      Logger.info("tCycle = " + tCycle);
    }
    // Did we get pulses inside the prescribed timing window?
    if (tCycle > 1000000 && tCycle < 1200000) {
      int dutyCycle = (int)((100 * tHigh) / tCycle);
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
      // No, so tell caller that we can measure angle this trip
      return false;
    }
  }

  @Override
  public void valueChanged(DigitalInputEvent event) {
    long nanoTime = event.getNanoTime();
    boolean value = event.getValue();

    // Records high pulse time
    if (value == false && lastValue == true) {
      lock.lock();
      try {
        tHigh = nanoTime - lastNanos;
        lastValue = value;
        lastNanos = nanoTime;
      } finally {
        lock.unlock();
      }
    // Records low pulse time
    } else if (value == true && lastValue == false) {
      lock.lock();
      try {
        tLow = nanoTime - lastNanos;
        lastValue = value;
        lastNanos = nanoTime;
      } finally {
        lock.unlock();
      }
    } 
  }
}