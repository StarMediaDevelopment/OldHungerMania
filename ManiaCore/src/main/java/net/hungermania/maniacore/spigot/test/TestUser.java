package net.hungermania.maniacore.spigot.test;

import net.hungermania.manialib.data.annotations.ColumnInfo;
import net.hungermania.manialib.data.annotations.TableInfo;
import net.hungermania.manialib.data.model.IRecord;

import java.util.UUID;

@TableInfo(tableName = "testusers")
public class TestUser implements IRecord {
    private int id;
    private UUID uuid;
    @ColumnInfo(length = 16) private String name;
    private boolean incognito;
    private long lastLogin;
    private double coins;

    private TestUser() {}
    
    public TestUser(UUID uuid, String name, boolean incognito, long lastLogin, double coins) {
        this.uuid = uuid;
        this.name = name;
        this.incognito = incognito;
        this.lastLogin = lastLogin;
        this.coins = coins;
    }

    public int getId() {
        return this.id;
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

    public String toString() {
        return "TestUser{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", name='" + name + '\'' +
                ", incognito=" + incognito +
                ", lastLogin=" + lastLogin +
                ", coins=" + coins +
                '}';
    }
}
