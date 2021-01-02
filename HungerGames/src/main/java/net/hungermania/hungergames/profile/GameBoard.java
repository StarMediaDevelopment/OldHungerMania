package net.hungermania.hungergames.profile;

import net.hungermania.hungergames.game.Game;
import net.hungermania.hungergames.game.GamePlayer;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.user.PlayerBoard;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import org.bukkit.ChatColor;

public class GameBoard extends PlayerBoard {
    private final int tributesLine;
    private final int mutationsLine;
    private final int spectatorsLine;
    private final int killsLine;
    private final int coinsLine;
    private final int topKillerLine;
    private final Game game;
    private final SpigotUser user;
    private final int serverLine;
    
    public GameBoard(Game game, SpigotUser spigotUser) {
        super(ManiaUtils.color("&6&lHUNGER GAMES"));
        this.game = game;
        this.user = spigotUser;
        addLine("", ChatColor.GOLD + "" + ChatColor.BOLD + "MAP:", "");
        addLine(ChatColor.WHITE + "", game.getMap().getName(), ""); //Might have to add a check for this
        addLine("", ChatColor.LIGHT_PURPLE.toString(), "");
        addLine("", ChatColor.GOLD + "" + ChatColor.BOLD + "PLAYERS", "");
        GamePlayer topKiller = game.getTopKiller();
        String name;
        if (topKiller == null) {
            name = "None";
        } else {
            name = topKiller.getUser().getName();
        }
        this.topKillerLine = addLine("", ChatColor.WHITE + "Top Killer: " + ChatColor.YELLOW, name);
        this.tributesLine = addLine("", ChatColor.WHITE + "Tributes: " + ChatColor.GREEN, "" + game.getTributesTeam().size());
        this.mutationsLine = addLine("", ChatColor.WHITE + "Mutations: " + ChatColor.LIGHT_PURPLE, "" + game.getMutationsTeam().size());
        this.spectatorsLine = addLine("", ChatColor.WHITE + "Spectators: " + ChatColor.RED, "" + game.getSpectatorsTeam().size());
        addLine("", ChatColor.YELLOW.toString(), "");
        addLine("", ChatColor.GOLD + "" + ChatColor.BOLD + "STATS:", "");
        this.killsLine = addLine("", ChatColor.WHITE + "Kills: " + ChatColor.YELLOW, "");
        this.coinsLine = addLine("", ChatColor.WHITE + "Coins: " + ChatColor.YELLOW, "" + game.getPlayer(user.getUniqueId()).getEarnedCoins());
        addLine("", ChatColor.GRAY.toString(), "");
        addLine("", ChatColor.GOLD + "" + ChatColor.BOLD + "SERVER:", "");
        this.serverLine = addLine(ChatColor.WHITE.toString(), ManiaCore.getInstance().getServerManager().getCurrentServer().getName(), "");
        addLine(ChatColor.YELLOW + "play.", "hungermania.net", "");
        spigotUser.setScoreboard(this);
        send(spigotUser.getBukkitPlayer());
    }
    
    public void update() {
        String restarting = "";
        setLine(serverLine, restarting);
        setLine(tributesLine, "" + game.getTributesTeam().size());
        setLine(mutationsLine, "" + game.getMutationsTeam().size());
        setLine(spectatorsLine, "" + game.getSpectatorsTeam().size());
        setLine(killsLine, "" + game.getPlayer(user.getUniqueId()).getKills());
        setLine(coinsLine, "" + game.getPlayer(user.getUniqueId()).getEarnedCoins());
        GamePlayer topKiller = game.getTopKiller();
        String name;
        if (topKiller == null) {
            name = "None";
        } else {
            name = topKiller.getUser().getName();
        }
        setLine(topKillerLine, name);
    }
}
