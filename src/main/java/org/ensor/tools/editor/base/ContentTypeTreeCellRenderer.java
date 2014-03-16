/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Jon
 */
public class ContentTypeTreeCellRenderer extends DefaultTreeCellRenderer {
    GameEditorBase mGameEditor;
    
    public ContentTypeTreeCellRenderer(GameEditorBase geb) {
        mGameEditor = geb;
    }

    @Override
    public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

        Component c = super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);

        ContentLinkTreeNode node =
                (ContentLinkTreeNode)value;
        ContentLink nodeInfo =
                (ContentLink)(node.getUserObject());
        setIcon(mGameEditor.contentTypeIcon(nodeInfo.getContentType()));
        setText(nodeInfo.toString());
        setToolTipText(nodeInfo.toString());
        return this;
        
    }
}
