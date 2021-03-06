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

import java.util.List;

/**
 * This class represents a list of elements.
 * @author Jon
 */
public final class ListAtom extends ListBase implements
        Iterable<Atom> {

    private ListAtom() {
        super();
    }

    private ListAtom(final List<Atom> aAtoms) {
        super(aAtoms);
    }

    private ListAtom(final ImmutableList aList) {
        super();
        for (Atom a : aList) {
            mList.add(a.getMutable());
        }
    }
    private ListAtom(final ListAtom aList) {
        super();
        for (Atom a : aList) {
            mList.add(a.getMutable());
        }
    }
    /**
     * This method creates a new list object which contains the given
     * elements.
     * @param aAtoms A list of objects to copy.
     * @return A newly created mutable ListAtom object.
     */
    public static ListAtom newAtom(final List<Atom> aAtoms) {
        return new ListAtom(aAtoms);
    }
    /**
     * This method creates a new (empty) list object.
     * @return The list object created.
     */
    public static ListAtom newAtom() {
        return new ListAtom();
    }
    /**
     * This method creates a new list from the given immutable
     * list.  This essentially creates a mutable copy of an immutable object.
     * @param aList An immutable list to copy.
     * @return A mutable copy of the list.
     */
    public static ListAtom newAtom(final ImmutableList aList) {
        return new ListAtom(aList);
    }
    /**
     * This method creates a new list from the given immutable
     * list.  This essentially creates a mutable copy of an immutable object.
     * @param aList A list to copy.
     * @return A mutable copy of the list.
     */
    public static ListAtom newAtom(final ListAtom aList) {
        return new ListAtom(aList);
    }
    /**
     * This method adds the specified element to the end of the list.
     * @param aAtom The element to add to the list.
     */
    protected void append(final Atom aAtom) {
        mList.add(aAtom);
    }
    /**
     * This method creates a new list and adds it as a child of this
     * list.
     * @return The newly created list appended to this list.
     */
    public ListAtom newList() {
        ListAtom list = ListAtom.newAtom();
        mList.add(list);
        return list;
    }
    /**
     * This method appends a copy of the given list to the list.
     * @param aList A list to copy and append.
     */
    public void append(ListAtom aList) {
        ListAtom list = ListAtom.newAtom(aList);
        mList.add(list);
    }
    /**
     * This method appends a copy of the given dictionary to the list.
     * @param aList A dictionary to copy and append.
     */
    public void append(DictionaryAtom aList) {
        DictionaryAtom list = DictionaryAtom.newAtom(aList);
        mList.add(list);
    }
    /**
     * This method creates a new dictionary and adds it as a child of this
     * list.
     * @return The newly created dictionary appended to this list.
     */
    public DictionaryAtom newDictionary() {
        DictionaryAtom dict = DictionaryAtom.newAtom();
        mList.add(dict);
        return dict;
    }

    /**
     * This method adds the specified element to the end of the list.
     * @param aAtom The element to add to the list.
     */
    public void append(final String aAtom) {
        append(StringAtom.newAtom(aAtom));
    }
    /**
     * This method adds the specified element to the end of the list.
     * @param aAtom The element to add to the list.
     */
    public void append(final int aAtom) {
        append(IntAtom.newAtom(aAtom));
    }
    /**
     * This method adds the specified element to the end of the list.
     * @param aAtom The element to add to the list.
     */
    public void append(final long aAtom) {
        append(IntAtom.newAtom(aAtom));
    }
    /**
     * This method adds the specified element to the end of the list.
     * @param aAtom The element to add to the list.
     */
    public void append(final float aAtom) {
        append(RealAtom.newAtom(aAtom));
    }
    /**
     * This method adds the specified element to the end of the list.
     * @param aAtom The element to add to the list.
     */
    public void append(final double aAtom) {
        append(RealAtom.newAtom(aAtom));
    }
    /**
     * This method adds the specified element to the end of the list.
     * @param aAtom The element to add to the list.
     */
    public void append(final boolean aAtom) {
        append(BoolAtom.newAtom(aAtom));
    }
    /**
     * This method returns the list value at the specified location of the
     * list.
     * @param i An index into the list (starting with zero and ending with
     *          size-1).
     * @return The element at the specified index.
     */
    public ListAtom getList(final int i) {
        return (ListAtom) get(i);
    }
    /**
     * This method returns the dictionary value at the specified location of the
     * list.
     * @param i An index into the list (starting with zero and ending with
     *          size-1).
     * @return The element at the specified index.
     */
    public DictionaryAtom getDictionary(final int i) {
        return (DictionaryAtom) get(i);
    }
    @Override
    public ImmutableList getImmutable() {
        Atom[] list = new Atom[mList.size()];
        int i = 0;
        for (Atom a : mList) {
            Atom ia = a.getImmutable();
            list[i++] = ia;
        }
        return ImmutableList.newAtom(list);
    }

    /**
     * This method constructs a new mutable dictionary
     * from the immutable version of the dictionary.
     * @return A mutable version of the dictionary.
     */
    @Override
    public ListAtom getMutable() {
        return this;
    }
}
