/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.robots.editor;

import org.ensor.robots.editor.cookers.CookerTagDef;
import org.ensor.robots.editor.AppletCookedArchive;

/**
 *
 * @author Jon
 */
public class CookerEngine extends org.ensor.tools.editor.base.CookerEngine {
    public CookerEngine(String aContentRoot) {
        super();
        
        registerCooker(Globals.TAG_DEF, new CookerTagDef(Globals.TAG_DEF));
        
        // Register cooked archives.
        // These are the archives we will write the
        // various categories to.
        registerCookerArchive(new AppletCookedArchive(aContentRoot + "content\\",
                "C:\\msys\\1.0\\home\\Jon\\SpaceTaxiGameApplet\\src\\org\\ensor\\games\\spacetaxi\\content\\"));
    }
}
