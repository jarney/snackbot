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
 * This object is a string object wrapped in an Atom.  The StringAtom
 * object is immutable.
 * @author Jon
 */
public final class StringAtom extends Atom {
    private final String mValue;
    private StringAtom(final String value) {
        super(ATOM_TYPE_STRING);
        mValue = value;
    }
    /**
     * This method creates a new String atom from the given String object.
     * @param aValue A string to represent as an Atom.
     * @return The StringAtom object.
     */
    public static StringAtom newAtom(final String aValue) {
        return new StringAtom(aValue);
    }
    /**
     * This method returns the actual string content of the atom.
     * @return The string value of this atom.
     */
    @Override
    public String toString() {
        return mValue;
    }
    /**
     * This method tests whether two StringAtom objects are
     * equivalent.  They are equivalent if they are both objects
     * of type StringAtom and they both contain strings which are equal.
     * @param obj An object to test for equivalence.
     * @return True if the objects contain strings which are equal.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof StringAtom) {
            StringAtom strObj = (StringAtom) obj;
            return mValue.equals(strObj.mValue);
        }
        return false;
    }
    @Override
    public int hashCode() {
        return mValue.hashCode();
    }

    @Override
    public StringAtom getImmutable() {
        return this;
    }

    /**
     * This method returns this object itself
     * since there is no mutable version of this class.
     * @return This object itself.
     */
    @Override
    public StringAtom getMutable() {
        return this;
    }

}
