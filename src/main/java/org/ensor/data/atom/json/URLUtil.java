/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ensor.data.atom.json;
import java.net.URLEncoder;
import org.ensor.data.atom.Atom;
import org.ensor.data.atom.BoolAtom;
import org.ensor.data.atom.IDictionaryVisitable;
import org.ensor.data.atom.IDictionaryVisitor;
import org.ensor.data.atom.IListVisitable;
import org.ensor.data.atom.IntAtom;
import org.ensor.data.atom.RealAtom;
import org.ensor.data.atom.StringAtom;

/**
 * The JSONUtil class is responsible for converting DictionaryAtom and
 * ListAtom classes into appropriate JSON objects.  These are used
 * primarily by the SmartFox extension classes to convert internal
 * events into events that are stimulated back to the client (and
 * perhaps eventually to other SmartFox instances).
 *
 * @author Jon
 */
public final class URLUtil {
    private URLUtil() {
    }
    /**
     * This method encodes the given dictionary in a URL encoded format.
     * @param atom The dictionary to encode.
     * @return The URL encoded string which represents this dictionary.
     * @throws java.lang.Exception If an encoding or character set exception
     *         is thrown, it is propagated.
     */
    public static String urlEncode(final IDictionaryVisitable atom)
            throws Exception {

        final StringBuilder data = new StringBuilder();

        IDictionaryVisitor visitor = new IDictionaryVisitor() {
            boolean firstTime = true;

            public void visit(final String key, final Atom value)
                    throws Exception {
                String encodedKey = URLEncoder.encode(key, "UTF-8");
                String encodedValue = "";

                switch (value.getType()) {
                    case Atom.ATOM_TYPE_BOOLEAN:
                        BoolAtom boolAtom = (BoolAtom) value;
                        encodedValue = URLEncoder.encode(
                                Boolean.toString(boolAtom.getValue()), "UTF-8");
                        break;
                    case Atom.ATOM_TYPE_DICTIONARY:
                        IDictionaryVisitable dictionaryAtom =
                                (IDictionaryVisitable) value;
                        encodedValue = URLEncoder.encode(
                                JSONStringSerializer.instance().serializeTo(
                                        dictionaryAtom),
                                "UTF-8");
                        break;
                    case Atom.ATOM_TYPE_INT:
                        IntAtom intAtom = (IntAtom) value;
                        encodedValue = URLEncoder.encode(
                                Integer.toString(intAtom.getValue()), "UTF-8");
                        break;
                    case Atom.ATOM_TYPE_LIST:
                        IListVisitable listAtom = (IListVisitable) value;
                        encodedValue = URLEncoder.encode(
                                JSONStringSerializer.instance().serializeTo(
                                        listAtom),
                                "UTF-8");
                        break;
                    case Atom.ATOM_TYPE_REAL:
                        RealAtom realAtom = (RealAtom) value;
                        encodedValue = URLEncoder.encode(
                                Double.toString(realAtom.getValue()), "UTF-8");
                        break;
                    case Atom.ATOM_TYPE_STRING:
                        StringAtom stringAtom = (StringAtom) value;
                        encodedValue = URLEncoder.encode(
                                stringAtom.toString(), "UTF-8");
                        break;
                    default:
                        break;
                }
                if (!firstTime) {
                    data.append("&");
                }

                data.append(encodedKey);
                data.append("=");
                data.append(encodedValue);
                firstTime = false;
            }
        };
        atom.visitPairs(visitor);

        return data.toString();
    }
}
