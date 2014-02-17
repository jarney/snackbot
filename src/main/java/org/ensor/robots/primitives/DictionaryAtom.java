/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ensor.robots.primitives;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * A dictionary is a name-value pair mapping for data.  Dictionaries are used
 * for passing data from one biote to another or from a biote to a client.
 * The data is stored internally in
 * {@link org.ensor.robots.primitives.Atom Atom} objects, each of which
 * has a type.  The data can then be serialized as a JSON object or other form
 * of type-safe serialized data.
 * @author Jon
 */
public class DictionaryAtom extends Atom implements JSONSerializable, URLSerializable{
    private HashMap<Atom,Atom>        mMap;
    public DictionaryAtom() {
        super(ATOM_TYPE_DICTIONARY);
        mMap = new HashMap<Atom,Atom>();
    }
    public DictionaryAtom(DictionaryAtom atom) {
        mMap = atom.mMap;
    }
    public boolean isEmpty() {
        return mMap.isEmpty();
    }
    public boolean containsKey(int key) {
        return mMap.containsKey(new IntAtom(key));
    }
    public boolean containsKey(String key) {
        return mMap.containsKey(new StringAtom(key));
    }
    public void setValue(Atom key, Atom value) {
        if (value == null) {
            mMap.remove(key);
        }
        else {
            mMap.put(key, value);
        }
    }
    public Atom getValue(String key) {
        return getValue(new StringAtom(key));
    }
    public Atom getValue(Atom key) {
        Atom atom = mMap.get(key);
        if (atom == null) {
            return null;
        }
        return atom;
    }
    public void setString(String key, String value) {
        mMap.put(new StringAtom(key), new StringAtom(value));
    }
    public void setBool(String key, boolean value) {
        mMap.put(new StringAtom(key), new BoolAtom(value));
    }
    public void setInt(String key, long value) {
        mMap.put(new StringAtom(key), new IntAtom(value));
    }
    public void setInt(String key, int value) {
        mMap.put(new StringAtom(key), new IntAtom(value));
    }
    public void setReal(String key, double value) {
        mMap.put(new StringAtom(key), new RealAtom(value));
    }
    public void setReal(String key, float value) {
        mMap.put(new StringAtom(key), new RealAtom(value));
    }
    public void setList(String key, ListAtom value) {
        mMap.put(new StringAtom(key), value);
    }
    public void setDictionary(String key, DictionaryAtom value) {
        mMap.put(new StringAtom(key), value);
    }
    public IntAtom getIntAtom(String key) {
        Atom atom = getValue(new StringAtom(key));
        if (atom == null) {
            return null;
        }
        if (atom.getType() == Atom.ATOM_TYPE_INT) {
            return (IntAtom)atom;
        }
        return null;
    }
    public int getInt(String key) {
        return getIntAtom(key).getValue();
    }
    public BoolAtom getBoolAtom(String key) {
        Atom atom = getValue(new StringAtom(key));
        if (atom == null) {
            return null;
        }
        if (atom.getType() == Atom.ATOM_TYPE_BOOLEAN) {
            return (BoolAtom)atom;
        }
        return null;
    }
    public boolean getBool(String key) {
        return getBoolAtom(key).getValue();
    }
    public boolean getBool(String...key) {
        int i;
        DictionaryAtom d = this;
        for (i = 0; i < key.length-1; i++) {
            d = d.getDictionary(key[i]);
        }
        return d.getBool(key[key.length-1]);
    }
    public RealAtom getRealAtom(String key) {
        Atom atom = getValue(new StringAtom(key));
        if (atom == null) {
            return null;
        }
        if (atom.getType() == Atom.ATOM_TYPE_REAL) {
            return (RealAtom)atom;
        }
        return null;
    }
    public double getReal(String key) {
        return getRealAtom(key).getValue();
    }
    public double getReal(String...key) {
        int i;
        DictionaryAtom d = this;
        for (i = 0; i < key.length-1; i++) {
            d = d.getDictionary(key[i]);
        }
        return d.getReal(key[key.length-1]);
    }
    public DictionaryAtom getDictionary(String...key) {
        int i;
        DictionaryAtom d = this;
        for (i = 0; i < key.length-1; i++) {
            d = d.getDictionary(key[i]);
        }
        return d.getDictionary(key[key.length-1]);
    }
    public ListAtom getList(String...key) {
        int i;
        DictionaryAtom d = this;
        for (i = 0; i < key.length-1; i++) {
            d = d.getDictionary(key[i]);
        }
        return d.getList(key[key.length-1]);
    }
    public int getInt(String... key) {
        int i;
        DictionaryAtom d = this;
        for (i = 0; i < key.length-1; i++) {
            d = d.getDictionary(key[i]);
        }
        return d.getInt(key[key.length-1]);
    }
    public String getString(String... key) {
        int i;
        DictionaryAtom d = this;
        for (i = 0; i < key.length-1; i++) {
            d = d.getDictionary(key[i]);
            if (d == null) {
                return null;
            }
        }
        return d.getString(key[key.length-1]);
    }
    public String getString(String key) {
        Atom atom = getValue(new StringAtom(key));
        if (atom == null) {
            return null;
        }
        if (atom.getType() == Atom.ATOM_TYPE_STRING) {
            return ((StringAtom)atom).getValue();
        }
        return null;
    }
    public DictionaryAtom getDictionary(String key) {
        Atom atom = getValue(new StringAtom(key));
        if (atom == null) {
            return null;
        }
        if (atom.getType() == Atom.ATOM_TYPE_DICTIONARY) {
            return (DictionaryAtom)atom;
        }
        return null;
    }
    public DictionaryAtom getDictionary(int key) {
        Atom atom = getValue(new IntAtom(key));
        if (atom == null) {
            return null;
        }
        if (atom.getType() == Atom.ATOM_TYPE_DICTIONARY) {
            return (DictionaryAtom)atom;
        }
        return null;
    }
    public ListAtom getList(String key) {
        Atom atom = getValue(new StringAtom(key));
        if (atom == null) {
            return null;
        }
        if (atom.getType() == Atom.ATOM_TYPE_LIST) {
            return (ListAtom)atom;
        }
        return null;
    }
    
