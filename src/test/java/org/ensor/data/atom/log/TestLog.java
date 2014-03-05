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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.ensor.data.atom.DictionaryAtom;
import org.ensor.data.atom.ImmutableDict;
import org.ensor.data.atom.ListAtom;
import org.ensor.data.atom.json.JSONStringSerializer;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author jona
 */
public class TestLog {
    @Test
    public void testLog() {
        DictionaryAtom dict = DictionaryAtom.newAtom();
        
        DictionaryAtom subdict = DictionaryAtom.newAtom();
        ListAtom sublist = ListAtom.newAtom();

        ListAtom nonemptylist = ListAtom.newAtom();
        nonemptylist.append("one");
        
        dict.setList("nonemptylist", nonemptylist);
        dict.setList("list", sublist);
        dict.setDictionary("dict", subdict);
        dict.setString("string", "string");
        dict.setReal("real", 1.2f);
        dict.setInt("int", 12334);
        dict.setBoolean("bool", Boolean.FALSE);
        dict.setBoolean("bool2", Boolean.TRUE);
        
        Logger l = Logger.getGlobal();
        l.setLevel(Level.INFO);
        
        final List<LogRecord> records = new ArrayList<LogRecord>();
        
        Handler handler = new Handler() {

            @Override
            public void publish(LogRecord lr) {
                records.add(lr);
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        };
        
        l.addHandler(handler);

        AtomLogger.dump(l, "dict:", dict);

        l.removeHandler(handler);
        
        Assert.assertEquals(10, records.size());
        
    }
}
