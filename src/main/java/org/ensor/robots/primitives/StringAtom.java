/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ensor.robots.primitives;

/**
 *
 * @author Jon
 */
public class StringAtom extends Atom {
    private String mValue;
    public StringAtom(String value) {
        super(ATOM_TYPE_STRING);
        mValue = value;
    }
    public String getValue() {
        return mValue;
    }
    public void setValue(String value) {
        mValue = value;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StringAtom) {
            StringAtom strObj = (StringAtom)obj;
            return mValue.equals(strObj.mValue);
        }
        return false;
    }
    @Override
    public int hashCode() {
        return mValue.hashCode();
    }
    public void dump(java.util.logging.Logger log, String prefix) {
        log.info(prefix + ":" + mValue + " (StringAtom)");
    }
}
