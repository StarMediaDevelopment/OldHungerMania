package net.hungermania.maniacore.memory;

import lombok.Getter;
import lombok.Setter;
import net.hungermania.maniacore.api.util.ManiaUtils;

@Getter @Setter
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
    
    public Task task() {
        return new Task(this);
    }
    
    public static class Task {
        MemoryHook memoryHook;
        @Getter long start = 0, end = 0;
    
        public Task(MemoryHook memoryHook) {
            this.memoryHook = memoryHook;
        }
        
        public Task start() {
            this.start = System.currentTimeMillis();
            return this;
        }
        
        public Task end() {
            this.end = System.currentTimeMillis();
            ManiaUtils.updateMemoryHook(memoryHook, start, end);
            return this;
        }
    }
}