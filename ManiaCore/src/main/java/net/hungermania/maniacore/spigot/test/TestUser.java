package net.hungermania.maniacore.spigot.test;

import lombok.Getter;
import lombok.Setter;
import net.hungermania.manialib.data.annotations.ColumnInfo;
import net.hungermania.manialib.data.annotations.TableInfo;
import net.hungermania.manialib.data.model.IRecord;

import java.util.UUID;

@TableInfo(tableName = "testusers")
@Setter
@Getter
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
