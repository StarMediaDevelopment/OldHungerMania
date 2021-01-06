package net.hungermania.maniacore.api.util;

import net.hungermania.maniacore.spigot.updater.UpdateType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

//TODO 
/*
Example
startGameTimer(45000L, new ReturnableCallback<TimerSnapshot, Boolean>() {
								@Override
								public Boolean callback(TimerSnapshot timer){
									int players = getVisiblePlayers().size();
									if(timer.getUpdatingNow().contains(UpdateType.TICK) && !vipOverride){
										if(players >= game.getMinPlayers() && !timer.isRunning()){
											timer.reset();
											timer.run();
											sendMessage(MsgType.SUCCESS +"Minimum player requirement met. Game starting shortly...");
											playSound(Sound.LEVEL_UP, 1.0F, 1.0F);
										}
										else if(players < game.getMinPlayers()){
											//timer.reset(); // constantly reset timer so it still looks like 45 seconds
											if(timer.isRunning()){
												timer.setPaused(true);	 // so that it isn't marked running
												sendMessage(MsgType.INFO + "Not enough players to meet the " + game.getMinPlayers() + "-player minimum.");
												playSound(Sound.NOTE_BASS, 1, 1);
											}
										}
									}
									if(timer.isRunning() && timer.getUpdatingNow().contains(UpdateType.SECOND)){
										int time = timer.getSecondsLeft();
										if (time <= 0) {
											if(map == null || !map.isLoading()){
												selectMap();
												return false;
											}
										}
										if((time % 15 == 0 && time >= 15) || (time % 5 == 0 && time < 15 && time >= 5) || (time < 5 && time > 0)){
											if(map == null)
												sendMessage(MsgType.INFO + "§lVoting closes in §f§l" + timer.getSecondsLeft() + "s§e§l.");
											else
												sendMessage(MsgType.INFO + "§lGame starts in §f§l" + timer.getSecondsLeft() + "s§e§l.");
											playSound(Sound.CLICK, 1, 1);
										}
										if(time <= 5)
											for(NexusPlayer player : playerManager.getPlayers())
												if(getInventory(player) != null && getInventory(player) instanceof VIPPanel)
													player.closeInventory();
									}
									return true;
								}
							});
							logInfo("Lobby started");
 */


public class Timer {
    private static List<TimerSnapshot> queuedTasks = new ArrayList<>();
    private static List<Timer> timers = new ArrayList<>();
    public Map<UpdateType, Long> lastUpdates = new HashMap<>();
    private ReturnableCallback<TimerSnapshot, Boolean> callback;
    private boolean cancelled = false;
    private long length = -1;
    private long paused = -1;
    private boolean running = false;
    private long time;
    
    public Timer(ReturnableCallback<TimerSnapshot, Boolean> callback) {
        this.callback = callback;
        timers.add(this);
    }
    
    public static void startTimerUpdater(JavaPlugin plugin) {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public synchronized void run() {
                Iterator<Timer> iterator = timers.iterator();
                while (iterator.hasNext()) {
                    Timer timer = iterator.next();
                    if (timer.isCancelled()) {
                        iterator.remove();
                        continue;
                    }
                    
                    List<UpdateType> types = new ArrayList<>();
                    for (UpdateType type : UpdateType.values()) {
                        long last = timer.lastUpdates.getOrDefault(type, 0L);
                        if (System.currentTimeMillis() - last >= type.getLength()) {
                            timer.lastUpdates.put(type, System.currentTimeMillis());
                            types.add(type);
                        }
                    }
                    timer.count();
                    
                    queuedTasks.add(new TimerSnapshot(timer, timer.time, types.toArray(new UpdateType[0])));
                }
            }
        }, 0L, 1L);
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public synchronized void run() {
                for (TimerSnapshot snapshot : queuedTasks) {
                    if (snapshot != null && snapshot.getTimer() != null && !snapshot.getTimer().callback.callback(snapshot)) {
                        timers.remove(snapshot.getTimer());
                    }
                }
                
                queuedTasks.clear();
            }
        }, 1L, 2L);
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public void count() {
        if (!running) { return; }
        time -= 50;
    }
    
    public static String formatTime(int time) {
        final int minutes = time / 60;
        time -= minutes * 60;
        String s = "";
        if (minutes < 10) {
            s = s + "0" + minutes + "m";
        } else if (minutes >= 10) {
            s = s + minutes + "m";
        }
        if (time < 10 && time >= 0) {
            s = s + "0" + time + "s";
        } else if (time >= 10) {
            s = s + time + "s";
        }
        return s;
    }
    
    public static String formatLongerTime(int time) {
        int minutes = (time / 60);
        time -= minutes * 60;
        int hours = (minutes / 60);
        minutes -= hours * 60;
        return (hours > 0 ? (hours < 10 ? "0" : "") + hours + "h" : "") + (minutes < 10 ? "0" : "") + minutes + "m" + (time < 10 ? "0" : "") + time + "s";
    }
    
    public static String formatTimeShort(long seconds) {
        long mins = seconds / 60L;
        long secs = seconds % 60L;
        String text = "";
        if (mins > 0L) {
            text = text + mins + "m";
        }
        if (secs > 0L) {
            text = text + secs + "s";
        }
        return text;
    }
    
    public long reset() {
        return setLength(length);
    }
    
    public long setLength(long l) {
        this.length = l;
        this.time = l;
        return l;
    }
    
    public void setPaused(boolean paused) {
        if (!paused) {
            setLength(getTimeLeft());
            this.paused = -1;
        } else { this.paused = System.currentTimeMillis(); }
        running = !paused;
    }
    
    public long getTimeLeft() {
        return time;
    }
    
    public Timer run(long length) {
        setLength(length);
        run();
        return this;
    }
    
    public Timer run() {
        if (length < 0) { throw new NullPointerException("There is no length for the timer"); }
        running = true;
        return this;
    }
    
    public int getSecondsElapsed() {
        return toSeconds(getTimeElapsed());
    }
    
    public static int toSeconds(long milliseconds) {
        return (int) Math.floor(milliseconds / 1000.0);
    }
    
    public long getTimeElapsed() {
        return length - time;
    }
    
    public boolean hasElapsed() {
        return hasElapsed(this.length);
    }
    
    public boolean hasElapsed(long length) {
        return getTimeElapsed() >= length;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public int getSecondsLeft() {
        return toSeconds(getTimeLeft());
    }
    
    public long getLength() {
        return length;
    }
    
    public void cancel() {
        this.cancelled = true;
    }
}
