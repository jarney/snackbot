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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a list of objects of mixed type.
 * @author Jon
 */
abstract class ListBase extends Atom implements
        Iterable<Atom>,
        IListVisitable {

    protected final List<Atom>            mList;

    protected ListBase() {
        super(ATOM_TYPE_LIST);
        mList = new ArrayList<Atom>();
    }

    protected ListBase(final List<Atom> aAtoms) {
        super(ATOM_TYPE_LIST);
        mList = aAtoms;
    }
    /**
     * This method returns the number of elements contained
     * within the list.
     * @return The number of elements in the list.
     */
    public int size() {
        return mList.size();
    }
    /**
     * This method returns the element at the specified location of the list.
     * @param i An index into the list (starting with zero and ending with
     *          size-1).
     * @return The element at the specified index.
     */
    public Atom get(final int i) {
        return mList.get(i);
    }
    /**
     * This method returns the string value at the specified location of the
     * list.
     * @param i An index into the list (starting with zero and ending with
     *          size-1).
     * @return The element at the specified index.
     */
    public String getString(final int i) {
        return ((StringAtom) get(i)).toString();
    }
    /**
     * This method returns the string value at the specified location of the
     * list.
     * @param i An index into the list (starting with zero and ending with
     *          size-1).
     * @return The element at the specified index.
     */
    public int getInt(final int i) {
        return ((IntAtom) get(i)).getValue();
    }
    /**
     * This method returns the boolean value at the specified location of the
     * list.
     * @param i An index into the list (starting with zero and ending with
     *          size-1).
     * @return The element at the specified index.
     */
    public boolean getBool(final int i) {
        return ((BoolAtom) get(i)).getValue();
    }
    /**
     * This method returns the floating point value at the specified location
     * of the list.
     * @param i An index into the list (starting with zero and ending with
     *          size-1).
     * @return The element at the specified index.
     */
    public double getReal(final int i) {
        return ((RealAtom) get(i)).getValue();
    }

    @Override
    public Iterator<Atom> iterator() {
        return mList.iterator();
    }

    public void visitAtoms(final IListVisitor aVisitor) throws Exception {
        for (Atom a : mList) {
            aVisitor.visit(a);
        }
    }
}
