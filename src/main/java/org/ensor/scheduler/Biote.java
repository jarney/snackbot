/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ensor.scheduler;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author jona
 */
public class Biote {
    private final Scheduler mScheduler;
    private ConcurrentLinkedQueue<Event> mEvents;
    private HashMap<Class, EventHandler> mHandlers;
    private final Integer mId;
    
    protected Biote(Integer aId, Scheduler s) {
        mScheduler = s;
        mId = aId;
        mEvents = new ConcurrentLinkedQueue<Event>();
    }
    
    protected void addEvent(Event e) {
        mEvents.add(e);
        mScheduler.markReady(this);
    }

    Integer getId() {
        return mId;
    }

    protected void executeEvents() {
        for (Event e : mEvents) {
            handle(e);
        }
    }

    private void handle(Event e) {
        EventHandler h = mHandlers.get(e.getClass());
        h.handle(e);
    }

}
