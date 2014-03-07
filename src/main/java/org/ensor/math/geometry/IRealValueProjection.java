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
 * This interface represents a way to project an object
 * into the
  * <a href="http://en.wikipedia.org/wiki/Real_number">
 * real numbers</a>.  This is
 * particularly useful as a
 * <a href="http://en.wikipedia.org/wiki/Projection_%28mathematics%29">
 * projection</a>
 * which extracts a particular
 * <a href="http://en.wikipedia.org/wiki/Coordinate_system">coordinate</a>
 * from
 * a vector for example.
 * @param <T> Type of value to extract data from.
 * @author jona
 */
public interface IRealValueProjection<T> {
    /**
     * The value of the projection for this object.
     * @param aValue The object to project into the real numbers.
     * @return The value of the projection for the given object.
     */
    double getValue(final T aValue);
}
