# Project : Follow The Path

<img src="ProjectFollowThePath.jpg" alt="Follow The Path" style="width: 1024px;"/>

Once the robot has found a path and made it smooth, the robot has to actually
follow it accurately.  The robot will begin moving attempting to follow the
path by sending direction and speed commands to the motors.  The reality of
the situation is that the robot will not follow this path precisely, so we have
to correct periodically for drift and inaccuracies of our environment.

## Inputs

 * A smooth path we wish to follow (see the
[Smooth Running](project-smooth-running.html) project).
 * Periodic inputs from the robot giving a corrected position, speed, angle.

## Outputs

 * Movement and speed desired for the robot in order to correct for error
   and get back to following the path.
 * If error exceeds a particular threshold, abort and raise an abort event.

## Hint

 * Study [PID position](http://en.wikipedia.org/wiki/PID_controller)
and servo control algorithms.
