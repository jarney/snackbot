/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base;

import javax.swing.JPanel;

import org.ensor.data.atom.DictionaryAtom;

/**
 *
 * @author Jon
 */
public interface IEditor {
    public boolean validateContent();
    public void read(DictionaryAtom aDocument) throws Exception;
    public DictionaryAtom write();
    public JPanel getPanel();
}
