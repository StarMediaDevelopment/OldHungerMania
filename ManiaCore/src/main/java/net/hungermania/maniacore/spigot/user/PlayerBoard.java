package net.hungermania.maniacore.spigot.user;

import net.hungermania.maniacore.api.util.Utils;
import net.hungermania.maniacore.spigot.util.ScoreboardBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.SortedMap;
import java.util.TreeMap;

public class PlayerBoard {
    
    private String name;
    private Scoreboard scoreboard;
    private Objective objective;
    private SortedMap<Integer, Team> teams = new TreeMap<>();
    private int scoreIndex = 15;
    
    public PlayerBoard(String name) {
        this.name = name;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = this.scoreboard.registerNewObjective(Utils.color(name), "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    
    public static ScoreboardBuilder start(String name) {
        return new ScoreboardBuilder(name);
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int addLine(String prefix, String main, String suffix) {
        int position;
        if (this.teams.isEmpty()) {
            position = 0;
        } else {
            position = this.teams.lastKey() + 1;
        }
        
//        String entryName = "";
//
//        color:
//        for (ChatColor color : ChatColor.values()) {
//            for (Team team : this.teams.values()) {
//                if (team.getEntries().contains(color.toString())) {
//                    continue color;
//                }
//            }
//
//            entryName = color.toString();
//            break;
//        }
    
        Team team = this.scoreboard.registerNewTeam(main);
        team.addEntry(main);
        team.setPrefix(prefix);
        team.setSuffix(suffix);
        objective.getScore(team.getName()).setScore(scoreIndex);
        scoreIndex--;
        this.teams.put(position, team);
        return position;
    }
    
    public void setLine(int line, String text) {
        Team team = this.teams.get(line);
        if (team == null) {
            return;
        }
        
        team.setSuffix(ChatColor.translateAlternateColorCodes('&', text));
    }
    
    public String getName() {
        return name;
    }
    
    public void send(Player player) {
        if (player != null) {
            player.setScoreboard(this.scoreboard);
        }
    }
    
    public void update() {
    
    }
}
