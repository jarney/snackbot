/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ensor.algorithms.astar;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * This is an abstract implementation of the classic
 * <a href="http://en.wikipedia.org/wiki/A*">AStar algorithm</a>.
 *
 * The implementation is in terms of three abstract interfaces representing
 * concepts in a graph traversal.  The first is the IPathMover which is
 * the object or entity which is traversing the graph.  This object is
 * represented abstractly because in many cases, the costs associated with a
 * graph depend on the combination of the map and the entity traversing it.
 * The second concept represented is a node (IPathNode) in the map.  The most
 * important concept about the node is its ability to distinguish itself from
 * other nodes, so the only concept represented is the node's equality method.
 * The third and final concept modeled is the IMap.  The map is an association
 * between nodes and the neighbors of a node.  In addition, the map can obtain
 * a movement cost for moving between two adjacent nodes and can estimate a
 * heuristic cost for moving between two non-adjacent nodes.
 * @param <M> This parameter is the type of object which can move through
 *            the graph.  This object must implement the IPathMover interface.
 * @param <N> This parameter is the type of node in the graph which represents
 *            the map for which to find a path.  The type must implement the
 *            IPathNode interface.
 * @author Jon
 */
public class AStar<M extends IPathMover, N extends IPathNode> {

    private static final Comparator<AStarNode> COMPARATOR =
            new Comparator<AStarNode>() {
                public int compare(final AStarNode n2, final AStarNode n1) {
                    if (n1.mCost == n2.mCost) {
                        return 0;
                    }
                    if (n1.mCost < n2.mCost) {
                        return 1;
                    }
                    return -1;

                }
    };

    /**
     * This method finds the smallest cost path for the given mover
     * to traverse from the startNode to the endNode on the given map.
     * If no path exists, this method returns 'null' indicating that no
     * path regardless of cost could be found for this mover on this map.
     *
     * @param mover The entity moving on the map.
     * @param map The map on which to attempt to find a path.
     * @param startNode The beginning node of the path to find.
     * @param endNode The end node of the path to find.
     * @param maxSearchDepth The maximum depth for which to allow the search
     *                       to continue.  Note that the A-Star algorithm is
     *                       exponential in the worst-case, so using a search
     *                       depth to limit the search is often desirable
     *                       although can sometimes lead to no path being found.
     * @return An ordered list of nodes in a path from startNode to endNode or
     *         null if no such path was found.
     */
    public List<N> findPath(
            final M mover,
            final IMap<M, N> map,
            final N startNode,
            final N endNode,
            final int maxSearchDepth) {
        // easy first check, if the destination is blocked, we can't get there

        if (!map.isValid(mover, startNode)) {
            return null;
        }
        if (!map.isValid(mover, endNode)) {
            return null;
        }

        ArrayList<AStarNode<N>> closed = new ArrayList<AStarNode<N>>();
        ArrayList<AStarNode<N>> open = new ArrayList<AStarNode<N>>();
        HashMap<N, AStarNode<N>> allNodes = new HashMap<N, AStarNode<N>>();

        // initial state for A*. The closed group is empty. Only the starting

        AStarNode<N> startAStarNode = new AStarNode<N>(startNode, 0, 0, null);
        open.add(startAStarNode);
        AStarNode<N> targetNode = null;
        // while we haven'n't exceeded our max search depth
        int maxDepth = 0;
        while ((maxDepth < maxSearchDepth) && (!open.isEmpty())) {
            // pull out the first node in our open list, this is determined to

            // be the most likely to be the next step based on our heuristic
            AStarNode<N> current = Collections.min(open, COMPARATOR);
            if (current.mNode.equals(endNode)) {
                targetNode = current;
                break;
            }

            open.remove(current);
            closed.add(current);

            Collection<N> neighbors = map.getNeighbors(mover, current.mNode);
            for (N n : neighbors) {
                if (!map.isValid(mover, n)) {
                    continue;
                }

                AStarNode<N> neighbour = allNodes.get(n);
                if (neighbour == null) {
                    neighbour = new AStarNode<N>(n, 0, 0, null);
                    allNodes.put(n, neighbour);
                }

                // the cost to get to this node is cost the current plus
                // the movement
                double nextStepCost = current.mCost +
                        map.getCost(mover, current.mNode, neighbour.mNode);
                if (nextStepCost < neighbour.mCost) {
                    if (open.contains(neighbour)) {
                        open.remove(neighbour);
                    }
                    if (closed.contains(neighbour)) {
                        closed.remove(neighbour);
                    }
                }

                // if the node hasn't already been processed and discarded then
                // reset it's cost to our current cost and add it as a next
                // possible step (i.e. to the open list)
                if (!open.contains(neighbour) &&
                        !(closed.contains(neighbour))) {
                    neighbour.mCost = nextStepCost;
                    neighbour.mHeuristic = map.getHeuristic(
                            mover,
                            current.mNode,
                            endNode);
                    neighbour.mParent = current;
                    maxDepth = Math.max(maxDepth, current.mDepth + 1);
                    open.add(neighbour);
                }
            }
        }
        // since we'e've run out of search
        // there was no path. Just return null

        if (targetNode == null) {
            return null;
        }

        // At this point we've definitely found a path so we can uses the parent

        // references of the nodes to find out way from the target location back

        // to the start recording the nodes on the way.

        LinkedList<N> pathList = new LinkedList<N>();

        AStarNode<N> target = targetNode;
        while (target != null) {
            pathList.addFirst(target.mNode);
            target = target.mParent;
        }

        // thats it, we have our path

        return pathList;
    }

}

