/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base;

import org.ensor.data.atom.DictionaryAtom;
import org.ensor.data.atom.ListAtom;
/**
 *
 * @author Jon
 */
public abstract class CookerBase implements ICooker {
    private String                  mCategory;
    private ListAtom                mData;
    private ICookerArchiveInterface mCookerArchiveInterface;
    public CookerBase(String aCategory) {
        mCategory = aCategory;
        mData = ListAtom.newAtom();
    }
    
    @Override
    public void beginCook(ICookerArchiveInterface aCookerArchiveInterface) throws Exception {
        mCookerArchiveInterface = aCookerArchiveInterface;
        mData = ListAtom.newAtom();
    }

    @Override
    public void cookElement(int aId, DictionaryAtom aDict) throws Exception {
        DictionaryAtom cookedData = cookElementImpl(aDict);
        DictionaryAtom cookedDataContainer = DictionaryAtom.newAtom();
        cookedDataContainer.newDictionary("data", cookedData);
        cookedDataContainer.setInt("id", aId);
        mData.append(cookedDataContainer);
    }
    
    public final String getCookedResourceName(String filename) {
        return mCookerArchiveInterface.getCookedResourceName(filename);
    }
    public abstract DictionaryAtom cookElementImpl(DictionaryAtom aDict) throws Exception;
    
    @Override
    public void endCook() throws Exception {
        mCookerArchiveInterface.writeCookedData(mCategory, mData);
    }
}
