/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.robots.editor.objects;

import org.ensor.data.atom.DictionaryAtom;
import org.ensor.tools.editor.base.ContentLink;
import org.ensor.tools.editor.base.IContent;

/**
 *
 * @author Jon
 */
public class TagDef implements IContent {
    
    public void read(DictionaryAtom aDict) {
    }
    
    public DictionaryAtom write() {
        DictionaryAtom dict = DictionaryAtom.newAtom();
        return dict;
    }

    public boolean isReferenced(ContentLink aLink) {
        return false;
    }

    public void read(ContentLink aLink) throws Exception {
        
    }

}
