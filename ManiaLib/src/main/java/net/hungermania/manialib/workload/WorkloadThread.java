package net.hungermania.manialib.workload;

import java.util.ArrayDeque;

public class WorkloadThread implements Runnable {
    
    private static final int MAX_MS_PER_TICK = 10;
    private final ArrayDeque<Workload> workloadDeque;
    
    public WorkloadThread() {
        workloadDeque = new ArrayDeque<>();
    }
    
    public void addLoad(Workload workload) {
        this.workloadDeque.add(workload);
    }
    
    @Override
    public void run() {
        long stopTime = System.currentTimeMillis() + MAX_MS_PER_TICK;
        while (!workloadDeque.isEmpty() && System.currentTimeMillis() <= stopTime) {
            workloadDeque.poll().compute();
        }
    }
}