# Project: Tank Drive

<img src="ProjectTankDrive.jpg" alt="Tank Drive" style="width: 1024px;"/>

In order for the robot to move around the world, it must be able to turn
speed and direction commands into commands for each of the motors which control
it.  The motors are configured in a "tank drive" configuration.  Each of the
motors can spin in either direction.  The movement of each of the motors
produces a speed and direction for the robot.  There is a little tricky
mathematics and trigonometry for figuring out how the movement of the robot
relates to the movement of each of the motors.  This project is about
solving that problem.

## Inputs

 * Desired direction and speed for the robot to move.
 * Feedback from the motors indicating the actual speed and direction traveled.

## Outputs

 * Speed and direction commands for each of the motors.

## Hints

 * Trigonometry
 * Commands for movement are incoming events to subscribe to.
 * Feedback about actual position goes back as events too.


