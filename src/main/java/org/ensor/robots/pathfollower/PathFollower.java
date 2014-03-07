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

package org.ensor.robots.pathfollower;

import java.util.ArrayList;
import java.util.List;
import org.ensor.algorithms.cubicspline.NaturalSpline3D;
import org.ensor.data.atom.Atom;
import org.ensor.data.atom.DictionaryAtom;
import org.ensor.data.atom.ImmutableDict;
import org.ensor.data.atom.ImmutableList;
import org.ensor.math.geometry.Vector3;
import org.ensor.robots.scheduler.Biote;
import org.ensor.robots.scheduler.BioteManager;
import org.ensor.robots.scheduler.Event;
import org.ensor.robots.scheduler.IEventHandler;

/**
 * This Biote allows a path to be planned and followed.  It requires a mover
 * Biote to send movement updates to and it also subscribes to navigation
 * path planning events sent from a navigation Biote.  The navigation path
 * planner sends a start and a goal location as well as a list of waypoints
 * to follow.  This Biote builds a cubic spline path between them and begins
 * to issue movement commands to the motors to make the movement happen.
 * Periodically it also receives events from the position estimation system
 * so that the position and direction can be re-updated to match the latest
 * estimates.  When the path has been finished, a path completion event is
 * raised back to the Biote who requested the path to be followed.
 * @author jona
 */
public class PathFollower extends Biote {

    private static final Event TICK_EVENT = new Event("PathFollower-Tick");
    /// The ID of the timer used to tick the simulation.
    private Integer mTimerId;
    /// The biote ID of the biote we can send a position
    // command to.
    private Integer mMoverBioteId;
    /// The most recent location estimate.
    private Vector3 mLastLocation;
    private Vector3 mLastVel;

    /// The goal location and velocity.
    private Vector3 mGoalLocation;
    private Vector3 mGoalVel;
    
    /// The last time we received a location update (in ms).
    private Integer mLastLocationTime;
    
    /// The biote to notify when the path
    // has been completed.
    private Integer mCompleteNotify;
    
    private NaturalSpline3D mSpline;
    /**
     *
     * @param aBioteManager
     */
    public PathFollower(final BioteManager aBioteManager) {
        super(aBioteManager, false);
        mTimerId = null;
    }

    @Override
    protected void onInit(final Event message) throws Exception {
        subscribe("Navigation-PathPlanned", new IEventHandler() {
            public void process(final Event msg) throws Exception {
                onPathPlanned(msg);
            }
        });
        subscribe("Navigation-LocationEstimate", new IEventHandler() {
            public void process(Event msg) throws Exception {
                onLocationUpdated(msg);
            }
        });
        subscribe("PathFollower-PathFinished", new IEventHandler() {
            public void process(Event msg) throws Exception {
                onPathFinished(msg);
            }
        });
        subscribe("PathFollower-Tick", new IEventHandler() {
            public void process(Event msg) throws Exception {
                onPathTick(msg);
            }
        });
    }

    @Override
    protected void onFinalize(final Event message) throws Exception {

    }

    /**
     * Most of the work of the path follower is done in the
     * planning step here and in the tick update function.
     *
     * The planning begins with a start, end, and a list of waypoints.
     * Planning proceeds as follows:
     * <ul>
     * <li>Create cubic spline to represent a smooth path.
     * <li>Establish a smooth acceleration to top speed and a smooth
     *     deceleration to final speed.
     * <li>Divide path into 3 logical segments (accelerate, cruise, decelerate).
     * <li>Map the 3 segments into the distance, time, and path parameter [0-1]
     * for each segment.
     * <li>Set up timer to tick the execution.
     * </ul>
     * @param aEvent This parameter is the event which comes in from
     *               the planning system.
     */
    private void onPathPlanned(final Event aEvent) {
        ImmutableDict start = aEvent.getData().getDictionary("start");
        ImmutableDict startVel = aEvent.getData().getDictionary("start-vel");
        mCompleteNotify = aEvent.getData().getInt("complete-notify-biote");
        mLastLocation = readPoint(start);
        mLastVel = readPoint(startVel);
        ImmutableDict end = aEvent.getData().getDictionary("end");
        ImmutableDict endVel = aEvent.getData().getDictionary("end-vel");
        mGoalLocation = readPoint(end);
        mGoalVel = readPoint(endVel);
        
        ImmutableList pointList = aEvent.getData().getList("path");
        
        List<Vector3> points = new ArrayList<Vector3>();
        for (Atom a : pointList) {
            ImmutableDict point = (ImmutableDict) a;
            Vector3 p = readPoint(point);
            points.add(p);
        }
        
        mSpline = new NaturalSpline3D(points);
        
        mTimerId = startTimer(100, TICK_EVENT, false);
    }
    
    protected Vector3 readPoint(ImmutableDict aDict) {
        return new Vector3(
                aDict.getReal("x"),
                aDict.getReal("y"),
                aDict.getReal("z"));
    }
    
    private void onPathFinished(final Event aEvent) {
        // If our caller asked us to, let's notify them
        // of the path completion.
        if (mCompleteNotify != null) {
            sendStimulus(mCompleteNotify, aEvent);
        }
    }
    private void onLocationUpdated(final Event aEvent) {
        
    }
    private void onPathTick(final Event aEvent) {
        double dir = 0;
        double vel = 0;
        
        // XXX Calculate the direction and velocity
        // from our current position back to the
        // path we wish to follow.


        // Send the movement request to the mover.
        DictionaryAtom move = DictionaryAtom.newAtom();
        move.setReal("direction", dir);
        move.setReal("velocity", vel);
        Event moveRequest = new Event("Mover-MoveRequest", move);
        sendStimulus(this.mMoverBioteId, moveRequest);
    }
}
