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

package org.ensor.math.geometry;

/**
 * This method represents an element of a
 * 2-dimensional
 * <a href="http://en.wikipedia.org/wiki/Vector_space">vector space</a>
 * .
 * @author jona
 */
public class Vector2
    implements
        IHasX,
        IHasY,
        IVectorWithNorm<Vector2> {
    
    public static final Vector2 ZERO = new Vector2(0, 0);

    protected final double mX;
    protected final double mY;
    /**
     * The constructor creates a new vector from the given coordinate
     * components.
     * @param x The x coordinate of the vector to create.
     * @param y The y coordinate of the vector to create.
     */
    public Vector2(final double x, final double y) {
        mX = x;
        mY = y;
    }
    /**
     * This method returns the 'X' component of the vector.
     * @return The 'X' component of the vector.
     */
    public double getX() {
        return mX;
    }
    /**
     * This method returns the 'Y' component of the vector.
     * @return The 'Y' component of the vector.
     */
    public double getY() {
        return mY;
    }
    /**
     * This method returns the vector resulting from adding
     * this vector to the given vector.
     * @param aOther The vector to add.
     * @return The result of adding the two vectors.
     */
    public Vector2 add(final Vector2 aOther) {
        return new Vector2(mX + aOther.mX, mY + aOther.mY);
    }
    /**
     * This method returns the vector resulting from subtracting the given
     * vector from this vector.
     * @param aValue The result of subtracting the given vector from this one.
     * @return The vector resulting from subtracting the vectors.
     */
    public Vector2 subtract(final Vector2 aValue) {
        return new Vector2(mX - aValue.mX, mY - aValue.mY);
    }
    /**
     * This method returns the vector resulting from multiplying
     * this vector by the given number.
     * @param aValue The value to multiply.
     * @return The vector resulting from the multiplication.
     */
    public Vector2 multiply(final double aValue) {
        return new Vector2(mX * aValue, mY * aValue);
    }
    /**
     * This method returns the negative of this vector.
     * @return The vector resulting from negating this vector.
     */
    public Vector2 negate() {
        return new Vector2(-mX, -mY);
    }
    /**
     * Computes the Euclidean length of this
     * vector.
     * @return Euclidean length.
     */
    public double length() {
        return EuclideanMetric.VECTOR2.distance(ZERO, this);
    }
}
