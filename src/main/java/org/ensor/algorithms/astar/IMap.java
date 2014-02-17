/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ensor.algorithms.astar;

import java.util.Collection;
/**
 * This interface represents a map or graph to be traversed
 * using the AStar algorithm.  The map must implement a way to retrieve
 * nodes adjacent to the current node (for the given mover) and must be able
 * to determine if it is valid for a given mover to traverse the given node.
 * In addition, the cost and heuristic measures must be implemented so that
 * the relative cost of various path elements can be known so that the
 * AStar algorithm will complete in a deterministic way.
 *
 * @author Jon
 * @param <Mover> This class represents the entity moving on the map.
 * @param <Node> This class represents a node in the map.
 */
public interface IMap<Mover extends IPathMover, Node extends IPathNode> {

    /**
     * The getNeighbors method must return a list of neighbors for
     * the given node for the given mover.  The method must operate
     * in a reflexive way (i.e. there must be no one-way traversals) implied
     * by this map.  If there is an node "X" returned by this method for
     * the node "Y" then then the node "Y" must be returned for the node "X".
     *
     * @param mover The entity moving on the map.
     * @param node The node on the map for which the neighbors should be found.
     * @return The list of nodes immediately reachable by the mover directly
     *         from the given node.
     */
    Collection<Node> getNeighbors(Mover mover, Node node);

    /**
     * This method determines whether the given mover can traverse the given
     * node.  The map, mover, and nodes can determine their own rules for
     * determining whether this traversal is valid.
     *
     * @param mover The entity moving on the map.
     * @param node The node for which to test traversability.
     * @return True if the mover is allowed to pass the given node.
     */
    boolean isValid(Mover mover, Node node);

    /**
     * This method returns the cost of the given mover traversing from the
     * startNode to the nextNode (assumed to be adjacent nodes).  The scale
     * and values of the costs are determined by the map and have only meaning
     * relative to other costs.  It is assumed that the cost of the overall path
     * is the linear cost of each of the adjacent moves.  It is also assumed
     * that costs are independent of the order of traversal meaning that
     * getCost(M, S, E) == getCost(M, E, S) must always be true.
     *
     * @param mover The entity moving on the map.
     * @param startNode The node from which to start.
     * @param nextNode The node on which to end.
     * @return The cost of the given mover traversing from startNode to
     *         endNode on this map.
     */
    double getCost(Mover mover, Node startNode, Node nextNode);

    /**
     * This method provides an estimate for the cost of a mover traversing
     * from the startNode to the nextNode (not necessarily adjacent) in the
     * map.  It is assumed that the costs are independent of the order of
     * traversal meaning that getHeuristic(M, S, E) == getHeuristic(M, E, S)
     * must always be true.
     
     * @param mover The entity moving on the map.
     * @param startNode The node from which to start.
     * @param nextNode The end node for which to estimate cost.
     * @return The estimated cost of traversing from the startNode to
     *         the endNode on this map.
     */
    double getHeuristic(Mover mover, Node startNode, Node nextNode);
}
