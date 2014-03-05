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

import org.ensor.math.geometry.IVector;
import org.ensor.math.geometry.Vector3;

/**
 *
 * @author jona
 */
public abstract class CubicInterpolatorBase<VectorType extends IVector> {
    
    private static final double QUARTER = 0.25;
    private static final double HALFWAY = 0.5;
    private static final double THREEQUARTERS = 0.75;

    public abstract VectorType getValue(double aPosition);
    
    /**
     * This method estimates the length of the spline by dividing it into
     * 4 equal segments and then measuring the point-to-point distance of
     * each of those segments.  This is not an exact length calculation,
     * however, it does account for most of the error which would occur from
     * the variance between the actual spline and a straight-line path.
     * @return An estimated length for the path.  Note that the actual length
     *         is always slightly more than the value returned here.
     */
    public double getPathLength() {
            VectorType p1 = getValue(0);
            VectorType p2 = getValue(QUARTER);
            VectorType p3 = getValue(HALFWAY);
            VectorType p4 = getValue(THREEQUARTERS);
            VectorType p5 = getValue(1);

            double d = p1.distance(p2) +
                    p2.distance(p3) +
                    p3.distance(p4) +
                    p4.distance(p5);
            return d;
    }

}
