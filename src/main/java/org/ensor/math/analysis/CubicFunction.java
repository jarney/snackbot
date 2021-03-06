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
 * This class represents a
 * <a href="http://en.wikipedia.org/wiki/Cubic_function">cubic polynomial</a>.
 * @author jona
 */
public class CubicFunction
    implements
        IDifferentiableFunction<CubicFunction> {

    private final QuadraticFunction mFirstDerivative;

    // These are the cubic polynomial parameters
    // which give the value of the curve at that point.
    private final double mA;
    private final double mB;
    private final double mC;
    private final double mD;

    // These are the quadratic polynomial
    // parameters which give the first derivative of the
    // curve at that point.
    private final double mDD;
    private final double mDC;

    private static final double K2 = 2;
    private static final double K3 = 3;

    /**
     * This constructor creates a cubic expression evaluator
     * which calculates the value of the expression 'a+bt+ct^2+dt^3'.
     * @param a Constant-order parameter.
     * @param b Linear-order parameter.
     * @param c Quadratic-order parameter.
     * @param d Cubic-order parameter.
     */
    public CubicFunction(
            final double a,
            final double b,
            final double c,
            final double d) {
        mA = a;
        mB = b;
        mC = c;
        mD = d;
        mDD = K3 * mD;
        mDC = K2 * mC;
        mFirstDerivative = new QuadraticFunction(
                mB, mDC, mDD
        );
    }
    /**
     * This constructor creates a cubic expression evaluator
     * which calculates the value of the expression 'a+bt+ct^2+dt^3'.
     * @param aQuadratic Quadratic function to cast as cubic.
     */
    public CubicFunction(
            final QuadraticFunction aQuadratic) {
        this(aQuadratic.getA(),
                aQuadratic.getB(),
                aQuadratic.getC(),
                0);
    }
    /**
     * Returns the constant-order coefficient.
     * @return The constant-order coefficient.
     */
    protected double getA() {
        return mA;
    }
    /**
     * Returns the linear-order coefficient.
     * @return The linear-order coefficient.
     */
    protected double getB() {
        return mB;
    }
    /**
     * Returns the quadratic-order coefficient.
     * @return The quadratic-order coefficient.
     */
    protected double getC() {
        return mC;
    }
    /**
     * Returns the cubic-order coefficient.
     * @return The cubic-order coefficient.
     */
    protected double getD() {
        return mD;
    }
    /**
    * This method evaluates the cubic expression for the given path parameter
    * and returns the value of the cubic expression at that point.
    * @param t The path parameter.
    * @return The value of the cubic expression at that point.
    */
    public double getValue(final double t) {
        return (((mD * t) + mC) * t + mB) * t + mA;
    }
    /**
    * This method returns the first derivative of this cubic function
    * which is a quadratic function.
    * @return The function representing the first derivative
    *         of this function.
    */
    public QuadraticFunction getDerivative() {
        return mFirstDerivative;
    }

    /**
     * This method returns the cubic function
     * described by adding this cubic function to the given one.
     * @param aValue A cubic function to add to this one.
     * @return The cubic function resulting from adding these two
     *         together.
     */
    public CubicFunction add(
            final CubicFunction aValue) {
        return new CubicFunction(
                mA + aValue.mA,
                mB + aValue.mB,
                mC + aValue.mC,
                mD + aValue.mD);
    }
    /**
     * This method subtracts the given cubic function from this one
     * and returns the result as a new cubic function.
     * @param aValue The cubic function to subtract.
     * @return The resulting cubic function.
     */
    public CubicFunction subtract(
            final CubicFunction aValue) {
        return new CubicFunction(
                mA - aValue.mA,
                mB - aValue.mB,
                mC - aValue.mC,
                mD - aValue.mD);
    }
    /**
     * This method multiplies the given cubic function by a constant
     * value and returns the result as a new cubic function.
     * @param aValue The value to multiply.
     * @return The resulting cubic function.
     */
    public CubicFunction multiply(
            final double aValue) {
        return new CubicFunction(
                mA * aValue,
                mB * aValue,
                mC * aValue,
                mD * aValue);
    }
    /**
     * This method negates this cubic function and returns a copy of
     * the negated function.
     * @return The resulting (negated) cubic function.
     */
    public CubicFunction negate() {
        return new CubicFunction(
                -mA,
                -mB,
                -mC,
                -mD);
    }
}
