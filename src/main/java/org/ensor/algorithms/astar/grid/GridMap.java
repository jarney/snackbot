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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.ensor.algorithms.astar.IMap;
import org.ensor.algorithms.astar.AStar;

/**
 *
 * @author Jon
 */
public abstract class GridMap implements IMap<GridMover,GridNode> {
    private HashMap<Integer, HashMap<Integer, GridNode>> mRows;
    private int mWidth;
    private int mHeight;

    public GridMap() {
        mRows = new HashMap<Integer, HashMap<Integer, GridNode>>();
        mWidth = 0;
        mHeight = 0;
    }
    public void addNode(GridNode n) {
        HashMap<Integer, GridNode> column = mRows.get(n.y);
        if (column == null) {
            column = new HashMap<Integer, GridNode>();
            mRows.put(n.y, column);
        }
        mWidth = n.x > mWidth ? n.x : mWidth;
        mHeight = n.y > mHeight ? n.y : mHeight;
        column.put(n.x, n);
    }
    public void removeNode(int x, int y) {
        HashMap<Integer, GridNode> column = mRows.get(y);
        if (column == null) return;
        column.remove(x);
        if (column.isEmpty()) {
            mRows.remove(y);
        }
    }
    public void addNode(int x, int y, int passableFlags) {
        GridNode n = getNode(x,y);
        if (n == null) {
            n = new GridNode();
        }
        n.x = x;
        n.y = y;
        n.passableFlags = passableFlags;
        addNode(n);
    }
    public int getWidth() {
        return mWidth+1;
    }
    public int getHeight() {
        return mHeight+1;
    }
    public GridNode getNode(int x, int y) {
        HashMap<Integer, GridNode> column = mRows.get(y);
        if (column == null) return null;
        GridNode n = column.get(x);
        return n;
    }
    protected void addPassableNode(GridMover mover, ArrayList<GridNode> neighbors, int x, int y) {
        GridNode n = getNode(x, y);
        if (n == null) return;
        if (!mover.canPass(n)) return;
        neighbors.add(n);
    }
    public abstract Collection<GridNode> getNeighbors(GridMover mover, GridNode node);
    public boolean isValid(GridMover mover, GridNode node) {
        GridNode n = getNode(node.x, node.y);
        if (n == null) return false;
        return mover.canPass(n);
    }
    public double getCost(GridMover mover, GridNode startNode, GridNode endNode) {
        return java.lang.Math.abs(startNode.x-endNode.x) + java.lang.Math.abs(startNode.y - endNode.y);
    }
    public double getHeuristic(GridMover mover, GridNode startNode, GridNode endNode) {
        return java.lang.Math.abs(startNode.x-endNode.x) + java.lang.Math.abs(startNode.y - endNode.y);
    }
}
