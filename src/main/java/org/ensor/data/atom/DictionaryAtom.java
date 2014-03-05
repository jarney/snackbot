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
import java.util.Map;
import java.util.Set;

/**
 * A dictionary is a name-value pair mapping for data.  Dictionaries are used
 * for passing data from one system to another.
 * The data is stored internally in
 * {@link org.ensor.data.atom.Atom Atom} objects, each of which
 * has a type.  The data can then be serialized as a JSON object or other form
 * of type-safe serialized data through the ISerializer interface.
 * @author Jon
 */
public final class DictionaryAtom extends DictBase
    implements
        Iterable<Map.Entry<String, Atom>> {

    /**
     * This method constructs a new dictionary.
     */
    private DictionaryAtom() {
        super();
    }
    private DictionaryAtom(final Map.Entry<String, Atom> [] entries) {
        super();
        for (Map.Entry<String, Atom> e : entries) {
            mMap.put(e.getKey(), e.getValue().getMutable());
        }
    }
    private DictionaryAtom(final Iterable<Map.Entry<String, Atom>> entries) {
        super();
        for (Map.Entry<String, Atom> e : entries) {
            mMap.put(e.getKey(), e.getValue().getMutable());
        }
    }
    /**
     * This method creates a new dictionary from the given entries.
     * @param entries A set of entries with which to initialize the dictionary.
     * @return A newly constructed dictionary.
     */
    public static DictionaryAtom newAtom(
            final Map.Entry<String, Atom> [] entries) {
        return new DictionaryAtom(entries);
    }
    /**
     * This method creates a new dictionary from the given entries.
     * @param entries A set of entries with which to initialize the dictionary.
     * @return A newly constructed dictionary.
     */
    public static DictionaryAtom newAtom(
            final Iterable<Map.Entry<String, Atom>> entries) {
        return new DictionaryAtom(entries);
    }
    /**
     * This method creates a new empty dictionary.
     * @return A newly constructed dictionary.
     */
    public static DictionaryAtom newAtom() {
        return new DictionaryAtom();
    }
    /**
     * This method creates a new dictionary and initializes it with the content
     * of the given immutable dictionary.
     * @param aDict An immutable dictionary with which to initialize this
     *              dictionary.
     * @return A newly constructed dictionary which contains a copy of the
     *         given dictionary.
     */
    public static DictionaryAtom newAtom(final ImmutableDict aDict) {
        return newAtom(aDict.mMap.entrySet());
    }

    /**
     * This method sets the given atom at the specified key in the
     * dictionary.
     * @param aKey The key to add to the dictionary.
     * @param aValue The value to associate with this key.
     */
    public void setValue(final String aKey, final Atom aValue) {
        mMap.put(aKey, aValue);
    }
    /**
     * This method sets the specified integer at the specified key
     * of the dictionary.
     * @param aKey The key to add to the dictionary.
     * @param aValue The integer value to associate.
     */
    public void setInt(final String aKey, final int aValue) {
        setValue(aKey, IntAtom.newAtom(aValue));
    }
    /**
     * This method sets the specified floating point number at the
     * specified key of the dictionary.
     * @param aKey The key to add to the dictionary.
     * @param aValue The floating point value to associate.
     */
    public void setReal(final String aKey, final float aValue) {
        setValue(aKey, RealAtom.newAtom(aValue));
    }
    /**
     * This method sets the specified double floating point number at the
     * specified key of the dictionary.
     * @param aKey The key to add to the dictionary.
     * @param aValue The double floating point value to set.
     */
    public void setReal(final String aKey, final double aValue) {
        setValue(aKey, RealAtom.newAtom(aValue));
    }
    /**
     * This method sets the specified boolean value at the specified key
     * of the dictionary.
     * @param aKey The key to add to the dictionary.
     * @param aValue The boolean value to set.
     */
    public void setBoolean(final String aKey, final boolean aValue) {
        setValue(aKey, BoolAtom.newAtom(aValue));
    }
    /**
     * This method sets the specified list value at the specified key of the
     * dictionary.
     * @param aKey The key to add to the dictionary.
     * @param aList The list to add to the dictionary.
     */
    public void setList(final String aKey, final ListAtom aList) {
        setValue(aKey, aList);
    }
    /**
     * This method sets the specified list value at the specified key of the
     * dictionary.  If the dictionary is immutable, a mutable copy
     * is placed into the dictionary instead.
     * @param aKey A key to add to the dictionary.
     * @param aList The list to convert to a mutable copy and set.
     */
    public void setList(final String aKey, final ImmutableList aList) {
        setValue(aKey, aList.getMutable());
    }
    /**
     * This method sets the specified string value at the specified key of
     * the dictionary.
     * @param aKey The key to add to the dictionary.
     * @param aValue The string value to add to the dictionary.
     */
    public void setString(final String aKey, final String aValue) {
        setValue(aKey, StringAtom.newAtom(aValue));
    }
    /**
     * This method sets the specified dictionary value at the specified key
     * of the dictionary.
     * @param aKey The key to add to the dictionary.
     * @param aValue The dictionary to set.
     */
    public void setDictionary(final String aKey, final DictionaryAtom aValue) {
        setValue(aKey, aValue);
    }
    /**
     * This method sets the specified dictionary value at the specified key
     * of the dictionary.  If the dictionary is immutable, a mutable copy
     * is placed into the dictionary instead.
     * @param aKey A key to add to the dictionary.
     * @param aValue The dictionary to convert to a mutable copy and set.
     */
    public void setDictionary(final String aKey, final ImmutableDict aValue) {
        setValue(aKey, aValue.getMutable());
    }
    /**
     * This method returns the dictionary for the given key.
     * @param key The key at which to return the dictionary.
     * @return The dictionary for the given key.
     */
    public DictionaryAtom getDictionary(final String key) {
        return (DictionaryAtom) getValue(key);
    }
    /**
     * This method returns the list for the given key.
     * @param key The key at which to return the list.
     * @return The list at the given key of the dictionary.
     */
    public ListAtom getList(final String key) {
        return (ListAtom) getValue(key);
    }

    /**
     * This method constructs an immutable copy of the dictionary and returns
     * it.
     * @return An immutable copy of the dictionary.
     */
    public ImmutableDict getImmutable() {
        Set<Map.Entry<String, Atom>> entrySet = mMap.entrySet();
        ArrayList<Map.Entry<String, Atom>> list =
                new ArrayList<Map.Entry<String, Atom>>();
        for (Map.Entry<String, Atom> e : entrySet) {
            Pair<String, Atom> p = new Pair<String, Atom>(e.getKey(),
                    e.getValue().getImmutable());
            list.add(p);
        }
        return ImmutableDict.newAtom(list);
    }

    /**
     * This class is already mutable, therefore this method simply
     * returns the instance of the same class.
     * @return A reference to the mutable version of this class.
     */
    @Override
    public DictionaryAtom getMutable() {
        return this;
    }
}
