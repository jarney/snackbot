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

package org.ensor.robots.scheduler;

/**
 * The Biote module class provides the capability to add functionality
 * to a particular Biote without the Biote itself being aware that it
 * carries this functionality.  This is specifically useful for adding
 * things like network communications or database functionality
 * to the Biote and allowing it to subscribe and unsubscribe to
 * events which other modules or the Biote itself is not aware of.
 * @param <T> The type of Biote we're adding a module to.  This type
 *            must be derived from the base Biote class, but may be
 *            any specific type of Biote.
 * @author Jon
 */
public abstract class BioteModule<T extends Biote> {
    private final T mBiote;
    
    /**
     * The constructor for a Biote module contains the Biote which it is a
     * part of.  Classes which implement this may perform other operations
     * in the constructor, but will always have access to the Biote
     * they are attached to.
     * @param aBiote The Biote that this module should be connected to.
     */
    public BioteModule(final T aBiote) {
        mBiote = aBiote;
    }
    /**
     * This method returns the Biote that this module is connected to.
     * @return The Biote that this module is connected to.
     */
    public Biote getBiote() {
        return mBiote;
    }
    
    protected abstract void onInit(Event message) throws Exception;
    protected abstract void onInitComplete(Event message) throws Exception;
}
