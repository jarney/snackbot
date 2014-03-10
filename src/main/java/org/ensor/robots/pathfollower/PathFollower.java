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
import org.ensor.math.analysis.IFunction;
import org.ensor.math.analysis.LinearFunction;
import org.ensor.math.analysis.QuadraticFunction;
import org.ensor.math.geometry.Vector3;
import org.ensor.threads.biote.Biote;
import org.ensor.threads.biote.BioteManager;
import org.ensor.threads.biote.Event;
import org.ensor.threads.biote.IEventHandler;

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
 *
 * The objective of following the path smoothly
 * is to begin at our initial velocity v0 and accelerate
 * at acceleration parameter 'a' up to final cruising speed 'vc'
 * and hold that speed until it is time to decelerate down to the final
 * velocity v1.
 * <pre>
 *          vc
 *    /-----------------\
 *   /                   \
 *  /                     \
 * / v0                    \ v1
 *
 * </pre>
 *
 * Planning for this begins as follows:
 * <ul>
 * <li>Calculate the cubic spline between the waypoints and from that, the
 *     overall path length.
 * <li>Calculate the time we spend accelerating and decelerating.
 * <li>Calculate the distance we spend accelerating and decelerating.
 * <li>Obtain the total path length and subtract the distance of acceleration
 *     to obtain the distance we spend at cruise speed.
 * <li>Divide by cruise velocity to find the cruise time.
 * <li>Add up all of the times to determine the total time to traverse the
 *     path.
 * <li>Create a piecewise linear function mapping time 't' into distance
 *     traveled: d = f(t).
 * <li>Create a piecewise linear function mapping time 't' into the
 *     velocity we should be at right now.
 * <li>Divide this piecewise function by the total distance to travel to obtain
 *     a piecewise function mapping time (t) into the path interval [0-1].
 * <li>During the 'tick' function, we can now simply evaluate the time elapsed
 *     to obtain an estimate of where we 'should' be.
 * <li>We then plot a course based on where we 'should' be for the next tick
 *     and where we are now.
 * </ul>
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
    /// The last time we received a location update (in ms).
    private Integer mLastLocationTime;
    
    private Integer mPathStartTime;
    

    /// The goal location and velocity.
    private Vector3 mGoalLocation;
    private Vector3 mGoalVel;

    private IFunction mDistanceFunction;

    /// The biote to notify when the path
    // has been completed.
    private Integer mCompleteNotify;
    
    private NaturalSpline3D mSpline;
    
    // Roughly one tenth of a second.
    private static final int TICK_DURATION_MILLISECONDS = 100;
    private static final double MILLISECONDS_PER_SECOND = 1000.0;
    private static final double TICK_DURATION_SECONDS =
                            (double) TICK_DURATION_MILLISECONDS /
                                     MILLISECONDS_PER_SECOND;
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
        
        // Input parameters
        // of the system, these are properties
        // of the robot as the maximum acceleration and
        // maximum deceleration, and maximum cruising speed.
        double mAccel = 1;
        double mDecel = 1;
        double mVMax = 1;
        
        // Starting and ending velocity.
        double v0 = mLastVel.length();
        double v1 = mGoalVel.length();
        
        mSpline = NaturalSpline3D.createInterpolators(points);
        
        // Calculate the cubic spline between the waypoints and from that, the
        // overall path length.
        double dPath = mSpline.getLength();
        
        // Calculate the time we spend accelerating and decelerating.
        double tAccel = (mVMax - v0) / mAccel;
        double tDecel = (mVMax - v1) / mDecel;

        // Calculate the distance we spend accelerating and decelerating.
        double dAccel = v0 * tAccel + mAccel * tAccel * tAccel / 2;
        double dDecel = v0 * tDecel + mDecel * tDecel * tDecel / 2;
        
        double dCruising = dPath - dAccel - dDecel;
        
        // If the total cruising distance is negative, then this means
        // we don't reach top speed before decelerating.  Therefore
        // we need to adjust our times and distances.
        if (dCruising < 0) {
            dCruising = 0;
            // XXX: TODO: Recalculate tAccel and tDecel because we're
            //            in this region.
        }

        // d = d0 + v0t + v(t)*t;
        // d = d0 + v0t + at^2

        // Accelerating from 0 to tAccel
        QuadraticFunction distance1 = new QuadraticFunction(0, v0, mAccel);

        LinearFunction distance2 = new LinearFunction(dAccel, mVMax);
        
        // Decelerating from tCruise to tCruise + tDecel
        QuadraticFunction distance3 = new QuadraticFunction(dCruising, v0, mAccel);

        // Compose these together.
        mDistanceFunction = distance3;
        
        mTimerId = startTimer(TICK_DURATION_MILLISECONDS, TICK_EVENT, false);
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
        
        // Obtain the amount of time elapsed along the curve.
        double relativeTime = getElapsed();
        // From this, obtain the position along the spline corresponding
        // to the time we expect the next tick to occur.
        double dNext = mDistanceFunction.getValue(
                relativeTime + TICK_DURATION_SECONDS);

        // From the spline, this is the location
        // of where we expect to be for the next tick.
        Vector3 nextLocation = mSpline.getPosition(dNext);

        // This is the amount of distance
        // we need to cover for the next tick.
        Vector3 direction = nextLocation.subtract(mLastLocation);
        
        double dir = 0;
        double vel = 0;

        // If the distance we need to travel is small enough, we
        // leave the velocity and direction zero.
        double directionLength = direction.length();
        if (directionLength >= 0) {
            // Therefore, this is the velocity
            // we need to get.
            vel = directionLength / TICK_DURATION_SECONDS;

            // Normalize the vector to 1.
            direction = direction.multiply(1 / directionLength);

            // This is the angle we need to head.
            dir = Math.atan2(direction.getX(), direction.getY());
        }
        
        // Send the movement request to the mover.
        // Velocity is absolute while
        // direction is relative to our present direction.
        DictionaryAtom move = DictionaryAtom.newAtom();
        move.setReal("direction", dir);
        move.setReal("velocity", vel);
        Event moveRequest = new Event("Mover-MoveRequest", move);
        sendStimulus(this.mMoverBioteId, moveRequest);
    }
    /**
     * Returns the amount of time which has elapsed since we began following
     * the path.
     * @return The number of seconds elapsed since we began following
     *         the path.
     */
    private double getElapsed() {
        long tms = System.currentTimeMillis() - mPathStartTime;
        return (double) tms / MILLISECONDS_PER_SECOND;
    }
}
