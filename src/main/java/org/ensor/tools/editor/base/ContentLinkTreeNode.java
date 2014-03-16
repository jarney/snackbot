/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base;
import javax.swing.tree.*;



/**
 *
 * @author Jon
 */
public class ContentLinkTreeNode extends DefaultMutableTreeNode {
    private GameEditorBase                      mGameEditorBase;
    public ContentLinkTreeNode(GameEditorBase aGameEditorBase) {
        super();
        mGameEditorBase = aGameEditorBase;
    }
    @Override
    public void setUserObject(Object aObject) {
        if (aObject instanceof String) {
            ContentLink cl = (ContentLink)super.getUserObject();
            String changedName = (String)aObject;
            cl.setName(changedName);
        }
        else {
            super.setUserObject(aObject);
        }
    }
    @Override
    public String toString() {
        ContentLink cl = (ContentLink)super.getUserObject();
        return cl.toString();
    }
    public ContentLink getLink() {
        Object o = super.getUserObject();
        if (o instanceof ContentLink) {
            return (ContentLink)o;
        }
        return null;
    }
}
