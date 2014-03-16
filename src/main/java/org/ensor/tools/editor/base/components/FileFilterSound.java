/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base.components;

import javax.swing.filechooser.FileFilter;
import java.io.File;
/**
 *
 * @author Jon
 */
public class FileFilterSound extends FileFilter {
    public static final String mSoundPattern = ".*\\.(mp3|ogg|wav)$";
    public static final String mDescription = "Audio Files";
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        return f.getName().matches(mSoundPattern);
    }
    @Override
    public String getDescription() {
        return mDescription;
    }
}
