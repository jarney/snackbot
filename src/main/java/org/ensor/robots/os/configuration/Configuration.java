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

package org.ensor.robots.os.configuration;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.ensor.data.atom.DictionaryAtom;
import org.ensor.data.atom.ImmutableDict;
import org.ensor.data.atom.xml.XMLSerializer;
import org.ensor.robots.network.server.BioteSocket;
import org.w3c.dom.Document;

/**
 *
 * @author jona
 */
public class Configuration {
    
    private DictionaryAtom mConfiguration;
    private final XMLSerializer mSerializer;
    private static final String CONFIG_FILE_LOCATION = "configuration.xml";
    
    public Configuration() throws Exception {
        mSerializer = new XMLSerializer();
    }

    public synchronized void load() {

        try {
            File fXmlFile = new File(CONFIG_FILE_LOCATION);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document configurationXML = dBuilder.parse(fXmlFile);

            ImmutableDict configFromFile =
                    mSerializer.serializeFrom(configurationXML);
            DictionaryAtom configurationDictionary = configFromFile.getMutable();
            
            mConfiguration = configurationDictionary;
        }
        catch (Exception ex) {
            Logger.getLogger(BioteSocket.class.getName()).log(
                    Level.WARNING,
                    "Exception starting configuration", ex);
            
            mConfiguration = DictionaryAtom.newAtom();
        }
    }

    public synchronized void save() throws Exception {
        Document doc = mSerializer.serializeTo(mConfiguration);

        // write the content into xml file
        TransformerFactory transformerFactory =
                TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(CONFIG_FILE_LOCATION));
        
        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);

        transformer.transform(source, result);
    }

    public synchronized void setConfigurationNode(
            final String aNodeName,
            final DictionaryAtom aDict
    ) {
        mConfiguration.setDictionary(aNodeName, aDict.getImmutable());
    }
    
    public synchronized DictionaryAtom getConfigurationNode(
            final String aNodeName) {
        DictionaryAtom node = mConfiguration.getDictionary(aNodeName);
        if (node == null) {
            return null;
        }
        
        return node.getImmutable().getMutable();
    }
    
}
