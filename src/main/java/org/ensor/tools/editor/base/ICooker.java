/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base;

import org.ensor.data.atom.DictionaryAtom;
/**
 *
 * @author Jon
 */
public interface ICooker {
    public void beginCook(ICookerArchiveInterface aArchive) throws Exception;
    public void cookElement(int aId, DictionaryAtom contentFilename) throws Exception;
    public void endCook() throws Exception;
}
