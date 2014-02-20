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

import java.util.Collection;
import java.util.ArrayList;
/**
 * This map implements the semantics of a hexagonal grid for the purpose of
 * graph node traversal.  The grid is implemented by an underlying square grid
 * where certain diagonal moves are permitted.
 * @author Jon
 */
public class HexMap extends GridMap {
    /**
     * This method constructs the hexagonal grid map based on the underlying
     * GridMap implementation.
     */
    public HexMap() {
        super();
    }
    /**
     * This method returns the set of neighbors of the current node
     * which can be reached by the specified mover.  The set of nodes is the
     * 6 nodes top, bottom, left, right, upper-left, lower-right.
     * @param mover The mover which is attempting to move across the nodes.
     * @param node The node from which to start.
     * @return The list of adjacent nodes which can be reached from this node
     *         by this mover.
     */
    @Override
    public Collection<GridNode> getNeighbors(
            final GridMover mover,
            final GridNode node) {
        ArrayList<GridNode> neighbors = new ArrayList<GridNode>();

        addPassableNode(mover, neighbors, node.x() + 1, node.y());
        addPassableNode(mover, neighbors, node.x() - 1, node.y());

        addPassableNode(mover, neighbors, node.x(), node.y() + 1);
        addPassableNode(mover, neighbors, node.x(), node.y() - 1);

        addPassableNode(mover, neighbors, node.x() - 1, node.y() - 1);
        addPassableNode(mover, neighbors, node.x() + 1, node.y() + 1);

        return neighbors;
    }
    /**
     * This method calculates the cost of traversing
     * from the start node to the end node for the given
     * mover.
     * @param mover The mover traversing the path.
     * @param startNode The start node to traverse.
     * @param endNode The end node to traverse.
     * @return The cost of the mover going from startNode to endNode.
     */
    @Override
    public double getCost(
            final GridMover mover,
            final GridNode startNode,
            final GridNode endNode) {
        int ax = startNode.x() + (startNode.y()) / 2;
        int bx = endNode.x() + (endNode.y()) / 2;
        int dx = bx - ax;
        int dy = endNode.y() - startNode.y();
        return (java.lang.Math.abs(dx) +
                java.lang.Math.abs(dy) +
                java.lang.Math.abs(dx - dy)) / 2;
    }
    /**
     * This method calculates the cost of traversing
     * from the start node to the end node for the given
     * mover.
     * @param mover The mover traversing the path.
     * @param startNode The start node to traverse.
     * @param endNode The end node to traverse.
     * @return The cost of the mover going from startNode to endNode.
     */
    @Override
    public double getHeuristic(
            final GridMover mover,
            final GridNode startNode,
            final GridNode endNode) {
        return getCost(mover, startNode, endNode);
    }
}
