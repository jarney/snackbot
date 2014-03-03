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

package org.ensor.data.atom.json;

import java.util.ArrayList;
import org.ensor.data.atom.ISerializer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.ensor.data.atom.Atom;
import org.ensor.data.atom.BoolAtom;
import org.ensor.data.atom.IDictionaryVisitable;
import org.ensor.data.atom.IDictionaryVisitor;
import org.ensor.data.atom.IListVisitable;
import org.ensor.data.atom.IListVisitor;
import org.ensor.data.atom.ImmutableDict;
import org.ensor.data.atom.ImmutableList;
import org.ensor.data.atom.IntAtom;
import org.ensor.data.atom.Pair;
import org.ensor.data.atom.RealAtom;
import org.ensor.data.atom.StringAtom;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author jona
 */
public class JSONSerializer
    implements ISerializer<JSONObject, JSONArray> {
    
    private static final JSONSerializer mInstance = new JSONSerializer();
    private JSONSerializer() {
    }
    public static JSONSerializer instance() {
        return mInstance;
    }
    
    public JSONObject serializeTo(IDictionaryVisitable aDict) throws Exception {
        final JSONObject jso = new JSONObject();
        
        IDictionaryVisitor visitor = new IDictionaryVisitor() {
            public void visit(String key, Atom value) throws Exception {
                switch (value.getType()) {
                    case Atom.ATOM_TYPE_BOOLEAN:
                        BoolAtom boolAtom = (BoolAtom) value;
                        jso.put(key, boolAtom.getValue());
                        break;
                    case Atom.ATOM_TYPE_DICTIONARY:
                        IDictionaryVisitable dictionaryAtom = 
                                (IDictionaryVisitable) value;
                        jso.put(key, serializeTo(dictionaryAtom));
                        break;
                    case Atom.ATOM_TYPE_INT:
                        IntAtom intAtom = (IntAtom) value;
                        jso.put(key, intAtom.getValue());
                        break;
                    case Atom.ATOM_TYPE_LIST:
                        IListVisitable listAtom = (IListVisitable) value;
                        jso.put(key, serializeTo(listAtom));
                        break;
                    case Atom.ATOM_TYPE_REAL:
                        RealAtom realAtom = (RealAtom) value;
                        jso.put(key, realAtom.getValue());
                        break;
                    case Atom.ATOM_TYPE_STRING:
                        StringAtom stringAtom = (StringAtom) value;
                        jso.put(key, stringAtom.toString());
                        break;
                }
            }
            
        };
        
        aDict.visitPairs(visitor);
        
        return jso;
    }

    public JSONArray serializeTo(final IListVisitable aListAtom)
            throws Exception {
        final JSONArray jso = new JSONArray();

        IListVisitor visitor = new IListVisitor() {
            public void visit(Atom value) throws Exception {
                switch (value.getType()) {
                    case Atom.ATOM_TYPE_BOOLEAN:
                        BoolAtom boolAtom = (BoolAtom) value;
                        jso.put(boolAtom.getValue());
                        break;
                    case Atom.ATOM_TYPE_DICTIONARY:
                        IDictionaryVisitable dv = (IDictionaryVisitable) value;
                        jso.put(serializeTo(dv));
                        break;
                    case Atom.ATOM_TYPE_INT:
                        IntAtom intAtom = (IntAtom) value;
                        jso.put(intAtom.getValue());
                        break;
                    case Atom.ATOM_TYPE_LIST:
                        IListVisitable listAtom = (IListVisitable) value;
                        jso.put(serializeTo(listAtom));
                        break;
                    case Atom.ATOM_TYPE_REAL:
                        RealAtom realAtom = (RealAtom) value;
                        jso.put(realAtom.getValue());
                        break;
                    case Atom.ATOM_TYPE_STRING:
                        StringAtom stringAtom = (StringAtom) value;
                        jso.put(stringAtom.toString());
                        break;
                }
            }

        };
        
        aListAtom.visitAtoms(visitor);
            
        return jso;
        
    }
    
    public ImmutableDict serializeFrom(JSONObject aFrom) throws Exception {
        Iterator<String> it = aFrom.keys();
        List<Map.Entry<String, Atom>> entryList =
                new ArrayList<Map.Entry<String, Atom>>();
        while (it.hasNext()) {
            String key = it.next();
            Object value = aFrom.get(key);
            if (value instanceof Boolean) {
                Boolean booleanValue = (Boolean) value;
                entryList.add(new Pair<String, Atom>(key,
                        BoolAtom.newAtom(booleanValue)));
            }
            else if (value instanceof JSONArray) {
                ImmutableList list = serializeFromList((JSONArray) value);
                entryList.add(new Pair<String, Atom>(key, list));
            }
            else if (value instanceof JSONObject) {
                ImmutableDict dictionary = serializeFrom((JSONObject) value);
                entryList.add(new Pair<String, Atom>(key, dictionary));
            }
            else if (value instanceof Number) {
                Number numberValue = (Number) value;
                if (value instanceof Integer) {
                    entryList.add(new Pair<String, Atom>(key,
                            IntAtom.newAtom(numberValue.intValue())));
                }
                else {
                    entryList.add(new Pair<String, Atom>(key,
                            RealAtom.newAtom(numberValue.doubleValue())));
                }
            }
            else if (value instanceof String) {
                String stringValue = (String) value;
                entryList.add(new Pair<String, Atom>(key,
                        StringAtom.newAtom(stringValue)));
            }
        }
        ImmutableDict dict = ImmutableDict.newAtom(entryList);
        return dict;
    }

    public ImmutableList serializeFromList(JSONArray aArray) throws Exception {
        List<Atom> entryList =
                new ArrayList<Atom>();
        int i;
        for (i = 0; i < aArray.length(); i++) {
            Object value = aArray.get(i);
            if (value instanceof Boolean) {
                Boolean booleanValue = (Boolean) value;
                entryList.add(BoolAtom.newAtom(booleanValue));
            }
            else if (value instanceof JSONArray) {
                ImmutableList listValue = serializeFromList((JSONArray) value);
                entryList.add(listValue);
            }
            else if (value instanceof JSONObject) {
                ImmutableDict dictionaryValue =
                        serializeFrom((JSONObject) value);
                entryList.add(dictionaryValue);
            }
            else if (value instanceof Number) {
                Number numberValue = (Number) value;
                if (value instanceof Integer) {
                    entryList.add(IntAtom.newAtom(numberValue.intValue()));
                }
                else {
                    entryList.add(RealAtom.newAtom(numberValue.floatValue()));
                }
            }
            else if (value instanceof String) {
                String stringValue = (String) value;
                entryList.add(StringAtom.newAtom(stringValue));
            }
        }
        return ImmutableList.newAtom(entryList);
    }

}
