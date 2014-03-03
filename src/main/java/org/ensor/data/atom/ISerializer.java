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

/**
 * This interface provides a uniform way in which dictionaries and lists can
 * be serialized and de-serialized.
 * @param <DictType> The data type for a serialized dictionary.
 * @param <ListType> The data type for a serialized list.
 * @author jona
 */
public interface ISerializer<DictType, ListType> {
    /**
     * This method serializes a dictionary and converts it into the data type
     * corresponding to the DictType.
     * @param aDict A dictionary to serialize.
     * @return A serialized version of the given dictionary.
     * @throws Exception If there is a serialization exception, it is thrown.
     */
    DictType serializeTo(IDictionaryVisitable aDict) throws Exception;
    /**
     * This method serializes a dictionary and converts it into the data type
     * corresponding to the ListType.
     * @param aList A list to serialize.
     * @return A serialized version of the given list.
     * @throws Exception If there is a serialization exception, it is thrown.
     */
    ListType serializeTo(IListVisitable aList) throws Exception;
    /**
     * This method serializes a dictionary from the DictType format into
     * an ImmutableDict object.
     * @param aFrom The serialized version of a dictionary.
     * @return An ImmutableDict object containing the serialized data.
     * @throws Exception If there is a serialization exception, it is thrown.
     */
    ImmutableDict serializeFrom(DictType aFrom) throws Exception;
    /**
     * This method serializes a list from the ListType format into
     * an ImmutableList object.
     * @param aFrom The serialized version of a list.
     * @return An ImmutableList object containing the serialized data.
     * @throws Exception If there is a serialization exception, it is thrown.
     */
    ImmutableList serializeFromList(ListType aFrom) throws Exception;
}
