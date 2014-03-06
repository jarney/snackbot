/*
 * The MIT License
 *
 * Copyright 2014 Jon Arney, Ensor Robotics.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.ensor.algorithms.cubicspline;

import org.ensor.math.geometry.IPath;

/**
 * This is the base class for interpolated paths.  Each path segment has
 * methods to get the position, velocity, and segment length.  This collection
 * of segments can calculate the overall path length and provide position,
 * velocity, and overall path length.  In addition, the curve parameter runs
 * from 0 at the beginning of the path into '1' at the end of the path.  This
 * is mapped into each of the segment interpolators' interval from 0 to 1
 * for each of the segments.
 * @author jona
 */
class PathBase<ValueType>
    implements IPath<ValueType> {
    protected final int mPathSegments;
    protected final IInterpolator<ValueType>[] mSegments;

    // start position for each segment. (by definition, segment 0 starts at 0).
    protected final double[] segmentStarts;

    // 1/(start-end)
    protected final double[] segmentLengthInverses;
    
    // Total length of the path.
    // It is a bit of a hack that this is not final.
    protected final double mPathLength;

    public PathBase(final IInterpolator<ValueType>[] aInterpolators) {
        mPathSegments = aInterpolators.length;
        segmentStarts = new double[mPathSegments];
        segmentLengthInverses = new double[mPathSegments];
        mSegments = aInterpolators;
        
        // Calculate the length of each segment
        // and the overall path position start.
        double lastSegmentEnd = 0;
        for (int i = 0; i < mPathSegments; i++) {
            segmentStarts[i] = lastSegmentEnd;
            // To calculate the path length, we add up 4 distances
            // along the path segment for each segment.

            double d = mSegments[i].getPathLength();

            segmentLengthInverses[i] = 1 / d;

            lastSegmentEnd = segmentStarts[i] + d;
        }
        mPathLength = lastSegmentEnd;

    }
    /**
     * This method returns the overall length of the path
     * to be traveled.
     * @return The overall length of the path to be traveled.
     */
    public double getLength() {
        return mPathLength;
    }

    private int getSegment(final double aTime) {
        for (int i = 1; i < segmentStarts.length; i++) {
            if (segmentStarts[i] >= aTime) {
                return i - 1;
            }
        }
        return segmentStarts.length-1;
    }

    /**
     * This method returns the length along the path segment
     * for the given overall path parameter 'aTime'.
     *
     * |----segment 1 ----|---segment 2---|----aTime------|
     *                                        ^^^ Double from 0 to 1 along
     *                                            this segment.
     *
     */
    private double getParameter(final int aSegment, final double aTime) {
        // Starting point of the segment.
        double segmentStart = segmentStarts[aSegment];
        // Ending point of the segment.
        double segmentLengthInverse = segmentLengthInverses[aSegment];

        return (aTime - segmentStart) * segmentLengthInverse;
    }

    /**
     * This method returns the position for the given parameter
     * along the spline.  The number from 0 to 1 maps to a particular
     * path segment which then is evaluated as a cubic polynomial which gives
     * the position along the path.
     * @param aTime A path parameter ranging from 0 at the start of the
     *              path to 1 at the end of it.
     * @return The 3d position corresponding to this path parameter.
     */
    public ValueType getPosition(final double aTime) {

        double tOverall = mPathLength * aTime;
        int segment = getSegment(tOverall);
        double t = getParameter(segment, tOverall);

        // From 't', we evaluate the cubic polynomial which gives the
        // value of each of the coordinates.
        ValueType v = mSegments[segment].getValue(t);

        return v;
    }

    /**
     * This method returns the first derivative of the curve at the given
     * location along the path (dx/dt, dy/dt, dz/dt).
     * @param aTime The position along the path (0...1).
     * @return The derivative vector (dx/dt, dy/dt, dz/dt) for the given
     *         path parameter.
     */
    public ValueType getDirection(final double aTime) {

        double tOverall = mPathLength * aTime;
        int segment = getSegment(tOverall);
        double t = getParameter(segment, tOverall);

        // From 't', we calculate the derivative
        // by evaluating the cubic polynomial's derivative
        // which in turn is a quadratic polynomial.
        ValueType v = mSegments[segment].getDerivative(t);
        return v;
    }


}
