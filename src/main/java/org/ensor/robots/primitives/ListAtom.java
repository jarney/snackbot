/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ensor.robots.primitives;

import java.util.LinkedList;
import java.util.Iterator;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
/**
 *
 * @author Jon
 */
public class ListAtom extends Atom implements Iterable<Atom>, JSONSerializable, URLSerializable {
    LinkedList<Atom>            mList;
    public ListAtom() {
        super(ATOM_TYPE_LIST);
        mList = new LinkedList<Atom>();
    }
    public int size() {
        return mList.size();
    }
    public Atom get(int i) {
        return mList.get(i);
    }
    public String getString(int i) {
        return ((StringAtom)get(i)).getValue();
    }
    public int getInt(int i) {
        return ((IntAtom)get(i)).getValue();
    }
    public ListAtom getList(int i) {
        return (ListAtom)get(i);
    }
    public DictionaryAtom getDictionary(int i) {
        return (DictionaryAtom)get(i);
    }
    public boolean getBool(int i) {
        return ((BoolAtom)get(i)).getValue();
    }
    public double getReal(int i) {
        return ((RealAtom)get(i)).getValue();
    }
    public void append(Atom atom) {
        mList.add(atom);
    }
    public void append(String value) {
        append(new StringAtom(value));
    }
    public void append(boolean value) {
        append(new BoolAtom(value));
    }
    public void append(long value) {
        append(new IntAtom(value));
    }
    public void append(int value) {
        append(new IntAtom(value));
    }
    public void append(double value) {
        append(new RealAtom(value));
    }
    public void append(float value) {
        append(new RealAtom(value));
    }
    public void append(ListAtom value) {
        append((Atom)value);
    }
    public void append(DictionaryAtom value) {
        append((Atom)value);
    }
    public void clear() {
        mList.clear();
    }
    
    @Override
    public Iterator<Atom> iterator() {
        return mList.iterator();
    }

    @Override
    public void dump(java.util.logging.Logger log, String prefix) {
        log.info(prefix + "(ListAtom)");
        Iterator<Atom> it = mList.iterator();
        int i = 0;
        while (it.hasNext()) {
            Atom value = it.next();
            value.dump(log, prefix + "." + i);
            i++;
        }
    }
    public Object serializeToJSONObject() throws Exception {
        Iterator<Atom> it = this.iterator();
        JSONArray jso = new JSONArray();
        while (it.hasNext()) {
            Atom value = it.next();
            switch (value.getType()) {
                case Atom.ATOM_TYPE_BOOLEAN:
                    BoolAtom boolAtom = (BoolAtom)value;
                    jso.put(boolAtom.getValue());
                    break;
                case Atom.ATOM_TYPE_DICTIONARY:
                    DictionaryAtom dv = (DictionaryAtom)value;
                    jso.put(dv.serializeToJSONObject());
                    break;
                case Atom.ATOM_TYPE_INT:
                    IntAtom intAtom = (IntAtom)value;
                    jso.put(intAtom.getValue());
                    break;
                case Atom.ATOM_TYPE_LIST:
                    ListAtom listAtom = (ListAtom)value;
                    jso.put(listAtom.serializeToJSON());
                    break;
                case Atom.ATOM_TYPE_NIL:
                    break;
                case Atom.ATOM_TYPE_REAL:
                    RealAtom realAtom = (RealAtom)value;
                    jso.put(realAtom.getValue());
                    break;
                case Atom.ATOM_TYPE_STRING:
                    StringAtom stringAtom = (StringAtom)value;
                    jso.put(stringAtom.getValue());
                    break;
            }
        }
        return jso;
    }
    public String serializeToJSON() throws Exception {
        return ((JSONArray)serializeToJSONObject()).toString();
    }
    public String serializeToJSON(int indent) throws Exception {
        return ((JSONArray)serializeToJSONObject()).toString(indent);
    }
    public void deserializeFromJSON(String jsonString) throws Exception {

    }
    public void deserializeFromJSON(Object obj) throws Exception {
        JSONArray jso = (JSONArray)obj;
        int i;
        for (i = 0; i < jso.length(); i++) {
            Object value = jso.get(i);
            if (value instanceof Boolean) {
                Boolean booleanValue = (Boolean)value;
                this.append(booleanValue.booleanValue());
            }
            else if (value instanceof JSONArray) {
                ListAtom listValue = new ListAtom();
                listValue.deserializeFromJSON(value);
                this.append(listValue);
            }
            else if (value instanceof JSONObject) {
                DictionaryAtom dictionaryValue = new DictionaryAtom();
                dictionaryValue.deserializeFromJSON(value);
                this.append(dictionaryValue);
            }
            else if (value instanceof Number) {
                Number numberValue = (Number)value;
                if (value instanceof Integer) {
                    this.append(numberValue.intValue());
                }
                else {
                   this.append(numberValue.doubleValue());
                }
            }
            else if (value instanceof String) {
                String stringValue = (String)value;
                this.append(stringValue);
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
            mList.clear();
        }
        finally {
            super.finalize();
        }
    }


}
