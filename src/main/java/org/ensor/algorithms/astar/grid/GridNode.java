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

import org.ensor.algorithms.astar.IPathNode;

/**
 *
 * @author Jon
 */
public class GridNode implements IPathNode {
    private final int mX;
    private final int mY;
    private int mMovementFlags;

    /**
     * The constructor creates a new grid node at the specified location
     * with the given movement flags.  The movement flags are compared with
     * the mover's movement flags in order to determine whether a particular
     * mover can pass this node.
     * @param x The x location of this node.
     * @param y The y location of this node.
     * @param movmentFlags The movement flags which indicate which movers
     *                     can pass the node.
     */
    public GridNode(
        final int x,
            final int y,
            final int movmentFlags) {
        mX = x;
        mY = y;
        mMovementFlags = movmentFlags;
    }
    /**
     * Returns the x location of this node.
     * @return The x location of this node.
     */
    public int x() {
        return mX;
    }
    /**
     * Returns the y location of this node.
     * @return The y location of this node.
     */
    public int y() {
        return mY;
    }
    /**
     * Returns the movement flags for this node.
     * @return The movement flags of this node.
     */
    public int getMovementFlags() {
        return mMovementFlags;
    }
    /**
     * Sets the movement flags for this node.
     * @param movementFlags The new set of movement flags for this node.
     */
    public void setMovementFlags(final int movementFlags) {
        mMovementFlags = movementFlags;
    }
    /**
     * This method masks on the given set of movement flags.
     * @param movementFlags The set of movement flags to add to the current set.
     */
    public void addMovementFlags(final int movementFlags) {
        mMovementFlags |= movementFlags;
    }
    /**
     * This method masks off the given set of movement flags.
     * @param movementFlags The set of movement flags to remove from the
     *                       current set.
     */
    public void removeMovementFlags(final int movementFlags) {
        mMovementFlags &= ~(movementFlags);
    }
    /**
     * This method determines if the given object is a node and if so,
     * whether it refers to the same grid location.  Movement flags are
     * not a part of the equality check, only location.
     * @param aN A node to test for equality.
     * @return True if the given object is a node which refers to the same
     *         grid location.
     */
    @Override
    public boolean equals(final Object aN) {
        if (!(aN instanceof GridNode)) {
            return false;
        }
        GridNode n = (GridNode) aN;
        return (mX == n.mX) && (mY == n.mY);
    }

    private static final int HASH_SEED = 7;
    private static final int HASH_MULTIPLIER = 31;

    @Override
    public int hashCode() {
        int hash = HASH_SEED;
        hash = HASH_MULTIPLIER * hash + this.mX;
        hash = HASH_MULTIPLIER * hash + this.mY;
        return hash;
    }

}
