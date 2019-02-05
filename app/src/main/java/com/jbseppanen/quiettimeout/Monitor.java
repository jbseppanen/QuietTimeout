package com.jbseppanen.quiettimeout;

import java.io.Serializable;

public class Monitor implements Serializable {

    public static final int DEFAULT_THRESHOLD = 1000;
    private int threshold, duration;

    public Monitor(int threshold, int duration) {
        this.threshold = threshold;
        this.duration = duration;
    }

    public Monitor() {
        this.threshold = DEFAULT_THRESHOLD;
        this.duration = 0;
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
}
