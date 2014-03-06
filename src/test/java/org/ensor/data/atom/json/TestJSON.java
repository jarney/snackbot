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

import org.ensor.data.atom.DictionaryAtom;
import org.ensor.data.atom.ImmutableDict;
import org.ensor.data.atom.ImmutableList;
import org.ensor.data.atom.ListAtom;
import org.junit.Assert;
import org.junit.Test;
/**
 *
 * @author jona
 */
public class TestJSON {
    @Test
    public void testDictSerializer() throws Exception {
        DictionaryAtom dict = DictionaryAtom.newAtom();
        
        ListAtom sublist = dict.newList("list");
        DictionaryAtom subdict = dict.newDictionary("dict");
        dict.setString("string", "string");
        dict.setReal("real", 1.2f);
        dict.setInt("int", 12334);
        dict.setBoolean("bool", Boolean.FALSE);
        
        JSONStringSerializer ser = JSONStringSerializer.instance();
        
        String jsonString = ser.serializeTo(dict);
        
        ImmutableDict fromserializer = ser.serializeFrom(jsonString);
        
        Assert.assertEquals(dict, fromserializer);
        
    }
    
    @Test
    public void testListSerializer() throws Exception {
        ListAtom list = ListAtom.newAtom();
        
        ListAtom sublist = list.newList();
        DictionaryAtom subdict = list.newDictionary();
        list.append("string");
        list.append(1.2f);
        list.append(12334);
        list.append(Boolean.FALSE);
        
        JSONStringSerializer ser = JSONStringSerializer.instance();
        
        String jsonString = ser.serializeTo(list);
        
        ImmutableList fromserializer = ser.serializeFromList(jsonString);
        
        Assert.assertEquals(list, fromserializer);
        
    }
}
