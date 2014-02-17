/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ensor.robots.primitives;

/**
 *
 * @author Jon
 */
public class RealAtom extends Atom {
    private double mValue;
    public RealAtom(double value) {
        super(ATOM_TYPE_REAL);
        mValue = value;
    }
    public RealAtom(float value) {
        super(ATOM_TYPE_REAL);
        mValue = (double)value;
    }
    public double getValue() {
        return mValue;
    }
    public void setValue(double value) {
        mValue = value;
    }
    public void setValue(float value) {
        mValue = (double)value;
    }
    public void dump(java.util.logging.Logger log, String prefix) {
        log.info(prefix + ":" + mValue + " (RealAtom)");
    }
}
