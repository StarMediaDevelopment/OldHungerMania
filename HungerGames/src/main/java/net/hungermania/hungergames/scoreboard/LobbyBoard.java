package net.hungermania.hungergames.scoreboard;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.lobby.Lobby;
import net.hungermania.hungergames.lobby.VoteTimer;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.user.PlayerBoard;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import org.bukkit.ChatColor;

public class LobbyBoard extends PlayerBoard {
    private final SpigotUser user;
    private final Lobby lobby;
    private final int waitingLine, neededLine, maximumnLine, roundNumberLine, timeLeftLine, votePowerLine;
    
    public LobbyBoard(Lobby lobby, SpigotUser user) {
        super(ManiaUtils.color("&6&lLOBBY"));
        this.user = user;
        this.lobby = lobby;
        
        addLine("", ChatColor.GOLD + "" + ChatColor.BOLD + "PLAYERS", "");
        this.waitingLine = addLine(ChatColor.WHITE + "", "Waiting: " + ChatColor.GREEN, lobby.getPlayers().size() + "");
        int needed = lobby.getGameSettings().getMinPlayers() - lobby.getPlayers().size();
        if (needed < 0) { needed = 0; }
        this.neededLine = addLine(ChatColor.WHITE + "", "Needed: " + ChatColor.RED, needed + "");
        this.maximumnLine = addLine(ChatColor.WHITE + "", "Maximum: " + ChatColor.LIGHT_PURPLE, lobby.getGameSettings().getMaxPlayers() + "");
        addLine("", ChatColor.BLACK + "", "");
        addLine("", ChatColor.GOLD + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "INFO", "");
        int roundNumber = HungerGames.getInstance().getGameManager().getGameCounter() + 1;
        int maxGames = lobby.getGameSettings().getMaxGames();
        this.roundNumberLine = addLine(ChatColor.WHITE + "", "Round #: " + ChatColor.YELLOW, roundNumber + "" + ChatColor.WHITE + "/" + ChatColor.AQUA + maxGames);
        VoteTimer voteTimer = lobby.getVoteTimer();
        int remainingSeconds = 45;
        if (voteTimer != null) {
            remainingSeconds = voteTimer.getRemainingSeconds();
        }
        this.timeLeftLine = addLine(ChatColor.WHITE + "", "Time Left: " + ChatColor.DARK_GREEN, remainingSeconds + "s");
        this.votePowerLine = addLine(ChatColor.WHITE + "", "Vote Power: " + ChatColor.BLUE, user.getRank().getVoteWeight() + "");
        addLine("", ChatColor.DARK_GRAY + "", "");
        addLine("", ChatColor.GOLD + "" + ChatColor.BOLD + "SERVER:", "");
        addLine(ChatColor.WHITE.toString(), ManiaCore.getInstance().getServerManager().getCurrentServer().getName(), "");
        addLine(ChatColor.YELLOW + "play.", "hungermania.net", "");
        user.setScoreboard(this);
        send(user.getBukkitPlayer());
    }
    
    public void update() {
        setLine(waitingLine, lobby.getPlayers().size() + "");
        int needed = lobby.getGameSettings().getMinPlayers() - lobby.getPlayers().size();
        if (needed < 0) { needed = 0; }
        setLine(neededLine, needed + "");
        setLine(maximumnLine, lobby.getGameSettings().getMaxPlayers() + "");
        int roundNumber = HungerGames.getInstance().getGameManager().getGameCounter() + 1;
        int maxGames = lobby.getGameSettings().getMaxGames();
        setLine(this.roundNumberLine, roundNumber + "" + ChatColor.WHITE + "/" + ChatColor.AQUA + "" + maxGames);
        VoteTimer voteTimer = lobby.getVoteTimer();
        int remainingSeconds = 45;
        if (voteTimer != null) {
            remainingSeconds = voteTimer.getRemainingSeconds();
        }
        setLine(timeLeftLine, remainingSeconds + "s");
        setLine(votePowerLine, user.getRank().getVoteWeight() + "");
    }
}
