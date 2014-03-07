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

import org.ensor.data.atom.ISerializer;
import org.ensor.data.atom.IDictionaryVisitable;
import org.ensor.data.atom.IListVisitable;
import org.ensor.data.atom.ImmutableDict;
import org.ensor.data.atom.ImmutableList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class implements a serializer which converts data from JSON strings
 * into dictionaries and back again.
 * @author jona
 */
public final class JSONStringSerializer implements ISerializer<String, String> {

    private static final JSONStringSerializer INSTANCE =
            new JSONStringSerializer();
    private JSONStringSerializer() {
    }
    /**
     * This method returns the singleton instance of the serializer.
     * @return The singleton instance of the JSON string serializer.
     */
    public static JSONStringSerializer instance() {
        return INSTANCE;
    }
    /**
     * This method converts a dictionary into a JSON string
     * containing the same data.
     * @param aDict A dictionary.
     * @return A JSON formatted string containing the same data.
     * @throws Exception If a serialization problem occurs, this method
     *                   throws an exception.
     */
    public String serializeTo(final IDictionaryVisitable aDict)
            throws Exception {
        return JSONSerializer.instance().serializeTo(aDict).toString();
    }
    /**
     * This method converts a list into a JSON string
     * containing the same data.
     * @param aList A list.
     * @return A JSON formatted string containing the same data.
     * @throws Exception If a serialization problem occurs, this method
     *                   throws an exception.
     */
    public String serializeTo(final IListVisitable aList) throws Exception {
        return JSONSerializer.instance().serializeTo(aList).toString();
    }
    /**
     * This method converts a JSON string into a dictionary.
     * @param aFrom A JSON formatted string.
     * @return A dictionary containing the same data.
     * @throws Exception If a serialization problem occurs, this method
     *                   throws an exception.
     */
    public ImmutableDict serializeFrom(final String aFrom)
            throws Exception {
        JSONObject obj = new JSONObject(aFrom);
        return JSONSerializer.instance().serializeFrom(obj);
    }
    /**
     * This method converts a JSON string into a list.
     * @param aFrom A JSON formatted string.
     * @return A list containing the same data.
     * @throws Exception If a serialization problem occurs, this method
     *                   throws an exception.
     */
    public ImmutableList serializeFromList(final String aFrom)
            throws Exception {
        JSONArray obj = new JSONArray(aFrom);
        return JSONSerializer.instance().serializeFromList(obj);
    }

}
