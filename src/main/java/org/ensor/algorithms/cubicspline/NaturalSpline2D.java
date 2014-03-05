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
import org.ensor.math.geometry.Vector2;

/**
 *
 * @author jona
 */
public class NaturalSpline2D
    extends PathBase<Vector2>
    implements IPath<Vector2> {

    private static final XValueExtractor XEXTRACTOR = new XValueExtractor();
    private static final YValueExtractor YEXTRACTOR = new YValueExtractor();

    static class XValueExtractor implements IValueExtractor<Vector2> {
        public double getValue(final Vector2 v) {
            return v.getX();
        }
    }
    static class YValueExtractor implements IValueExtractor<Vector2> {
        public double getValue(final Vector2 v) {
            return v.getY();
        }
    }

    /**
     * The constructor creates a natural cubic spline path
     * which can interpolate between the various points given
     * using a cubic polynomial for each path segment.  The path
     * segments are constructed in such a way that they are smooth everywhere
     * and their first derivatives are also smooth.
     *
     * @param aPoints The list of points to interpolate between.
     */
    public NaturalSpline2D(final Vector2[] aPoints) {
        super(createInterpolators(aPoints));
    }
    
    public static IInterpolator<Vector2>[] createInterpolators(
            final Vector2[] aPoints) {
        int len = aPoints.length;
        IInterpolator<Vector2>[] interpolators = new IInterpolator[len];
        
        for (int i = 0; i < len; i++) {
            // Initialize the interpolator segments.
            // by solving the matrix expression to find the cubic
            // expression parameters.
            double[] cx = new double[4];
            double[] cy = new double[4];
            double[] cz = new double[4];
            interpolators[i] = new CubicInterpolator2D(
                    cx, cy, cz
            );
        }

        return interpolators;
    }
}
