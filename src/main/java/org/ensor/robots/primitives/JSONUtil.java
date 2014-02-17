/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ensor.robots.primitives;
import org.json.JSONException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * The JSONUtil class is responsible for converting DictionaryAtom and ListAtom classes
 * into appropriate JSON objects.  These are used primarily by the SmartFox extension classes
 * to convert internal events into events that are stimulated back to the client (and perhaps eventually to other SmartFox instances).
 *
 * @author Jon
 */
public class JSONUtil {
    public static String urlEncode(DictionaryAtom atom) throws Exception {

        String data = "";

        boolean firstTime = true;
        for( Map.Entry<Atom, Atom> entry : atom.entrySet() ) {
            Atom atomKey = entry.getKey();
            Atom value = entry.getValue();
            if (atomKey.getType() != Atom.ATOM_TYPE_STRING) {
                throw new JSONException("Cannot serialize dictionary with non-string keys.");
            }
            StringAtom keyAtom = (StringAtom)atomKey;
            String key = keyAtom.getValue();
            String encodedKey = URLEncoder.encode(key, "UTF-8");
            String encodedValue = "";

            switch (value.getType()) {
                case Atom.ATOM_TYPE_BOOLEAN:
                    BoolAtom boolAtom = (BoolAtom)value;
                    encodedValue = URLEncoder.encode(Boolean.toString(boolAtom.getValue()), "UTF-8");
                    break;
                case Atom.ATOM_TYPE_DICTIONARY:
                    DictionaryAtom dictionaryAtom = (DictionaryAtom)value;
                    encodedValue = URLEncoder.encode(dictionaryAtom.serializeToJSON(), "UTF-8");
                    break;
                case Atom.ATOM_TYPE_INT:
                    IntAtom intAtom = (IntAtom)value;
                    encodedValue = URLEncoder.encode(Integer.toString(intAtom.getValue()), "UTF-8");
                    break;
                case Atom.ATOM_TYPE_LIST:
                    ListAtom listAtom = (ListAtom)value;
                    encodedValue = URLEncoder.encode(listAtom.serializeToJSON(), "UTF-8");
                    break;
                case Atom.ATOM_TYPE_NIL:
                    break;
                case Atom.ATOM_TYPE_REAL:
                    RealAtom realAtom = (RealAtom)value;
                    encodedValue = URLEncoder.encode(Double.toString(realAtom.getValue()), "UTF-8");
                    break;
                case Atom.ATOM_TYPE_STRING:
                    StringAtom stringAtom = (StringAtom)value;
                    encodedValue = URLEncoder.encode(stringAtom.getValue(), "UTF-8");
                    break;
            }
            if (firstTime) {
                data += encodedKey + "=" + encodedValue;
                firstTime = false;
            }
            else {
                data += "&" + encodedKey + "=" + encodedValue;
            }
        }
        return data;
    }
}
