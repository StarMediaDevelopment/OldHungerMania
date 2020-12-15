package net.hungermania.hungergames.settings;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This controls default settings that all games will use. It is loaded from the database on each restart
 * Each game will store the settings for that game and not global
 * To change global settings, commands must be used or modify the database
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
public class GameSettings implements Cloneable {
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
    
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public GameSettings clone() {
        return new GameSettings(this.id, this.name, this.maxPlayers, this.minPlayers, this.startTimer, this.maxHealth, this.gracePeriodLength, this.maxGames, this.gameLength, this.maxMapOptions, this.deathmatchLength, this.startingCountdown, this.deathmatchPlayerStart, this.pearlCooldown, this.mutationDelay, this.nextGameStart, this.mutations, this.regeneration, this.gracePeriod, this.unlimitedPasses, this.timeProgression, this.weatherProgression, coinMultiplier, voteWeight, this.time, this.weather);
    }
}