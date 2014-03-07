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
 * This class represents a
 * <a href="http://en.wikipedia.org/wiki/Quadratic_function">
 * quadratic function</a>.
 * @author jona
 */
public class QuadraticFunction implements IDifferentiableFunction {

    private final LinearFunction mFirstDerivative;

    // These are the cubic polynomial parameters
    // which give the value of the curve at that point.
    private final double mA;
    private final double mB;
    private final double mC;

    // These are the quadratic polynomial
    // parameters which give the first derivative of the
    // curve at that point.
    private final double mDC;

    private static final double K2 = 2;

    /**
     * This constructor creates a cubic expression evaluator
     * which calculates the value of the expression 'a+bt+ct^2.
     * @param a Constant-order parameter.
     * @param b Linear-order parameter.
     * @param c Quadratic-order parameter.
     */
    public QuadraticFunction(
            final double a,
            final double b,
            final double c) {
        mA = a;
        mB = b;
        mC = c;
        mDC = K2 * mC;
        mFirstDerivative = new LinearFunction(
                mB, mDC
        );
    }
    /**
    * This method evaluates the quadratic expression for the given path
    * parameter and returns the value of the cubic expression at that point.
    * @param t The path parameter.
    * @return The value of the cubic expression at that point.
    */
    public double getValue(final double t) {
        return ((mC * t) + mB) * t + mA;
    }
    /**
    * This method returns the first derivative of the quadratic
    * function which is a linear function.
    * @return The function representing the first derivative
    *         of this function.
    */
    public LinearFunction getDerivative() {
        return mFirstDerivative;
    }
}
