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
 * This interface represents an element of a
 * <a href="http://en.wikipedia.org/wiki/Vector_space">vector space</a>.
 * The element assumes that the
 * <a href="http://en.wikipedia.org/wiki/Field_%28mathematics%29">field 'F'</a>
 * of the vector space is
 * the
 * <a href="http://en.wikipedia.org/wiki/Real_number">
 * real numbers</a>
 * (as represented approximately by a double).
 * @param <VectorType> The concrete type of the vector.
 * @author jona
 */
public interface IVector<VectorType> {
    /**
     * This method represents addition of two vectors to produce a third.
     * @param aValue The vector to be added.
     * @return The vector resulting from adding this vector to the given vector.
     */
    VectorType add(VectorType aValue);
    /**
     * This method represents subtraction of two vectors to produce a third.
     * @param aValue The result of subtracting the given vector from this one.
     * @return The vector resulting from subtracting the vectors.
     */
    VectorType subtract(VectorType aValue);
    /**
     * This method returns a new vector which is the result of multiplying this
     * vector by the given number.
     * @param aValue A number to multiply.
     * @return The vector resulting from multiplying this vector by the given
     *         number.
     */
    VectorType multiply(double aValue);
    /**
     * This method returns a new vector which is the result of multiplying this
     * vector by minus-one (-1).
     * @return The negation of this vector.
     */
    VectorType negate();
}
