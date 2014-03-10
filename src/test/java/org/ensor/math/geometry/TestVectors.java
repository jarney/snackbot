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

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author jona
 */
public class TestVectors {
    private static final double DOUBLE_TOLERANCE = 0.002;

    @Test
    public void testVectors2() {
        Vector2 v1 = new Vector2(2, 1);
        Vector2 v2 = new Vector2(1, 2);
        
        Vector2 result = v2.subtract(v1).negate().multiply(-1).add(v1);
        
        Assert.assertEquals(v2.getX(), result.getX(), DOUBLE_TOLERANCE);
        Assert.assertEquals(v2.getY(), result.getY(), DOUBLE_TOLERANCE);
    }
    @Test
    public void testVectors3() {
        Vector3 v1 = new Vector3(2, 1, 0);
        Vector3 v2 = new Vector3(1, 2, 1);
        
        Vector3 result = v2.subtract(v1).negate().multiply(-1).add(v1);
        
        Assert.assertEquals(v2.getX(), result.getX(), DOUBLE_TOLERANCE);
        Assert.assertEquals(v2.getY(), result.getY(), DOUBLE_TOLERANCE);
        Assert.assertEquals(v2.getZ(), result.getZ(), DOUBLE_TOLERANCE);
    }
    @Test
    public void testVectors4() {
        Vector4 v1 = new Vector4(2, 1, 1, 2);
        Vector4 v2 = new Vector4(1, 2, 0, 1);
        
        Vector4 result = v2.subtract(v1).negate().multiply(-1).add(v1);
        
        Assert.assertEquals(v2.getX(), result.getX(), DOUBLE_TOLERANCE);
        Assert.assertEquals(v2.getY(), result.getY(), DOUBLE_TOLERANCE);
        Assert.assertEquals(v2.getZ(), result.getZ(), DOUBLE_TOLERANCE);
        Assert.assertEquals(v2.getW(), result.getW(), DOUBLE_TOLERANCE);
    }
    
}
