/*
 * The MIT License
 *
 * Copyright 2015 Jon Arney, Ensor Robotics.
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

package org.ensor.robots.differentialdrive;

import org.ensor.algorithms.control.pid.IMover;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.ensor.algorithms.control.pid.IController;
import org.ensor.algorithms.control.pid.ISensor;

/**
 *
 * @author jona
 */
public class MoverComposite
    implements IMover<Position, SpeedAndTurnRate> {

    private final List<IMover<Position, SpeedAndTurnRate>> mList;
    private int mCurrent = 0;
    
    /**
     * This class represents a composite set of movers
     * each with its own control set and goal reached tolerance.
     * This mover allows each of the controllers to act in turn.
     * The goal of this controller is reached when each of the
     * controllers is reached in sequence.
     */
    public MoverComposite() {
        mList = new ArrayList<IMover<Position, SpeedAndTurnRate>>();
        mCurrent = 0;
    }
    
    /**
     * This class represents a composite mover consisting of a single mover.
     * @param aMover The single mover to proxy.
     */
    public MoverComposite(final IMover aMover) {
        this();
        mList.add(aMover);
    }

    /**
     * This constructor creates a mover from the list of given movers.
     * @param aList The list of movers to execute in sequence.
     */
    public MoverComposite(final List<IMover<Position, SpeedAndTurnRate>> aList) {
        this();
        mList.addAll(aList);
    }
    /**
     * This constructor creates a mover from the list of given movers.
     * @param aList The list of movers to execute in sequence.
     */
    public MoverComposite(final IMover<Position, SpeedAndTurnRate>[] aList) {
        this(Arrays.asList(aList));
    }
    
    /**
     * This method performs the movement simulation for the current mover.
     * When that mover is complete, it moves to the next mover in the list,
     * executing each of them until the overall goal of all of the movers
     * is reached.
     * @param aTimeElapsed The time elapsed since the last simulation.
     * @param aSensor The sensor returning the current position.
     * @param aController The controller allowing control of speed and
     *                    turn rate.
     */
    public void tick(
            final double aTimeElapsed,
            final ISensor<Position> aSensor,
            final IController<SpeedAndTurnRate> aController) {
        if (mCurrent >= mList.size()) {
            return;
        }
        
        IMover currentController =
                mList.get(mCurrent);
        currentController.tick(aTimeElapsed, aSensor, aController);
        if (currentController.goalReached()) {
            mCurrent++;
        }
        
    }

    /**
     * This method returns true when all of the individual goals for the
     * individual movers has been reached.
     * @return True when all of the goals have been reached sequentially.
     */
    public boolean goalReached() {
        return (mCurrent >= mList.size());
    }
    
    
    
    
}
