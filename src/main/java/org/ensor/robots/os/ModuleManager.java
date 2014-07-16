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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ensor.algorithms.toposort.INode;
import org.ensor.algorithms.toposort.TopologicalSort;

/**
 * This class implements a scheme for managing startup and shutdown of modules.
 * The scheme is based on a topological sort which ensures that when modules
 * are started, any modules on which they depend have been started first.
 *
 * @author jona
 */
public class ModuleManager implements IModuleManager {
    
    private final Map<Class, IModule> mModules;
    private final List<IModule> mModulesRunning;
    
    class ModuleNode implements INode<IModule> {

        private final IModule mMod;
        
        ModuleNode(final IModule aMod) {
            mMod = aMod;
        }
        
        public IModule getNode() {
            return mMod;
        }

        public List<IModule> getDependencies() {
            List<IModule> mods = new ArrayList<IModule>();

            Class[] dependentClasses = mMod.getDependencies();
            
            if (dependentClasses != null) {
                for (Class cl : mMod.getDependencies()) {
                    mods.add(mModules.get(cl));
                }
            }
            
            return mods;
        }
        
    }
    
    public ModuleManager() {
        mModules = new HashMap<Class, IModule>();
        mModulesRunning = new ArrayList<IModule>();
    }
    
    /**
     * This method registers a module with the module manager which will
     * take responsibility for making sure that the module is only started
     * if the dependent modules have already been started.
     *
     * @param aModule A module to manage with the module manager.
     */
    public void register(final IModule aModule) {
        mModules.put(aModule.getClass(), aModule);
    }

    /**
     * This method starts all modules in order of dependency by collecting
     * the modules into a digraph and then performing a topological sort.
     * The modules are then started in order of dependency thereby assuring
     * that they are started in the correct order.
     */
    public void startAll() throws Exception {
        List<INode<IModule>> moduleNodes = getAllNodes();
        TopologicalSort<IModule> topologicalSort =
                new TopologicalSort<IModule>();
        List<IModule> sortedList =
                topologicalSort.getTopologicalSort(moduleNodes);
        
        try {
            for (IModule m : sortedList) {
                m.start(this);
                mModulesRunning.add(m);
            }
        }
        // If there's an exception during startup, we shut down
        // again and throw an exception to represent
        // the exception which caused the shutdown.
        catch (Exception ex) {
            try {
                Collections.reverse(mModulesRunning);
                for (IModule m : mModulesRunning) {
                    m.shutdown(this);
                }
            }
            catch (Exception doubleFault) {
                // We don't re-throw this because
                // we really want to know the original cause
                // of the startup failure.  Most likely this is
                // what we want to fix and not the shutdown error.
            }
            throw ex;
        }
        
    }
    
    /**
     * This method shuts down all modules which have been registered in
     * reverse order of the startup.  This ensures that when dependent modules
     * are shut down, they don't have their dependency shut down first.
     */
    public void shutdownAll() throws Exception {
        // Shutdown happens in the reverse order of startup.
        Collections.reverse(mModulesRunning);
        
        for (IModule m : mModulesRunning) {
            m.shutdown(this);
        }
    }

    private List<INode<IModule>> getAllNodes() {
        List<INode<IModule>> moduleNodes = new ArrayList<INode<IModule>>();
        
        for (Class c : mModules.keySet()) {
            IModule module = mModules.get(c);
            ModuleNode mn = new ModuleNode(module);
            moduleNodes.add(mn);
        }
        
        return moduleNodes;
    }
    
    
    
}
