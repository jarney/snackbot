/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ensor.robots.primitives;

/**
 * An integer atom stores an integer.
 * 
 * @author Jon
 */
public class IntAtom extends Atom {
    private int mValue;
    public IntAtom(long value) {
        super(ATOM_TYPE_INT);
        mValue = (int)value;
    }
    public IntAtom(int value) {
        super(ATOM_TYPE_INT);
        mValue = value;
    }
    public int getValue() {
        return mValue;
    }
    public void setValue(long value) {
        mValue = (int)value;
    }
    public void setValue(int value) {
        mValue = value;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IntAtom) {
            IntAtom strObj = (IntAtom)obj;
            return mValue == strObj.mValue;
        }
        return false;
    }
    @Override
    public int hashCode() {
        return mValue;
    }
    public void dump(java.util.logging.Logger log, String prefix) {
        log.info(prefix + ":" + mValue + " (IntAtom)");
    }
}
