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

import org.ensor.algorithms.astar.IPathMover;

/**
 * This is an entity which can move on a grid.  The entity
 * has an integer set of flags (bitmapped) which indicate which
 * nodes the mover is allowed to traverse.  This mover implements a "canPass"
 * method which indicates whether the mover is permitted to pass a particular
 * node.
 *
 * @author Jon
 */
public class GridMover implements IPathMover {
    private int mMovementFlags;
    /**
     * This method creates a grid mover with the movement flags specified
     * by the given integer.  The movement flags integer is a bitmap which
     * indicates what nodes the mover is allowed to traverse.
     *
     * @param aPassableFlags The bitmap of flags which indicate movement ability
     *                       for nodes in the map.
     */
    public GridMover(final int aPassableFlags) {
        mMovementFlags = aPassableFlags;
    }

    /**
     * This method returns the current set of movement flags.
     * @return The current set of movement flags as a bitmap.
     */
    public int getMovementFlags() {
        return mMovementFlags;
    }

    /**
     * This method sets the current set of movement flags
     * as an integer bitmap.
     * @param aMovementFlags The set of movement flags to use for this mover
     *                       as a bitmap integer.
     */
    public void setMovementFlags(final int aMovementFlags) {
        mMovementFlags = aMovementFlags;
    }
    /**
     * This method determines if this mover is allowed to traverse the given
     * node.
     * @param n The node to test for the ability to pass.
     * @return True if this mover is permitted to pass this node.
     */
    public boolean canPass(final GridNode n) {
        return (mMovementFlags & n.getMovementFlags()) != 0;
    }
}
