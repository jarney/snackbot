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
 * Constant values mainly for the purpose of enabling and disabling
 * logging functionality.
 * @author Jon
 */
public final class Constants {

    private Constants() { }
    
    /**
     * The ID of the 'init' or 'bootstrap' Biote.
     */
    public static final int BIOTE_INIT = 1;

    /**
     * The ID of the first Biote to be added to the Biote
     * manager after the 'init' Biote.
     */
    public static final int BIOTE_FIRST_GENERATED_ID = 2;

    // Logging constants
    public static final boolean LOG_ALL_CATEGORIES = false;

    public static final boolean LOG_BIOTE_MANAGER = false || LOG_ALL_CATEGORIES;
    public static final boolean LOG_BIOTE_CORE = false || LOG_ALL_CATEGORIES;
    public static final boolean LOG_BOOLEAN_SET = true || LOG_ALL_CATEGORIES;
    public static final boolean LOG_INIT_BIOTE = false || LOG_ALL_CATEGORIES;
    public static final boolean LOG_ERROR = true;

    // Biote type ids
    public static final int BIOTE_TYPE_INIT             = 1;
    public static final int BIOTE_TYPE_DATABASE         = 2;
    public static final int BIOTE_TYPE_GAME_FIRST       = 3;

    // Timing info
    public static final float LONG_EVENT_SECONDS        = 5;

}
