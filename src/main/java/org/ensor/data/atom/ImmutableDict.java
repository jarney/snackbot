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
import java.util.Map;

/**
 * This class is an immutable version of the
 * {@link org.ensor.data.atom.DictionaryAtom DictionaryAtom} container.
 * It is threadsafe by virtue of the fact that it cannot change and therefore
 * threads cannot have different views of this object.  It also ensures that
 * different event and thread handlers have the same view of the data contained
 * within this container.
 * @author Jon
 */
public final class ImmutableDict
    extends DictBase
    implements Iterable<Map.Entry<String, Atom>> {

    private static final ImmutableDict EMPTY_DICTIONARY = new ImmutableDict();
    /**
     * This method constructs a new dictionary.
     */
    private ImmutableDict() {
        super();
    }
    private ImmutableDict(final ImmutableDict atom) {
        super(atom.mMap);
    }

    private ImmutableDict(final Map.Entry<String, Atom> [] entries) {
        super();
        for (Map.Entry<String, Atom> e : entries) {
            Atom value = e.getValue();
            Atom ivalue = value.getImmutable();
            mMap.put(e.getKey(), ivalue);
        }
    }

    private ImmutableDict(final Iterable<Map.Entry<String, Atom>> entries) {
        super();
        for (Map.Entry<String, Atom> e : entries) {
            Atom value = e.getValue();
            Atom ivalue = value.getImmutable();
            mMap.put(e.getKey(), ivalue);
        }
    }
    /**
     * This method returns an immutable dictionary consisting of the
     * given entries.  If any of the entries given are not immutable,
     * they are converted into their immutable counterparts before adding
     * them to the collection.
     * @param entries An array of entries to add to the immutable collection.
     * @return An immutable dictionary consisting of the entries given
     *         converted (if necessary) into immutable objects first.
     */
    public static ImmutableDict newAtom(
            final Map.Entry<String, Atom> [] entries) {
        return new ImmutableDict(entries);
    }
    /**
     * This method returns an immutable dictionary consisting of the
     * given entries.  If any of the entries given are not immutable,
     * they are converted into their immutable counterparts before adding
     * them to the collection.
     * @param entries An array of entries to add to the immutable collection.
     * @return An immutable dictionary consisting of the entries given
     *         converted (if necessary) into immutable objects first.
     */
    public static ImmutableDict newAtom(
            final Iterable<Map.Entry<String, Atom>> entries) {
        return new ImmutableDict(entries);
    }
    /**
     * This method returns an immutable dictionary consisting of the
     * empty dictionary.
     * @return The empty immutable dictionary.
     */
    public static ImmutableDict newAtom() {
        return EMPTY_DICTIONARY;
    }
    /**
     * This method constructs a new immutable dictionary object from the given
     * immutable dictionary.  This method actually just returns the dictionary
     * object given because there is really no point in creating a new copy
     * of an already immutable object.
     * @param aDict The immutable object to copy.
     * @return A copy (really a reference to the same object) of the given
     *         immutable dictionary.
     */
    public static ImmutableDict newAtom(final ImmutableDict aDict) {
        return aDict;
    }
    /**
     * This method creates a copy of the given dictionary which is immutable.
     * The given dictionary is mutable, thus a copy of the elements of the
     * dictionary as well as a deep copy of all of the elements below it
     * must be made in order to ensure that this dictionary is really immutable.
     * @param aDict A mutable dictionary to create a copy of.
     * @return An immutable copy of the given dictionary.
     */
    public static ImmutableDict newAtom(final DictionaryAtom aDict) {
        return newAtom(aDict.entrySet());
    }
    /**
     * This method returns the dictionary at the specified key location.
     * Note that this method always returns an immutable object because this
     * itself is immutable and was constructed so that all of the descendents
     * are also immutable.
     * @param key The key for which to find and return the dictionary.
     * @return The dictionary at the specified key location.
     */
    public ImmutableDict getDictionary(final String key) {
        Atom atom = getValue(key);
        if (atom == null) {
            return null;
        }
        if (atom.getType() == Atom.ATOM_TYPE_DICTIONARY) {
            return (ImmutableDict) atom;
        }
        return null;
    }
    /**
     * This method returns the list at the specified key location.
     * Note that this method always returns an immutable object because this
     * itself is immutable and was constructed so that all of the descendents
     * are also immutable.
     * @param key The key for which to find and return the list.
     * @return The list at the specified key location.
     */
    public ImmutableList getList(final String key) {
        Atom atom = getValue(key);
        if (atom == null) {
            return null;
        }
        if (atom.getType() == Atom.ATOM_TYPE_LIST) {
            return (ImmutableList) atom;
        }
        return null;
    }
    @Override
    public ImmutableDict getImmutable() {
        return this;
    }
    /**
     * This method returns an iterator over the set of entries in the
     * dictionary.
     * @return An iterator over the entries in the dictionary.
     */
    @Override
    public Iterator<Map.Entry<String, Atom>> iterator() {
        return new ImmutableIterator<Map.Entry<String, Atom>>(
                mMap.entrySet().iterator()
        );
    }

    /**
     * This method constructs a new mutable dictionary
     * from the immutable version of the dictionary.
     * @return A mutable version of the dictionary.
     */
    @Override
    public DictionaryAtom getMutable() {
        return DictionaryAtom.newAtom(this);
    }

}
