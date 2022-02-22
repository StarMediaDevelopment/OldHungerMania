package net.hungermania.maniacore.memory;

import net.hungermania.maniacore.api.util.ManiaUtils;

public class MemoryHook {
    private String name;
    private int lowest, highest;
    private int[] recentRuns = new int[10];
    
    public MemoryHook(String name) {
        this.name = name;
    }
    
    public void addRecentRun(int value) {
        boolean added = false;
        for (int i = 0; i < recentRuns.length; i++) {
            int recentRun = recentRuns[i];
            if (recentRun == 0) {
                recentRuns[i] = value;
                added = true;
                break;
            }
        }
        
        if (!added) {
            for (int i = 0; i < recentRuns.length; i++) {
                if (i != 0) {
                    if (i == recentRuns.length - 1) {
                        recentRuns[i] = value;
                    } else {
                        recentRuns[i - 1] = recentRuns[i];
                    }
                }
            }
        }
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getLowest() {
        return lowest;
    }
    
    public void setLowest(int lowest) {
        this.lowest = lowest;
    }
    
    public int getHighest() {
        return highest;
    }
    
    public void setHighest(int highest) {
        this.highest = highest;
    }
    
    public int[] getRecentRuns() {
        return recentRuns;
    }
    
    public void setRecentRuns(int[] recentRuns) {
        this.recentRuns = recentRuns;
    }
    
    public Task task() {
        return new Task(this);
    }
    
    public static class Task {
        MemoryHook memoryHook;
        long start = 0, end = 0;
    
        public Task(MemoryHook memoryHook) {
            this.memoryHook = memoryHook;
        }
        
        public Task start() {
            this.start = System.currentTimeMillis();
            return this;
        }
    
        public void setMemoryHook(MemoryHook memoryHook) {
            this.memoryHook = memoryHook;
        }
    
        public void setStart(long start) {
            this.start = start;
        }
    
        public void setEnd(long end) {
            this.end = end;
        }
    
        public MemoryHook getMemoryHook() {
            return memoryHook;
        }
    
        public long getStart() {
            return start;
        }
    
        public long getEnd() {
            return end;
        }
    
        public Task end() {
            this.end = System.currentTimeMillis();
            ManiaUtils.updateMemoryHook(memoryHook, start, end);
            return this;
        }
    }
}