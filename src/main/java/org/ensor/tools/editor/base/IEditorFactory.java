/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base;

import javax.swing.Icon;

/**
 *
 * @author Jon
 */
public interface IEditorFactory {
    public IEditor newEditor();
    public IContent newContent();
}
