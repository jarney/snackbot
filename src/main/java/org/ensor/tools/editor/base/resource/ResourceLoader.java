/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base.resource;

import org.ensor.tools.editor.base.ContentLink;
import org.ensor.data.atom.ImmutableDict;
import org.ensor.data.atom.DictionaryAtom;
import org.ensor.data.atom.xml.XMLSerializer;
import org.ensor.io.fileformats.xml.XMLIO;
import org.w3c.dom.Document;
/**
 *
 * @author Jon
 */
public abstract class ResourceLoader {
    private String mContentRootDir;
    public ResourceLoader(String aContentRootDir) {
        mContentRootDir = aContentRootDir;
    }
    public static ResourceLoader  mInstance;
    public static void setInstance(ResourceLoader aInstance) {
        mInstance = aInstance;
    }
    public static ResourceLoader getInstance() {
        return mInstance;
    }
    public DictionaryAtom loadResource(int aId) {
        String filename = mContentRootDir + "content\\data\\" + aId + ".xml";


        try {
            // Now can use various encoding types (xml, json, cooked)
            XMLSerializer reader = new XMLSerializer();
            Document doc = XMLIO.getInstance().read(filename);
            ImmutableDict contentDict = reader.serializeFrom(doc);
            return contentDict.getMutable();
        }
        catch (Exception ex) {
            System.out.println("Exception loading resource: " + aId);
            ex.printStackTrace(System.out);
            return DictionaryAtom.newAtom();
        }
    }
    public abstract ContentLink getResourceLink(int aId);
    
    public static Integer getId(ContentLink aId) {
        if (aId == null) return null;
        return aId.getId();
    }
}
