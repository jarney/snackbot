/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ensor.robots.primitives;

/**
 *
 * @author Jon
 */
public interface URLSerializable {
    public String serializeToURL();
    public void serializeFromURL(String jsonString);
}
