/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base;

import org.ensor.data.atom.ListAtom;

/**
 *
 * @author Jon
 */
public interface ICookerArchiveInterface {
    public String getCookedResourceName(String filename);
    public void writeCookedData(String aCookedCategoryName, ListAtom aData) throws Exception;
}
