# Project: Smooth Running (Done)

<img src="ProjectSmoothRunning.jpg" alt="Smooth Running" style="width: 1024px;"/>

## Inputs

 * A list of 3d points which go from a starting location to an ending location.

## Outputs

 * A smooth path (like a spline) which take you from the starting point
   to the ending point as a parameterized curve.  At any point in time "t",
   I should be able to ask the question, "What 3d position should I be in?".
   Expressed mathematically, this produces a function "(x,y,z) = f(t)" which
   maps a relative position along the curve to a 3d position.

## Hint

 * Implement a simple cubic spline.

