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
 * 4-dimensional
 * <a href="http://en.wikipedia.org/wiki/Vector_space">vector space</a>
 * .
 * @author jona
 */
public class Vector4
    implements
        IHasX,
        IHasY,
        IHasZ,
        IHasW,
        IVectorWithNorm<Vector4> {

    protected final double mX;
    protected final double mY;
    protected final double mZ;
    protected final double mW;
    /**
     * The constructor creates a new vector from the given coordinate
     * components.
     * @param x The x coordinate of the vector to create.
     * @param y The y coordinate of the vector to create.
     * @param z The z coordinate of the vector to create.
     * @param w The w coordinate of the vector to create.
     */
    public Vector4(
            final double x,
            final double y,
            final double z,
            final double w) {
        mX = x;
        mY = y;
        mZ = z;
        mW = w;
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
     * This method returns the 'Z' component of the vector.
     * @return The 'Z' component of the vector.
     */
    public double getZ() {
        return mZ;
    }
    /**
     * This method returns the 'W' component of the vector.
     * @return The 'W' component of the vector.
     */
    public double getW() {
        return mW;
    }
    /**
     * This method returns the vector resulting from adding
     * this vector to the given vector.
     * @param aValue The vector to add.
     * @return The result of adding the two vectors.
     */
    public Vector4 add(final Vector4 aValue) {
        return new Vector4(
                mX + aValue.mX,
                mY + aValue.mY,
                mZ + aValue.mZ,
                mW + aValue.mW);
    }
    /**
     * This method returns the vector resulting from subtracting the given
     * vector from this vector.
     * @param aValue The result of subtracting the given vector from this one.
     * @return The vector resulting from subtracting the vectors.
     */
    public Vector4 subtract(final Vector4 aValue) {
        return new Vector4(
                mX - aValue.mX,
                mY - aValue.mY,
                mZ - aValue.mZ,
                mW - aValue.mW);
    }
    /**
     * This method returns the vector resulting from multiplying
     * this vector by the given number.
     * @param aValue The value to multiply.
     * @return The vector resulting from the multiplication.
     */
    public Vector4 multiply(final double aValue) {
        return new Vector4(mX * aValue, mY * aValue, mZ * aValue, mW * aValue);
    }
    /**
     * This method returns the negative of this vector.
     * @return The vector resulting from negating this vector.
     */
    public Vector4 negate() {
        return new Vector4(-mX, -mY, -mZ, -mW);
    }
    /**
     * Computes the Euclidean length of this
     * vector.
     * @return Euclidean length.
     */
    public double length() {
        return EuclideanMetric.VECTOR4.distance(this, this);
    }
}
