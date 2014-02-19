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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.ensor.algorithms.astar.AStar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author jona
 */
public class TestAStar {

    /**
     * This test verifies that the correct path is found for this map.
     * @throws IOException
     * @throws JSONException 
     */
    @Test
    public void testPathFinding() throws IOException, JSONException {
        GridMapCompassMoves gridMap = new GridMapCompassMoves();
        performGridTest(gridMap, "scurve-compass.json");
    }
    
    /**
     * This test checks the case where the start point does not have a
     * valid map location.
     * @throws IOException
     * @throws JSONException 
     */
    @Test
    public void testPathFindingBadStart() throws IOException, JSONException {
        GridMapCompassMoves gridMap = new GridMapCompassMoves();
        performGridTest(gridMap, "badStart.json");
    }
    /**
     * This test checks the case where the end point does not have
     * a valid map location.
     * @throws IOException
     * @throws JSONException 
     */
    @Test
    public void testPathFindingBadEnd() throws IOException, JSONException {
        GridMapCompassMoves gridMap = new GridMapCompassMoves();
        performGridTest(gridMap, "badEnd.json");
    }
    
    /**
     * This test checks the case where the start and end points
     * are valid, but there's no path from start to end
     * in the map.
     * @throws IOException
     * @throws JSONException 
     */
    @Test
    public void testPathFindingNoPath() throws IOException, JSONException {
        GridMapCompassMoves gridMap = new GridMapCompassMoves();
        performGridTest(gridMap, "nopath.json");
    }
    
    /**
     * This tests the case of a valid start and end nodes, but no path
     * between them for a diagonal movement map.
     * @throws IOException
     * @throws JSONException 
     */
    @Test
    public void testPathFindingNoPathDiagonals() throws IOException, JSONException {
        GridMapDiagonalMoves gridMap = new GridMapDiagonalMoves();
        performGridTest(gridMap, "nopath.json");
    }

    /**
     * This tests grids where diagonal moves are allowed.
     * @throws IOException
     * @throws JSONException 
     */
    @Test
    public void testPathFindingDiagonalMoves() throws IOException, JSONException {
        GridMapDiagonalMoves gridMap = new GridMapDiagonalMoves();
        performGridTest(gridMap, "scurve-diagonal.json");
    }
    /**
     * This tests grids where diagonal moves are allowed.
     * @throws IOException
     * @throws JSONException 
     */
    @Test
    public void testPathFindingHexMap() throws IOException, JSONException {
        HexMap gridMap = new HexMap();
        performGridTest(gridMap, "hexmap.json");
    }
    
    public void performGridTest(GridMap aMap, String aTestJSONFile) throws IOException, JSONException{
        BufferedReader bf = new BufferedReader(
                new InputStreamReader(
                    this.getClass().getResourceAsStream(aTestJSONFile)
                )
        );
        
        GridMover mover = new GridMover();
        mover.passableFlags = 1;
        
        String jsonString = bf.readLine();
        
        JSONObject jsonObject = new JSONObject(jsonString);
        
        JSONArray array = jsonObject.getJSONArray("map");
        
        for (int i = 0; i < array.length(); i++) {
            JSONObject node = array.getJSONObject(i);
            aMap.addNode(node.getInt("x"), node.getInt("y"),
                    node.getInt("passableFlags"));
        }

        JSONObject start = jsonObject.getJSONObject("start");
        JSONObject end = jsonObject.getJSONObject("end");
        
        GridNode s = new GridNode();
        s.x = start.getInt("x");
        s.y = start.getInt("y");
        
        GridNode e = new GridNode();
        e.x = end.getInt("x");
        e.y = end.getInt("y");

        List<GridNode>       path;
    
        AStar<GridMover, GridNode> astar = new AStar<GridMover, GridNode>();
        path = astar.findPath(
            mover,
            aMap,
            s, e, 20);
        

        JSONArray pathArray = jsonObject.optJSONArray("path");
        if (pathArray == null) {
            Assert.assertNull(path);
            return;
        }
        
        Assert.assertNotNull(path);
        
        Assert.assertEquals(pathArray.length(), path.size());
        
        for (int i = 0; i < pathArray.length(); i++) {
            JSONObject pathNode = pathArray.getJSONObject(i);
            GridNode n = path.get(i);
            
            Assert.assertEquals(pathNode.getInt("x"), n.x);
            Assert.assertEquals(pathNode.getInt("y"), n.y);
        }
    }
    
    
}
