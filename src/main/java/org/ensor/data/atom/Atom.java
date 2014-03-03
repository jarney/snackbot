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
 * An atom is the primitive abstract data container for events.  It consists
 * of a type and derived classes hold the actual data associated with the atom.
 * @author Jon
 */
public abstract class Atom {
    private final int mType;

    /**
     * This constant indicates that the object is a string.
     */
    public static final int ATOM_TYPE_STRING = 1;
    /**
     * This constant indicates that the object is an integer.
     */
    public static final int ATOM_TYPE_INT = 2;
    /**
     * This constant indicates that the object is a boolean value.
     */
    public static final int ATOM_TYPE_BOOLEAN = 3;
    /**
     * This constant indicates that the object is a floating point
     * number (of the highest precision available).
     */
    public static final int ATOM_TYPE_REAL = 4;
    /**
     * This constant indicates that the object is a list of other objects.
     */
    public static final int ATOM_TYPE_LIST = 5;
    /**
     * This constant indicates that the object is an associative array
     * of key-value pairs.  For this type, the key is always presumed to
     * be a string.
     */
    public static final int ATOM_TYPE_DICTIONARY = 6;
    /**
     * The constructor indicates the type of atom to be constructed.  The
     * constructor must only be called from the constructor of one of the
     * primitive atom types.
     * @param atomType The type of the atom being constructed.
     */
    protected Atom(final int atomType) {
        mType = atomType;
    }
    /**
     * This indicates the type of data indicates by this object.
     * @return The type of atom represented by this object.
     */
    public int getType() {
        return mType;
    }
    /**
     * This method can be used to obtain an immutable representation of the
     * same data structure.  Most of the primitive types are already immutable
     * and only the container (list, dictionary) types undergo some type of
     * conversion in order to provide an immutable version.
     * @return An immutable version of the atom.
     */
    public abstract Atom getImmutable();

    /**
     * This method returns a mutable copy of the atom if the atom has
     * a mutable counterpart.
     * @return A mutable version of the atom (if one exists).
     */
    public abstract Atom getMutable();

};
