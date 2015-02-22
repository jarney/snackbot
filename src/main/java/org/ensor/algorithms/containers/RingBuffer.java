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

package org.ensor.algorithms.containers;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is a ring-buffer which allows data to be added to it
 * but continues to retain a fixed-size.
 *
 * @author jona
 * @param <T> Type of data contained within the buffer.
 */
public class RingBuffer<T> implements Iterable<T>, Collection<T> {
    private final List<T> mData;
    private final int mSize;

    /**
     * The constructor creates a ring-buffer
     * guaranteed not to exceed a specific size.
     *
     * @param aSize The maximum size of the buffer.
     */
    public RingBuffer(final int aSize) {
        mData = new LinkedList<T>();
        mSize = aSize;
    }
    /**
     * Adds an element to the ring buffer
     * and removes the first element in the event
     * that there are more than the maximum number
     * of elements.
     * @param aData Data element to add to the buffer.
     * @return Returns true indicating that a new element was added.
     */
    public boolean add(final T aData) {
        boolean rc = mData.add(aData);
        trimRing();
        return rc;
    }
    private void trimRing() {
        while (mData.size() > mSize) {
            mData.remove(0);
        }
    }
    /**
     * Returns an iterator capable of iterating
     * the buffer.
     * @return An iterator which will iterate each of the elements in the ring
     *         beginning with the least recently added.
     */
    public Iterator<T> iterator() {
        return mData.iterator();
    }
    /**
     * This method returns the element from the ring buffer
     * represented by the ith element.
     * @param i The index of the element to return.
     * @return The element from the ring buffer at the specified index.
     */
    public T get(final int i) {
        return mData.get(i);
    }

    /**
     * Returns the total number of elements inside the buffer.
     *
     * @return The number of elements in the buffer.
     */
    public int size() {
        return mData.size();
    }
    
    /**
     * Returns the maximum size of the buffer.
     * @return The maximum number of elements that this buffer can hold.
     */
    public int getMaxSize() {
        return mSize;
    }
    
    /**
     * Removes all elements from the buffer.
     */
    public void clear() {
        mData.clear();
    }
    /**
     * This method returns true if there are no elements in the ring.
     * @return True if the number of elements in the buffer is zero.
     */
    public boolean isEmpty() {
        return mData.isEmpty();
    }

    /**
     * Returns true if the given element is contained within
     * the ring buffer.
     * @param o An element to check for containment.
     * @return True if the given object is present in the ring buffer.
     */
    public boolean contains(final Object o) {
        return mData.contains(o);
    }
    /**
     * Converts the ring buffer into a fixed-length
     * array containing the objects in the ring buffer.
     * @return Returns a fixed length array containing the ring buffer contents.
     */
    public Object[] toArray() {
        return mData.toArray();
    }

    /**
     * Converts the ring buffer into a fixed-length
     * array of the same type as the ring-buffer itself.
     * @param <T> The type of data in the ring buffer.
     * @param ts An array of the correct type.
     * @return A fixed length array containing the elements from the
     *         ring buffer.
     */
    public <T> T[] toArray(final T[] ts) {
        return mData.toArray(ts);
    }
    /**
     * Removes a single object from the ring buffer.
     * @param o Object to remove from ring buffer.
     * @return Returns true if the item was removed and false if the item
     *         was not in the ring-buffer.
     */
    public boolean remove(final Object o) {
        return mData.remove(o);
    }
    /**
     * Tests whether the given collection is entirely contained
     * within this collection.
     * @param clctn A collection of elements to test for containment.
     * @return True if the given collection is a subset of this container.
     */
    public boolean containsAll(final Collection<?> clctn) {
        return mData.containsAll(clctn);
    }
    /**
     * Adds all of the elements in the given collection to this
     * ring buffer.  If the size of the ring buffer is exceeded,
     * then the buffer is trimmed in order to maintain the
     * maximum size.
     * @param clctn A collection to add to the ring buffer.
     * @return True if any elements were added to the buffer.
     */
    public boolean addAll(final Collection<? extends T> clctn) {
        boolean rc = mData.addAll(clctn);
        trimRing();
        return rc;
    }
    /**
     * Removes all of the given elements from the ring-buffer.
     * @param clctn A collection to remove from the buffer.
     * @return True if any elements were removed.
     */
    public boolean removeAll(final Collection<?> clctn) {
        return mData.removeAll(clctn);
    }
    /**
     * Retains only the elements in this collection that are contained in the
     * specified collection (optional operation).
     *
     * @param clctn collection containing elements to be retained in this
     *              collection.
     * @return True if this collection changed as a result of the call.
     */
    public boolean retainAll(final Collection<?> clctn) {
        return mData.retainAll(clctn);
    }
}
