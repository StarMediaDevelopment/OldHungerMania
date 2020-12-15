package net.hungermania.maniacore.spigot.updater;

public enum UpdateType {
	TICK(50L),
	QUARTER_SEC(50L*5L),
	SECOND(50L*20L);

	private final long length;
	private long lastRun = 0;

	UpdateType(long length){
		this.length = length;
	}

	public long getLength(){
		return length;
	}

	public boolean run(){
		if((System.currentTimeMillis() - lastRun) > length){
			lastRun = System.currentTimeMillis();
			return true;
		}
		return false;
	}

	public long getLastRun(){
		return lastRun;
	}}
