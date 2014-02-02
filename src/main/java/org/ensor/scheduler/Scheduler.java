/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ensor.scheduler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author jona
 */
public class Scheduler {
    private int mId;
    private ConcurrentLinkedQueue<Biote> mReadyBiotes;
    private ConcurrentHashMap<Integer,Biote> mBiotes;
    
    public Scheduler() {
        mId = 0;
        mBiotes = new ConcurrentHashMap<Integer, Biote>();
        mReadyBiotes = new ConcurrentLinkedQueue<Biote>();
    }
    
    public Biote newBiote() {
        Biote b = new Biote(mId++, this);
        mBiotes.put(b.getId(), b);
        return b;
    }
    
    protected void markReady(Biote aBiote) {
        mReadyBiotes.add(aBiote);
    }

    protected Biote getReadyBiote() {
        return mReadyBiotes.poll();
    }
    
}
