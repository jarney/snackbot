/*i
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * WorldEditorFrame.java
 *
 * Created on Apr 22, 2011, 1:21:36 PM
 */
package org.ensor.tools.editor.base;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.*;
import java.awt.event.MouseEvent;
import javax.swing.tree.*;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.*;
import org.ensor.data.atom.*;
import org.ensor.data.atom.xml.XMLSerializer;
import org.ensor.tools.editor.base.resource.ResourceLoader;

import org.w3c.dom.*;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import org.ensor.io.fileformats.xml.XMLIO;

/**
 *
 * @author Jon
 */
public abstract class GameEditorBase extends javax.swing.JFrame {

    private HashMap<String, IEditorFactory>     mEditorTypes;
    
    private HashMap<Component, Integer>         mScrollpaneToId;
    private HashMap<Integer, Component>         mIdToScrollpane;
    private HashMap<Integer, EditorInstance>    mOpenEditors;
    private JTree                               treeEditors;
    private static GameEditorBase               mInstance;
    
    private HashMap<Integer, ContentLink>       mContentById;
    private int                                 mContentId;
    private CookerEngine                        mCookerEngine;
    private DefaultListModel                    mReferenceListModel;
    private ArrayList<ContentLink>              mReferenceListShadow;
    public static GameEditorBase getInstance() { return mInstance;}
    public static void setInstance(GameEditorBase base) { mInstance = base; }
    
    /** Creates new form WorldEditorFrame */
    public GameEditorBase() {
        initComponents();
        
        mEditorTypes = new HashMap<String, IEditorFactory>();
        mOpenEditors = new HashMap<Integer, EditorInstance>();

        mScrollpaneToId = new HashMap<Component, Integer>();
        mIdToScrollpane = new HashMap<Integer, Component>();
        
        mContentById = new HashMap<Integer, ContentLink>();
        mContentId = 0;

        ResourceLoader.setInstance(new GameEditorResourceLoader(this));
        
        registerHandlers();
        try {
            readContentIndex();
        }
        catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace(System.out);
        }
        
