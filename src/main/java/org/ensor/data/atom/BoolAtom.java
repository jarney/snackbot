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

package org.ensor.data.atom;
/**
 * This is the primitive boolean data type.  There are only 2 instances of
 * this object because there are only 2 possible values.  The newAtom
 * method returns the appropriate instance of this immutable object.
 *
 * @author Jon
 */
public final class BoolAtom extends Atom {

    private static final BoolAtom TRUEATOM = new BoolAtom(true);
    private static final BoolAtom FALSEATOM = new BoolAtom(false);

    private final boolean mFlag;
    private BoolAtom(final boolean flag) {
        super(ATOM_TYPE_BOOLEAN);
        mFlag = flag;
    }
    /**
     * This method returns one of the 2 primitive immutable
     * Boolean objects (true and false).
     * @param aValue The boolean value to return.
     * @return The boolean atom object.
     */
    public static BoolAtom newAtom(final boolean aValue) {
        if (aValue) {
            return TRUEATOM;
        }
        else {
            return FALSEATOM;
        }
    }

    /**
     * This method returns the boolean value
     * of the object.
     * @return The boolean primitive value of the object.
     */
    public boolean getValue() {
        return mFlag;
    }

    /**
     * This method returns an immutable version
     * of this object.  Since the boolean object is
     * already immutable, it simply returns this instance
     * object itself.
     * @return An immutable version of this object.
     */
    @Override
    public BoolAtom getImmutable() {
        return this;
    }

    /**
     * This method returns this object itself
     * since there is no mutable version of this class.
     * @return This object itself.
     */
    @Override
    public BoolAtom getMutable() {
        return this;
    }

    /**
     * This method returns a string representation of the
     * boolean flag.
     *
     * @return A string representing the value of the boolean flag.
     */
    @Override
    public String toString() {
        if (mFlag) {
            return "true";
        }
        else {
            return "false";
        }
    }

}
