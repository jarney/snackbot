/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ensor.algorithms.astar;

/**
 * This class represents a node in an AStar search.  The class is for the
 * internal use of the AStar algorithm and should not be used outside this
 * package.
 * @author Jon
 */
final class AStarNode<T extends IPathNode> {
    AStarNode(final T node,
            final double cost,
            final int depth,
            final AStarNode<T> parent) {
        mNode = node;
        mCost = cost;
        mDepth = depth;
        mParent = parent;
        mHeuristic = 0;
    }
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object o) {
        if (o instanceof AStarNode) {
            AStarNode<T> n = (AStarNode<T>) o;
            return mNode.equals(n.mNode);
        }
        return false;
    }
    private static final int HASH_SEED = 5;
    private static final int HASH_MULTIPLIER = 53;
    private static final int BITS_PER_INT = 32;
    @Override
    public int hashCode() {
        int hash = HASH_SEED;
        int nodeHashCode = 0;
        if (mNode != null) {
            nodeHashCode = mNode.hashCode();
        }
        hash = HASH_MULTIPLIER * hash + nodeHashCode;
        hash = HASH_MULTIPLIER * hash +
                (int) (Double.doubleToLongBits(this.mCost) ^
                        (Double.doubleToLongBits(this.mCost) >>> BITS_PER_INT));
        hash = HASH_MULTIPLIER * hash + this.mDepth;
        hash = HASH_MULTIPLIER * hash + (int)
                (Double.doubleToLongBits(this.mHeuristic) ^
                (Double.doubleToLongBits(this.mHeuristic) >>> BITS_PER_INT));
        return hash;
    }

// CHECKSTYLE:OFF
    protected T mNode;
    protected double mCost;
    protected int mDepth;
    protected double mHeuristic;
    protected AStarNode<T> mParent;
// CHECKSTYLE:ON
}
