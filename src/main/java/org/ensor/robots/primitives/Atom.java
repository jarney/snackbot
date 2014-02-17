package org.ensor.robots.primitives;

/**
 * An atom is the primitive abstract data container for events.  It consists of a type
 * and derived classes hold the actual data associated with the atom.
 * @author Jon
 */
public abstract class Atom {
    public static final int ATOM_TYPE_STRING = 1;
    public static final int ATOM_TYPE_INT = 2;
    public static final int ATOM_TYPE_BOOLEAN = 3;
    public static final int ATOM_TYPE_REAL = 4;
    public static final int ATOM_TYPE_LIST = 5;
    public static final int ATOM_TYPE_DICTIONARY = 6;
    public static final int ATOM_TYPE_NIL = 7;
    public Atom() {
        mType = ATOM_TYPE_NIL;
    }
    public Atom(int atomType) {
        mType = atomType;
    }
    public int getType() {return mType;}
    private int mType;
    public abstract void dump(java.util.logging.Logger log, String prefix);

};