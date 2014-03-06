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

package org.ensor.algorithms.cubicspline;

import org.ensor.algorithms.astar.grid.*;
import org.ensor.algorithms.astar.grid.GridNode;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;
import org.ensor.math.geometry.Vector2;

/**
 *
 * @author Jon
 */
public class TestGridMapCanvas extends java.awt.Canvas {
    
    protected static final int GRID_SIZE_PIXELS = 30;
    protected static final int GRID_CENTER = 30/2;
    protected static final int STEP_SIZE_SPLINE = 100;
    
    
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
        g.clearRect(0, 0, getWidth(), getHeight());
        /*
         * Draw the grid.
         */
        g.setColor(Color.blue);
        for (int j = 0; j < mMain.mGridMap.getHeight(); j++) {
            for (int i = 0; i < mMain.mGridMap.getWidth(); i++) {
                GridNode n = mMain.mGridMap.getNode(i, j);
                if (n != null) {
                    g.fillRect(i*GRID_SIZE_PIXELS, j*GRID_SIZE_PIXELS,
                            GRID_SIZE_PIXELS-1,
                            GRID_SIZE_PIXELS-1);
                }
            }
        }
        /*
         * Draw the starting point
         */
        if (mMain.mStartPoint != null) {
            g.setColor(Color.red);
            g.fillOval(mMain.mStartPoint.x()*GRID_SIZE_PIXELS, mMain.mStartPoint.y()*GRID_SIZE_PIXELS, 
                    GRID_SIZE_PIXELS-1, GRID_SIZE_PIXELS-1);
        }
        /*
         * Draw the ending point
         */
        if (mMain.mEndPoint != null) {
            g.setColor(Color.green);
            g.fillOval(mMain.mEndPoint.x()*GRID_SIZE_PIXELS, mMain.mEndPoint.y()*GRID_SIZE_PIXELS,
                    GRID_SIZE_PIXELS-1, GRID_SIZE_PIXELS-1);
        }
        /*
         * Draw the path (if there is one...)
         */
        if (mMain.mPath != null) {
            g.setColor(Color.orange);
            Iterator<GridNode> it = mMain.mPath.iterator();
            while (it.hasNext()) {
                GridNode n = it.next();
                g.fillRect(
                        n.x()*GRID_SIZE_PIXELS+GRID_CENTER-2,
                        n.y()*GRID_SIZE_PIXELS+GRID_CENTER-2,
                        4, 4);
            }
        }
        
        if (mMain.mSpline != null) {
            g.setColor(Color.magenta);
            for (int i = 0; i < STEP_SIZE_SPLINE; i++) {
                double t0 = ((double)(i) / (double)STEP_SIZE_SPLINE);
                double t1 = ((double)(i + 1) / (double)STEP_SIZE_SPLINE);
                Vector2 v1 = mMain.mSpline.getPosition(t0);
                Vector2 v2 = mMain.mSpline.getPosition(t1);
                
                int x1 = (int)(v1.getX() * GRID_SIZE_PIXELS + GRID_CENTER);
                int y1 = (int)(v1.getY() * GRID_SIZE_PIXELS + GRID_CENTER);
                int x2 = (int)(v2.getX() * GRID_SIZE_PIXELS + GRID_CENTER);
                int y2 = (int)(v2.getY() * GRID_SIZE_PIXELS + GRID_CENTER);
                g.drawLine(x1, y1, x2, y2);
                
//                Vector2 d1 = mMain.mSpline.getDirection(t0);
//                Vector2 vd2 = v1.add(d1);
//                
//                int dx1 = (int)(v1.getX() * GRID_SIZE_PIXELS + GRID_CENTER);
//                int dy1 = (int)(v1.getY() * GRID_SIZE_PIXELS + GRID_CENTER);
//                int dx2 = (int)(vd2.getX() * GRID_SIZE_PIXELS + GRID_CENTER);
//                int dy2 = (int)(vd2.getY() * GRID_SIZE_PIXELS + GRID_CENTER);
//                g.drawLine(dx1, dy1, dx2, dy2);
            }
        }
        
    }

}
