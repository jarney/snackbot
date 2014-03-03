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
 * An integer atom stores an integer.
 *
 * @author Jon
 */
public final class IntAtom extends Atom {

    private static final int MIN_INTEGER = 0;
    private static final int MAX_INTEGER = 9;

    private static final IntAtom [] ATOMS = {
        new IntAtom(0),
        new IntAtom(1),
        new IntAtom(2),
        new IntAtom(3),
        new IntAtom(4),
        new IntAtom(5),
        new IntAtom(6),
        new IntAtom(7),
        new IntAtom(8),
        new IntAtom(9)
    };

    private final int mValue;
    private IntAtom(final long value) {
        super(ATOM_TYPE_INT);
        mValue = (int) value;
    }
    private IntAtom(final int value) {
        super(ATOM_TYPE_INT);
        mValue = value;
    }
    /**
     * This method constructs a new integer object corresponding to
     * the specified integer value.
     * @param aValue The integer value to store.
     * @return The integer object created.
     */
    public static IntAtom newAtom(final int aValue) {
       if (aValue >= MIN_INTEGER && aValue <= MAX_INTEGER) {
            return ATOMS[aValue];
        }
        return new IntAtom(aValue);
     }
    /**
     * This method constructs a new integer object corresponding to
     * the specified integer value.
     * @param aValue The integer value to store.
     * @return The integer object created.
     */
    public static IntAtom newAtom(final long aValue) {
        if (aValue >= MIN_INTEGER && aValue <= MAX_INTEGER) {
            return ATOMS[(int) aValue];
        }
        return new IntAtom(aValue);
    }
    /**
     * This method returns the integer value of this atom.
     * @return The integer value of this atom.
     */
    public int getValue() {
        return mValue;
    }
    /**
     * This method tests the given object for equivalence with this one.
     * The objects are equal if they are both of type IntAtom and the value
     * of the integers is the same.
     * @param obj The object to test for equivalence.
     * @return True if the given object is equivalent to this one.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof IntAtom) {
            IntAtom strObj = (IntAtom) obj;
            return mValue == strObj.mValue;
        }
        return false;
    }
    @Override
    public int hashCode() {
        return mValue;
    }

    @Override
    public Atom getImmutable() {
        return this;
    }

    /**
     * This method returns this object itself
     * since there is no mutable version of this class.
     * @return This object itself.
     */
    @Override
    public IntAtom getMutable() {
        return this;
    }

    @Override
    public String toString() {
        return "" + mValue;
    }
}
