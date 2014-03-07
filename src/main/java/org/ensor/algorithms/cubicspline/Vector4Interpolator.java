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

import org.ensor.math.geometry.Vector4;

/**
 * This class models a 3d cubic spline by using primitive spline interpolators
 * to assemble a 3d spline.  It provides methods to estimate the position,
 * velocity at any point along the path as well as the overall path length.
 * @author jona
 */
class Vector4Interpolator
    extends VectorInterpolatorBase<Vector4>
    implements IInterpolator<Vector4> {

    private final IPrimitiveInterpolator mXInterpolator;
    private final IPrimitiveInterpolator mYInterpolator;
    private final IPrimitiveInterpolator mZInterpolator;
    private final IPrimitiveInterpolator mWInterpolator;

    /**
     * This constructor creates a 2d cubic spline based a cubic interpolator
     * for each coordinate.
     * @param aXInterp A cubic interpolator for the x coordinate.
     * @param aYInterp A cubic interpolator for the y coordinate.
     * @param aZInterp A cubic interpolator for the z coordinate.
     * @param aWInterp A cubic interpolator for the w coordinate.
     */
    public Vector4Interpolator(
            final IPrimitiveInterpolator aXInterp,
            final IPrimitiveInterpolator aYInterp,
            final IPrimitiveInterpolator aZInterp,
            final IPrimitiveInterpolator aWInterp
    ) {
        mXInterpolator = aXInterp;
        mYInterpolator = aYInterp;
        mZInterpolator = aZInterp;
        mWInterpolator = aWInterp;
    }
    /**
     * This method returns the position along the path for the given path
     * parameter 't'.
     * @param t The input path parameter (running from 0 to 1).
     * @return The position for the given path parameter.
     */
    public Vector4 getValue(final double t) {
        return new Vector4(
                mXInterpolator.getValue(t),
                mYInterpolator.getValue(t),
                mZInterpolator.getValue(t),
                mWInterpolator.getValue(t)
        );
    }
    /**
     * This method returns the first derivative of the path
     * vector with respect to the path parameter 't'.  Essentially, this is
     * the velocity vector with respect to 't'.
     * @param t The input path parameter (running from 0 to 1).
     * @return The velocity vector at the given path parameter 't'.
     */
    public Vector4 getDerivative(final double t) {
        return new Vector4(
                mXInterpolator.getDerivative(t),
                mYInterpolator.getDerivative(t),
                mZInterpolator.getDerivative(t),
                mWInterpolator.getDerivative(t)
        );
    }


}
