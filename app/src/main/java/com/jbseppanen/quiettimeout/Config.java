package com.jbseppanen.quiettimeout;

public class Config {

    private NoiseMonitor noiseMonitor;
    private Timer timer;

    public Config(NoiseMonitor noiseMonitor, Timer timer) {
        this.noiseMonitor = noiseMonitor;
        this.timer = timer;
    }


    public NoiseMonitor getNoiseMonitor() {
        return noiseMonitor;
    }

    public void setNoiseMonitor(NoiseMonitor noiseMonitor) {
        this.noiseMonitor = noiseMonitor;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
}
