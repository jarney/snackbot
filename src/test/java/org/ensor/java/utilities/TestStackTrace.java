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

package org.ensor.java.utilities;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author jona
 */
public class TestStackTrace {
    @Test
    public void testStackTrace() {
        try {
            throw new Exception("sometest");
        }
        catch (Exception ex) {
            String str = StackTrace.getStackTrace(ex);
            
            String containsString = 
                    "at org.ensor.java.utilities." +
                    "TestStackTrace.testStackTrace(TestStackTrace.java";
            // Make sure that the stack trace includes this
            // function.
            Assert.assertTrue(str.contains(containsString));
        }
        
        // Cover the constructor for the hell of it.
        StackTrace st = new StackTrace();
        Assert.assertNotNull(st);
    }
}
