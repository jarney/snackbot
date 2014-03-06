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
public class Vector4 implements IVector<Vector4> {
    public static final IValueExtractor<Vector4> XEXTRACTOR = new XValueExtractor();
    public static final IValueExtractor<Vector4> YEXTRACTOR = new YValueExtractor();
    public static final IValueExtractor<Vector4> ZEXTRACTOR = new ZValueExtractor();
    public static final IValueExtractor<Vector4> WEXTRACTOR = new WValueExtractor();

    static class XValueExtractor implements IValueExtractor<Vector4> {
        public double getValue(final Vector4 v) {
            return v.getX();
        }
    }
    static class YValueExtractor implements IValueExtractor<Vector4> {
        public double getValue(final Vector4 v) {
            return v.getY();
        }
    }
    static class ZValueExtractor implements IValueExtractor<Vector4> {
        public double getValue(final Vector4 v) {
            return v.getZ();
        }
    }
    static class WValueExtractor implements IValueExtractor<Vector4> {
        public double getValue(final Vector4 v) {
            return v.getW();
        }
    }
    private final double mX;
    private final double mY;
    private final double mZ;
    private final double mW;
    
    public Vector4(double x, double y, double z, double w) {
        mX = x;
        mY = y;
        mZ = z;
        mW = w;
    }
    public double distance(Vector4 p2) {
        double dx = (p2.mX - mX);
        double dy = (p2.mY - mY);
        double dz = (p2.mZ - mZ);
        double dw = (p2.mW - mW);
        return dx * dx + dy * dy + dz * dz + dw * dw;
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
    public double getW() {
        return mZ;
    }

    public Vector4 add(Vector4 aOther) {
        return new Vector4(
                aOther.mX + mX,
                aOther.mY + mY,
                aOther.mZ + mZ,
                aOther.mW + mW);
    }



}
