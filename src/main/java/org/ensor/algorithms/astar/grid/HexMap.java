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

import org.ensor.algorithms.astar.AStar;

import java.util.Collection;
import java.util.ArrayList;
/**
 *
 * @author Jon
 */
public class HexMap extends GridMap {
    public HexMap() {
        super();
    }
    @Override
    public Collection<GridNode> getNeighbors(GridMover mover, GridNode node) {
        ArrayList<GridNode> neighbors = new ArrayList<GridNode>();

        addPassableNode(mover, neighbors, node.x+1, node.y);
        addPassableNode(mover, neighbors, node.x-1, node.y);

        addPassableNode(mover, neighbors, node.x, node.y+1);
        addPassableNode(mover, neighbors, node.x, node.y-1);

        addPassableNode(mover, neighbors, node.x+1, node.y+1);
        addPassableNode(mover, neighbors, node.x+1, node.y-1);

        return neighbors;
    }
    @Override
    public double getCost(GridMover mover, GridNode startNode, GridNode endNode) {
        int Ax = startNode.x + (startNode.y)/2;
        int Bx = endNode.x + (endNode.y)/2;
        int dx = Bx - Ax;
        int dy = endNode.y - startNode.y;
        return (java.lang.Math.abs (dx) + java.lang.Math.abs (dy) + java.lang.Math.abs (dx-dy)) / 2;
        
    }
    @Override
    public double getHeuristic(GridMover mover, GridNode startNode, GridNode endNode) {
        int Ax = startNode.x + (startNode.y)/2;
        int Bx = endNode.x + (endNode.y)/2;
        int dx = Bx - Ax;
        int dy = endNode.y - startNode.y;
        return (java.lang.Math.abs (dx) + java.lang.Math.abs (dy) + java.lang.Math.abs (dx-dy)) / 2;
    }
}