        treeEditors.setCellRenderer(new ContentTypeTreeCellRenderer(this));
        treeEditors.getModel().addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                ContentLinkTreeNode node;
                node = (ContentLinkTreeNode)
                         (e.getTreePath().getLastPathComponent());
                int k;
                for (k = 0; k < node.getChildCount(); k++) {
                    ContentLinkTreeNode child = (ContentLinkTreeNode)node.getChildAt(k);
                    ContentLink contentLink = (ContentLink)child.getUserObject();
                    int i;
                    for (i = 0; i < mEditorTabControl.getTabCount(); i++) {
                        Integer contentId = mScrollpaneToId.get(mEditorTabControl.getComponentAt(i));
                        if (contentId == null) continue;
                        if (contentLink.getId() == contentId.intValue()) {
                            System.out.println("Title changed");
                            mEditorTabControl.setTitleAt(i, contentLink.toString());
                        }
                    }
                }
            }
            @Override
            public void treeNodesInserted(TreeModelEvent e) {
            }
            @Override
            public void treeNodesRemoved(TreeModelEvent e) {
            }
            @Override
            public void treeStructureChanged(TreeModelEvent e) {
            }
        });

        treeEditors.setTransferHandler(new TransferHandler() {

            @Override
            public boolean canImport(TransferHandler.TransferSupport info) {
                Transferable t = info.getTransferable();
                String idString = null;
                try {
                    idString = (String)t.getTransferData(DataFlavor.stringFlavor);
                }
                catch (Exception ex) {
                    return false;
                }
                
                return true;
            }

            // Drop into correct location.
            @Override
            public boolean importData(TransferHandler.TransferSupport info) {
                int dropRow = -1;
                Transferable t = info.getTransferable();
                
                ContentLink contentLink = new ContentLink();
                try {
                    String s = (String)info.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    if (!contentLink.deserialize(s)) {
                        return false;
                    }
                }
                catch (Exception ex) {
                    return false;
                }
                
                ContentLinkTreeNode dropNode = null;
                
                for (dropRow = 0; dropRow < treeEditors.getRowCount(); dropRow++) {
                    dropNode = (ContentLinkTreeNode)treeEditors.getPathForRow(dropRow).getLastPathComponent();
                    ContentLink dropContentLink = (ContentLink)dropNode.getUserObject();
                    if (dropContentLink.equals(contentLink)) {
                        break;
                    }
                    dropNode = null;
                }
                if (dropNode == null) return false;
                
                ContentLinkTreeNode destNode = (ContentLinkTreeNode)treeEditors.getDropLocation().getPath().getLastPathComponent();
                int ind = treeEditors.getDropLocation().getChildIndex();

                ContentLink dropContentLink = (ContentLink)dropNode.getUserObject();
                ContentLink destContentLink = (ContentLink)destNode.getUserObject();
                if (!destContentLink.getContentType().equals("list")) {
                    ContentLinkTreeNode parent = (ContentLinkTreeNode)destNode.getParent();
                    ind = parent.getIndex(destNode);
                    destNode = parent;
                }
                
                if (ind == -1 || ind >= destNode.getChildCount()) {
                    destNode.add(dropNode);
                }
                else {
                    destNode.insert(dropNode, ind);
                }
               
                treeEditors.updateUI();
		return true;
            }
            @Override
            public int getSourceActions(JComponent c) {
                return COPY_OR_MOVE;
            }
            @Override
            protected Transferable createTransferable(JComponent c) {
                JTree jt = (JTree)c;
                int id = jt.getSelectionRows()[0];
                
                ContentLinkTreeNode dropNode = (ContentLinkTreeNode)treeEditors.getPathForRow(id).getLastPathComponent();
                ContentLink contentLink = (ContentLink)dropNode.getUserObject();
                StringSelection is = new StringSelection(contentLink.serialize());
                
                return is;
            }
        });
        treeEditors.setDropMode(DropMode.ON_OR_INSERT);
        treeEditors.setRootVisible(false);
        
        
        treeEditors.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                onTreeClicked(evt);
            }
        });
        mReferenceListShadow = new ArrayList<ContentLink>();
        mReferenceList.removeAll();
        mReferenceListModel = new DefaultListModel();
        mReferenceList.setModel(mReferenceListModel);

        mReferenceList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Only handle double-click.
                if (evt.getClickCount() != 2) return;
                Object selected = mReferenceList.getSelectedValue();
                if (selected == null) return;
                int ind = mReferenceList.getSelectedIndex();
                ContentLink selectedLink = mReferenceListShadow.get(ind);
                openContent(selectedLink);
            }
        });
        
    }
    public List<ContentLink> toLinks(List<Integer> tags) {
        List<ContentLink> r = new ArrayList<ContentLink>();
        for (Integer i : tags) {
            r.add(getContentById(i));
        }
        return r;
    }
    public List<Integer> fromLinks(List<Object> tags) {
        List<Integer> list = new ArrayList<Integer>();
        for (Object linkObject : tags) {
            ContentLink link = (ContentLink)linkObject;
            Integer effectDefId = link.getId();
            list.add(effectDefId);
        }
        return list;
    }
    public static void setValid(Component aComponent, boolean aValid) {
        if (aValid) {
            aComponent.setBackground(UIManager.getColor("Button.background"));
        }
        else {
            aComponent.setBackground(Color.red);
        }
    }

    public abstract void registerHandlers();
    
    public void registerEditor(String contentType, final IEditorFactory f) {
        mEditorTypes.put(contentType, f);
    }
    
    public void setCooker(CookerEngine aEngine) {
        mCookerEngine = aEngine;
    }
    
    public String getContentRoot() {
        return "content\\assets\\";
    }
    
    public Icon contentTypeIcon(String aContentType) {
       java.net.URL resource = getClass().getResource("/org/ensor/games/base/tools/icons/folder.png") ;
        return new ImageIcon(resource);
    }
    
    public void updateReferenceList(ContentLink aLink) {
        mReferenceListModel.clear();
        mReferenceListShadow.clear();
        for (ContentLink content : mContentById.values()) {
            IEditorFactory editorFactory = mEditorTypes.get(content.getContentType());
            if (editorFactory == null) continue;
            IContent contentInstance = editorFactory.newContent();
            try {
                contentInstance.read(content);
            }
            catch (Exception ex) {
                ex.printStackTrace(System.out);
                mReferenceListModel.addElement(content.toString());
                mReferenceListShadow.add(content);
            }
            if (contentInstance.isReferenced(aLink)) {
                mReferenceListModel.addElement(content.toString());
                mReferenceListShadow.add(content);
            }
        }
    }
     public boolean isReferenced(ContentLink aLink) {
        for (ContentLink content : mContentById.values()) {
            System.out.println("Loading content : " + content.toString());
            IEditorFactory editorFactory = mEditorTypes.get(content.getContentType());
            if (editorFactory == null) return true;
            IContent contentInstance = editorFactory.newContent();
            try {
                contentInstance.read(content);
            }
            catch (Exception ex) {
                ex.printStackTrace(System.out);
                return true;
            }
            if (contentInstance.isReferenced(aLink)) {
                System.out.println("Content " + aLink.toString() + " is referenced by " + content.toString());
                return true;
            }
        }
        
        return false;
    }

    private void deleteContent(ContentLink aLink) {
        if (!aLink.getContentType().equals("list")) {
            if (isReferenced(aLink)) {
                System.out.println("Refusing to delete content which is referenced");
                return;
            }
        }
        
        
        EditorInstance ei = mOpenEditors.get(aLink.getId());
        if (ei != null) {
            System.out.println("Deleting from open editors");
            mOpenEditors.remove(aLink.getId());
            Integer id = mScrollpaneToId.get(mEditorTabControl.getSelectedComponent());
            mScrollpaneToId.remove(mEditorTabControl.getSelectedComponent());
            mIdToScrollpane.remove(id);
            mEditorTabControl.remove(mEditorTabControl.getSelectedIndex());
        }
        System.out.println("Deleting from tree");
        ContentLinkTreeNode tn = (ContentLinkTreeNode)treeEditors.getModel().getRoot();
        deleteContentFromTree(tn, aLink);
        treeEditors.updateUI();

        System.out.println("Deleting from content by ID");
        mContentById.remove(aLink.getId());
        
        String filename = getContentFilename(aLink);
        if (filename != null) {
            File f = new File(filename);
            f.delete();
        }
    }
    private void deleteContentFromTree(ContentLinkTreeNode tn, ContentLink aLink) {
        int i;
        for (i = 0; i < tn.getChildCount(); i++) {
            ContentLinkTreeNode ctn = (ContentLinkTreeNode)tn.getChildAt(i);
            System.out.println("Testing links " + aLink + " vs " + ctn.getLink());
            System.out.println("id " + aLink.getId() + " vs " + ctn.getLink().getId());
            if (ctn.getLink().equals(aLink)) {
                System.out.println("Removing link");
                tn.remove(ctn);
            }
            deleteContentFromTree(ctn, aLink);
        }
    }
    
    private IEditor newContent(ContentLinkTreeNode tnf, int aId, String aContentType) {
        IEditorFactory ef = mEditorTypes.get(aContentType);
        IEditor e = ef.newEditor();
        ContentLink newContentLink = new ContentLink(
            aId, aContentType, "New " + aContentType);
        EditorInstance ei = new EditorInstance(e, newContentLink);
        ei.setTNF(tnf);
        displayContent(ei, newContentLink);
        return e;
    }

    public void openContent(ContentLink aLink) {
        EditorInstance eb = null;
        if (mOpenEditors.containsKey(aLink.getId())) {
            mEditorTabControl.setSelectedComponent(mIdToScrollpane.get(aLink.getId()));
            return;
        }
        IEditorFactory ef = mEditorTypes.get(aLink.getContentType());
        if (ef == null) {
            return;
        }
        try {
            eb = new EditorInstance(ef.newEditor(), aLink);
            DictionaryAtom contentDict = ResourceLoader.getInstance().loadResource(aLink.getId());
            eb.getEditor().read(contentDict);
        }
        catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace(System.out);
        }

        if (eb == null) {
            return;
        }
        displayContent(eb, aLink);
    }
    private void displayContent(EditorInstance eb, ContentLink aLink) {
        Icon icon = contentTypeIcon(aLink.getContentType());

        
        JScrollPane sp = new JScrollPane();
        sp.setViewportView(eb.getEditor().getPanel());
        mEditorTabControl.addTab(aLink.toString(), icon, sp);
        mEditorTabControl.setSelectedComponent(sp);

        mOpenEditors.put(aLink.getId(), eb);
        mScrollpaneToId.put(sp, aLink.getId());
        mIdToScrollpane.put(aLink.getId(), sp);
    }
    public ContentLink getContentById(Integer id) {
        if (id == null) return null;
        return mContentById.get(id);
    }

    public void saveContentIndex() throws Exception 
    {
        ContentLinkTreeNode rootMutableTreeNode = (ContentLinkTreeNode)treeEditors.getModel().getRoot();
        DictionaryAtom contentIndexDict = DictionaryAtom.newAtom();

        writeContentDictionaryEntry(rootMutableTreeNode, contentIndexDict);
        
        XMLSerializer xmlSerializer = new XMLSerializer();
        Document doc2 = xmlSerializer.serializeTo(contentIndexDict);
        XMLIO.getInstance().write(doc2, "content/index.xml");
        
    }

    public void writeContentDictionaryEntry(ContentLinkTreeNode contentNode, DictionaryAtom el) {
        ContentLink contentLink = (ContentLink)contentNode.getUserObject();

        el.setString("type", contentLink.getContentType());
        el.setString("name", contentLink.getName());
        el.setInt("id", contentLink.getId());
        if (contentLink.getContentType().equals("list")) {
            ListAtom childList = el.newList("children");
            int i;
            for (i = 0; i < contentNode.getChildCount(); i++) {
                ContentLinkTreeNode childNode = (ContentLinkTreeNode)contentNode.getChildAt(i);
                DictionaryAtom child = childList.newDictionary();
                writeContentDictionaryEntry(childNode, child);
                childList.append(child);
            }
        }
    }

    public final void readContentIndex() 
            throws Exception
    {
        Document doc;
        try {
            doc = XMLIO.getInstance().read("content/index.xml");
        }
        catch (Exception ex) {
            doc = XMLIO.getInstance().newDocument();
        }
        XMLSerializer r = new XMLSerializer();
        ImmutableDict contentIndexDict = r.serializeFrom(doc);
        
        ContentLinkTreeNode uiRoot = new ContentLinkTreeNode(this);
        uiRoot.setUserObject(new ContentLink(0, "list", "root"));
        
        readContentNode(contentIndexDict, uiRoot);

        treeEditors = new JTree(uiRoot);
        treeEditors.setEditable(true);
        treeEditors.setDragEnabled(true);
        mTreePanel.setViewportView(treeEditors);
    }
    
    private void readContentNode(ImmutableDict el, ContentLinkTreeNode root)
    {
        String nodeName = el.getString("name");
        String nodeType = el.getString("type");
        int contentId = el.getInt("id");
        if (mContentId < contentId) mContentId = contentId;
        if (nodeType.equalsIgnoreCase("list")) {
            ImmutableList childList = el.getList("children");
            for (Atom n : childList) {
                ImmutableDict child = (ImmutableDict)n;
                
                ContentLinkTreeNode childTreeNode = new ContentLinkTreeNode(this);
                readContentNode(child, childTreeNode);
                root.add(childTreeNode);
            }
            ContentLink contentLink = new ContentLink(0, nodeType, nodeName);
            root.setUserObject(contentLink);
        }
        else {
            ContentLink contentLink = new ContentLink(contentId, nodeType, nodeName);
            root.setUserObject(contentLink);
            mContentById.put(contentId, contentLink);
        }
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jMenu2 = new javax.swing.JMenu();
        mTreePanel = new javax.swing.JScrollPane();
        mEditorTabControl = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        mReferenceList = new javax.swing.JList();
        mMenuBar = new javax.swing.JMenuBar();
        mFileMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        mMenuFileSave = new javax.swing.JMenuItem();
        mMenuFileExit = new javax.swing.JMenuItem();
        mEditMenu = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        mMenuToolsCookAll = new javax.swing.JMenuItem();

        jMenu2.setText("jMenu2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mTreePanel.setPreferredSize(new java.awt.Dimension(200, 400));

        mEditorTabControl.setPreferredSize(new java.awt.Dimension(400, 30));
        mEditorTabControl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mEditorTabControlMouseReleased(evt);
            }
        });

        mReferenceList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(mReferenceList);

        mFileMenu.setText("File");

        jMenuItem1.setText("Open");
        mFileMenu.add(jMenuItem1);

        mMenuFileSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mMenuFileSave.setText("Save");
        mMenuFileSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mMenuFileSaveActionPerformed(evt);
            }
        });
        mFileMenu.add(mMenuFileSave);

        mMenuFileExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        mMenuFileExit.setText("Exit");
        mMenuFileExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mMenuFileExitActionPerformed(evt);
            }
        });
        mFileMenu.add(mMenuFileExit);

        mMenuBar.add(mFileMenu);

        mEditMenu.setText("Edit");
        mMenuBar.add(mEditMenu);

        jMenu1.setText("Tools");

        mMenuToolsCookAll.setText("Cook All");
        mMenuToolsCookAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mMenuToolsCookAllActionPerformed(evt);
            }
        });
        jMenu1.add(mMenuToolsCookAll);

        mMenuBar.add(jMenu1);

        setJMenuBar(mMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(mTreePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mEditorTabControl, javax.swing.GroupLayout.DEFAULT_SIZE, 1058, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mEditorTabControl, javax.swing.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mTreePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mEditorTabControlMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mEditorTabControlMouseReleased
        
        if (evt.getButton() == 3) {
            mOpenEditors.remove(mScrollpaneToId.get(mEditorTabControl.getSelectedComponent()));
            Integer id = mScrollpaneToId.get(mEditorTabControl.getSelectedComponent());
            mScrollpaneToId.remove(mEditorTabControl.getSelectedComponent());
            mIdToScrollpane.remove(id);
            mEditorTabControl.remove(mEditorTabControl.getSelectedIndex());
        }

    }//GEN-LAST:event_mEditorTabControlMouseReleased

    private void mMenuToolsCookAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mMenuToolsCookAllActionPerformed
        try {
            onCookAll();
        }
        catch (Exception ex) {
            System.out.println("Exception cooking data");
            ex.printStackTrace(System.out);
        }
    }//GEN-LAST:event_mMenuToolsCookAllActionPerformed

    public boolean validateEditors() {
        boolean isValid = true;
        for (EditorInstance ei : mOpenEditors.values()) {
            IEditor e = ei.getEditor();
            ei.validateContent();
        }
        
        return isValid;
    }
    public void saveNewToTree() {
        for (Integer id : mOpenEditors.keySet()) {
            EditorInstance ei = mOpenEditors.get(id);
            IEditor e = ei.getEditor();
            ContentLink newContent = ei.getLink();

            ContentLinkTreeNode tnf = ei.getTNF();
            if (tnf != null && ei.isValid()) {
                ContentLinkTreeNode newContentTreeNode = new ContentLinkTreeNode(this);
                newContentTreeNode.setUserObject(newContent);
                tnf.add(newContentTreeNode);
                ei.setTNF(null);
                mContentById.put(newContent.getId(), newContent);
            }
            
        }
        treeEditors.updateUI();
    }
    
    private String getContentFilename(ContentLink aLink) {
        if (aLink.getContentType().equals("list")) return null;
        return "content/data/" + aLink.getId() + ".xml";
    }
    
    private void mMenuFileSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mMenuFileSaveActionPerformed

        // Ensure that all open editors have valid content.
        validateEditors();
        
        // Write new editors into the tree.
        saveNewToTree();
        
        try {
            saveContentIndex();
        }
        catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace(System.out);
        }
        java.io.File contentDir = new java.io.File("content/data/");
        contentDir.mkdirs();
        for (Integer aId : mOpenEditors.keySet()) {
            try {
                EditorInstance ei = mOpenEditors.get(aId);
                IEditor eb = ei.getEditor();

                if (ei.isValid()) {
                    String filename = getContentFilename(getContentById(aId));

                    DictionaryAtom contentDict = eb.write();

                    XMLSerializer w = new XMLSerializer();
                    Document doc = w.serializeTo(contentDict);
                    XMLIO.getInstance().write(doc, filename);
                }
            }
            catch (Exception ex) {
                System.out.println("Could not write document: " + ex.toString());
                ex.printStackTrace(System.out);
            }
        }
        
    }//GEN-LAST:event_mMenuFileSaveActionPerformed

    private void mMenuFileExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mMenuFileExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_mMenuFileExitActionPerformed

    public void onCookAll() throws Exception {
        mCookerEngine.cookAll(mContentById);
    }
    
    private void cookAllToArchive(
            HashMap<String, List<Integer>> contentByType,
            ICookerArchiveInterface archiveInterface) throws Exception {
    }
    
    private void contextMenuList(final ContentLink contentLink, final ContentLinkTreeNode tnf, int x, int y) {
        JPopupMenu menu = new JPopupMenu(); 
        JMenuItem newCategory = new JMenuItem("New Folder");
        final GameEditorBase geb = this;
        newCategory.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ContentLinkTreeNode newFolderNode = new ContentLinkTreeNode(geb);
                mContentId++;
                ContentLink newFolder = new ContentLink(mContentId, "list", "New Folder");
                newFolderNode.setUserObject(newFolder);
                tnf.add(newFolderNode);
                treeEditors.updateUI();
            }
        });
        menu.add(newCategory);
        
        for (final String contentType : mEditorTypes.keySet()) {
            JMenuItem newComponent = new JMenuItem("New " + contentType);
            newComponent.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mContentId++;
                    newContent(tnf, mContentId, contentType);
                }
            });

            menu.add(newComponent);
        }
        if (tnf.getChildCount() == 0) {
            JMenuItem deleteItem = new JMenuItem("Delete");
            deleteItem.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Deleting content " + contentLink.toString());
                    deleteContent(contentLink);
                    return;
                }
            });
            menu.add(deleteItem);
        }
        
        menu.show(treeEditors, x, y);
    }
    private void contextMenuContent(final ContentLink contentLink, final ContentLinkTreeNode tnf, int x, int y) {
        JPopupMenu menu = new JPopupMenu(); 
        
        JMenuItem openComponent = new JMenuItem("Open " + contentLink.getName());
        openComponent.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openContent(contentLink);
            }
        });
        menu.add(openComponent);
        
        
        JMenuItem deleteComponent = new JMenuItem("Delete");
        deleteComponent.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteContent(contentLink);
            }
        });
        menu.add(deleteComponent);
        
        menu.show(treeEditors, x, y);
    }
    
    private void onTreeClicked(java.awt.event.MouseEvent evt) {
        int selRow = treeEditors.getRowForLocation(evt.getX(), evt.getY());
        TreePath selPath = treeEditors.getPathForLocation(evt.getX(), evt.getY());
        ContentLink contentLink = null;
        ContentLinkTreeNode tn = null;
        
        if (selPath != null) {
            tn = (ContentLinkTreeNode)selPath.getLastPathComponent();
        }
        else {
            tn = (ContentLinkTreeNode)treeEditors.getModel().getRoot();
        }
        
        Object co = tn.getUserObject();
        if (!(co instanceof ContentLink)) {
            return;
        }
        contentLink = (ContentLink)co;
        
        if (evt.getButton() == MouseEvent.BUTTON3) {
            if (contentLink.getContentType().equals("list")) {
                contextMenuList(contentLink, tn, evt.getX(), evt.getY());
            }
            else {
                contextMenuContent(contentLink, tn, evt.getX(), evt.getY());
            }
        }
        else {
            if(selRow != -1) {
                 if(evt.getClickCount() == 1) {
                     if (!contentLink.getContentType().equals("list")) {
                         updateReferenceList(contentLink);
                     }
                 }
                 else if(evt.getClickCount() == 2) {
                     if (!contentLink.getContentType().equals("list")) {
                         openContent(contentLink);
                     }
                 }
             }
         }
            
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenu mEditMenu;
    private javax.swing.JTabbedPane mEditorTabControl;
    private javax.swing.JMenu mFileMenu;
    private javax.swing.JMenuBar mMenuBar;
    private javax.swing.JMenuItem mMenuFileExit;
    private javax.swing.JMenuItem mMenuFileSave;
    private javax.swing.JMenuItem mMenuToolsCookAll;
    private javax.swing.JList mReferenceList;
    private javax.swing.JScrollPane mTreePanel;
    // End of variables declaration//GEN-END:variables
}
