/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base;

/**
 *
 * @author Jon
 */
public class EditorInstance {
    private ContentLinkTreeNode         mTNF;
    private IEditor                     mEditor;
    private ContentLink                 mContentLink;
    private boolean                     mIsValid;
    
    public EditorInstance(IEditor aEditor, ContentLink aLink) {
        mEditor = aEditor;
        mContentLink = aLink;
        mTNF = null;
        mIsValid = false;
    }
    public void validateContent() {
        mIsValid = mEditor.validateContent();
    }
    public boolean isValid() {
        return mIsValid;
    }
    public void setTNF(ContentLinkTreeNode tnf) {
        mTNF = tnf;
    }
    public ContentLinkTreeNode getTNF() {
        return mTNF;
    }
    public IEditor getEditor() { return mEditor; }
    public ContentLink getLink() {
        return mContentLink;
    }
}
