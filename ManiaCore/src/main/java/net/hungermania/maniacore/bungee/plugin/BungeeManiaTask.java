package net.hungermania.maniacore.bungee.plugin;

import net.hungermania.maniacore.plugin.ManiaTask;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class BungeeManiaTask implements ManiaTask {
    
    private ScheduledTask task;
    
    public BungeeManiaTask(ScheduledTask task) {
        this.task = task;
    }
    
    public void cancel() {
        task.cancel();
    }
}
