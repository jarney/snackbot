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

package org.ensor.algorithms.astar.grid;

import org.ensor.algorithms.astar.grid.GridNode;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;

/**
 *
 * @author Jon
 */
public class TestGridMapCanvas extends java.awt.Canvas {
    TestGridMapJFrame mMain;
    int mRenderMode;
    public static final int RENDER_MODE_HEX = 1;
    public static final int RENDER_MODE_GRID = 2;
    public TestGridMapCanvas(TestGridMapJFrame main) {
        mMain = main;
        mRenderMode = RENDER_MODE_HEX;
    }
    public void setRenderMode(int renderMode) {
        mRenderMode = renderMode;
    }
    @Override
    public void paint(Graphics g) {
        int i;
        int j;
        g.clearRect(0, 0, getWidth(), getHeight());
        /*
         * Draw the grid.
         */
        g.setColor(Color.blue);
        for (j = 0; j < mMain.mGridMap.getHeight(); j++) {
            for (i = 0; i < mMain.mGridMap.getWidth(); i++) {
                GridNode n = mMain.mGridMap.getNode(i, j);
                if (n != null) {
                    g.fillRect(i*10, j*10, 9, 9);
                }
            }
        }
        /*
         * Draw the starting point
         */
        if (mMain.mStartPoint != null) {
            g.setColor(Color.red);
            g.fillOval(mMain.mStartPoint.x()*10, mMain.mStartPoint.y()*10, 9, 9);
        }
        /*
         * Draw the ending point
         */
        if (mMain.mEndPoint != null) {
            g.setColor(Color.green);
            g.fillOval(mMain.mEndPoint.x()*10, mMain.mEndPoint.y()*10, 9, 9);
        }
        /*
         * Draw the path (if there is one...)
         */
        if (mMain.mPath != null) {
            g.setColor(Color.orange);
            Iterator<GridNode> it = mMain.mPath.iterator();
            while (it.hasNext()) {
                GridNode n = it.next();
                g.fillRect(n.x()*10+3, n.y()*10+3, 3, 3);
            }
        }
    }

}
