package org.ensor.robots.editor;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import org.ensor.robots.editor.editors.EditorTagDef;
import org.ensor.robots.editor.objects.TagDef;
import java.awt.Color;
import org.ensor.tools.editor.base.GameEditorBase;
import org.ensor.tools.editor.base.IEditorFactory;
import org.ensor.tools.editor.base.IEditor;
import org.ensor.tools.editor.base.IContent;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import java.awt.Graphics;
import org.ensor.data.atom.*;
import org.ensor.data.atom.xml.*;
import java.awt.image.BufferedImage;
/**
 *
 * @author Jon
 */
public class SpaceTaxiEditor extends GameEditorBase {
    
    @Override
    public void registerHandlers() {
        final GameEditorBase geb = this;
        registerEditor("TagDef", new IEditorFactory() {
            @Override
            public IEditor newEditor() {
                return new EditorTagDef(geb);
            }
            @Override
            public IContent newContent() {
                return new TagDef();
            }
            
        });

        CookerEngine ce = new CookerEngine("");
        this.setCooker(ce);
    }
    @Override
    public Icon contentTypeIcon(String aContentType) {
        try {
            java.net.URL resource = this.getClass().getResource("icons/" + aContentType + "_16.png");
            return new ImageIcon(resource);
        }
        catch (Exception ex16) {
            try {
                java.net.URL resource = this.getClass().getResource("icons/" + aContentType + "_32.png");
                return new ImageIcon(resource);
            }
            catch (Exception ex32) {
                try {
                    java.net.URL resource = this.getClass().getResource("icons/" + aContentType + ".png");
                    return new ImageIcon(resource);
                }
                catch (Exception exnormal) {
                    return super.contentTypeIcon(aContentType);
                }
            }
        }
    }
    
    private java.awt.Image constructMissingAsset(int aId) {
        BufferedImage missingAssetImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics gFrame = missingAssetImage.getGraphics();
        gFrame.setColor(Color.red);
        gFrame.fillRect(0, 0, 64, 64);
        gFrame.fillRect(64, 64, 64, 64);
        gFrame.setColor(Color.green);
        gFrame.fillRect(64, 0, 64, 64);
        gFrame.fillRect(0, 64, 64, 64);
        gFrame.setColor(Color.black);
        gFrame.drawString("id " + aId, 0, 0);
        return missingAssetImage;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
//                try {
//                    javax.swing.UIManager.setLookAndFeel("net.infonode.gui.laf.InfoNodeLookAndFeel");
//                }
//                catch (Exception ex) {
//                    System.out.println(ex.toString());
//                    ex.printStackTrace(System.out);
//                    System.exit(1);
//                }
                SpaceTaxiEditor editor = new SpaceTaxiEditor();
                editor.setVisible(true);
                GameEditorBase.setInstance(editor);
            }
        });
    }
}
