/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base.components;

import org.ensor.tools.editor.base.ContentLink;
/**
 *
 * @author Jon
 */
public abstract class ComponentContentLinkOrderedListBaseEventHandler {
    public abstract Object onLinkAdded(ContentLink aLink);
    public abstract void onLinkRemoved(Object aLink);
    public abstract void onLinkSelected(Object aLink);
    public abstract void onListReordered();

}
