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

import org.junit.Assert;
import org.junit.Test;


/**
 * Test some basic algebraic properties of polynomials.
 * @author jona
 */
public class TestPolynomials {
    
    private static final double TEST_FACTOR = 7.0;
    private static final double DOUBLE_TOLERANCE = 0.002;
    private static final int ITERATIONS = 100;
    
    static class VectorPropertyAsserter<T extends IDifferentiableFunction<T>> {
        public void assertProperties(
                final T aFunctionA,
                final T aFunctionB) {
            for (int i = 0; i < ITERATIONS; i++) {
                double t = (double) i / ITERATIONS;

                // Verify some algebraic properties:
                
                // f(t) + g(t) = (f + g)(t)
                T sum = aFunctionA.add(aFunctionB);
                double s1 = sum.getValue(t);
                double s2 = aFunctionA.getValue(t) + aFunctionB.getValue(t);
                Assert.assertEquals(s1, s2, DOUBLE_TOLERANCE);
                
                
                // f(t) - g(t) = (f - g)(t)
                T dif = aFunctionA.subtract(aFunctionB);
                double d1 = dif.getValue(t);
                double d2 = aFunctionA.getValue(t) - aFunctionB.getValue(t);
                Assert.assertEquals(d1, d2, DOUBLE_TOLERANCE);
                
                // k f(t) = (kf)(t)
                T mul = aFunctionA.multiply(TEST_FACTOR);
                double m1 = mul.getValue(t);
                double m2 = aFunctionA.getValue(t)*TEST_FACTOR;
                Assert.assertEquals(m1, m2, DOUBLE_TOLERANCE);
                
                // -f(t) = (-f)(t)
                T neg = aFunctionA.negate();
                double n1 = neg.getValue(t);
                double n2 = -aFunctionA.getValue(t);
                Assert.assertEquals(n1, n2, DOUBLE_TOLERANCE);
            }
        }
    }
    
    public static final double K1 = 2.4;
    public static final double K2 = 7.9;
    public static final double K3 = 33.1;
    public static final double K4 = 5.7;

    private CubicFunction getCubic1() {
        return new CubicFunction(K1, K2, K3, K4);
    }
    private CubicFunction getCubic2() {
        return new CubicFunction(K2, K3, K4, K1);
    }
    
    @Test
    public void testCubic() {
        CubicFunction df1 = getCubic1();
        CubicFunction df2 = new CubicFunction(df1.getDerivative());
        
        VectorPropertyAsserter<CubicFunction> asserter =
                new VectorPropertyAsserter<CubicFunction>();
        
        asserter.assertProperties(df1, df2);
        
    }
    @Test
    public void testQuadratic() {
        QuadraticFunction df1 = getCubic1().getDerivative();
        QuadraticFunction df2 = new QuadraticFunction(df1.getDerivative());
        
        VectorPropertyAsserter<QuadraticFunction> asserter =
                new VectorPropertyAsserter<QuadraticFunction>();
        
        asserter.assertProperties(df1, df2);

        Assert.assertTrue(df1.hasRoots());
        
        double root0 = df1.getRoot0();
        Assert.assertEquals(0, df1.getValue(root0), DOUBLE_TOLERANCE);
        double root1 = df1.getRoot1();
        Assert.assertEquals(0, df1.getValue(root1), DOUBLE_TOLERANCE);
        
        Assert.assertTrue(root0 < root1);
        
        // This one, for example, has no root
        // since the discriminant is negative: b^2-4ac = K1^2 - 4*K1^2 = -3K^2.
        QuadraticFunction noroots = new QuadraticFunction(K1, K1, K1);
        Assert.assertFalse(noroots.hasRoots());
        
        
        
    }
    @Test
    public void testLinear() {
        LinearFunction df1 = getCubic1().getDerivative().getDerivative();
        LinearFunction df2 = new LinearFunction(df1.getDerivative());
        
        VectorPropertyAsserter<LinearFunction> asserter =
                new VectorPropertyAsserter<LinearFunction>();
        
        asserter.assertProperties(df1, df2);
        
        double root = df1.getRoot();
        Assert.assertEquals(0, df1.getValue(root), DOUBLE_TOLERANCE);
        
    }
    
    
    @Test
    public void testConstant() {
        ConstantFunction df1 = getCubic1().getDerivative().getDerivative().getDerivative();
        ConstantFunction df2 = getCubic2().getDerivative().getDerivative().getDerivative();
        
        VectorPropertyAsserter<ConstantFunction> asserter =
                new VectorPropertyAsserter<ConstantFunction>();
        
        asserter.assertProperties(df1, df2);
        
    }
}
