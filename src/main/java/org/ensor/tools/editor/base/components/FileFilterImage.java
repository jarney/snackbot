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
public class FileFilterImage extends FileFilter {
    public static final String mPattern = ".*\\.(jpg|png|jpeg)$";
    public static final String mDescription = "Image Files";
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        return f.getName().matches(mPattern);
    }
    @Override
    public String getDescription() {
        return mDescription;
    }
}
