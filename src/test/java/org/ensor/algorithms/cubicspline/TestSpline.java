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

package org.ensor.algorithms.cubicspline;

import java.util.ArrayList;
import java.util.List;
import org.ensor.math.geometry.EuclideanMetric;
import org.ensor.math.geometry.IMetric;
import org.ensor.math.geometry.IPath;
import org.ensor.math.geometry.IVectorWithNorm;
import org.ensor.math.geometry.Vector2;
import org.ensor.math.geometry.Vector3;
import org.ensor.math.geometry.Vector4;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author jona
 */
public class TestSpline {
    
    private static final double DOUBLE_TOLERANCE = 0.002;
    
    static class SplineConstantAsserter
            <VecType extends IVectorWithNorm<VecType>> {
        IPath<VecType> mPath;
        IMetric<VecType> mMetric;
        VecType mPoint;
        SplineConstantAsserter(
                final IPath<VecType> aPath,
                final IMetric<VecType> aMetric,
                final VecType aPoint
        ) {
            mPath = aPath;
            mMetric = aMetric;
            mPoint = aPoint;
        }
        public void doAsserts() {
            // Check that the start point equals the point given.
            VecType s = mPath.getPosition(0);
            Assert.assertEquals(0, mMetric.distance(mPoint, s),
                    DOUBLE_TOLERANCE);

            // Check that the end point equals the point given.
            VecType e = mPath.getPosition(1);
            Assert.assertEquals(0, mMetric.distance(mPoint, e),
                    DOUBLE_TOLERANCE);


            // Check that the distance of the spline is trivial.
            Assert.assertEquals(0, mPath.getLength(),
                    DOUBLE_TOLERANCE);

            // Check that the direction is trivial.
            Assert.assertEquals(0, mPath.getDirection(0).length(),
                    DOUBLE_TOLERANCE);
            Assert.assertEquals(0, mPath.getDirection(1).length(),
                    DOUBLE_TOLERANCE);
        }
    }
    
    /**
     * Check that 1 point creates the 'constant'
     * spline.
     */
    @Test
    public void test2DConstant() {
        
        // Test the constant spline.
        List<Vector2> points = new ArrayList<Vector2>();
        Vector2 p1 = new Vector2(1, 2);
        points.add(p1);
        NaturalSpline2D spline = NaturalSpline2D.createInterpolators(points);
        SplineConstantAsserter<Vector2> splineAsserter =
                new SplineConstantAsserter<Vector2>(
                        spline,
                        EuclideanMetric.VECTOR2,
                        p1);
        splineAsserter.doAsserts();
    }
    
    /**
     * Check that 1 point creates the 'constant'
     * spline.
     */
    @Test
    public void test3DConstant() {
        // Test the constant spline.
        List<Vector3> points = new ArrayList<Vector3>();
        Vector3 p1 = new Vector3(1, 2, 2);
        points.add(p1);
        NaturalSpline3D spline = NaturalSpline3D.createInterpolators(points);
        SplineConstantAsserter<Vector3> splineAsserter =
                new SplineConstantAsserter<Vector3>(
                        spline,
                        EuclideanMetric.VECTOR3,
                        p1);
        splineAsserter.doAsserts();
    }
    /**
     * Check that 1 point creates the 'constant'
     * spline.
     */
    @Test
    public void test4DConstant() {
        // Test the constant spline.
        List<Vector4> points = new ArrayList<Vector4>();
        Vector4 p1 = new Vector4(1, 2, 2, 1);
        points.add(p1);
        NaturalSpline4D spline = NaturalSpline4D.createInterpolators(points);
        SplineConstantAsserter<Vector4> splineAsserter =
                new SplineConstantAsserter<Vector4>(
                        spline,
                        EuclideanMetric.VECTOR4,
                        p1);
        splineAsserter.doAsserts();
    }
    
