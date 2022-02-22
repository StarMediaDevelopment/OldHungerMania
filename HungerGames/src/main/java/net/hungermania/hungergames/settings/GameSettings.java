package net.hungermania.hungergames.settings;

import net.hungermania.manialib.data.model.IRecord;

/**
 * This controls default settings that all games will use. It is loaded from the database on each restart
 * Each game will store the settings for that game and not global
 * To change global settings, commands must be used or modify the database
 */


public class GameSettings implements Cloneable, IRecord {
    private int id;
    private String name = "default";
    
    private int maxPlayers = 24;
    private int minPlayers = 6;
    private int startTimer = 45;
    private int maxHealth = 20;
    private int gracePeriodLength = 60;
    private int maxGames = 5;
    private int gameLength = 20;
    private int maxMapOptions = 5;
    private int deathmatchLength = 5;
    private int startingCountdown = 30;
    private int deathmatchPlayerStart = 4;
    private int pearlCooldown = 5;
    private int mutationDelay = 2;
    private int nextGameStart = 10;
    private boolean mutations = true;
    private boolean regeneration = true;
    private boolean gracePeriod = true;
    private boolean unlimitedPasses = false;
    private boolean timeProgression = false;
    private boolean weatherProgression = false;
    private boolean coinMultiplier = true;
    private boolean voteWeight = true;
    private Time time = Time.NOON;
    private Weather weather = Weather.CLEAR;
    
    public GameSettings() { }
    
    public GameSettings(int id, String name, int maxPlayers, int minPlayers, int startTimer, int maxHealth, int gracePeriodLength, int maxGames, int gameLength, int maxMapOptions, int deathmatchLength, int startingCountdown, int deathmatchPlayerStart, int pearlCooldown, int mutationDelay, int nextGameStart, boolean mutations, boolean regeneration, boolean gracePeriod, boolean unlimitedPasses, boolean timeProgression, boolean weatherProgression, boolean coinMultiplier, boolean voteWeight, Time time, Weather weather) {
        this.id = id;
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.startTimer = startTimer;
        this.maxHealth = maxHealth;
        this.gracePeriodLength = gracePeriodLength;
        this.maxGames = maxGames;
        this.gameLength = gameLength;
        this.maxMapOptions = maxMapOptions;
        this.deathmatchLength = deathmatchLength;
        this.mutations = mutations;
        this.regeneration = regeneration;
        this.gracePeriod = gracePeriod;
        this.unlimitedPasses = unlimitedPasses;
        this.timeProgression = timeProgression;
        this.weatherProgression = weatherProgression;
        this.time = time;
        this.weather = weather;
        this.startingCountdown = startingCountdown;
        this.deathmatchPlayerStart = deathmatchPlayerStart;
        this.pearlCooldown = pearlCooldown;
        this.mutationDelay = mutationDelay;
        this.nextGameStart = nextGameStart;
        this.voteWeight = voteWeight;
        this.coinMultiplier = coinMultiplier;
    }
    
    @Override
    public int getId() {
        return id;
    }
    
    @Override
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    
    public int getMinPlayers() {
        return minPlayers;
    }
    
    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }
    
    public int getStartTimer() {
        return startTimer;
    }
    
    public void setStartTimer(int startTimer) {
        this.startTimer = startTimer;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
    
    public int getGracePeriodLength() {
        return gracePeriodLength;
    }
    
    public void setGracePeriodLength(int gracePeriodLength) {
        this.gracePeriodLength = gracePeriodLength;
    }
    
    public int getMaxGames() {
        return maxGames;
    }
    
    public void setMaxGames(int maxGames) {
        this.maxGames = maxGames;
    }
    
    public int getGameLength() {
        return gameLength;
    }
    
    public void setGameLength(int gameLength) {
        this.gameLength = gameLength;
    }
    
    public int getMaxMapOptions() {
        return maxMapOptions;
    }
    
    public void setMaxMapOptions(int maxMapOptions) {
        this.maxMapOptions = maxMapOptions;
    }
    
    public int getDeathmatchLength() {
        return deathmatchLength;
    }
    
    public void setDeathmatchLength(int deathmatchLength) {
        this.deathmatchLength = deathmatchLength;
    }
    
    public int getStartingCountdown() {
        return startingCountdown;
    }
    
    public void setStartingCountdown(int startingCountdown) {
        this.startingCountdown = startingCountdown;
    }
    
    public int getDeathmatchPlayerStart() {
        return deathmatchPlayerStart;
    }
    
    public void setDeathmatchPlayerStart(int deathmatchPlayerStart) {
        this.deathmatchPlayerStart = deathmatchPlayerStart;
    }
    
    public int getPearlCooldown() {
        return pearlCooldown;
    }
    
    public void setPearlCooldown(int pearlCooldown) {
        this.pearlCooldown = pearlCooldown;
    }
    
    public int getMutationDelay() {
        return mutationDelay;
    }
    
    public void setMutationDelay(int mutationDelay) {
        this.mutationDelay = mutationDelay;
    }
    
    public int getNextGameStart() {
        return nextGameStart;
    }
    
    public void setNextGameStart(int nextGameStart) {
        this.nextGameStart = nextGameStart;
    }
    
    public boolean isMutations() {
        return mutations;
    }
    
    public void setMutations(boolean mutations) {
        this.mutations = mutations;
    }
    
    public boolean isRegeneration() {
        return regeneration;
    }
    
    public void setRegeneration(boolean regeneration) {
        this.regeneration = regeneration;
    }
    
    public boolean isGracePeriod() {
        return gracePeriod;
    }
    
    public void setGracePeriod(boolean gracePeriod) {
        this.gracePeriod = gracePeriod;
    }
    
    public boolean isUnlimitedPasses() {
        return unlimitedPasses;
    }
    
    public void setUnlimitedPasses(boolean unlimitedPasses) {
        this.unlimitedPasses = unlimitedPasses;
    }
    
    public boolean isTimeProgression() {
        return timeProgression;
    }
    
    public void setTimeProgression(boolean timeProgression) {
        this.timeProgression = timeProgression;
    }
    
    public boolean isWeatherProgression() {
        return weatherProgression;
    }
    
    public void setWeatherProgression(boolean weatherProgression) {
        this.weatherProgression = weatherProgression;
    }
    
    public boolean isCoinMultiplier() {
        return coinMultiplier;
    }
    
    public void setCoinMultiplier(boolean coinMultiplier) {
        this.coinMultiplier = coinMultiplier;
    }
    
    public boolean isVoteWeight() {
        return voteWeight;
    }
    
    public void setVoteWeight(boolean voteWeight) {
        this.voteWeight = voteWeight;
    }
    
    public Time getTime() {
        return time;
    }
    
    public void setTime(Time time) {
        this.time = time;
    }
    
    public Weather getWeather() {
        return weather;
    }
    
    public void setWeather(Weather weather) {
        this.weather = weather;
    }
    
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public GameSettings clone() {
        return new GameSettings(this.id, this.name, this.maxPlayers, this.minPlayers, this.startTimer, this.maxHealth, this.gracePeriodLength, this.maxGames, this.gameLength, this.maxMapOptions, this.deathmatchLength, this.startingCountdown, this.deathmatchPlayerStart, this.pearlCooldown, this.mutationDelay, this.nextGameStart, this.mutations, this.regeneration, this.gracePeriod, this.unlimitedPasses, this.timeProgression, this.weatherProgression, coinMultiplier, voteWeight, this.time, this.weather);
    }
}