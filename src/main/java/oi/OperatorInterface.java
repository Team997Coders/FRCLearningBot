package oi;

public class OperatorInterface {
  private final JInputJoystick gamepad;
  private long lastPolled;
  private final long maxPollIntervalMs = 20;

  public OperatorInterface(JInputJoystick gamepad) {
    this.gamepad = gamepad;
  }

  private void pollController() {
    long now = System.currentTimeMillis();
    if ((now - lastPolled) > maxPollIntervalMs) {
      gamepad.pollController();
      lastPolled = System.currentTimeMillis();
    }
  }

  public float getLeftYAxis() {
    pollController();
    return gamepad.getY_LeftJoystick_Value();
  }

  public float getLeftXAxis() {
    pollController();
    return gamepad.getX_LeftJoystick_Value();
  }

  public float getRightYAxis() {
    pollController();
    return gamepad.getY_RightJoystick_Value();
  }
}