    static class SplineLinearAsserter
            <VecType extends IVectorWithNorm<VecType>> {
        IPath<VecType> mPath;
        IMetric<VecType> mMetric;
        VecType mStart;
        VecType mEnd;
        SplineLinearAsserter(
                final IPath<VecType> aPath,
                final IMetric<VecType> aMetric,
                final VecType aStart,
                final VecType aEnd
        ) {
            mPath = aPath;
            mMetric = aMetric;
            mStart = aStart;
            mEnd = aEnd;
        }
        public void doAsserts() {
            // Check that the start point equals the point given.
            VecType s = mPath.getPosition(0);
            Assert.assertEquals(0, mMetric.distance(mStart, s),
                    DOUBLE_TOLERANCE);

            // Check that the end point equals the point given.
            VecType e = mPath.getPosition(1);
            Assert.assertEquals(0, mMetric.distance(mEnd, e),
                    DOUBLE_TOLERANCE);


            // Check that the distance is the simple distance between points.
            Assert.assertEquals(
                    mMetric.distance(mStart, mEnd),
                    mPath.getLength(),
                    DOUBLE_TOLERANCE);

        }
    }
    /**
     * Check that 1 point creates the 'constant'
     * spline.
     */
    @Test
    public void test2DLinear() {
        
        // Test the constant spline.
        List<Vector2> points = new ArrayList<Vector2>();
        Vector2 p1 = new Vector2(1, 2);
        points.add(p1);
        Vector2 p2 = new Vector2(2, 4);
        points.add(p2);
        NaturalSpline2D spline = NaturalSpline2D.createInterpolators(points);
        SplineLinearAsserter<Vector2> splineAsserter =
                new SplineLinearAsserter<Vector2>(
                        spline,
                        EuclideanMetric.VECTOR2,
                        p1, p2);
        splineAsserter.doAsserts();
    }
    
    /**
     * Check that 1 point creates the 'constant'
     * spline.
     */
    @Test
    public void test3DLinear() {
        // Test the constant spline.
        List<Vector3> points = new ArrayList<Vector3>();
        Vector3 p1 = new Vector3(1, 2, 2);
        points.add(p1);
        Vector3 p2 = new Vector3(2, 1, 2);
        points.add(p2);
        NaturalSpline3D spline = NaturalSpline3D.createInterpolators(points);
        SplineLinearAsserter<Vector3> splineAsserter =
                new SplineLinearAsserter<Vector3>(
                        spline,
                        EuclideanMetric.VECTOR3,
                        p1, p2);
        splineAsserter.doAsserts();
    }
    /**
     * Check that 1 point creates the 'constant'
     * spline.
     */
    @Test
    public void test4DLinear() {
        // Test the constant spline.
        List<Vector4> points = new ArrayList<Vector4>();
        Vector4 p1 = new Vector4(1, 2, 2, 1);
        points.add(p1);
        Vector4 p2 = new Vector4(2, 1, 2, 1);
        points.add(p2);
        NaturalSpline4D spline = NaturalSpline4D.createInterpolators(points);
        SplineLinearAsserter<Vector4> splineAsserter =
                new SplineLinearAsserter<Vector4>(
                        spline,
                        EuclideanMetric.VECTOR4,
                        p1, p2);
        splineAsserter.doAsserts();
    }
    
