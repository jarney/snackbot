# Where Am I?

<img src="ProjectWhereAmI.jpg" alt="Where Am I?" style="width: 1024px;"/>

This project involves the question of what is the actual location of the
robot.  Plenty of estimates of position are available based on the starting
position and the distance traveled.  These estimates will be flawed, however,
due to environmental factors such as friction and wheel slippage.  This project
involves perceiving the world and deducing (triangulating) the position of
the robot based on features perceived in the environment.

## Inputs

 * [XBox-Kinect(TM)](http://en.wikipedia.org/wiki/Kinect) depth maps.
 * Current best estimate of position (possibly calculated from several sources)
 * Collection of environmental features from previous perceptions.

## Outputs

 * A better estimate of the current position.

## Hints

 * Use [OpenCV](http://opencv.org/) for this.
