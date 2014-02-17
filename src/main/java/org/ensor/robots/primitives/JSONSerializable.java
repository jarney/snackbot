/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ensor.robots.primitives;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author Jon
 */
public interface JSONSerializable {
    public String serializeToJSON() throws Exception ;
    public String serializeToJSON(int i) throws Exception ;
    public Object serializeToJSONObject() throws Exception;

    public void deserializeFromJSON(String jsonString) throws Exception;
    public void deserializeFromJSON(Object obj) throws Exception;


}
