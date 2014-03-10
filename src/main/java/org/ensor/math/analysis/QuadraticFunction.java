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

import static java.lang.Double.NaN;
import org.ensor.math.geometry.IVector;

/**
 * This class represents a
 * <a href="http://en.wikipedia.org/wiki/Quadratic_function">
 * quadratic function</a>.
 * @author jona
 */
public class QuadraticFunction
    implements
        IDifferentiableFunction<QuadraticFunction> {

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
    private static final double K4 = 4;

    private boolean mCalculatedRoots;
    private boolean mHasRoots;
    private double mRoot0;
    private double mRoot1;

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
        mCalculatedRoots = false;
        mHasRoots = false;
        mRoot0 = -NaN;
        mRoot1 = NaN;
    }
    /**
     * This constructor creates a cubic expression evaluator
     * which calculates the value of the expression 'a+bt+ct^2.
     * @param aLinearFunction A linear function to extend.
     */
    public QuadraticFunction(final LinearFunction aLinearFunction) {
        mA = aLinearFunction.getA();
        mB = aLinearFunction.getB();
        mC = 0;
        mDC = K2 * mC;
        mFirstDerivative = new LinearFunction(
                mB, mDC
        );
        mCalculatedRoots = false;
        mHasRoots = false;
        mRoot0 = -NaN;
        mRoot1 = NaN;
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
     * This method returns true if the polynomial
     * has a 'root'.  A 'root' is a value 't' such that
     * the value of the polynomial is equal to zero.  The quadratic
     * polynomial has at most 2 roots.
     * @return True if the quadratic polynomial has at least one root.
     */
    public boolean hasRoots() {
        calculateRoots();
        return mHasRoots;
    }
    /**
     * The smallest root of the polynomial or -NaN if no such
     * root exists.
     * @return The value of the smallest root of the polynomial.
     */
    public double getRoot0() {
        calculateRoots();
        return mRoot0;
    }
    /**
     * The largest root of the polynomial or -NaN if no such
     * root exists.
     * @return The value of the largest root of the polynomial.
     */
    public double getRoot1() {
        calculateRoots();
        return mRoot1;
    }
    private void calculateRoots() {
        if (mCalculatedRoots) {
            return;
        }
        double discriminant = mB * mB - K4 * mC * mA;
        if (discriminant > 0) {
            mHasRoots = true;
            double sqrtDisc = Math.sqrt(discriminant);
            double root0 = (-mB + sqrtDisc) / (K2 * mC);
            double root1 = (-mB - sqrtDisc) / (K2 * mC);
            if (root0 < root1) {
                mRoot0 = root0;
                mRoot1 = root1;
            }
            else {
                mRoot1 = root0;
                mRoot0 = root1;
            }
        }
        mCalculatedRoots = true;
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
    /**
     * This method returns the quadratic function
     * described by adding this quadratic function to the given one.
     * @param aValue A quadratic function to add to this one.
     * @return The quadratic function resulting from adding these two
     *         together.
     */
    public QuadraticFunction add(
            final QuadraticFunction aValue) {
        return new QuadraticFunction(
            mA + aValue.mA,
            mB + aValue.mB,
            mC + aValue.mC);
    }
    /**
     * This method subtracts the given quadratic function from this one
     * and returns the result as a new quadratic function.
     * @param aValue The quadratic function to subtract.
     * @return The resulting quadratic function.
     */
    public QuadraticFunction subtract(
            final QuadraticFunction aValue) {
        return new QuadraticFunction(
            mA - aValue.mA,
            mB - aValue.mB,
            mC - aValue.mC);
    }
    /**
     * This method multiplies the given quadratic function by a constant
     * value and returns the result as a new quadratic function.
     * @param aValue The value to multiply.
     * @return The resulting quadratic function.
     */
    public QuadraticFunction multiply(
            final double aValue) {
        return new QuadraticFunction(
            mA * aValue,
            mB * aValue,
            mC * aValue);
    }
    /**
     * This method negates this quadratic function and returns a copy of
     * the negated function.
     * @return The resulting (negated) quadratic function.
     */
    public QuadraticFunction negate() {
        return new QuadraticFunction(
            -mA,
            -mB,
            -mC);
    }
}
