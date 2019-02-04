package com.jbseppanen.quiettimeout;

public class Timer {

    private int duration, timeLeft, startTime;

    public Timer(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
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
}
