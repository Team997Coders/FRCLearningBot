import com.diozero.devices.Servo;

import edu.wpi.first.wpilibj.command.Scheduler;

import net.java.games.input.*;

import oi.JInputJoystick;
import oi.OperatorInterface;

import org.pmw.tinylog.Logger;
import org.team997coders.wpilibj.SchedulerTask;

import devices.DigisparkFeedbackEncoder;
import devices.ParallaxHallEffectFeedbackSensor;
import subsystems.DriveTrain;

// This robot program uses continuous rotation servos from Parallax and the Servo class in diozero.
// See https://www.parallax.com/sites/default/files/downloads/900-00008-Continuous-Rotation-Servo-Documentation-v2.2.pdf
// for Parallax continous servo documentation.


public class Main {
  // Define constants
  private static final int leftWheelPin = 24;     // PI GPIO 24 is the left wheel, pin 13 on Stamp board
  private static final int rightWheelPin = 23;    // PI GPIO 23 is the right wheel, pin 12 on Stamp board

  public static void main(String[] args) throws InterruptedException {
    // Create a gamepad
    JInputJoystick gamepad = new JInputJoystick(Controller.Type.STICK, Controller.Type.GAMEPAD);

    // Check if a gamepad was found.
    if (!gamepad.isControllerConnected()){
      Logger.info("No gamepad controller found!");
      System.exit(1);
    }

    // Instantiate hardware
//    Servo.Trim trim = new Servo.Trim(1.5f, 1.5f, 1.3f, 1.7f);   // For slow parallax 360 rotation servos
    Servo.Trim trim = new Servo.Trim(1.5f, 1.5f, 1.28f, 1.72f);
    try (Servo leftWheelServo = new Servo(leftWheelPin, trim.getMaxPulseWidthMs(), 50, trim);
        Servo rightWheelServo = new Servo(rightWheelPin, trim.getMinPulseWidthMs(), 50, trim)) {
      leftWheelServo.setInverted(true);

      // Set up communication with servo feedback encoders
      DigisparkFeedbackEncoder digisparkFeedbackEncoder = new DigisparkFeedbackEncoder();
      ParallaxHallEffectFeedbackSensor leftWheelFeedbackSensor = 
          new ParallaxHallEffectFeedbackSensor(digisparkFeedbackEncoder, ParallaxHallEffectFeedbackSensor.WheelSide.LEFT);
      ParallaxHallEffectFeedbackSensor rightWheelFeedbackSensor = 
          new ParallaxHallEffectFeedbackSensor(digisparkFeedbackEncoder, ParallaxHallEffectFeedbackSensor.WheelSide.RIGHT);

      // Set up the operator interface so we can get joystick feedback
      OperatorInterface operatorInterface = new OperatorInterface(gamepad);

      // Start the command scheduler
      SchedulerTask schedulerTask = new SchedulerTask(Scheduler.getInstance());
      schedulerTask.start();
      Logger.info("Robot command scheduler started.");

      // Instantiate subsystems
      DriveTrain driveTrain = new DriveTrain(
        trim, 
        leftWheelServo, 
        rightWheelServo, 
        digisparkFeedbackEncoder,
        leftWheelFeedbackSensor, 
        rightWheelFeedbackSensor, 
        operatorInterface);

      while (true) {
        Thread.sleep(100);
      }
    }
  }
}