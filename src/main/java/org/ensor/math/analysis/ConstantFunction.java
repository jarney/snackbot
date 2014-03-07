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
 * This class represents an interpolator which returns a constant value
 * for the entire interval.
 * @author jona
 */
public class ConstantFunction implements IDifferentiableFunction {
    /**
     * This field represents the constant function which returns zero in all
     * locations.
     */
    public static final IDifferentiableFunction ZERO =
            new ConstantFunction(0);

    private final double mValue;

    /**
     * The constructor creates an interpolator which returns the
     * same value for the entire interval.
     * @param aValue The constant value of the interpolation for the interval.
     */
    public ConstantFunction(final double aValue) {
        mValue = aValue;
    }
    /**
     * This method returns the point value for the specified path
     * parameter.  The path parameter runs from 0 to 1.
     * @param t Path parameter.
     * @return The point between start and end of path corresponding to this
     *         path location.
     */
    public double getValue(final double t) {
        return mValue;
    }
    /**
     * This method returns the first derivative of the function.
     * For a constant function, this is identically the ZERO function.
     * @return Exactly zero since the first derivative of a constant value
     *         is zero.
     */
    public IDifferentiableFunction getDerivative() {
        return ZERO;
    }

}
