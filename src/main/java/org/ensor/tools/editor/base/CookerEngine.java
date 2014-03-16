/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base;

import org.ensor.data.atom.DictionaryAtom;
import org.ensor.tools.editor.base.resource.ResourceLoader;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Jon
 */
public class CookerEngine {
    private HashMap<String, ICooker>            mCookers;
    private List<ICookerArchiveInterface>       mCookerArchives;
    
    public CookerEngine() {
        mCookers = new HashMap<String, ICooker>();
        mCookerArchives = new ArrayList<ICookerArchiveInterface>();
    }
    public final void registerCooker(String aContentType, ICooker aCooker) {
        mCookers.put(aContentType, aCooker);
    }
    public final void registerCookerArchive(ICookerArchiveInterface aArchiveInterface) {
        mCookerArchives.add(aArchiveInterface);
    }
    public void cookAll(HashMap<Integer, ContentLink> aData) throws Exception {
        
        HashMap<String, List<Integer>>      contentByType;
        contentByType = new HashMap<String, List<Integer>>();

        // Sort content into categories for cooking:
        // Mise-en-place.
        for (ContentLink c : aData.values()) {
            List<Integer> l = null;
            if (contentByType.containsKey(c.getContentType())) {
                l = contentByType.get(c.getContentType());
            }
            else {
                l = new ArrayList<Integer>();
                contentByType.put(c.getContentType(), l);
            }
            l.add(c.getId());
        }
        for (ICookerArchiveInterface archiveInterface : mCookerArchives) {
            cookAllToArchive(contentByType, aData, archiveInterface);
        }
    }
    
    private void cookAllToArchive(
            HashMap<String, List<Integer>> contentByType,
            HashMap<Integer, ContentLink> contentById,
            ICookerArchiveInterface archiveInterface
            ) throws Exception {
        for (String contentType : mCookers.keySet()) {
            ICooker cooker = mCookers.get(contentType);
            cooker.beginCook(archiveInterface);
            if (contentByType.containsKey(contentType)) {
                List<Integer> listContent = contentByType.get(contentType);
                for (Integer contentId : listContent) {
                    ContentLink content = contentById.get(contentId);
                    DictionaryAtom contentDict = ResourceLoader.getInstance().
                            loadResource(content.getId());
                    cooker.cookElement(contentId, contentDict);
                }
            }
            cooker.endCook();
        }
    }
}
