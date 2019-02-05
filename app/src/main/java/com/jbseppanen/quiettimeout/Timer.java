package com.jbseppanen.quiettimeout;

import android.os.CountDownTimer;

public class Timer {

    public static final int COUNT_DOWN_INTERVAL = 1000;
    private int timeLeft, startTime;
    private long duration;
    private CountDownTimer countDownTimer;

    public Timer(long duration, CountDownTimer countDownTimer) {
        this.duration = duration;
        this.countDownTimer = countDownTimer;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime() {
        this.startTime = (int) (System.currentTimeMillis() / 1000);
    }

    public CountDownTimer getCountDownTimer() {
        return countDownTimer;
    }

    public void setCountDownTimer(CountDownTimer countDownTimer) {
        this.countDownTimer = countDownTimer;
    }
}
