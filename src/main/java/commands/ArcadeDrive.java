package commands;

import oi.OperatorInterface;

import subsystems.DriveTrain;

import org.pmw.tinylog.Logger;

import edu.wpi.first.wpilibj.command.Command;

public class ArcadeDrive extends Command {
  private final DriveTrain driveTrain;
  private final OperatorInterface operatorInterface;
  private long lastLeftRotationCount;

  public ArcadeDrive(
      DriveTrain driveTrain, 
      OperatorInterface operatorInterface) {
    super("ArcadeDrive");
    requires(driveTrain);
    this.driveTrain = driveTrain;
    this.operatorInterface = operatorInterface;
  }

  @Override
  protected void execute() {
    int leftWheelPct = deadband(operatorInterface.getLeftYAxis());
    int rightWheelPct = leftWheelPct;
    int xAxis = deadband(operatorInterface.getLeftXAxis());
    if (leftWheelPct > 0) {
      // make turn
      if (xAxis > 0) {
        // turn to the right
        rightWheelPct = rightWheelPct * (100 - xAxis);
      } else if (xAxis < 0) {
        // turn to the left
        leftWheelPct = leftWheelPct * (100 + xAxis);
      }
    } else if (leftWheelPct < 0) {
      // make turn
      if (xAxis < 0) {
        // turn to the right
        rightWheelPct = rightWheelPct * (100 - xAxis);
      } else if (xAxis > 0) {
        // turn to the left
        leftWheelPct = leftWheelPct * (100 + xAxis);
      }
    } else {
      // spin
      leftWheelPct = -xAxis;
      rightWheelPct = xAxis;
    }
    driveTrain.setLeftWheelDrive(leftWheelPct);
    driveTrain.setRightWheelDrive(rightWheelPct);
    driveTrain.updateFeedbackSensors();
    if (lastLeftRotationCount != driveTrain.getLeftWheelTickCount() / 360) {
      lastLeftRotationCount = driveTrain.getLeftWheelTickCount() / 360;
      Logger.info(lastLeftRotationCount);
    }
  }

  private int deadband(float rawJoystickValue) {
    if (rawJoystickValue > -0.05 && rawJoystickValue < 0.05) {
      return 0;
    } else {
      return Math.round(rawJoystickValue * 100);
    }
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
    driveTrain.stop();
  }
}