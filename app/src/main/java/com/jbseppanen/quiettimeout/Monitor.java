package com.jbseppanen.quiettimeout;

import java.io.Serializable;

public class Monitor implements Serializable {

    public static final int NO_ID = -1;

    public static final int DEFAULT_THRESHOLD = 5000;
    private int threshold, duration, id;

    public Monitor(int id, int threshold, int duration) {
        this.id = id;
        this.threshold = threshold;
        this.duration = duration;
    }

    public Monitor() {
        this.threshold = DEFAULT_THRESHOLD;
        this.duration = 60000;
        this.id = NO_ID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


    @Override
    public String toString() {
        String outputString = id + ":" + threshold + ":" + duration;
        return outputString;
    }
}
