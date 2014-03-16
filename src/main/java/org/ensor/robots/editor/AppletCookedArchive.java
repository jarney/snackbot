/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.robots.editor;

import org.ensor.tools.editor.base.ICookerArchiveInterface;
import org.ensor.data.atom.*;
import org.ensor.data.atom.json.JSONSerializer;

import org.json.JSONArray;

import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author Jon
 */
public class AppletCookedArchive implements ICookerArchiveInterface {
    private String mTargetArchiveDir;
    private String mSourceContentDir;
    
    public AppletCookedArchive(String aSourceContentDir, String aTargetArchiveDir) {
        mTargetArchiveDir = aTargetArchiveDir;
        mSourceContentDir = aSourceContentDir;
    }
    @Override
    public String getCookedResourceName(String filename) {
        return filename.replaceAll("\\\\", "/");
    }
    @Override
    public void writeCookedData(String aCookedCategoryName, ListAtom aData) throws Exception {
        JSONSerializer w = JSONSerializer.instance();
        JSONArray o = w.serializeTo(aData);
        String cookedDataString = o.toString();
        File cookedDataDir = new File(mTargetArchiveDir + "cookedData");
        cookedDataDir.mkdirs();
        File cookedDataFile = new File(mTargetArchiveDir + "cookedData\\" + aCookedCategoryName + ".json");
        FileWriter fw = new FileWriter(cookedDataFile);
        fw.write(cookedDataString);
        fw.close();
    }
}
