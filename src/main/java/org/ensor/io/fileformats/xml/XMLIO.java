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
package org.ensor.io.fileformats.xml;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import java.io.*;

/**
 *
 * @author Jon
 */
public final class XMLIO {
    
    private static final XMLIO INSTANCE = new XMLIO();
    
    private DocumentBuilderFactory              mDocumentBuilderFactory;
    private DocumentBuilder                     mDocumentBuilder;
    private Transformer                         mTransformer;

    /**
     * This method returns an instance of the XMLIO
     * XML file handler.
     * @return 
     */
    public static XMLIO getInstance() {
        return INSTANCE;
    }
    
    private XMLIO() {
        try {
            mDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            mDocumentBuilder = mDocumentBuilderFactory.newDocumentBuilder();
            mTransformer = TransformerFactory.newInstance().newTransformer();
            mTransformer.setOutputProperty("indent", "yes");
            
        }
        catch (Exception ex) {
            System.out.println("Exception opening editor, no document builder");
            ex.printStackTrace(System.out);
        }
    }
    public Document newDocument() {
        return mDocumentBuilder.newDocument();
    }
    public Document read(String filename) throws
            java.io.FileNotFoundException,
            java.io.IOException,
            org.xml.sax.SAXException {
        InputStream is = new FileInputStream(filename);
        return read(is);
    }
    public Document read(InputStream is) throws
            java.io.IOException,
            org.xml.sax.SAXException {
        Document doc = mDocumentBuilder.parse(is);
        return doc;
    }
    public void write(Document doc, String filename)
        throws javax.xml.transform.TransformerException {

        // Prepare the output file
        File file = new File(filename);
        Result result = new StreamResult(file);
        DOMSource source = new DOMSource(doc);

        mTransformer.transform(source, result);
    }
}
