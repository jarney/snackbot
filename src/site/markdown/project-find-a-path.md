# Project : Find A Path

<img src="ProjectFindAPath.jpg" alt="Find A Path" style="width: 1024px;"/>


## Inputs

 * A map of the world in a 3 dimensional array of boxes.  This is sometimes
   called a [voxel](http://en.wikipedia.org/wiki/Voxel) map.
   Each box is either "empty" or "full".  The robot cannot
   intersect with any of the "full" boxes.
 * Height, Width, Depth of the robot.  Assume for simplicity that the robot
   is shaped like a
   (cylinder)[http://en.wikipedia.org/wiki/Cylinder_%28geometry%29].
   The assumption is not exactly true, but is pretty
   close for all practical purposes.
 * A starting point (the current location of the robot).  This will be supplied
   by another system, so just assume that you can get it.

## Outputs

 * A list of 3d points which are the centers of the boxes in the
  [voxel](http://en.wikipedia.org/wiki/Voxel) map
   which are the shortest path that we can reach from start to end.

## Constraints

 * The robot must be able to fit in the path without hitting any of the "full"
   boxes.

## Hints
  * Use the [AStar algorithm](http://en.wikipedia.org/wiki/A*) (already implemented).
  * Implement the IMover, INode, IMap interfaces.
