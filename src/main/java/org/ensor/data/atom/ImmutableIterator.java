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

import java.util.Iterator;

/**
 * This is an implementation of an iterator which
 * removes the ability to change the content of a collection
 * by overloading the 'remove' method and ensuring that it does not
 * actually remove the element.
 * @author jona
 * @param <Type> The type of data the iterator will return.
 */
public class ImmutableIterator<Type> implements Iterator<Type> {

    private final Iterator<Type> mIterator;
    /**
     * The constructor creates a new iterator which doesn't allow
     * the underlying connection to change, thereby ensuring immutability
     * semantics of the collection.
     * @param aIterator An iterator on which to base this iterator.
     */
    public ImmutableIterator(final Iterator<Type> aIterator) {
        mIterator = aIterator;
    }
    /**
     * This is a proxy to the collection's iterator
     * allowing determination of whether or not there
     * is another element to be returned.
     * @return True if there are more elements in the collection to return.
     */
    public boolean hasNext() {
        return mIterator.hasNext();
    }
    /**
     * This is a proxy to the collection's iterator
     * allowing the next element of the iterator to be returned.
     * @return The next element of the collection being iterated.
     */
    public Type next() {
        return mIterator.next();
    }
    /**
     * This method does nothing because doing something would violate
     * the idea of the underlying collection being immutable.
     */
    public void remove() {
    }
}
