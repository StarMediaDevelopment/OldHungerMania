package net.hungermania.maniacore.api.leveling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.hungermania.manialib.data.model.IRecord;
import net.md_5.bungee.api.ChatColor;

@Getter @Setter @AllArgsConstructor @Builder
public class Level implements IRecord {
    private int number, totalXp, coinReward;
    private ChatColor numberColor;
    private int id;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
}