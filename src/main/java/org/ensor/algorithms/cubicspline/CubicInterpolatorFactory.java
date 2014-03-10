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

import org.ensor.math.analysis.ConstantFunction;
import org.ensor.math.analysis.LinearFunction;
import org.ensor.math.analysis.CubicFunction;
import org.ensor.math.analysis.IDifferentiableFunction;

/**
 * This factory creates a set of interpolators for the given set of points.
 * Each interpolator returned will have a path parameter over the interval
 * from 0 to 1 inclusive.
 * The interpolators returned will have the following properties:
 * <ul>
 * <li>Interval running from 0 to 1.
 * <li>Path runs through each control point.
 * <li>Path has zero second derivative at each control point.
 * <li>First derivative of each path segment's end
 *     is equal to the first derivative of the next path segment's start.
 * </ul>
 * @author jona
 */
class CubicInterpolatorFactory
    implements IInterpolatorFactory<IDifferentiableFunction> {

    private static final double K2 = 2.0;
    private static final double K3 = 3.0;
    private static final double K4 = 4.0;

    /**
     * This method returns a collection of interpolators for the given set of
     * control points.  If no points are given, null is returned.  If one point
     * is given, a 'constant' interpolator is generated.  If two points are
     * given, a linear interpolator is generated.  If three or more points are
     * given, a set of natural cubic splines is generated.
     * @param aPoints A set of control points for which to build an
     *                interpolator.
     * @return A set of interpolators.
     */
    public IDifferentiableFunction[] createInterpolators(
            final IValueCollection aPoints) {

        int nPoints = aPoints.length();

        // We must have some points in order to make this work.
        if (nPoints == 0) {
            return null;
        }
        // If we have only 1 point, just return a constant interpolator.
        if (nPoints == 1) {
            IDifferentiableFunction[] ci = new IDifferentiableFunction[1];
            ci[0] = new ConstantFunction(aPoints.getValue(0));
            return ci;
        }
        if (nPoints == 2) {
            IDifferentiableFunction[] ci = new IDifferentiableFunction[1];
            ci[0] = new LinearFunction(
                    aPoints.getValue(0),
                    aPoints.getValue(1) - aPoints.getValue(0));
            return ci;
        }
        int num = nPoints - 1;
        // If there are 'n' points, then there are
        // n-1 path segments between them.

        IDifferentiableFunction[] ci = new IDifferentiableFunction[num];

        /*
             We solve the equation
            [2 1       ] [D[0]]   [3(x[1] - x[0])  ]
            |1 4 1     | |D[1]|   |3(x[2] - x[0])  |
            |  1 4 1   | | .  | = |      .         |
            |    ..... | | .  |   |      .         |
            |     1 4 1| | .  |   |3(x[n] - x[n-2])|
            [       1 2] [D[n]]   [3(x[n] - x[n-1])]

            by using row operations to convert the matrix to upper triangular
            and then back sustitution.  The D[i] are the derivatives at
            the knots.
        */
        double[] gamma = new double[num + 1];
        double[] delta = new double[num + 1];
        double[] dd = new double[num + 1];

        gamma[0] = 1.0 / K2;
        for (int i = 1; i < num; i++) {
           gamma[i] = 1.0 / (K4 - gamma[i - 1]);
        }
        gamma[num] = 1.0 / (K2 - gamma[num - 1]);

        double p0 = (double) aPoints.getValue(0);
        double p1 = (double) aPoints.getValue(1);

        delta[0] = K3 * (p1 - p0) * gamma[0];

        for (int i = 1; i < num; i++) {
            p0 = (double) aPoints.getValue(i - 1);
            p1 = (double) aPoints.getValue(i);
            delta[i] = (K3 * (p1 - p0) - delta[i - 1]) * gamma[i];
        }

        p0 = (double) aPoints.getValue(num - 1);
        p1 = (double) aPoints.getValue(num);

        delta[num] = (K3 * (p1 - p0) - delta[num - 1]) * gamma[num];

        dd[num] = delta[num];
        for (int i = num - 1; i >= 0; i--) {
           dd[i] = delta[i] - gamma[i] * dd[i + 1];
        }

        for (int i = 0; i < num; i++) {
            // Initialize the interpolator segments.
            // by solving the matrix expression to find the cubic
            // expression parameters.

            p0 = (double) aPoints.getValue(i);
            p1 = (double) aPoints.getValue(i + 1);

            CubicFunction cit = new CubicFunction(
                    p0,
                    dd[i],
                    K3 * (p1 - p0) - K2 * dd[i] - dd[i + 1],
                    K2 * (p0 - p1) + dd[i] + dd[i + 1]
            );

            ci[i] = cit;
        }
        return ci;
    }

}
