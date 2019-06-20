package commands;

import oi.OperatorInterface;

import subsystems.DriveTrain;

import edu.wpi.first.wpilibj.command.Command;

public class TankDrive extends Command {
  private final DriveTrain driveTrain;
  private final OperatorInterface operatorInterface;

  public TankDrive(DriveTrain driveTrain, OperatorInterface operatorInterface) {
    super("TankDrive");
    requires(driveTrain);
    this.driveTrain = driveTrain;
    this.operatorInterface = operatorInterface;
  }

  @Override
  protected void execute() {
    driveTrain.setLeftWheelDrive(deadband(operatorInterface.getLeftYAxis()));
    driveTrain.setRightWheelDrive(deadband(operatorInterface.getRightYAxis()));
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

  @Override
  protected void interrupted() {
    end();
  }
}