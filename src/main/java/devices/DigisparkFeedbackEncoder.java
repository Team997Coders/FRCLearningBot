package devices;

import com.diozero.api.I2CConstants;
import com.diozero.api.I2CDevice;

/**
 * This class reads the custom firmware programmed into an
 * ATTINY85 microcontroller which interprets the duty cycle
 * pulses from up to two Parallax 360 high speed servos
 * with hall-sensor feedback encoders. The values are
 * read over bus 1 of the Pi I2C bus.
 */
public class DigisparkFeedbackEncoder {
  private static final int DIGISPARK_ADDRESS = 0x04;

  // Registers
  private static final byte LEFT_PCT_X_10 = 0x00;
  private static final byte RIGHT_PCT_X_10 = 0x02;

  private I2CDevice device;
  private int leftPctX10;
  private int rightPctX10;

  public DigisparkFeedbackEncoder() {
    device = new I2CDevice(
        I2CConstants.BUS_1, 
        DIGISPARK_ADDRESS, 
        I2CConstants.ADDR_SIZE_7, 
        I2CConstants.DEFAULT_CLOCK_FREQUENCY);
  }

  public DigisparkFeedbackEncoder(I2CDevice device) {
    this.device = device;
    this.leftPctX10 = 0;
    this.rightPctX10 = 0;
  }

  public void update() {
    device.writeByte(LEFT_PCT_X_10);
    // left must be read first
    byte leftPctX10L = device.readByte();
    byte leftPctX10H = device.readByte();
    leftPctX10 = ((leftPctX10H & 0xff) << 8) | (leftPctX10L & 0xff);
    byte rightPctX10L = device.readByte();
    byte rightPctX10H = device.readByte();
    rightPctX10 = ((rightPctX10H & 0xff) << 8) | (rightPctX10L & 0xff);
  }

  public int getLeftPctX10() {
    return leftPctX10;
  }

  public int getRightPctX10() {
    return rightPctX10;
  }
}