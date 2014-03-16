/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base;
import org.ensor.tools.editor.base.resource.ResourceLoader;
import org.ensor.data.atom.*;
import org.ensor.data.atom.xml.XMLSerializer;

/**
 *
 * @author Jon
 */
public class GameEditorResourceLoader extends ResourceLoader {
    private GameEditorBase mGameEditor;
    public GameEditorResourceLoader(GameEditorBase geb) {
        super("");
        mGameEditor = geb;

    }
    @Override
    public ContentLink getResourceLink(int aId) {
        return mGameEditor.getContentById(aId);
    }
}
