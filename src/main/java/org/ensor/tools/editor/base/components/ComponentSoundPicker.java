/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base.components;


/**
 *
 * @author Jon
 */
public class ComponentSoundPicker extends ComponentFilePickerBase {
    public ComponentSoundPicker() {
        super();
        mFileFilter = new FileFilterSound();
    }
}
