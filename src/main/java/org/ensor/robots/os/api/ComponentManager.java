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

package org.ensor.robots.os.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The component manager's purpose is to act as a central registry
 * for all of the components which are a part of the robot's anatomy.
 * The component manager is a singleton and holds all of the components
 * which make up the robot.  The components are associated in various and
 * complex ways, however, in the component manager, only a registration of name
 * to component is represented.  Other structure which may be present is not
 * modeled as a part of this registry.
 *
 * @author jona
 */
public class ComponentManager {

    private static final ComponentManager INSTANCE = new ComponentManager();

    private final Map<String, Object> mComponents;

    ComponentManager() {
        mComponents = new ConcurrentHashMap<String, Object>();
    }

    /**
     * This method returns the singleton instance of this component manager.
     *
     * @return Returns the single instance of the component manager which
     *         contains the registry of all components accessible in the robot.
     */
    public static ComponentManager getInstance() {
        return INSTANCE;
    }

    /**
     * This method registers a component with the
     * component manager.
     * @param aName The fully-qualified name of the component.
     * @param aComponent The component being registered.
     */
    public static void registerComponent(
            final String aName,
            final Object aComponent) {
        INSTANCE.mComponents.put(aName, aComponent);
    }

    /**
     * This method returns the component with the given
     * fully-qualified name.
     *
     * @param <T> Type of the component to return.
     * @param aName The fully-qualified name of a component.
     * @param aType Type of component to return.
     * @return The component with the specified ID.
     */
    public static <T> T getComponent(
            final String aName,
            final Class<T> aType) {
        return (T) INSTANCE.mComponents.get(aName);
    }
}
