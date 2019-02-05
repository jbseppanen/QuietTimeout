package com.jbseppanen.quiettimeout;

import android.os.CountDownTimer;

public class Monitor {

    public static final int DEFAULT_THRESHOLD = 1000;
    private int threshold;
    private CountDownTimer countDownTimer;

    public Monitor(int threshold, CountDownTimer countDownTimer) {
        this.threshold = threshold;
        this.countDownTimer = countDownTimer;
    }

    public Monitor() {
        this.threshold = DEFAULT_THRESHOLD;
        this.countDownTimer = null;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public CountDownTimer getCountDownTimer() {
        return countDownTimer;
    }

    public void setCountDownTimer(CountDownTimer countDownTimer) {
        this.countDownTimer = countDownTimer;
    }
}