    @Test
    public void testNoPoints() {
        List<Vector2> points2 = new ArrayList<Vector2>();
        NaturalSpline2D spline2 = NaturalSpline2D.createInterpolators(points2);
        Assert.assertNull(spline2);
        
        List<Vector3> points3 = new ArrayList<Vector3>();
        NaturalSpline3D spline3 = NaturalSpline3D.createInterpolators(points3);
        Assert.assertNull(spline3);
        
        List<Vector4> points4 = new ArrayList<Vector4>();
        NaturalSpline4D spline4 = NaturalSpline4D.createInterpolators(points4);
        Assert.assertNull(spline4);
    }
    @Test
    public void testNullPoints() {
        NaturalSpline2D spline2 = NaturalSpline2D.createInterpolators(null);
        Assert.assertNull(spline2);
        
        NaturalSpline3D spline3 = NaturalSpline3D.createInterpolators(null);
        Assert.assertNull(spline3);
        
        NaturalSpline4D spline4 = NaturalSpline4D.createInterpolators(null);
        Assert.assertNull(spline4);
    }
    static class SplineCubicAsserter
            <VecType extends IVectorWithNorm<VecType>> {
        IPath<VecType> mPath;
        IMetric<VecType> mMetric;
        VecType mStart;
        VecType mMid;
        VecType mEnd;
        SplineCubicAsserter(
                final IPath<VecType> aPath,
                final IMetric<VecType> aMetric,
                final VecType aStart,
                final VecType aMid,
                final VecType aEnd
        ) {
            mPath = aPath;
            mMetric = aMetric;
            mStart = aStart;
            mMid = aMid;
            mEnd = aEnd;
        }
        public void doAsserts() {
            // Check that the start point equals the point given.
            VecType s = mPath.getPosition(0);
            Assert.assertEquals(0, mMetric.distance(mStart, s),
                    DOUBLE_TOLERANCE);

            // Check that the end point equals the point given.
            VecType e = mPath.getPosition(1);
            Assert.assertEquals(0, mMetric.distance(mEnd, e),
                    DOUBLE_TOLERANCE);

            // Check that the mid point is equal to the mid point.
            VecType m1 = mPath.getPosition(0.499999);
            Assert.assertEquals(0, mMetric.distance(mMid, m1),
                    0.1);
            VecType m2 = mPath.getPosition(0.500001);
            Assert.assertEquals(0, mMetric.distance(mMid, m2),
                    0.1);


        }
    }
    /**
     * Check that 1 point creates the 'constant'
     * spline.
     */
    @Test
    public void test2DCubic() {
        
        // Test the constant spline.
        List<Vector2> points = new ArrayList<Vector2>();
        Vector2 p1 = new Vector2(1, 2);
        points.add(p1);
        Vector2 p2 = new Vector2(2, 4);
        points.add(p2);
        Vector2 p3 = new Vector2(2, 2);
        points.add(p3);
        NaturalSpline2D spline = NaturalSpline2D.createInterpolators(points);
        SplineCubicAsserter<Vector2> splineAsserter =
                new SplineCubicAsserter<Vector2>(
                        spline,
                        EuclideanMetric.VECTOR2,
                        p1, p2, p3);
        splineAsserter.doAsserts();
    }
    
    /**
     * Check that 1 point creates the 'constant'
     * spline.
     */
    @Test
    public void test3DCubic() {
        // Test the constant spline.
        List<Vector3> points = new ArrayList<Vector3>();
        Vector3 p1 = new Vector3(1, 2, 2);
        points.add(p1);
        Vector3 p2 = new Vector3(2, 1, 2);
        points.add(p2);
        Vector3 p3 = new Vector3(2, 2, 2);
        points.add(p3);
        NaturalSpline3D spline = NaturalSpline3D.createInterpolators(points);
        SplineCubicAsserter<Vector3> splineAsserter =
                new SplineCubicAsserter<Vector3>(
                        spline,
                        EuclideanMetric.VECTOR3,
                        p1, p2, p3);
        splineAsserter.doAsserts();
    }
    /**
     * Check that 1 point creates the 'constant'
     * spline.
     */
    @Test
    public void test4DCubic() {
        // Test the constant spline.
        List<Vector4> points = new ArrayList<Vector4>();
        Vector4 p1 = new Vector4(1, 2, 2, 1);
        points.add(p1);
        Vector4 p2 = new Vector4(2, 1, 2, 1);
        points.add(p2);
        Vector4 p3 = new Vector4(2, 1, 2, 2);
        points.add(p3);
        NaturalSpline4D spline = NaturalSpline4D.createInterpolators(points);
        SplineCubicAsserter<Vector4> splineAsserter =
                new SplineCubicAsserter<Vector4>(
                        spline,
                        EuclideanMetric.VECTOR4,
                        p1, p2, p3);
        splineAsserter.doAsserts();
    }
    
}
