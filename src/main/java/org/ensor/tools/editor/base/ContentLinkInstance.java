/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base;

/**
 *
 * @author Jon
 */
public class ContentLinkInstance {
    private ContentLink         mContentLink;
    private Object              mData;
    public ContentLinkInstance(ContentLink aLink) {
        mContentLink = aLink;
        mData = null;
    }
    public ContentLinkInstance(ContentLink aLink, Object aData) {
        mContentLink = aLink;
        mData = aData;
    }
    public ContentLink getLink() { return mContentLink; }
    public void setLink(ContentLink aLink) { mContentLink = aLink; }
    public void setData(Object aData) { mData = aData; }
    public Object getData() { return mData; }
}
