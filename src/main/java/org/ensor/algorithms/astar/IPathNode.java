/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ensor.algorithms.astar;
/**
 * One particular node in a map of nodes traversed with the AStar algorithm.
 * The only real requirement is that the node be able to distinguish itself
 * from other nodes and therefore must implement the 'equals' method per
 * standard java patterns.
 * @author Jon
 */
public interface IPathNode {
    /**
     * This method returns true if and only if the given object is equal to
     * this node.
     * @param aN An object to compare with this object for equality.
     * @return True if the two nodes are identical in the sense of the map.
     */
    @Override
    boolean equals(Object aN);
}
