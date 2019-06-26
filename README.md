# FRCLearningBot Project

This project demonstrates basic FRC coding practices in Java using a Raspberry Pi single board computer platform.
It uses the WPILIB command subsystem to process commands against hardware connected to the Pi. It uses the
pigpio library to communicate with the Pi GPIO header. This library provides the capability to access GPIO over network sockets, which means that the robot program can run on the workstation without deploying to the Pi. It uses the JInput library to process joystick input.

# Requirements
- Raspberry Pi
  - Install Raspbian
  - sudo apt-get install pigpio (if robot program will run on workstation)
  - sudo apt-get install default-jdk (optional if robot programs will run on Pi)
  - Configure wireless network using sudo raspi-config
- Workstation with at least Java 8 JDK installed
- Set the PIGPIOD_HOST variable in .vscode/launch.json to the host name/ip address of the Pi, if running robot program on workstation

# To Build Robot Program
- ./gradlew build

# To Run Robot Program On Workstation
- ./gradlew run
