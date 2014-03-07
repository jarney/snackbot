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
 *
 * @author jona
 */
public class Vector3
    implements
        IHasX,
        IHasY,
        IHasZ,
        IScalar<Vector3>,
        IVector<Vector3> {

    private final double mX;
    private final double mY;
    private final double mZ;
    
    public Vector3(double x, double y, double z) {
        mX = x;
        mY = y;
        mZ = z;
    }
    public double distance(Vector3 p2) {
        double dx = (p2.mX - mX);
        double dy = (p2.mY - mY);
        double dz = (p2.mZ - mZ);
        return dx * dx + dy * dy + dz * dz;
    }
    public double getX() {
        return mX;
    }
    public double getY() {
        return mY;
    }
    public double getZ() {
        return mZ;
    }
    public Vector3 add(Vector3 aOther) {
        return new Vector3(aOther.mX + mX, aOther.mY + mY, aOther.mZ + mZ);
    }

    public Vector3 multiply(double aValue) {
        return new Vector3(mX * aValue, mY * aValue, mZ * aValue);
    }

    public Vector3 negate() {
        return new Vector3(-mX, -mY, -mZ);
    }
}
