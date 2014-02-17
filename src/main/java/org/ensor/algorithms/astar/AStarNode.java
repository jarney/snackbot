/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ensor.algorithms.astar;

import java.util.Collection;
import java.lang.Comparable;
/**
 *
 * @author Jon
 */
class AStarNode<T extends IPathNode> {
    AStarNode(T node, double cost, int depth, AStarNode<T> parent) {
        mNode = node;
        mCost = cost;
        mDepth = depth;
        mParent = parent;
        mHeuristic = 0;
    }
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o instanceof IPathNode) {
            return mNode.equals(o);
        }
        if (o instanceof AStarNode) {
            AStarNode<T> n = (AStarNode<T>)o;
            return mNode.equals(n.mNode);
        }
        return false;
    }
    protected T mNode;
    protected double mCost;
    protected int mDepth;
    protected double mHeuristic;
    protected AStarNode<T> mParent;
}
