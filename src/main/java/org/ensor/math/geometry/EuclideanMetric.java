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
 * This method defines the
 * <a href="http://mathworld.wolfram.com/EuclideanMetric.html">euclidian metric
 * </a> which defines the concept of
 * <a href="http://en.wikipedia.org/wiki/Euclidean_distance">distance</a> in
 * a euclidian geometry.
 * @author jona
 */
public final class EuclideanMetric {
    /**
     * This field is the standard Euclidean metric over a two dimensional
     * vector space.
     */
    public static final IMetric<Vector2> VECTOR2 = new IMetric<Vector2>() {
        public double distance(final Vector2 aOne, final Vector2 aTwo) {
            double dx = (aOne.mX - aTwo.mX);
            double dy = (aOne.mY - aTwo.mY);
            return Math.sqrt(dx * dx + dy * dy);
        }
    };
    /**
     * This field is the standard Euclidean metric over a three dimensional
     * vector space.
     */
    public static final IMetric<Vector3> VECTOR3 = new IMetric<Vector3>() {
        public double distance(final Vector3 aOne, final Vector3 aTwo) {
            double dx = (aOne.mX - aTwo.mX);
            double dy = (aOne.mY - aTwo.mY);
            double dz = (aOne.mZ - aTwo.mZ);
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
    };
    /**
     * This field is the standard Euclidean metric over a four dimensional
     * vector space.
     */
    public static final IMetric<Vector4> VECTOR4 = new IMetric<Vector4>() {
        public double distance(final Vector4 aOne, final Vector4 aTwo) {
            double dx = (aOne.mX - aTwo.mX);
            double dy = (aOne.mY - aTwo.mY);
            double dz = (aOne.mZ - aTwo.mZ);
            double dw = (aOne.mW - aTwo.mW);
            return Math.sqrt(dx * dx + dy * dy + dz * dz + dw * dw);
        }
    };

    private EuclideanMetric() {
    }
}
