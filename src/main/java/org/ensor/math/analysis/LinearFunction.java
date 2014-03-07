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

package org.ensor.math.analysis;

/**
 * This class implements a
 * <a href="http://en.wikipedia.org/wiki/Linear_function">
 * linear function</a>.
 * @author jona
 */
public class LinearFunction implements IDifferentiableFunction {

    private final double mStart;
    private final double mDelta;
    private final ConstantFunction mFirstDerivative;

    /**
     * The constructor creates a linear interpolator which runs from the start
     * to the end point.
     * @param aStart Start point of the interpolation for t=0.
     * @param aDelta Slope of the line.
     */
    public LinearFunction(final double aStart, final double aDelta) {
        mStart = aStart;
        mDelta = aDelta;
        mFirstDerivative = new ConstantFunction(mDelta);
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
    /**
     * This method returns the first derivative of the linear function.
     * This is always a constant function which represents the slope
     * of the line.
     * @return The (constant) differentiable function which is the slope of this
     *         linear function.
     */
    public ConstantFunction getDerivative() {
        return mFirstDerivative;
    }

}
