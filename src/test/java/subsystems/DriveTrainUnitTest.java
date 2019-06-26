package subsystems;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.diozero.devices.Servo;

import org.junit.*;

import devices.DigisparkFeedbackEncoder;
import devices.ParallaxHallEffectFeedbackSensor;
import oi.OperatorInterface;

public class DriveTrainUnitTest {
  @Test 
  public void itShouldSetRightWheelWithPositivePct() {
    // Assemble
    Servo rightServoMock = mock(Servo.class);
    when(rightServoMock.getPulseWidthMs()).thenReturn(1.566f);
    Servo leftServoMock = mock(Servo.class);
    DigisparkFeedbackEncoder encoderMock = mock(DigisparkFeedbackEncoder.class);
    ParallaxHallEffectFeedbackSensor rightSensorMock = mock(ParallaxHallEffectFeedbackSensor.class);
    ParallaxHallEffectFeedbackSensor leftSensorMock = mock(ParallaxHallEffectFeedbackSensor.class);
    Servo.Trim trim = new Servo.Trim(1.5f, 1.5f, 1.28f, 1.72f);
    OperatorInterface oiMock = mock(OperatorInterface.class);
    DriveTrain driveTrain = new DriveTrain(trim, leftServoMock, rightServoMock, encoderMock, leftSensorMock, rightSensorMock, oiMock);

    // Act
    driveTrain.setRightWheelDrive(30);

    // Assert
    verify(rightServoMock, times(1)).setPulseWidthMs(1.566f);
    assertEquals(30, driveTrain.getRightWheelDrive());
  }
}