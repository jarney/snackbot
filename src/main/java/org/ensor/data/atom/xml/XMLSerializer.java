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
package org.ensor.data.atom.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.ensor.data.atom.RealAtom;
import org.ensor.data.atom.BoolAtom;
import org.ensor.data.atom.IntAtom;
import org.ensor.data.atom.StringAtom;
import org.ensor.data.atom.IListVisitor;
import org.ensor.data.atom.IDictionaryVisitor;
import org.ensor.data.atom.Atom;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.ensor.data.atom.IDictionaryVisitable;
import org.ensor.data.atom.IListVisitable;
import org.w3c.dom.*;
import org.ensor.data.atom.ISerializer;
import org.ensor.data.atom.ImmutableDict;
import org.ensor.data.atom.ImmutableList;
import org.ensor.data.atom.Pair;
/**
 *
 * @author Jon
 */
public class XMLSerializer implements ISerializer<Document, Document> {
    
    private final DocumentBuilder mDocumentBuilder;
    
    public XMLSerializer() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        mDocumentBuilder = factory.newDocumentBuilder();
    }
    
    public XMLSerializer(DocumentBuilder aDocumentBuilder) {
        mDocumentBuilder = aDocumentBuilder;
    }
    
    public ImmutableDict serializeFrom(Document aFrom) throws Exception {
        Element root = aFrom.getDocumentElement();
        XMLDictionaryReader xdw = new XMLDictionaryReader(root);
        ImmutableDict dict = xdw.read();
        return dict;
    }

    public ImmutableList serializeFromList(Document aFrom) throws Exception {
        Element root = aFrom.getDocumentElement();
        XMLListReader xdw = new XMLListReader(root);
        ImmutableList dict = xdw.read();
        return dict;
    }

    public Document serializeTo(final IDictionaryVisitable aDict) throws Exception {
        Document document = mDocumentBuilder.newDocument();
        final Element root = document.createElement("root");
        document.appendChild(root);
        aDict.visitPairs(new XMLDictionaryWalker(root));
        return document;
    }

    public Document serializeTo(final IListVisitable aList) throws Exception {
        Document document = mDocumentBuilder.newDocument();
        final Element root = document.createElement("root");
        document.appendChild(root);
        aList.visitAtoms(new XMLListWalker(root));
        return document;
    }

}

class XMLDictionaryReader {
    private final Element mElement;
    public XMLDictionaryReader(final Element aElement) {
        mElement = aElement;
    }
    public ImmutableDict read() {
        List<Map.Entry<String, Atom>> entryList =
                new ArrayList<Map.Entry<String, Atom>>();
        NodeList children = mElement.getChildNodes();
        int i;
        for (i = 0; i < children.getLength(); i++) {
            Node childNode = children.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element child = (Element) childNode;
            String key = child.getAttribute("key");
            int type = Integer.parseInt(child.getAttribute("type"));
            
            Atom value = XMLAtomReader.read(child, type);
            entryList.add(new Pair<String, Atom>(key, value));
        }
        
        return ImmutableDict.newAtom(entryList);
    }
}

class XMLListReader {
    Element mElement;
    public XMLListReader(Element aElement) {
        mElement = aElement;
    }
    public ImmutableList read() {
        NodeList children = mElement.getChildNodes();
        List<Atom> list = new ArrayList<Atom>();

        int i;
        for (i = 0; i < children.getLength(); i++) {
            Node childNode = children.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
            Element child = (Element)childNode;
            int type = Integer.parseInt(child.getAttribute("type"));
            Atom value = XMLAtomReader.read(child, type);
            list.add(value);
        }
        return ImmutableList.newAtom(list);
    }
}

class XMLAtomReader {
    public static Atom read(Element el, int type)
    {
        switch (type) {
            case Atom.ATOM_TYPE_DICTIONARY:
                XMLDictionaryReader dar = new XMLDictionaryReader(el);
                ImmutableDict dictValue = dar.read();
                return dictValue;
            case Atom.ATOM_TYPE_LIST:
                XMLListReader lr = new XMLListReader(el);
                ImmutableList listValue = lr.read();
                return listValue;
            case Atom.ATOM_TYPE_BOOLEAN:
                String v = innerText(el, "value");
                boolean b = Boolean.parseBoolean(v);
                BoolAtom ba = BoolAtom.newAtom(b);
                return ba;
            case Atom.ATOM_TYPE_INT:
                String vInt = innerText(el, "value");
                int intValue = Integer.parseInt(vInt);
                IntAtom ia = IntAtom.newAtom(intValue);
                return ia;
            case Atom.ATOM_TYPE_REAL:
                String vReal = innerText(el, "value");
                double realValue = Double.parseDouble(vReal);
                return RealAtom.newAtom(realValue);
            case Atom.ATOM_TYPE_STRING:
                String vString = innerText(el, "value");
                return StringAtom.newAtom(vString);
        }
        return null;
    }
    private static String innerText(Element el, String tag) {
        NodeList valueNodes = el.getElementsByTagName("value");
        if (valueNodes.getLength() != 1) return "";
        Element valueEl = (Element)valueNodes.item(0);
        return innerText(valueEl);
    }
    private static String innerText(Element valueEl) {
        NodeList valueTextNodes = valueEl.getChildNodes();
        int i;
        String text = "";
        for (i = 0; i < valueTextNodes.getLength(); i++) {
            Node n =  valueTextNodes.item(i);
            if (n.getNodeType() != Node.TEXT_NODE) continue;
            Text tn = (Text)n;
            text = text + tn.getTextContent();
        }
        return text;
    }
}


class XMLDictionaryWalker implements IDictionaryVisitor {
    Element     mRoot;
    XMLDictionaryWalker(Element root) {
        mRoot = root;
    }
    @Override
    public void visit(String aKey, Atom aValue) throws Exception {
        Element child = mRoot.getOwnerDocument().createElement("node");
        child.setAttribute("key", aKey.toString());
        child.setAttribute("type", "" + aValue.getType());
        XMLAtomWriter.write(child, aValue);
        mRoot.appendChild(child);
    }
}

class XMLListWalker implements IListVisitor {
    Element     mRoot;
    XMLListWalker(Element root) {
        mRoot = root;
    }
    @Override
    public void visit(Atom aValue) throws Exception {
        Element child = mRoot.getOwnerDocument().createElement("node");
        child.setAttribute("type", "" + aValue.getType());
        XMLAtomWriter.write(child, aValue);
        mRoot.appendChild(child);
    }
}

class XMLAtomWriter {
    public static void write(Element aEl, Atom aValue) throws Exception {
        if (aValue.getType() == Atom.ATOM_TYPE_DICTIONARY) {
            XMLDictionaryWalker w = new XMLDictionaryWalker(aEl);
            IDictionaryVisitable dict = (IDictionaryVisitable)aValue;
            dict.visitPairs(w);
        }
        else if (aValue.getType() == Atom.ATOM_TYPE_LIST) {
            XMLListWalker w = new XMLListWalker(aEl);
            IListVisitable list = (IListVisitable)aValue;
            list.visitAtoms(w);
        }
        else {
            Element childEl = aEl.getOwnerDocument().createElement("value");
            Text text = aEl.getOwnerDocument().createTextNode(aValue.toString());
            childEl.appendChild(text);
            aEl.appendChild(childEl);
        }
    }
}