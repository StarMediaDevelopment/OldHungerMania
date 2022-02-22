package net.hungermania.maniacore.api.leveling;

import net.hungermania.manialib.data.model.IRecord;
import net.md_5.bungee.api.ChatColor;

public class Level implements IRecord {
    private int number, totalXp, coinReward;
    private ChatColor numberColor;
    private int id;
    
    public Level(int number, int totalXp, int coinReward, ChatColor numberColor) {
        this.number = number;
        this.totalXp = totalXp;
        this.coinReward = coinReward;
        this.numberColor = numberColor;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
    
    public void setTotalXp(int totalXp) {
        this.totalXp = totalXp;
    }
    
    public void setCoinReward(int coinReward) {
        this.coinReward = coinReward;
    }
    
    public void setNumberColor(ChatColor numberColor) {
        this.numberColor = numberColor;
    }
    
    public int getNumber() {
        return number;
    }
    
    public int getTotalXp() {
        return totalXp;
    }
    
    public int getCoinReward() {
        return coinReward;
    }
    
    public ChatColor getNumberColor() {
        return numberColor;
    }
    
    public Level(int number, int totalXp, int coinReward, ChatColor numberColor, int id) {
        this.number = number;
        this.totalXp = totalXp;
        this.coinReward = coinReward;
        this.numberColor = numberColor;
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
}