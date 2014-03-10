# Projects

The snackbot project has been divided into a number of smaller sub-projects
each of which identifies a particular problem to solve.  The projects vary in
difficulty, however, each of the projects can be a very interesting engineering
exercise in its own right depending on how far you want to take it.

Each project has simple one-page summary sketch describing the inputs, the
outputs, and in some cases a hint as to how to proceed.  Very little detail is
given so that the person working on that component can maximize their
creativity in solving the problem.

## [Find A Path](project-find-a-path.html)

This project involves finding a path from a starting location to a goal
location.  It assumes that the starting location, ending location, and map
of the world are available (each of those is its own project).

## [Smooth Running : Done](project-smooth-running.html)

This project assumes that a list of waypoints has been found which correspond to
the shortest path to the goal.  In order to get from "a" to "b" smoothly,
however, we would like to avoid sharp corners and abrupt starts and stops.
This project involves taking a list of points and finding a smooth path between
them.  A cubic spline between the points is a good example of a technique that
would work well here.

## [Follow The Path](project-follow-the-path.html)

This project involves taking a smooth parameterized curve (like a cubic spline)
and issuing commands to the robot drive system to follow the path.  Periodic
position updates will also come in telling us how far we have deviated from
the desired path.  We must smoothly correct for those deviations and maintain
the path as accurately as we can.


## [See The World](project-see-the-world.html)

This project involves receiving input from an XBot Kinect sensor or other
sensing device to deduce the map of the world.

## [Hear Me](project-hear-me.html)

This project involves receiving voice commands given by the robot user and
turning them into events which can be sent over the robot's event bus as
commands.

## [Tank Drive](project-tank-drive.html)

This project involves receiving commands in the form of speed and direction
from the path following system.  These must be turned into commands to the left
and right motors which will cause the robot to follow that command.  This is
done by issuing commands to the IMotorWithEncoder interface for the left and
right motors.

In addition, this command must give periodic outputs back to the path following
system which indicate estimates of how far the robot has deviated from
the desired commands.  This is done by reading the encoder outputs from the
motors and translating it back into actual distance and angle traveled.

## [Where Am I?](project-where-am-i.html)

This project involves looking at features in the robot's environment and
correlating them with features seen in the environment previously.  Once the
features have been correlated, the position of the robot relative to those
features can be identified and a refined estimate of the robot's actual
position can be given.
