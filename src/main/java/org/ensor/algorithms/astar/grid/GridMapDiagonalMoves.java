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
 * This class represents a grid map on which a mover is allowed to move in the
 * four cardinal compass directions as well as four diagonal directions.
 * The adjacency returned by the getNeighbors method reflects this fact.
 * @author Jon
 */
public class GridMapDiagonalMoves extends GridMap {
    /**
     * This method's default constructor simply instantiates the base grid
     * map class and inherits most of its functionality from there.
     */
    public GridMapDiagonalMoves() {
        super();
    }
    /**
     * This method returns the neighbors of the given node for the given
     * mover the four cardinal directions as well as the four diagonals.
     * @param mover The mover to find neighbors for in the map.
     * @param node The node from which to find neighboring nodes.
     * @return A collection of neighboring nodes.
     */
    public Collection<GridNode> getNeighbors(
            final GridMover mover,
            final GridNode node) {
        ArrayList<GridNode> neighbors = new ArrayList<GridNode>();
        addPassableNode(mover, neighbors, node.x() + 1, node.y() + 1);
        addPassableNode(mover, neighbors, node.x() + 1, node.y());
        addPassableNode(mover, neighbors, node.x() + 1, node.y() - 1);

        addPassableNode(mover, neighbors, node.x(), node.y() + 1);
        addPassableNode(mover, neighbors, node.x(), node.y() - 1);

        addPassableNode(mover, neighbors, node.x() - 1, node.y() + 1);
        addPassableNode(mover, neighbors, node.x() - 1, node.y());
        addPassableNode(mover, neighbors, node.x() - 1, node.y() - 1);

        return neighbors;
    }

}
