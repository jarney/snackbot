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

package org.ensor.threads.biote;

/**
 * This is the base event handler object.  It is abstract and should mainly be
 * used to instantiate anonymous objects for the purpose of subscribing to
 * events.
 * Typical use of this object is during event subscriptions as in the following:
 *
 * <pre>
 *
 * subscribe("Event-StaticData-LoadComplete", new EventHandler() {
 *         public void process(EPropTree msg) throws Exception {
 *              onStaticDataLoadComplete(msg);
 *          }
 *       });
 *
 * </pre>
 * @author Jon
 */
public interface IEventHandler {
    /**
     * This method is the handler for this event.  The method is called
     * when the event is received.
     * @param msg The event to be handled.
     * @throws Exception Any exceptions thrown by the handler will be
     *                   caught and logged by the Biote manager.
     */
    void process(Event msg) throws Exception;
};
