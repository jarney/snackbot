/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.robots.editor.cookers;

import org.ensor.tools.editor.base.CookerBase;
import org.ensor.data.atom.DictionaryAtom;
/**
 *
 * @author Jon
 */
public class CookerTagDef extends CookerBase {
    public CookerTagDef(String aCategory) {
        super(aCategory);
    }
    @Override
    public DictionaryAtom cookElementImpl(DictionaryAtom aContent) {
        return aContent;
    }
}
