/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base;

/**
 *
 * @author Jon
 */
public class ContentLink {
    private int mId;
    private String mContentType;
    private String mName;
    public ContentLink() {
        mId = 0;
        mContentType = "Uninitialized";
        mName = "Untitled";
    }
    public ContentLink(int aId, String aContentType, String aName) {
        mContentType = aContentType;
        mId = aId;
        mName = aName;
    }
    public int getId() {
        return mId;
    }
    public String getContentType() {
        return mContentType;
    }
    public String getName() {
        return mName;
    }
    public void setName(String aName) {
        mName = aName.replaceAll("\\[.*\\]", "");
    }
    @Override
    public String toString() {
        if (mContentType.equals("list")) return mName;
        return "[" + mId + "]" + mName;
    }
    public String serialize() {
        return "ensor://" + mContentType + "/" + mId + "/" + mName;
    }
    public boolean deserialize(String urlString) {
        try {
            int protoEndIndex = urlString.indexOf("://");
            String protocol = urlString.substring(0, protoEndIndex);
            if (!protocol.equals("ensor")) return false;
            
            String hostPath = urlString.substring(protoEndIndex+3);
            String hp[] = hostPath.split("/");
            if (hp.length != 3) return false;
            mContentType = hp[0];
            mId = Integer.parseInt(hp[1]);
            mName = hp[2];
            return true;
        }
        catch (Exception ex) {
            System.out.println("Failed to parse" + ex);
            ex.printStackTrace(System.out);
            return false;
        }
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ContentLink)) return false;
        ContentLink co = (ContentLink)o;
        
        if (!co.mContentType.equals(mContentType)) return false;
        if (co.mContentType.equals("list")) {
            return super.equals(o);
        }
        
        if (co.mId != mId) return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + this.mId;
        hash = 61 * hash + (this.mContentType != null ? this.mContentType.hashCode() : 0);
        return hash;
    }
}
