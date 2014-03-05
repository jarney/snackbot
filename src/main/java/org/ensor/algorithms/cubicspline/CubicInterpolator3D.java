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

import org.ensor.math.geometry.Vector3;

/**
 * This class models a 3d cubic spline by using primitive spline interpolators
 * to assemble a 3d spline.  It provides methods to estimate the position,
 * velocity at any point along the path as well as the overall path length.
 * @author jona
 */
public class CubicInterpolator3D
    extends CubicInterpolatorBase<Vector3>
    implements IInterpolator<Vector3> {

    private final CubicInterpolator mXInterpolator;
    private final CubicInterpolator mYInterpolator;
    private final CubicInterpolator mZInterpolator;

    /**
     * This constructor creates a 3d cubic spline based on each of the
     * 3 vector component's 4 cubic polynomial coefficients.
     * @param xCoefficients The cubic polynomial coefficients
     *                      which make up the polynomial
     *                      x[0] + tx[1] + t^2x[2] + t^3x[3];
     * @param yCoefficients The cubic polynomial coefficients
     *                      which make up the polynomial
     *                      y[0] + ty[1] + t^2y[2] + t^3y[3];
     * @param zCoefficients The cubic polynomial coefficients
     *                      which make up the polynomial
     *                      z[0] + tz[1] + t^2z[2] + t^3z[3];
     */
    public CubicInterpolator3D(
            final double []xCoefficients,
            final double []yCoefficients,
            final double []zCoefficients
    ) {
        mXInterpolator = new CubicInterpolator(
                xCoefficients[0],
                xCoefficients[1],
                xCoefficients[2],
                xCoefficients[3]);
        mYInterpolator = new CubicInterpolator(
                yCoefficients[0],
                yCoefficients[1],
                yCoefficients[2],
                yCoefficients[3]);
        mZInterpolator = new CubicInterpolator(
                zCoefficients[0],
                zCoefficients[1],
                zCoefficients[2],
                zCoefficients[3]);
    }
    /**
     * This method returns the position along the path for the given path
     * parameter 't'.
     * @param t The input path parameter (running from 0 to 1).
     * @return The position for the given path parameter.
     */
    public Vector3 getValue(final double t) {
        return new Vector3(
                mXInterpolator.getValue(t),
                mYInterpolator.getValue(t),
                mZInterpolator.getValue(t)
        );
    }
    /**
     * This method returns the first derivative of the path
     * vector with respect to the path parameter 't'.  Essentially, this is
     * the velocity vector with respect to 't'.
     * @param t The input path parameter (running from 0 to 1).
     * @return The velocity vector at the given path parameter 't'.
     */
    public Vector3 getDerivative(final double t) {
        return new Vector3(
                mXInterpolator.getDerivative(t),
                mYInterpolator.getDerivative(t),
                mZInterpolator.getDerivative(t)
        );
    }


}
