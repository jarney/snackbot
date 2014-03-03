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
 * This class represents a floating point number.
 * @author Jon
 */
public final class RealAtom extends Atom {

    private static RealAtom mZero = new RealAtom(0);
    private static RealAtom mOne = new RealAtom(1);
    private static RealAtom mTwo = new RealAtom(2);
    private static RealAtom mMinusOne = new RealAtom(-1);

    private final double mValue;
    private RealAtom(final double value) {
        super(ATOM_TYPE_REAL);
        mValue = value;
    }
    private RealAtom(final float value) {
        super(ATOM_TYPE_REAL);
        mValue = (double) value;
    }
    /**
     * This method creates a new floating point atom corresponding
     * to the given value.
     * @param aValue The floating point number to represent.
     * @return A new floating point number Atom.
     */
    public static RealAtom newAtom(final float aValue) {
        if (aValue == 0.0f) {
            return mZero;
        }
        else if (aValue == 1.0f) {
            return mOne;
        }
        else if (aValue == 2.0f) {
            return mTwo;
        }
        else if (aValue == -1.0f) {
            return mMinusOne;
        }
        return new RealAtom(aValue);
    }
    /**
     * This method creates a new floating point atom corresponding
     * to the given value.
     * @param aValue The floating point number to represent.
     * @return A new floating point number Atom.
     */
    public static RealAtom newAtom(final double aValue) {
        if (aValue == 0.0) {
            return mZero;
        }
        else if (aValue == 1.0) {
            return mOne;
        }
        else if (aValue == 2.0) {
            return mTwo;
        }
        else if (aValue == -1.0) {
            return mMinusOne;
        }
        return new RealAtom(aValue);
    }
    /**
     * This method returns the floating point number contained
     * within this object.
     * @return The floating point number contained within this object.
     */
    public double getValue() {
        return mValue;
    }
    /**
     * This method tests whether two RealAtom objects are equal
     * by testing whether they are both of the type RealAtom and whether
     * they contain floating point values with the same value.
     * @param aOther An object to test for equivalence.
     * @return True if the objects represent the same floating point number.
     */
    @Override
    public boolean equals(final Object aOther) {
        if (aOther instanceof RealAtom) {
            RealAtom other = (RealAtom) aOther;
            return other.mValue == mValue;
        }
        return false;
    }

    private static final int HASH_BASE = 7;
    private static final int HASH_MULTIPLIER = 37;
    private static final int BITS_PER_INT = 32;
    @Override
    public int hashCode() {
        int hash = HASH_BASE;
        hash = HASH_MULTIPLIER * hash +
                (int) (Double.doubleToLongBits(this.mValue) ^
                      (Double.doubleToLongBits(this.mValue) >>> BITS_PER_INT));
        return hash;
    }

    @Override
    public RealAtom getImmutable() {
        return this;
    }

    /**
     * This method returns this object itself
     * since there is no mutable version of this class.
     * @return This object itself.
     */
    @Override
    public RealAtom getMutable() {
        return this;
    }
    /**
     * This method converts the floating point value into a string and returns
     * that string.
     * @return A string containing a representation of the floating point
     *         number.
     */
    @Override
    public String toString() {
        return "" + mValue;
    }
}
