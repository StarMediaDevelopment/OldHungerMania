package net.hungermania.maniacore.api.leveling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

@Getter @Setter @AllArgsConstructor @Builder
public class Level {
    private int number, totalXp, coinReward;
    private ChatColor numberColor;
}