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

import org.ensor.math.geometry.IVector;

/**
 * This class implements a
 * <a href="http://en.wikipedia.org/wiki/Linear_function">
 * linear function</a>.
 * @author jona
 */
public class LinearFunction
    implements
        IDifferentiableFunction<LinearFunction> {

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
    public LinearFunction(final ConstantFunction aConstant) {
        this(aConstant.getA(), 0);
    }
    /**
     * Returns the constant-order coefficient.
     * @return The constant-order coefficient.
     */
    protected double getA() {
        return mStart;
    }
    /**
     * Returns the linear-order coefficient.
     * @return The linear-order coefficient.
     */
    protected double getB() {
        return mDelta;
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
    /**
     * This method returns the value of 't' for which the linear expression
     * evaluates to zero.
     * @return The value of the parameter such that the expression evaluates to
     *         to zero.
     */
    public double getRoot() {
        return -mStart / mDelta;
    }
    /**
     * This function returns a new linear function which is
     * the sum of this function and the given
     * function.
     * @param aValue A linear function to add.
     * @return The sum of adding this linear function to the given function.
     */
    public LinearFunction add(final LinearFunction aValue) {
        return new LinearFunction(
                mStart + aValue.mStart,
                mDelta + aValue.mDelta);
    }
    /**
     * This function returns a new linear function which is the
     * result of subtracting the given function from this function.
     * @param aValue A linear function to subtract.
     * @return The result of subtracting the given function from this one.
     */
    public LinearFunction subtract(final LinearFunction aValue) {
        return new LinearFunction(
                mStart - aValue.mStart,
                mDelta - aValue.mDelta);
    }
    /**
     * This function returns a new linear function which is the
     * result of multiplying this function by the given number.
     * @param aValue A number to multiply this function by.
     * @return The result of multiplying this function by the given number.
     */
    public LinearFunction multiply(final double aValue) {
        return new LinearFunction(mStart * aValue, mDelta * aValue);
    }
    /**
     * This function returns a new function which is the negation of
     * this one.
     * @return The negation of this function.
     */
    public LinearFunction negate() {
        return new LinearFunction(-mStart, -mDelta);
    }
}
