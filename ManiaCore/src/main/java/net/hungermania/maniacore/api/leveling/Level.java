package net.hungermania.maniacore.api.leveling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.hungermania.manialib.data.model.IRecord;
import net.md_5.bungee.api.ChatColor;

@Getter
@Setter
@AllArgsConstructor
@Builder
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

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
}