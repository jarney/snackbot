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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
abstract class DictBase extends Atom
    implements
        Iterable<Map.Entry<String, Atom>>,
        IDictionaryVisitable {

    protected final Map<String, Atom>        mMap;

    /**
     * This method constructs a new dictionary.
     */
    protected DictBase() {
        super(ATOM_TYPE_DICTIONARY);
        mMap = new HashMap<String, Atom>();
    }
    protected DictBase(final Map<String, Atom> aMap) {
        super(ATOM_TYPE_DICTIONARY);
        mMap = aMap;
    }

    private DictBase(final Map.Entry<String, Atom> [] entries) {
        this();
        for (Map.Entry<String, Atom> e : entries) {
            mMap.put(e.getKey(), e.getValue());
        }
    }
    private DictBase(final List<Map.Entry<String, Atom>> entries) {
        this();
        for (Map.Entry<String, Atom> e : entries) {
            mMap.put(e.getKey(), e.getValue());
        }
    }
    /**
     * This method determines if the dictionary has any elements inside it.
     *
     * @return True if the dictionary has no elements inside it.
     */
    public boolean isEmpty() {
        return mMap.isEmpty();
    }

    /**
     * Determines if the specified key has an element associated with it.
     * @param key The key to check.
     * @return True if the given key is associated with any element.
     */
    public boolean containsKey(final String key) {
        return mMap.containsKey(key);
    }
    /**
     * Returns the data associated with this key or null if no
     * such element is set.
     * @param key The key for which to retrieve the data.
     * @return The atomic data element associated with this key.
     */
    public Atom getValue(final String key) {
        return mMap.get(key);
    }
    protected IntAtom getIntAtom(final String key) {
        Atom atom = getValue(key);
        if (atom == null) {
            return null;
        }
        if (atom.getType() == Atom.ATOM_TYPE_INT) {
            return (IntAtom) atom;
        }
        return null;
    }
    /**
     * This method returns the integer associated with the given
     * key.  Note that if no integer is associated with this key, a null-pointer
     * exception may be thrown.
     * @param key The key for the integer to retrieve.
     * @return The integer associated with this dictionary key.
     */
    public int getInt(final String key) {
        return getIntAtom(key).getValue();
    }
    protected BoolAtom getBoolAtom(final String key) {
        Atom atom = getValue(key);
        if (atom == null) {
            return null;
        }
        if (atom.getType() == Atom.ATOM_TYPE_BOOLEAN) {
            return (BoolAtom) atom;
        }
        return null;
    }
    /**
     * This method returns the boolean associated with the given key.
     * Note that if no boolean is associated with this key, a null-pointer
     * exception may be thrown.
     * @param key The key for the boolean to retrieve.
     * @return The boolean associated with this dictionary key.
     */
    public boolean getBool(final String key) {
        return getBoolAtom(key).getValue();
    }
    protected RealAtom getRealAtom(final String key) {
        Atom atom = getValue(key);
        if (atom == null) {
            return null;
        }
        if (atom.getType() == Atom.ATOM_TYPE_REAL) {
            return (RealAtom) atom;
        }
        return null;
    }
    /**
     * This method retrieves the floating point number associated with
     * the given key.  Note that if no floating point number is associated
     * with this key, a null-pointer exception may be thrown.
     * @param key The key to read.
     * @return The floating point number associated with the given key.
     */
    public double getReal(final String key) {
        return getRealAtom(key).getValue();
    }
    /**
     * This method retrieves the string associated with the given key.
     * If a non-string value is associated with this key, the value will be
     * converted into a string using that atom's "toString" method.
     *
     * @param key The key to retrieve.
     * @return A string representation of the data stored at this location.
     */
    public String getString(final String key) {
        Atom atom = getValue(key);
        if (atom == null) {
            return null;
        }
        return atom.toString();
    }
    /**
     * This method returns the set of keys which are defined for this
     * dictionary.
     * @return The set of keys associated with this dictionary.
     */
    public Set<String> keySet() {
        return mMap.keySet();
    }
    /**
     * This method returns the set of entries (key-value pairs)
     * in the dictionary.
     * @return A set containing the key-value pairs in the dictionary.
     */
    public Set<Map.Entry<String, Atom>> entrySet() {
        return mMap.entrySet();
    }
    /**
     * This method returns an iterator over the set of entries in the
     * dictionary.
     * @return An iterator over the entries in the dictionary.
     */
    @Override
    public Iterator<Map.Entry<String, Atom>> iterator() {
        return mMap.entrySet().iterator();
    }
    /**
     * This method provides a visitor which allows each of the key-value
     * pairs to be visited.  The visitation is not recursive, dictionaries
     * and lists stored within this dictionary are not visited.
     * @param aVisitor The visitor object which can visit each of the elements.
     * @throws Exception If the visitor throws an exception, it is re-thrown
     *                   and the visitation stops.
     */
    public void visitPairs(final IDictionaryVisitor aVisitor) throws Exception {
        for (Map.Entry<String, Atom> entry : mMap.entrySet()) {
            aVisitor.visit(entry.getKey(), entry.getValue());
        }
    }

}
