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

package org.ensor.algorithms.toposort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class contains the necessary logic to perform a
 * <a href="http://en.wikipedia.org/wiki/Topological_sorting">topological
 * sort</a>
 * of a collection of abstract nodes.  This can be used conveniently for
 * dependency processing for modules and for laying out a list of tasks with
 * interdependency so that another process can execute them and be sure
 * that any ordering rules are observed.
 *
 * @param <T> The type of node that this sort should be sorting based on.
 *
 * @author jona
 */
public class TopologicalSort<T> {

    static class Node<T> {
        protected final T mNode;
        protected final HashSet<Edge<T>> inEdges;
        protected final HashSet<Edge<T>> outEdges;
        Node(final T aNode) {
            mNode = aNode;
            inEdges = new HashSet<Edge<T>>();
            outEdges = new HashSet<Edge<T>>();
        }
        Node<T> addEdge(final Node<T> node) {
          Edge e = new Edge(this, node);
          outEdges.add(e);
          node.inEdges.add(e);
          return this;
        }
        @Override
        public boolean equals(final Object other) {
            return mNode.equals(((Node<T>) other).mNode);
        }
        @Override
        public int hashCode() {
            return mNode.hashCode();
        }
    }

    static class Edge<T> {
        protected final Node<T> mFrom;
        protected final Node<T> mTo;
        Edge(final Node<T> from, final Node<T> to) {
          this.mFrom = from;
          this.mTo = to;
        }

        @Override
        public boolean equals(final Object obj) {
          Edge<T> e = (Edge<T>) obj;
          return e.mFrom == mFrom && e.mTo == mTo;
        }

        private static final int HASH_SEED = 3;
        private static final int HASH_MULTIPLIER = 41;
        @Override
        public int hashCode() {
            int hash = HASH_SEED;
            int from = mFrom.hashCode();
            int to = mTo.hashCode();
            hash = HASH_MULTIPLIER * hash + from;
            hash = HASH_MULTIPLIER * hash + to;
            return hash;
        }
    }

    /**
     * Given a directed graph (represented as a list of nodes with
     * dependencies), return a topological sort for the graph.
     * @param aNodes A list of nodes and their dependencies.
     * @return A topological sort for the given graph or null if
     *         the graph contains cycles.
     */
    public List<T> getTopologicalSort(final List<INode<T>> aNodes) {
        Set<Node<T>> mNodes;

        mNodes = new HashSet<Node<T>>();

        Map<T, Node<T>> nodeMap = new HashMap<T, Node<T>>();
        for (INode<T> n : aNodes) {

            addNode(n.getNode(), mNodes, nodeMap);

            List<T> depList = n.getDependencies();
            for (T dep : depList) {
                addNode(dep, mNodes, nodeMap);
            }
        }

        for (INode<T> toNode : aNodes) {
            Node<T> from = nodeMap.get(toNode.getNode());
            List<T> depList = toNode.getDependencies();
            for (T dep : depList) {
                Node<T> to = nodeMap.get(dep);
                to.addEdge(from);
            }
        }

        //L <- Empty list that will contain the sorted elements
        ArrayList<Node<T>> workingList = new ArrayList<Node<T>>();

        //S <- Set of all nodes with no incoming edges
        HashSet<Node<T>> nodeSet = new HashSet<Node<T>>();
        for (Node<T> n : mNodes) {
          if (n.inEdges.isEmpty()) {
            nodeSet.add(n);
          }
        }

        //while S is non-empty do
        while (!nodeSet.isEmpty()) {
          //remove a node n from S
          Node<T> n = nodeSet.iterator().next();
          nodeSet.remove(n);

          //insert n into L
          workingList.add(n);

          //for each node m with an edge e from n to m do
          for (Iterator<Edge<T>> it = n.outEdges.iterator(); it.hasNext();) {
            //remove edge e from the graph
            Edge<T> e = it.next();
            Node<T> m = e.mTo;
            it.remove(); //Remove edge from n
            m.inEdges.remove(e); //Remove edge from m

            //if m has no other incoming edges then insert m into S
            if (m.inEdges.isEmpty()) {
              nodeSet.add(m);
            }
          }
        }
        //Check to see if all edges are removed
        boolean cycle = false;
        for (Node<T> n : mNodes) {
            if (!n.inEdges.isEmpty()) {
                cycle = true;
                break;
            }
        }
        if (cycle) {
            return null;
        }
        else {
            List<T> list = new ArrayList<T>();
            for (Node<T> n : workingList) {
                list.add(n.mNode);
            }
            return list;
        }
    }

    private void addNode(final T n,
            final Set<Node<T>> set,
            final Map<T, Node<T>> map) {
        Node<T> node = map.get(n);
        if (node != null) {
            return;
        }
        node = new Node<T>(n);
        map.put(n, node);
        set.add(node);
    }
}
