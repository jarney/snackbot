# Project: See The World

<img src="ProjectSeeTheWorld.jpg" alt="See The World" style="width: 1024px;"/>

In order to plan a path and follow it, the robot must be able to perceive
the world around it.  This project involves interfacing with an 
[XBox-Kinect(TM)](http://en.wikipedia.org/wiki/Kinect)
sensor to perceive the world and represent it as a voxel grid.  This 
[voxel](http://en.wikipedia.org/wiki/Voxel) grid will be used for
navigation, path planning and following in order to avoid hitting things.

## Inputs

 * Depth map from [XBox-Kinect(TM)](http://en.wikipedia.org/wiki/Kinect) sensor.
 * Estimated current position.

## Outputs

 * A [voxel](http://en.wikipedia.org/wiki/Voxel) grid of the world.

## Hints

 * Use [OpenCV](http://opencv.org/) for this.

