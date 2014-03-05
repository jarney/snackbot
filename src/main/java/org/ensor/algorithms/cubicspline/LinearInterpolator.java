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

/**
 * This class implements an interpolator which is based on a linear
 * relation between the input parameter and the distance traveled from
 * start to end.
 * @author jona
 */
public class LinearInterpolator implements IPrimitiveInterpolator {

    private final double mStart;
    private final double mDelta;

    /**
     * The constructor creates a linear interpolator which runs from the start
     * to the end point.
     * @param aStart Start point of the interpolation for t=0.
     * @param aEnd End point of the interpolation for t=1.
     */
    LinearInterpolator(final double aStart, final double aEnd) {
        mStart = aStart;
        mDelta = aEnd - aStart;
    }
    /**
     * This method returns the point value for the specified path
     * parameter.  The path parameter runs from 0 to 1.
     * @param t Path parameter.
     * @return The point between start and end of path corresponding to this
     *         path location.
     */
    public double getValue(final double t) {
        return mStart + t * mDelta;
    }

}
