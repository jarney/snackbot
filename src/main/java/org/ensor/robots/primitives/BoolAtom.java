/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ensor.robots.primitives;
/**
 * This is the primitive boolean data type.
 * @author Jon
 */
public class BoolAtom extends Atom {
    private boolean mFlag;
    BoolAtom(boolean flag) {
        super(ATOM_TYPE_BOOLEAN);
        mFlag = flag;
    }
    public void setValue(boolean flag) { mFlag = flag;}
    public boolean getValue() {return mFlag;}
    public void dump(java.util.logging.Logger log, String prefix) {
        if (mFlag) {
            log.info(prefix + ":" + "true (BoolAtom)");
        }
        else {
            log.info(prefix + ":" + "false (BoolAtom)");
        }
    }
}
