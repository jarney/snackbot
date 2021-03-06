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

import java.util.List;
import org.ensor.math.geometry.IRealValueProjection;

/**
 * This class implements a collection of values of abstract type
 * &lt;VectorType&gt; for which there exists a way to map them into
 * R by providing a value extractor.  This is useful for extacting the 'X'
 * component of a Vector3 object, for example without making a separate
 * container to hold a collection of 'double' values.
 * @author jona
 */
class VectorValueCollection<VectorType> implements IValueCollection {
    private final List<VectorType> mValues;
    private final IRealValueProjection<VectorType> mExtractor;
    public VectorValueCollection(
            final List<VectorType> aValues,
            final IRealValueProjection<VectorType> aExtractor) {
        mValues = aValues;
        mExtractor = aExtractor;
    }
    public int length() {
        return mValues.size();
    }
    public double getValue(final int i) {
        return mExtractor.getValue(mValues.get(i));
    }
}
