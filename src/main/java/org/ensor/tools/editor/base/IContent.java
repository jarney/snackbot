/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base;

/**
 *
 * @author Jon
 */
public interface IContent {
    public boolean isReferenced(ContentLink aLink);
    public void read(ContentLink aLink) throws Exception;
}
