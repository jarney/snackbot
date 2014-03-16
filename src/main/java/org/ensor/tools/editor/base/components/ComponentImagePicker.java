/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base.components;


/**
 *
 * @author Jon
 */
public class ComponentImagePicker extends ComponentFilePickerBase {
    public ComponentImagePicker() {
        super();
        mFileFilter = new FileFilterImage();
    }
}
