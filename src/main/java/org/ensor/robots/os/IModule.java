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

package org.ensor.robots.os;

/**
 * This interface represents a module of the system.  A module has
 * a list of other modules on which the module is dependent.  When a
 * module is requested to start, the system ensures that all of the
 * dependent modules have already been started.  Similarly, when a module
 * is shut down, any modules which are no longer needed are also shut down.
 *
 * @author jona
 */
public interface IModule {

    /**
     * This method returns the list of dependencies of this
     * module which in turn is the list of classes which must
     * be first initialized before this module can be initialized.
     *
     * @return A list of dependent modules.
     */
    Class[] getDependencies();

    /**
     * This method implements the procedure necessary to start the
     * module's systems.
     * @param aManager The module manager object.
     * @throws Exception If an exception occurs during startup, it is
     *                   propagated back up to the module manager so that
     *                   the other modules can be shut down.
     */
    void start(IModuleManager aManager) throws Exception;

    /**
     * This method implements the shutdown procedure for a module.
     * @param aManager The manager managing these modules.
     * @throws Exception If an exception occurs during shutdown, nothing
     *                   can really be done about it other than to log it.
     */
    void shutdown(IModuleManager aManager) throws Exception;
    
}
