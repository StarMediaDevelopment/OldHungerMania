package net.hungermania.maniacore.spigot.test.old;

import java.util.UUID;

public class OldTestUser {
    private int id;
    private UUID uuid;
    private String name;
    private boolean incognito;
    private long lastLogin;
    private double coins;

    public OldTestUser(int id, UUID uuid, String name, boolean incognito, long lastLogin, double coins) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.incognito = incognito;
        this.lastLogin = lastLogin;
        this.coins = coins;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIncognito() {
        return incognito;
    }

    public void setIncognito(boolean incognito) {
        this.incognito = incognito;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public double getCoins() {
        return coins;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }
}
