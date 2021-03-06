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

import java.util.List;

/**
 * This interface implements a directed graph node
 * by representing the node itself as well as any outgoing
 * edges to the node.  The remainder of the graph structure may be
 * deduced by a simple collection of such nodes.
 *
 * @param <T> The type of node that this graph represents.
 *
 * @author jona
 */
public interface INode<T> {

    /**
     * Returns the node of the graph.
     *
     * @return This node.
     */
    T getNode();

    /**
     * Returns the list of nodes on which this node
     * depends.
     *
     * @return List of dependency nodes.
     */
    List<T> getDependencies();
}
