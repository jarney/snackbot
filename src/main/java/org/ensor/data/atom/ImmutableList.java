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
 * This class represents a list of immutable objects and the list itself
 * is immutable.
 * @author Jon
 */
public final class ImmutableList extends ListBase implements
        Iterable<Atom> {

    private ImmutableList(final Iterable<Atom> aAtoms) {
        super();
        for (Atom value : aAtoms) {
            Atom ivalue = value.getImmutable();
            mList.add(ivalue);
        }
    }
    private ImmutableList(final Atom[] aAtoms) {
        super();
        for (Atom value : aAtoms) {
            Atom ivalue = value.getImmutable();
            mList.add(ivalue);
        }
    }
    /**
     * This method creates a new immutable list from the given collection of
     * objects.
     * @param aList A collection to copy.
     * @return An immutable list which contains immutable versions of the given
     *         elements.
     */
    public static ImmutableList newAtom(final Iterable<Atom> aList) {
        return new ImmutableList(aList);
    }
    /**
     * This method creates a new immutable list from the given collection of
     * objects.
     * @param aList A collection to copy.
     * @return An immutable list which contains immutable versions of the given
     *         elements.
     */
    public static ImmutableList newAtom(final Atom[] aList) {
        return new ImmutableList(aList);
    }
    /**
     * This method creates a new immutable list from the given collection of
     * objects.
     * @param aList A collection to copy.
     * @return An immutable list which contains immutable versions of the given
     *         elements.
     */
    public static ImmutableList newAtom(final ImmutableList aList) {
        return aList;
    }
    /**
     * This method returns the list value at the specified location of the
     * list.
     * @param i An index into the list (starting with zero and ending with
     *          size-1).
     * @return The element at the specified index.
     */
    public ImmutableList getList(final int i) {
        return (ImmutableList) get(i);
    }
    /**
     * This method returns the dictionary value at the specified location of the
     * list.
     * @param i An index into the list (starting with zero and ending with
     *          size-1).
     * @return The element at the specified index.
     */
    public ImmutableDict getDictionary(final int i) {
        return (ImmutableDict) get(i);
    }
    @Override
    public ImmutableList getImmutable() {
        return this;
    }

    /**
     * This method constructs a new mutable dictionary
     * from the immutable version of the dictionary.
     * @return A mutable version of the dictionary.
     */
    @Override
    public ListAtom getMutable() {
        return ListAtom.newAtom(this);
    }

}
