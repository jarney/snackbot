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

import java.util.Map;

/**
 * This class represents a pair of objects similar to the std::pair from
 * the C++ STL libraries.
 * @author jona
 * @param <F> The type of the first element of the pair.
 * @param <S> The type of the second element of the pair.
 */
public final class Pair<F, S> implements Map.Entry<F, S> {
    private final F mFirst;
    private S mSecond;
    /**
     * The constructor creates a new pair from the two
     * individual elements.
     * @param aFirst The first element of the pair.
     * @param aSecond The second element of the pair.
     */
    public Pair(final F aFirst, final S aSecond) {
        mFirst = aFirst;
        mSecond = aSecond;
    }
    /**
     * This method returns the key (first element) of the pair.
     * @return The first element of the pair.
     */
    public F getKey() {
        return mFirst;
    }
    /**
     * This method returns the value (second element) of the pair.
     * @return The second element of the pair.
     */
    public S getValue() {
        return mSecond;
    }
    /**
     * This method sets the second value of the pair and returns its
     * original value.
     * @param v The new value for the second element of the pair.
     * @return The original value of the second element of the pair.
     */
    public S setValue(final S v) {
        S oldValue = mSecond;
        mSecond = v;
        return oldValue;
    }
    
    @Override
    public boolean equals(final Object aObject) {
        if (aObject instanceof Pair) {
            Pair other = (Pair) aObject;
            return mFirst.equals(other.getKey()) &&
                    mSecond.equals(other.getValue());
        }
        
        return false;
    }

    private static final int HASH_BASE = 7;
    private static final int HASH_MULTIPLIER = 97;
    
    @Override
    public int hashCode() {
        int hash = HASH_BASE;
        hash = HASH_MULTIPLIER * hash +
                (this.mFirst != null ? this.mFirst.hashCode() : 0);
        hash = HASH_MULTIPLIER * hash +
                (this.mSecond != null ? this.mSecond.hashCode() : 0);
        return hash;
    }
    
}
