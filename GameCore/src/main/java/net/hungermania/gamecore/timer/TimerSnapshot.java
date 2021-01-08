package net.hungermania.gamecore.timer;

import net.hungermania.maniacore.spigot.updater.UpdateType;

import java.util.Arrays;
import java.util.List;

public class TimerSnapshot {
    private long time;
    private Timer timer;
    private List<UpdateType> updatingNow;
    
    public TimerSnapshot(Timer timer, long time, UpdateType... updatingNow) {
        this.timer = timer;
        this.time = time;
        this.updatingNow = Arrays.asList(updatingNow);
    }
    
    public Timer getTimer() {
        return timer;
    }
    
    public List<UpdateType> getUpdatingNow() {
        return updatingNow;
    }
    
    public long reset() {
        return setLength(timer.getLength());
    }
    
    public long setLength(long l) {
        return timer.setLength(l);
    }
    
    public void setPaused(boolean paused) {
        timer.setPaused(paused);
    }
    
    public int getSecondsElapsed() {
        return Timer.toSeconds(getTimeElapsed());
    }
    
    public long getTimeElapsed() {
        return timer.getLength() - time;
    }
    
    public boolean hasElapsed() {
        return hasElapsed(timer.getLength());
    }
    
    public boolean hasElapsed(long length) {
        return getTimeElapsed() >= length;
    }
    
    public boolean isRunning() {
        return timer.isRunning();
    }
    
    public int getSecondsLeft() {
        return Timer.toSeconds(getTimeLeft());
    }
    
    public long getTimeLeft() {
        return time;
    }
    
    public void run() {
        timer.run();
    }
    
    public void run(long length) {
        timer.run(length);
    }
}
