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
 * This class is a
 * <a href="http://en.wikipedia.org/wiki/Projection_%28mathematics%29">
 * projection</a>
 * which returns a vector's 'Y'
 * <a href="http://en.wikipedia.org/wiki/Coordinate_system">coordinate</a>
 * .
 * @author jona
 */
public final class YProjection implements IRealValueProjection<IHasY> {
    /**
     * The
     * <a href="http://en.wikipedia.org/wiki/Projection_%28mathematics%29">
     * projection</a>
     * which returns a vector's 'Y'
     * <a href="http://en.wikipedia.org/wiki/Coordinate_system">coordinate</a>
     * .
     */
    public static final IRealValueProjection<IHasY> PROJECTION =
            new YProjection();
    /**
     * Returns the 'Y' coordinate.
     * @param aValue The vector to project.
     * @return The 'Y' coordinate.
     */
    public double getValue(final IHasY aValue) {
         return aValue.getY();
     }

}
