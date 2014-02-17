package org.ensor.robots.primitives;


/**
 * String class for player names; names are compared without regard to case.
 */
public class NameString implements Comparable<NameString>
{
    //=========================================================================
    public NameString( String aString )
    {
        if( aString == null )
            throw new NullPointerException();

        mName = aString;
        mLowerString = mName.toLowerCase();
    }
    
    //=========================================================================
    @Override
    public boolean equals( Object aOther )
    {
        if( !(aOther instanceof NameString) )
            return false;

        NameString other = (NameString)aOther;
        return mLowerString.equals(other.mLowerString);
    }

    //=========================================================================
    @Override
    public int hashCode() 
    {
        return mLowerString.hashCode();
    }
    
    //=========================================================================
    @Override
    public String toString() 
    {
        return mName;
    }
    
    //=========================================================================
    @Override
    public int compareTo( NameString aOther )
    {
        return mLowerString.compareTo(aOther.mLowerString);
    }

    // Data
    private final String mName;
    private final String mLowerString;
}
