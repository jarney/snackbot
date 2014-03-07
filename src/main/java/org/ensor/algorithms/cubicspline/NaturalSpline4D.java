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
import org.ensor.math.geometry.IPath;
import org.ensor.math.geometry.Vector4;
import org.ensor.math.geometry.WExtractor;
import org.ensor.math.geometry.XExtractor;
import org.ensor.math.geometry.YExtractor;
import org.ensor.math.geometry.ZExtractor;

/**
 *
 * @author jona
 */
public final class NaturalSpline4D
    extends PathBase<Vector4>
    implements IPath<Vector4> {

    /**
     * The constructor creates a natural cubic spline path
     * which can interpolate between the various points given
     * using a cubic polynomial for each path segment.  The path
     * segments are constructed in such a way that they are smooth everywhere
     * and their first derivatives are also smooth.
     *
     * @param aPoints The list of points to interpolate between.
     */
    public NaturalSpline4D(final List<Vector4> aPoints) {
        super(createInterpolators(aPoints));
    }

    private static IInterpolator<Vector4>[] createInterpolators(
            final List<Vector4> aPoints) {

        CubicInterpolatorFactory cif = new CubicInterpolatorFactory();

        IPrimitiveInterpolator[] xInterp = cif.createInterpolators(
                new VectorValueCollection(aPoints, XExtractor.XEXTRACTOR));
        IPrimitiveInterpolator[] yInterp = cif.createInterpolators(
                new VectorValueCollection(aPoints, YExtractor.YEXTRACTOR));
        IPrimitiveInterpolator[] zInterp = cif.createInterpolators(
                new VectorValueCollection(aPoints, ZExtractor.ZEXTRACTOR));
        IPrimitiveInterpolator[] wInterp = cif.createInterpolators(
                new VectorValueCollection(aPoints, WExtractor.WEXTRACTOR));

        int num = xInterp.length;
        IInterpolator<Vector4>[] interpolators = new IInterpolator[num];

        for (int j = 0; j < num; j++) {
            interpolators[j] = new Vector4Interpolator(
                    xInterp[j], yInterp[j], zInterp[j], wInterp[j]
            );
        }

        return interpolators;
    }

}