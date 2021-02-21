package net.hungermania.maniacore.spigot.cmd;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.leveling.Level;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.records.NicknameRecord;
import net.hungermania.maniacore.api.stats.Stats;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.user.SpigotUserManager;
import net.hungermania.maniacore.spigot.util.SpigotUtils;
import net.hungermania.manialib.sql.IRecord;
import net.hungermania.manialib.util.Constants;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StatsCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ManiaCore.getInstance().getPlugin().runTaskAsynchronously(() -> {
            User target = null;
            Rank senderRank = SpigotUtils.getRankFromSender(sender);
            if (args.length > 0) {
                Player t = Bukkit.getPlayer(args[0]);
                if (t != null) {
                    target = ManiaCore.getInstance().getUserManager().getUser(t.getUniqueId());
                } else {
                    for (User value : ((SpigotUserManager) ManiaCore.getInstance().getUserManager()).getUsers().values()) {
                        if (value.getNickname().isActive()) {
                            if (value.getNickname().getName().equalsIgnoreCase(args[0])) {
                                target = value;
                            }
                        } else {
                            if (value.getName().equalsIgnoreCase(args[0])) {
                                target = value;
                            }
                        }
                    }
                }
                
                if (target == null) {
                    List<IRecord> nicknameRecords = ManiaCore.getInstance().getDatabase().getRecords(NicknameRecord.class, "active", "true");
                    for (IRecord record : nicknameRecords) {
                        NicknameRecord nickRecord = (NicknameRecord) record;
                        if (nickRecord.toObject().getName().equalsIgnoreCase(args[0])) {
                            target = ManiaCore.getInstance().getUserManager().getUser(nickRecord.toObject().getPlayer());
                        }
                    }
                }
                
                if (target == null) {
                    ManiaCore.getInstance().getUserManager().getUser(args[0]);
                }
            } else {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ManiaUtils.color("&cYou must provide a target."));
                    return;
                }

                target = ManiaCore.getInstance().getUserManager().getUser(((Player) sender).getUniqueId());
            }

            if (target == null) {
                sender.sendMessage(ManiaUtils.color("&cCould not determine the target of the command."));
                return;
            }

            User user = ManiaCore.getInstance().getUserManager().getUser(target.getUniqueId());
            int kills, deaths, wins, losses, deathmatches, chestsFound, coins, exp, winStreak, score;

            boolean realStats = true;
            if (user.getNickname().isActive()) {
                if (senderRank.ordinal() > user.getRank().ordinal()) {
                    realStats = false;
                }

                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.getUniqueId().equals(target.getUniqueId())) {
                        realStats = true;
                    }
                }
            }
            System.out.println("Real Stats " + realStats);
            
            String name;

            if (realStats) {
                name = user.getName();
                kills = user.getStat(Stats.HG_KILLS).getAsInt();
                deaths = user.getStat(Stats.HG_DEATHS).getAsInt();
                wins = user.getStat(Stats.HG_WINS).getAsInt();
                chestsFound = user.getStat(Stats.HG_CHESTS_FOUND).getAsInt();
                winStreak = user.getStat(Stats.HG_WINSTREAK).getAsInt();
                deathmatches = user.getStat(Stats.HG_DEATHMATCHES).getAsInt();
                losses = user.getStat(Stats.HG_GAMES).getAsInt() - wins;
                coins = target.getStat(Stats.COINS).getAsInt();
                exp = target.getStat(Stats.EXPERIENCE).getAsInt();
                score = target.getStat(Stats.HG_SCORE).getAsInt();
            } else {
                name = user.getNickname().getName();
                kills = user.getFakedStat(Stats.HG_KILLS).getAsInt();
                deaths = user.getFakedStat(Stats.HG_DEATHS).getAsInt();
                wins = user.getFakedStat(Stats.HG_WINS).getAsInt();
                chestsFound = user.getFakedStat(Stats.HG_CHESTS_FOUND).getAsInt();
                winStreak = user.getFakedStat(Stats.HG_WINSTREAK).getAsInt();
                deathmatches = user.getFakedStat(Stats.HG_DEATHMATCHES).getAsInt();
                losses = user.getFakedStat(Stats.HG_GAMES).getAsInt() - wins;
                coins = target.getFakedStat(Stats.COINS).getAsInt();
                exp = target.getFakedStat(Stats.EXPERIENCE).getAsInt();
                score = target.getFakedStat(Stats.HG_SCORE).getAsInt();
            }

            double kdr;
            double wlr;

            if (deaths == 0) {
                kdr = kills;
            } else {
                kdr = kills / (deaths * 1.0);
            }

            if (losses == 0) {
                wlr = wins;
            } else {
                wlr = wins / (losses * 1.0);
            }

            Level level = ManiaCore.getInstance().getLevelManager().getLevel(exp);
            Level nextLevel = ManiaCore.getInstance().getLevelManager().getLevels().getOrDefault(level.getNumber() + 1, ManiaCore.getInstance().getLevelManager().getLevel(0));

            sender.sendMessage(ManiaUtils.color("&6&l>> &a" + name + "'s Stats"));
            sender.sendMessage(ManiaUtils.color("&6&l> &7Coins: &b" + coins));
            sender.sendMessage(ManiaUtils.color("&6&l> &7Level: &b" + level.getNumber()));
            sender.sendMessage(ManiaUtils.color("&6&l> &7Experience: &b" + exp + "     &e&lNext Level: &b" + (nextLevel.getTotalXp() - exp)));
            sender.sendMessage(ManiaUtils.color("&6&l> &7Kills: &b" + kills));
            sender.sendMessage(ManiaUtils.color("&6&l> &7Deaths: &b" + deaths));
            sender.sendMessage(ManiaUtils.color("&6&l> &7K/D: &b" + Constants.NUMBER_FORMAT.format(kdr)));
            sender.sendMessage(ManiaUtils.color("&6&l> &7Wins: &b" + wins));
            sender.sendMessage(ManiaUtils.color("&6&l> &7Losses: &b" + losses));
            sender.sendMessage(ManiaUtils.color("&6&l> &7W/L: &b" + Constants.NUMBER_FORMAT.format(wlr)));
            sender.sendMessage(ManiaUtils.color("&6&l> &7Win Streak: &b" + winStreak));
            sender.sendMessage(ManiaUtils.color("&6&l> &7Deathmatches Reached: &b" + deathmatches));
            sender.sendMessage(ManiaUtils.color("&6&l> &7Chests Found: &b" + chestsFound));
            sender.sendMessage(ManiaUtils.color("&6&l> &7Score: &b" + score));
        });
        return true;
    }
}
