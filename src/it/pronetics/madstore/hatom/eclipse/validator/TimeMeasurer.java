/*
 * ---------------------------------------------------------------------------------
 * TimeMeasurer.java - History of changes
 * ---------------------------------------------------------------------------------
 * 15/10/2008 - 1.0: First implementation.
 */

package it.pronetics.madstore.hatom.eclipse.validator;

/**
 * Simple class that measures a time interval.<br>
 * @author Andrea Castello
 * @version 1.0
 */
public class TimeMeasurer {

    // Time of measure start in milliseconds
    private long startTime;

    /**
     * Creates a new instance of TimeMeasurer that sets the measure start time in the moment of object
     * creation.<br>
     */
    public TimeMeasurer() {
        startTime = System.currentTimeMillis();
    }

    /**
     * @return the measure' start time in milliseconds
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Sets the measure' start time, overwriting the start time set at object creation time.<br>
     * @param startTime the measure' start time
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns the amount passed between the measure start time and the moment of this method invocation.<br>
     * @return the measure's elapsed time.<br>
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }
}