    public Set<Atom> keySet() {
        return mMap.keySet();
    }
    
    public Collection<Atom> values() {
        return mMap.values();
    }

    public Set<Map.Entry<Atom, Atom>> entrySet() {
        return mMap.entrySet();
    }

    public void dump(java.util.logging.Logger log, String prefix) {
        log.info(prefix + " (DictionaryAtom)");
        Iterator<Atom> it = mMap.keySet().iterator();
        while (it.hasNext()) {
            Atom key = it.next();
            Atom value = getValue(key);
            if (key.getType() == Atom.ATOM_TYPE_STRING) {
                StringAtom strKey = (StringAtom)key;
                if (value != null) {
                    value.dump(log, prefix + "." + strKey.getValue());
                }
                else {
                    log.info(prefix + "." + strKey.getValue() + " (null)");
                }
            }
            else if (key.getType() == Atom.ATOM_TYPE_INT) {
                IntAtom intKey = (IntAtom)key;
                if (value != null) {
                    value.dump(log, prefix + "." + intKey.getValue());
                }
                else {
                    log.info(prefix + "." + intKey.getValue() + " (null)");
                }
            }
        }
    }
    public Object serializeToJSONObject() throws Exception {
        JSONObject jso = new JSONObject();
        for ( Map.Entry<Atom, Atom> entry : this.entrySet() ) {
            Atom atomKey = entry.getKey();
            Atom value = entry.getValue();
            if (atomKey.getType() != Atom.ATOM_TYPE_STRING) {
                throw new JSONException("Cannot serialize dictionary with non-string keys.");
            }
            StringAtom keyAtom = (StringAtom)atomKey;
            String key = keyAtom.getValue();
            switch (value.getType()) {
                case Atom.ATOM_TYPE_BOOLEAN:
                    BoolAtom boolAtom = (BoolAtom)value;
                    jso.put(key, boolAtom.getValue());
                    break;
                case Atom.ATOM_TYPE_DICTIONARY:
                    DictionaryAtom dictionaryAtom = (DictionaryAtom)value;
                    jso.put(key, dictionaryAtom.serializeToJSONObject());
                    break;
                case Atom.ATOM_TYPE_INT:
                    IntAtom intAtom = (IntAtom)value;
                    jso.put(key, intAtom.getValue());
                    break;
                case Atom.ATOM_TYPE_LIST:
                    ListAtom listAtom = (ListAtom)value;
                    jso.put(key, listAtom.serializeToJSONObject());
                    break;
                case Atom.ATOM_TYPE_NIL:
                    break;
                case Atom.ATOM_TYPE_REAL:
                    RealAtom realAtom = (RealAtom)value;
                    jso.put(key, realAtom.getValue());
                    break;
                case Atom.ATOM_TYPE_STRING:
                    StringAtom stringAtom = (StringAtom)value;
                    jso.put(key, stringAtom.getValue());
                    break;
            }
        }
        return jso;
    }
    public String serializeToJSON() throws Exception {
        return ((JSONObject)serializeToJSONObject()).toString();
    }
    public String serializeToJSON(int indent) throws Exception {
        return ((JSONObject)serializeToJSONObject()).toString(indent);
    }

    public void deserializeFromJSON(String jsonString) throws Exception {
        JSONObject jso = new JSONObject(jsonString);
        deserializeFromJSON(jso);
    }
    public void deserializeFromJSON(Object obj) throws Exception {
        JSONObject jso = (JSONObject)obj;
        Iterator it = jso.keys();
        while (it.hasNext()) {
            String key = (String)it.next();
            Object value = jso.get(key);
            if (value instanceof Boolean) {
                Boolean booleanValue = (Boolean)value;
                this.setBool(key, booleanValue.booleanValue());
            }
            else if (value instanceof JSONArray) {
                ListAtom list = new ListAtom();
                list.deserializeFromJSON(value);
                this.setList(key, list);
            }
            else if (value instanceof JSONObject) {
                DictionaryAtom dictionary = new DictionaryAtom();
                dictionary.deserializeFromJSON(value);
                this.setDictionary(key, dictionary);
            }
            else if (value instanceof Number) {
                Number numberValue = (Number)value;
                if (value instanceof Integer) {
                    this.setInt(key, numberValue.intValue());
                }
                else {
                   this.setReal(key, numberValue.doubleValue());
                }
            }
            else if (value instanceof String) {
                String stringValue = (String)value;
                this.setString(key, stringValue);
            }
        }

    }
    public String serializeToURL() {
        return "";
    }
    public void serializeFromURL(String jsonString) {

    }
    @Override
    public void finalize() throws Throwable {
        try {
            mMap.clear();
        }
        finally {
            super.finalize();
        }
    }

}
