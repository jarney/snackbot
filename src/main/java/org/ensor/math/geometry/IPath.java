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
 * This interface models a smooth path function which is capable of returning
 * a vector position for any arbitrary point in time "t" along the path.
 * @param <VectorType> The type of position value returned by the path.
 * @author jona
 */
public interface IPath<VectorType> {

    /**
     * Returns the position along the path for the given
     * point in time.
     *
     * @param aTime Point in time from 0 (beginning of path) to 1 (end of path).
     * @return The 3d position associated with this point in time.
     */
    VectorType getPosition(double aTime);


    /**
     * Returns the <a href="http://en.wikipedia.org/wiki/Tangent">tangent</a>
     * to the curve for the given point in time.
     * @param aTime Point in time from 0 (beginning of path) to 1 (end of path).
     * @return The 3d tangent vector (normalized to 1) associated with this
     *         point in time.
     */
    VectorType getDirection(double aTime);

    /**
     * Returns the overall length of the path.  Note that this is
     * the actual length traveled, not the 'as the crow flies' distance
     * between start and end.  Also note that for simple paths such as linear
     * interpolation, this is fairly exact, however, for more complicated curves
     * like cubic splines, this is an estimate since making a closed-form
     * expression for these curves is extremely difficult.
     * @return The path length traveled from start to end.
     */
    double getLength();

}
