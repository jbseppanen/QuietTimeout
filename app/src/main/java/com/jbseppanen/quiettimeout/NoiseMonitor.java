package com.jbseppanen.quiettimeout;

public class NoiseMonitor {

    public static final int DEFAULT_THRESHOLD = 1000;
    private int threshold;

    public NoiseMonitor(int threshold) {
        this.threshold = threshold;
    }

    public NoiseMonitor() {
        this.threshold = DEFAULT_THRESHOLD;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
