/*
 * The MIT License
 *
 * Copyright 2014 Jon Arney, Ensor Robotics.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/*
 * TestGridMapJFrame.java
 *
 * Created on May 27, 2010, 10:14:39 AM
 */

package org.ensor.algorithms.cubicspline;
import org.ensor.algorithms.astar.grid.*;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import org.ensor.algorithms.astar.grid.GridMap;
import org.ensor.algorithms.astar.grid.GridMover;
import org.ensor.algorithms.astar.grid.GridNode;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ensor.algorithms.astar.AStar;
import org.ensor.data.atom.DictionaryAtom;
import org.ensor.math.geometry.IPath;
import org.ensor.math.geometry.Vector2;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Jon
 */
public class TestGridMapJFrame extends javax.swing.JFrame {
    protected GridMap              mGridMap;
    protected GridNode             mStartPoint;
    protected GridNode             mEndPoint;
    protected List<GridNode>       mPath;
    protected IPath<Vector2>       mSpline;
    
    private final TestGridMapCanvas mTestCanvas;
    private final GridMover            mMover;
    private final AStar<GridMover, GridNode> mAStar;
    private static final int STATE_MAP_SET = 1;
    private static final int STATE_MAP_CLEAR = 2;
    private static final int STATE_START_POS_CHANGE = 3;
    private static final int STATE_END_POS_CHANGE = 4;
    private int mState;
    /** Creates new form TestGridMapJFrame */
    public TestGridMapJFrame() {
        initComponents();
        //mGridMap = new GridMapCompassMoves();
        //mGridMap = new GridMapDiagonalMoves();
        mGridMap = new HexMap();
        mMover = new GridMover(1);
        mStartPoint = null;
        mEndPoint = null;
        mState = STATE_MAP_SET;
        mAStar = new AStar<GridMover, GridNode>();
        mSpline = null;
        
        mTestCanvas = new TestGridMapCanvas(this);
        mTestCanvas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                onCanvasMouseClicked(evt);
            }
        });
        mTestCanvas.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                onCanvasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(mTestCanvas);
    }
    private void mapToggle(int i, int j, boolean draw) {
        if (draw) {
           mGridMap.addNode(i, j, 1);
       }
       else {
           mGridMap.removeNode(i, j);
       }
    }
    private void setStart(int i, int j) {
        mStartPoint = new GridNode(i, j, 1);
    }
    private void setEnd(int i, int j) {
        mEndPoint = new GridNode(i, j, 1);
    }
    private void onCanvasMouseClicked(java.awt.event.MouseEvent evt) {
       int x = evt.getX();
       int y = evt.getY();
       int i = x/TestGridMapCanvas.GRID_SIZE_PIXELS;
       int j = y/TestGridMapCanvas.GRID_SIZE_PIXELS;

       switch (mState) {
           case STATE_MAP_SET:
                mapToggle(i,j, true);
                break;
           case STATE_MAP_CLEAR:
                mapToggle(i,j, false);
                break;
           case STATE_START_POS_CHANGE:
                setStart(i,j);
                break;
           case STATE_END_POS_CHANGE:
                setEnd(i,j);
                break;
       }
       render();
    }
    private void render() {
        if (mStartPoint != null && mEndPoint != null) {
            mPath = mAStar.findPath(
                    mMover,
                    mGridMap,
                    mStartPoint,
                    mEndPoint, 20);
            if (mPath != null) {
                List<Vector2> path = new ArrayList<Vector2>();
                for (GridNode n : mPath) {
                    Vector2 v = new Vector2(n.x(), n.y());
                    path.add(v);
                }
                
                mSpline = new NaturalSpline2D(path);
            }
        }
        mTestCanvas.repaint();
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        mSetMapButton = new javax.swing.JButton();
        mSetStartPosButton = new javax.swing.JButton();
        mSetEndPosButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mSetMapButton.setText("Set Map");
        mSetMapButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mSetMapButtonMouseClicked(evt);
            }
        });

        mSetStartPosButton.setText("Set Start Pos");
        mSetStartPosButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mSetStartPosButtonMouseClicked(evt);
            }
        });

        mSetEndPosButton.setText("Set End Pos");
        mSetEndPosButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mSetEndPosButtonMouseClicked(evt);
            }
        });

        jButton1.setText("Write Map");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(mSetMapButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mSetStartPosButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mSetEndPosButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mSetMapButton)
                    .addComponent(mSetStartPosButton)
                    .addComponent(mSetEndPosButton)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mSetEndPosButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mSetEndPosButtonMouseClicked
        mState = STATE_END_POS_CHANGE;
    }//GEN-LAST:event_mSetEndPosButtonMouseClicked

    private void mSetStartPosButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mSetStartPosButtonMouseClicked
        mState = STATE_START_POS_CHANGE;
    }//GEN-LAST:event_mSetStartPosButtonMouseClicked

    private void mSetMapButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mSetMapButtonMouseClicked
        if (mState == STATE_MAP_SET) {
            mState = STATE_MAP_CLEAR;
        }
        else {
            mState = STATE_MAP_SET;
        }
    }//GEN-LAST:event_mSetMapButtonMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            int width = mGridMap.getWidth();
            int height = mGridMap.getHeight();

            DictionaryAtom jsonMap2 = DictionaryAtom.newAtom();
            JSONObject jsonMap = new JSONObject();

            // Write the map
            jsonMap.put("width", width);
            jsonMap.put("height", height);
            jsonMap2.setInt("width", width);
            jsonMap2.setInt("height", height);
            
            JSONArray mapmap = new JSONArray();
            
            jsonMap.put("map", mapmap);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    GridNode n = mGridMap.getNode(x, y);
                    if (n != null) {
                        JSONObject node = new JSONObject();
                        node.put("x", x);
                        node.put("y", y);
                        node.put("passableFlags", n.getMovementFlags());
                        mapmap.put(node);
                    }
                }
            }

            // Write the start and end points.
            if (mStartPoint != null) {
                JSONObject startPoint = new JSONObject();
                startPoint.put("x", mStartPoint.x());
                startPoint.put("y", mStartPoint.y());

                jsonMap.put("start", startPoint);
            }
            if (mEndPoint != null) {
                JSONObject endPoint = new JSONObject();
                endPoint.put("x", mEndPoint.x());
                endPoint.put("y", mEndPoint.y());

                jsonMap.put("end", endPoint);
            }
            
            if (mPath != null) {
                JSONArray path = new JSONArray();
                for (GridNode n : mPath) {
                    JSONObject pathNode = new JSONObject();
                    pathNode.put("x", n.x());
                    pathNode.put("y", n.y());
                    path.put(pathNode);
                }
                jsonMap.put("path", path);
            }
            
            
            // Write everything to the file.
            String s = jsonMap.toString();
            
            FileOutputStream fos = new FileOutputStream("map.json");
            PrintStream ps = new PrintStream(fos);
            ps.println(s);
            ps.close();
            
        } catch (Exception ex) {
            Logger.getLogger(TestGridMapJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TestGridMapJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton mSetEndPosButton;
    private javax.swing.JButton mSetMapButton;
    private javax.swing.JButton mSetStartPosButton;
    // End of variables declaration//GEN-END:variables

}
