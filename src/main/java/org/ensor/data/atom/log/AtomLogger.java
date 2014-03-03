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

package org.ensor.data.atom.log;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ensor.data.atom.Atom;
import org.ensor.data.atom.BoolAtom;
import org.ensor.data.atom.DictionaryAtom;
import org.ensor.data.atom.IntAtom;
import org.ensor.data.atom.ListAtom;
import org.ensor.data.atom.RealAtom;
import org.ensor.data.atom.StringAtom;

/**
 *
 * @author jona
 */
public final class AtomLogger {

    private AtomLogger() {
    }
    
    public static void dump(
            final Logger log,
            final String prefix,
            final Atom aAtom) {
        switch (aAtom.getType()) {
            case Atom.ATOM_TYPE_DICTIONARY:
                dump(log, prefix, (DictionaryAtom) aAtom);
                break;
            case Atom.ATOM_TYPE_LIST:
                dump(log, prefix, (ListAtom) aAtom);
                break;
            case Atom.ATOM_TYPE_BOOLEAN:
                dump(log, prefix, (BoolAtom) aAtom);
                break;
            case Atom.ATOM_TYPE_INT:
                dump(log, prefix, (IntAtom) aAtom);
                break;
            case Atom.ATOM_TYPE_REAL:
                dump(log, prefix, (RealAtom) aAtom);
                break;
            case Atom.ATOM_TYPE_STRING:
                dump(log, prefix, (StringAtom) aAtom);
                break;
            default:
                log.log(Level.INFO, "{0}: unknown atom type {1}", new Object[] {
                    prefix, aAtom.getType()
                });
        }
    }
    public static void dump(Logger log, String prefix, BoolAtom aAtom) {
        if (aAtom.getValue()) {
            log.log(Level.INFO, "{0}: true (BoolAtom)", prefix);
        }
        else {
            log.log(Level.INFO, "{0}: false (BoolAtom)", prefix);
        }
    }

    public static void dump(Logger log, String prefix, DictionaryAtom aAtom) {
        log.log(Level.INFO, "{0} (DictionaryAtom)", prefix);
        for (Map.Entry<String, Atom> entry : aAtom.entrySet()) {
            String key = entry.getKey();
            Atom value = entry.getValue();
            if (value != null) {
                dump(log, prefix + "." + key, value);
            }
            else {
                log.log(Level.INFO, "{0}.{1} (null)", new Object[]{
                    prefix,
                    key
                });
            }
        }
    }
    
    public static void dump(
            final Logger log,
            final String prefix,
            final IntAtom aAtom) {
        log.log(Level.INFO, "{0}:{1} (IntAtom)", new Object[]{
            prefix,
            aAtom.getValue()
        });
    }

    public static void dump(
            final Logger log,
            final String prefix,
            final ListAtom aAtom) {
        log.log(Level.INFO, "{0}(ListAtom)", prefix);
        int i = 0;
        for (Atom value : aAtom) {
            dump(log, prefix + "." + i, value);
            i++;
        }
    }
    public static void dump(
            final Logger log,
            final String prefix,
            final RealAtom aAtom) {
        log.log(Level.INFO, "{0}:{1} (RealAtom)", new Object[]{
            prefix, aAtom.getValue()
        });
    }
    public static void dump(
            final Logger log,
            final String prefix,
            final StringAtom aAtom) {
        log.log(Level.INFO, "{0}:{1} (StringAtom)", new Object[]{
            prefix, aAtom.toString()
        });
    }
}
