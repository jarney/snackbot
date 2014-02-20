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
import org.ensor.algorithms.astar.IMap;

/**
 * This class is an abstract base class which provides functionality common
 * to most 'grid' type maps.  This includes a square grid, a hexagonal grid, or
 * any grid which is similar in nature.  This implements most of the IMap
 * methods from the IMap interface in the AStar package.
 * @author Jon
 */
public abstract class GridMap implements IMap<GridMover, GridNode> {
    private final HashMap<Integer, HashMap<Integer, GridNode>> mRows;
    private int mLeftExtent;
    private int mRightExtent;
    private int mTopExtent;
    private int mBottomExtent;

    protected GridMap() {
        mRows = new HashMap<Integer, HashMap<Integer, GridNode>>();
        mLeftExtent = 0;
        mRightExtent = 0;
        mTopExtent = 0;
        mBottomExtent = 0;
    }
    /**
     * This method adds a particular grid node to the map.  The node itself
     * contains information about itself including the position and any bitmap
     * flags which indicate the ability for various movers to pass the node.
     * @param n The node to add to the map.
     */
    public void addNode(final GridNode n) {
        HashMap<Integer, GridNode> column = mRows.get(n.y());
        if (column == null) {
            column = new HashMap<Integer, GridNode>();
            mRows.put(n.y(), column);
        }
        mRightExtent = Math.max(n.x(), mRightExtent);
        mLeftExtent = Math.min(n.x(), mLeftExtent);
        mBottomExtent = Math.max(n.y(), mBottomExtent);
        mTopExtent = Math.min(n.y(), mTopExtent);
        column.put(n.x(), n);
    }
    /**
     * This method removes the node at the specified position
     * in the map.
     *
     * @param x The x coordinate of the node to remove.
     * @param y The y coordinate of the node to remove.
     */
    public void removeNode(final int x, final int y) {
        HashMap<Integer, GridNode> column = mRows.get(y);
        if (column == null) {
            return;
        }
        column.remove(x);
        if (column.isEmpty()) {
            mRows.remove(y);
        }
    }
    /**
     * This method adds a node to the map at the specified x, y coordinates.
     * If there is a node already, this method adds the specified flags
     * to that node to permit passage by movers with matching flags.
     *
     * @param x The x coordinate of the node to add.
     * @param y The y coordinate of the node to add.
     * @param passableFlags The movement flags to add.
     */
    public void addNode(final int x, final int y, final int passableFlags) {
        GridNode n = getNode(x, y);
        if (n == null) {
            n = new GridNode(x, y, passableFlags);
        }
        n.addMovementFlags(passableFlags);
        addNode(n);
    }
    /**
     * This method returns the total width of the map.
     * @return Returns the width of the map.
     */
    public int getWidth() {
        return mRightExtent - mLeftExtent + 1;
    }
    /**
     * Returns the height of the map.
     * @return The height of the map.
     */
    public int getHeight() {
        return mBottomExtent - mTopExtent + 1;
    }
    /**
     * Returns the node in the position x,y.
     * @param x The x coordinate of the node to return.
     * @param y The y coordinate of the node to return.
     * @return The grid node at the specified position.
     */
    public GridNode getNode(final int x, final int y) {
        HashMap<Integer, GridNode> column = mRows.get(y);
        if (column == null) {
            return null;
        }
        GridNode n = column.get(x);
        return n;
    }
    protected void addPassableNode(
            final GridMover mover,
            final ArrayList<GridNode> neighbors,
            final int x,
            final int y) {
        GridNode n = getNode(x, y);
        if (n == null) {
            return;
        }
        if (!mover.canPass(n)) {
            return;
        }
        neighbors.add(n);
    }
    /**
     * This method returns a collection of neighbors of the given node
     * which are allowed to be traversed by the given mover.
     * @param mover The mover which will traverse the nodes.
     * @param node The node to begin the movement from.
     * @return A collection of nodes which can be reached by the given mover
     *         starting from the given node.
     */
    public abstract Collection<GridNode> getNeighbors(
            final GridMover mover,
            final GridNode node);
    /**
     * This method returns true if the given mover is allowed to traverse
     * the given node.
     * @param mover The mover to test for traversal.
     * @param node The node to test for traversal.
     * @return True if the given mover is allowed to pass the given node.
     */
    public boolean isValid(
            final GridMover mover,
            final GridNode node) {
        GridNode n = getNode(node.x(), node.y());
        if (n == null) {
            return false;
        }
        return mover.canPass(n);
    }

    /**
     * This method returns the cost of traversing from the start node to the
     * end node.  The cost is essentially the distance between the two nodes.
     *
     * @param mover The entity for which to calculate movement cost.
     * @param startNode The starting node from which to calculate movement cost.
     * @param endNode The ending node from which to calculate movement cost.
     * @return The cost of traversing from the start to the end node by the
     *         given mover.
     */
    public double getCost(
            final GridMover mover,
            final GridNode startNode,
            final GridNode endNode) {
        return java.lang.Math.abs(startNode.x() - endNode.x()) +
                java.lang.Math.abs(startNode.y() - endNode.y());
    }
    /**
     * An estimate of the distance between two nodes which may (or may not)
     * be adjacent.
     * @param mover The mover which is moving along the path.
     * @param startNode The starting node to estimate the cost from.
     * @param endNode The ending node from which to estimate the cost.
     * @return An estimate of the cost of traversing the path.
     */
    public double getHeuristic(
            final GridMover mover,
            final GridNode startNode,
            final GridNode endNode) {
        return java.lang.Math.abs(startNode.x() - endNode.x()) +
                java.lang.Math.abs(startNode.y() - endNode.y());
    }
}
