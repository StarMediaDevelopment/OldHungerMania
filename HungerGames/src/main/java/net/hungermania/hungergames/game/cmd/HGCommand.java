package net.hungermania.hungergames.game.cmd;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.game.Game;
import net.hungermania.hungergames.game.GamePlayer;
import net.hungermania.hungergames.game.death.*;
import net.hungermania.hungergames.game.enums.PlayerType;
import net.hungermania.hungergames.lobby.Lobby;
import net.hungermania.hungergames.records.GameSettingsRecord;
import net.hungermania.hungergames.settings.GameSettings;
import net.hungermania.hungergames.settings.Time;
import net.hungermania.hungergames.settings.Weather;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.mutations.MutationType;
import net.hungermania.maniacore.spigot.mutations.Mutations;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import net.hungermania.manialib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("DuplicatedCode")
public class HGCommand implements CommandExecutor {

    private HungerGames plugin;

    public HGCommand(HungerGames plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ManiaUtils.color("&cOnly players may do that."));
            return true;
        }

        Player player = (Player) sender;
        User user = plugin.getManiaCore().getUserManager().getUser(player.getUniqueId());
        if (!user.hasPermission(Rank.ADMIN)) {
            player.sendMessage(ManiaUtils.color("&cYou do not have permission to use that command."));
            return true;
        }

        if (!(args.length > 0)) {
            sender.sendMessage(ManiaUtils.color("&cYou must provide a sub command."));
            return true;
        }

        if (ManiaUtils.checkCmdAliases(args, 0, "playertrackers", "pt")) {
            Game game = plugin.getGameManager().getCurrentGame();
            if (game == null) {
                sender.sendMessage(ManiaUtils.color("&cThere is no active game."));
                return true;
            }

            if (!(args.length > 1)) {
                sender.sendMessage(ManiaUtils.color("&cYou must provide a value"));
                return true;
            }

            boolean value;
            try {
                value = Boolean.parseBoolean(args[1]);
            } catch (Exception e) {
                sender.sendMessage(ManiaUtils.color("&cYou must provide either true or false"));
                return true;
            }

            if (game.isPlayerTrackers() == value) {
                sender.sendMessage("&cThe player trackers setting is already " + value);
                return true;
            }

            game.setPlayerTrackers(value);
        } else if (ManiaUtils.checkCmdAliases(args, 0, "gameinfo", "gi")) {
            Game game = plugin.getGameManager().getCurrentGame();
            if (game == null) {
                sender.sendMessage(ManiaUtils.color("&cCurrent game is not available."));
                return true;
            }

            sender.sendMessage(ManiaUtils.color("&6&l>> &eGame Information"));

            if (args.length == 1) {
                String[] messages = new String[]{
                        "&eGame ID: &f" + game.getId(), "&eMap: &f" + game.getMap().getName(), "&eTotal Players: &f" + game.getPlayers().length, "&eTributes: &f" + game.getTributesTeam().size(), "&eSpectators: &f" + game.getSpectatorsTeam().size(), "&eMutations: &f" + game.getMutationsTeam().size(), "&eGame Start: &f" + Utils.formatDate(game.getGameStart()), "&eGrace End: &f" + Utils.formatDate(game.getGracePeriodEnd()), "&eCountdown Start: &f" + Utils.formatDate(game.getCountdownStart()), "&eDeathmatch Countdown Start: &f" + Utils.formatDate(game.getDeathmatchCountdownStart()), "&eState: &f" + game.getState().name(), "&eLooted Chests: &f" + game.getLootedChests().size()
                };

                for (String m : messages) {
                    sender.sendMessage(ManiaUtils.color("&6&l> " + m));
                }
                return true;
            }

            if (ManiaUtils.checkCmdAliases(args, 1, "players")) {
                if (!(args.length > 2)) {
                    sender.sendMessage(ManiaUtils.color("&cYou do not have enough arguments"));
                    return true;
                }

                if (ManiaUtils.checkCmdAliases(args, 2, "list")) {
                    sender.sendMessage(ManiaUtils.color("&6&l>> &eList of all game players."));
                    List<String> tributes = new ArrayList<>();
                    for (UUID tribute : game.getTributesTeam()) {
                        tributes.add(Bukkit.getPlayer(tribute).getName());
                    }
                    sender.sendMessage(ManiaUtils.color("&6&l> &aTributes&8: &f" + StringUtils.join(tributes, "&7, &f")));

                    List<String> spectators = new ArrayList<>();
                    for (UUID spectator : game.getSpectatorsTeam()) {
                        spectators.add(Bukkit.getPlayer(spectator).getName());
                    }
                    sender.sendMessage(ManiaUtils.color("&6&l> &cSpectators&8: &f" + StringUtils.join(spectators, "&7, &f")));

                    List<String> mutations = new ArrayList<>();
                    for (UUID tribute : game.getMutationsTeam()) {
                        mutations.add(Bukkit.getPlayer(tribute).getName());
                    }
                    sender.sendMessage(ManiaUtils.color("&6&l> &dMutations&8: &f" + StringUtils.join(mutations, "&7, &f")));
                } else if (ManiaUtils.checkCmdAliases(args, 2, "view")) {
                    if (!(args.length > 3)) {
                        sender.sendMessage(ManiaUtils.color("&cYou must provide a player name"));
                        return true;
                    }

                    GamePlayer gamePlayer = null;
                    for (GamePlayer gp : game.getPlayers()) {
                        if (gp.getUser().getName().equalsIgnoreCase(args[3])) {
                            gamePlayer = gp;
                            break;
                        }
                    }

                    if (gamePlayer == null) {
                        player.sendMessage(ManiaUtils.color("&cThe name you provided doesn't match an active player."));
                        return true;
                    }

                    PlayerType playerType = game.getPlayerType(gamePlayer.getUniqueId());
                    player.sendMessage(ManiaUtils.color("&6&l>> &dViewing information for player &e" + gamePlayer.getUser().getName()));
                    player.sendMessage(ManiaUtils.color("&6&l> &ePlayer Type: &f" + playerType.name()));
                    player.sendMessage(ManiaUtils.color("&6&l> &eGame Kills: &f" + gamePlayer.getKills()));
                    player.sendMessage(ManiaUtils.color("&6&l> &eKill Streak: &f" + gamePlayer.getKillStreak()));
                    player.sendMessage(ManiaUtils.color("&6&l> &eEarned Coins: &f" + gamePlayer.getEarnedCoins()));
                    player.sendMessage(ManiaUtils.color("&6&l> &eHas Mutated: &f" + gamePlayer.hasMutated()));
                    player.sendMessage(ManiaUtils.color("&6&l> &eHas Sponsored: &f" + gamePlayer.hasSponsored()));
                    player.sendMessage(ManiaUtils.color("&6&l> &eIs Mutating: &f" + gamePlayer.isMutating()));
                    if (playerType == PlayerType.MUTATION) {
                        player.sendMessage(ManiaUtils.color("&6&l> &eMutation Target: &f" + game.getPlayer(gamePlayer.getMutationTarget()).getUser().getName()));
                        player.sendMessage(ManiaUtils.color("&6&l> &eMutation Type: &f" + gamePlayer.getMutationType().name()));
                    }
                    if (playerType == PlayerType.SPECTATOR) {
                        player.sendMessage(ManiaUtils.color("&6&l> &eKilled by a Player: &f" + gamePlayer.isSpectatorByDeath()));
                    }
                    DeathInfo deathInfo = gamePlayer.getDeathInfo();
                    if (deathInfo != null) {
                        player.sendMessage(ManiaUtils.color("&6&l> &eDeath Type: &f" + deathInfo.getType()));
                        if (deathInfo instanceof DeathInfoEntity) {
                            DeathInfoEntity deathInfoEntity = (DeathInfoEntity) deathInfo;
                            player.sendMessage(ManiaUtils.color("&6&l> &eKilled by Entity: &f" + deathInfoEntity.getKiller()));
                        } else if (deathInfo instanceof DeathInfoKilledSuicide) {
                            DeathInfoKilledSuicide deathInfoKilledSuicide = (DeathInfoKilledSuicide) deathInfo;
                            player.sendMessage(ManiaUtils.color("&6&l> &eKilled by: &f" + game.getPlayer(deathInfoKilledSuicide.getKiller()).getUser().getName() + "'s Creeper Suicide"));
                        } else if (deathInfo instanceof DeathInfoPlayerKill) {
                            DeathInfoPlayerKill deathInfoPlayerKill = (DeathInfoPlayerKill) deathInfo;
                            GamePlayer killer = game.getPlayer(deathInfoPlayerKill.getKiller());
                            String itemName = DeathInfo.getHandItem(deathInfoPlayerKill.getHandItem());
                            player.sendMessage(ManiaUtils.color("&6&l> &eKilled by: &f" + killer.getUser().getName() + " using " + itemName));
                        } else if (deathInfo instanceof DeathInfoProjectile) {
                            DeathInfoProjectile deathInfoProjectile = (DeathInfoProjectile) deathInfo;
                            String killerName;
                            Entity shooter = deathInfoProjectile.getShooter();
                            if (shooter instanceof Player) {
                                Player playerShooter = (Player) shooter;
                                SpigotUser spigotUser = (SpigotUser) HungerGames.getInstance().getManiaCore().getUserManager().getUser(playerShooter.getUniqueId());
                                killerName = spigotUser.getName();
                            } else {
                                killerName = "&f" + Utils.capitalizeEveryWord(shooter.getType().name());
                            }
                            player.sendMessage(ManiaUtils.color("&6&l> &eKilled by: &b" + killerName + " &ffrom " + deathInfoProjectile.getDistance() + " blocks"));
                        } else if (deathInfo instanceof DeathInfoSuicide) {
                            player.sendMessage(ManiaUtils.color("&6&l> &eDeath by: &fCreeper Mutation suicide."));
                        } else {
                            player.sendMessage(ManiaUtils.color("&6&l> &eDied to unknown reasons."));
                        }
                    }
                    if (gamePlayer.isForcefullyAdded()) {
                        player.sendMessage(ManiaUtils.color("&6&l> &eForcefully Added by: &f" + gamePlayer.getForcefullyAddedActor().getName()));
                    }
                    if (gamePlayer.isRevived()) {
                        player.sendMessage(ManiaUtils.color("&6&l> &eRevived by: &f" + gamePlayer.getRevivedActor()));
                    }
                }
            }
        } else if (ManiaUtils.checkCmdAliases(args, 0, "revive")) {
            Game game = plugin.getGameManager().getCurrentGame();
            if (game == null) {
                sender.sendMessage(ManiaUtils.color("&cThere is no active game"));
            }

            if (!(args.length > 1)) {
                sender.sendMessage(ManiaUtils.color("&cYou must provide a target name."));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ManiaUtils.color("&cThe name you provided did not match an online player."));
                return true;
            }

            GamePlayer gamePlayer = game.getPlayer(target.getUniqueId());
            if (gamePlayer == null) {
                sender.sendMessage(ManiaUtils.color("&cThere was an error getting the game data for that player."));
                return true;
            }

            String message = null;
            switch (game.revivePlayer(gamePlayer, sender)) {
                case WAS_NOT_A_TRIBUTE:
                    message = "&cThat player was a tribute originally. Please use the command /hungergames addtribute instead.";
                    break;
                case SUCCESS:
                    message = "&aSuccessfully revived the player " + target.getName();
                    break;
                case SPAWN_ERROR:
                    message = "&cThere was a problem finding a spawn for that player. They were set as a spectator as a result.";
                    break;
                case INVALID_STATE:
                    message = "&cInvalid game state to add a tribute.";
                    break;
            }

            if (message != null) {
                sender.sendMessage(ManiaUtils.color(message));
            }
        } else if (ManiaUtils.checkCmdAliases(args, 0, "addtribute")) {
            Game game = plugin.getGameManager().getCurrentGame();
            if (game == null) {
                sender.sendMessage(ManiaUtils.color("&cThere is no game currently running."));
                return true;
            }

            if (!(args.length > 1)) {
                sender.sendMessage(ManiaUtils.color("&cYou must provide a target name."));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ManiaUtils.color("&cThe name you provided did not match an online player."));
                return true;
            }

            GamePlayer gamePlayer = game.getPlayer(target.getUniqueId());
            if (gamePlayer == null) {
                sender.sendMessage(ManiaUtils.color("&cThere was an error getting the game data for that player."));
                return true;
            }

            String message = null;
            switch (game.forceAddPlayer(gamePlayer, sender)) {
                case WAS_TRIBUTE:
                    message = "&cThat player was a tribute originally. Please use the command /hungergames addtribute instead.";
                    break;
                case SUCCESS:
                    message = "&aSuccessfully added the player " + target.getName();
                    break;
                case SPAWN_ERROR:
                    message = "&cThere was a problem finding a spawn for that player. They were set as a spectator as a result.";
                    break;
                case INVALID_STATE:
                    message = "&cInvalid game state to add a tribute.";
                    break;
            }

            if (message != null) {
                sender.sendMessage(ManiaUtils.color(message));
            }
        } else if (ManiaUtils.checkCmdAliases(args, 0, "forcedeathmatch", "fdm")) {
            Game game = plugin.getGameManager().getCurrentGame();
            if (game == null) {
                sender.sendMessage(ManiaUtils.color("&cThere is no game currently running."));
                return true;
            }

            String senderName;
            if (sender instanceof Player) {
                senderName = user.getColoredName();
            } else {
                senderName = "&4&lCONSOLE";
            }

            game.beginDeathmatch();
            game.sendMessage(ManiaUtils.color("&5&l>> &6The deathmatch has been forcefully started by " + senderName + "&6."));
        } else if (ManiaUtils.checkCmdAliases(args, 0, "restockchests", "rc")) {
            Game game = plugin.getGameManager().getCurrentGame();
            if (game == null) {
                sender.sendMessage(ManiaUtils.color("&cThere is no game currently running."));
                return true;
            }

            String senderName;
            if (sender instanceof Player) {
                senderName = user.getColoredName();
            } else {
                senderName = "&4&lCONSOLE";
            }

            game.restockChests();
            game.sendMessage(ManiaUtils.color("&5&l>> &6The chests have been forcefully restocked by " + senderName + "&6."));
        } else if (ManiaUtils.checkCmdAliases(args, 0, "mutations")) {
            if (!sender.hasPermission("mania.hungergames.playertrackers")) {
                sender.sendMessage(ManiaUtils.color("&cYou do not have permission to use that command."));
                return true;
            }

            Game game = plugin.getGameManager().getCurrentGame();
            if (game == null) {
                sender.sendMessage(ManiaUtils.color("&cThere is no active game."));
                return true;
            }

            if (!(args.length > 1)) {
                sender.sendMessage(ManiaUtils.color("&cYou must provide a value"));
                return true;
            }

            boolean value;
            try {
                value = Boolean.parseBoolean(args[1]);
            } catch (Exception e) {
                sender.sendMessage(ManiaUtils.color("&cYou must provide either true or false"));
                return true;
            }

            if (game.getGameSettings().isMutations() == value) {
                sender.sendMessage("&cThe mutations setting is already " + value);
                return true;
            }

            game.setMutations(value);
        } else if (ManiaUtils.checkCmdAliases(args, 0, "mutate")) {
            if (!(args.length > 1)) {
                player.sendMessage(ManiaUtils.color("&cYou must provide a player."));
                return true;
            }

            Game game = plugin.getGameManager().getCurrentGame();
            if (game == null) {
                sender.sendMessage(ManiaUtils.color("&cThere is no active game."));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(ManiaUtils.color("&cYou did not provide a valid player name."));
                return true;
            }

            GamePlayer gamePlayer = game.getPlayer(target.getUniqueId());
            if (gamePlayer == null) {
                player.sendMessage(ManiaUtils.color("&cThat player is not a part of the current game."));
                return true;
            }

            if (!game.getSpectatorsTeam().isMember(target.getUniqueId())) {
                player.sendMessage(ManiaUtils.color("&cThat player is not a spectator!"));
                return true;
            }

            MutationType type;
            try {
                type = MutationType.valueOf(args[2].toUpperCase());
            } catch (ArrayIndexOutOfBoundsException e) {
                player.sendMessage(ManiaUtils.color("&cYou must provide a mutation type."));
                return true;
            } catch (IllegalArgumentException e) {
                player.sendMessage(ManiaUtils.color("&cInvalid mutation type."));
                return true;
            }

            UUID mutantTarget = null;
            DeathInfo deathInfo = gamePlayer.getDeathInfo();
            if (deathInfo instanceof DeathInfoPlayerKill) {
                DeathInfoPlayerKill playerKill = (DeathInfoPlayerKill) deathInfo;
                mutantTarget = playerKill.getKiller();
            }

            if (mutantTarget == null) {
                if (!(args.length > 3)) {
                    player.sendMessage(ManiaUtils.color("&cYou must provide a target for that mutation."));
                    return true;
                }
            }

            if (args.length > 3) {
                Player p = Bukkit.getPlayer(args[3]);
                if (p == null) {
                    player.sendMessage(ManiaUtils.color("&cThat name did not match an online player."));
                    return true;
                }

                if (!game.getTributesTeam().isMember(p.getUniqueId())) {
                    player.sendMessage(ManiaUtils.color("&cThat player is not a tribute in the game."));
                    return true;
                }

                mutantTarget = p.getUniqueId();
            }

            if (mutantTarget == null) {
                player.sendMessage(ManiaUtils.color("&cCould not determine mutation target."));
                return true;
            }

            game.mutatePlayer(target.getUniqueId(), Mutations.MUTATIONS.get(type), mutantTarget);
            game.sendMessage("&d&l>> &c" + target.getName() + " has been forceully mutated by " + player.getName());
        } else if (ManiaUtils.checkCmdAliases(args, 0, "forcestart", "fs")) {
            Game game = plugin.getGameManager().getCurrentGame();
            if (game != null) {
                sender.sendMessage(ManiaUtils.color("&cThere is already an active game."));
                return true;
            }

            Lobby lobby = plugin.getLobby();

            if (lobby.getVoteTimer() != null) {
                player.sendMessage(ManiaUtils.color("&cThe timer has already started!"));
                return true;
            }

            for (String a : args) {
                if (a.startsWith("-t")) {
                    lobby.startGame();
                    lobby.sendMessage(ManiaUtils.color("&aThe lobby timer has been forcefully started by &b" + player.getName() + " &aand the timer was skipped."));
                    return true;
                }
            }

            lobby.startTimer();
            lobby.getVoteTimer().setForceStarted(true);
            lobby.sendMessage(ManiaUtils.color("&aThe lobby timer has been forcefully started by &b" + player.getName()));
        } else if (ManiaUtils.checkCmdAliases(args, 0, "settings")) {
            if (!(args.length > 1)) {
                player.sendMessage(ManiaUtils.color("&cYou must provide either a game setting name or update"));
                return true;
            }

            if (ManiaUtils.checkCmdAliases(args, 1, "update")) {
                plugin.getSettingsManager().load();
                player.sendMessage("&aUpdated the game settings from the database.");
                if (args.length > 2) {
                    if (args[2].equalsIgnoreCase("-g")) {
                        Game game = plugin.getGameManager().getCurrentGame();
                        if (game != null) {
                            game.setGameSettings(plugin.getSettingsManager().getCurrentSettings());
                            player.sendMessage("&aUpdated the current game's settings.");
                        } else {
                            player.sendMessage("&cThere is no active game right now.");
                        }
                    }
                }
                return true;
            }

            if (!(args.length > 2)) {
                player.sendMessage(ManiaUtils.color("&cYou must provide a value."));
                return true;
            }

            Field[] declaredFields = GameSettings.class.getDeclaredFields();
            for (Field field : declaredFields) {
                if (ManiaUtils.checkCmdAliases(args, 1, field.getName())) {
                    //Currently there is only ints, booleans and the Time and Weather types for the game settings
                    Object value = null;
                    try {
                        value = Integer.parseInt(args[2]);
                    } catch (Exception e) {
                    }

                    if (value == null) {
                        try {
                            value = Boolean.parseBoolean(args[2]);
                        } catch (Exception e) {
                        }
                    }

                    if (value == null) {
                        try {
                            value = Time.valueOf(args[2].toUpperCase());
                        } catch (Exception e) {
                        }
                    }

                    if (value == null) {
                        try {
                            value = Weather.valueOf(args[2].toUpperCase());
                        } catch (Exception e) {
                        }
                    }

                    if (value == null) {
                        player.sendMessage(ManiaUtils.color("&cInvalid value type. The only ones are integers, true, false, and the Time and Weather types."));
                        return true;
                    }

                    boolean updateGlobal = false;
                    for (String s : args) {
                        if (s.equalsIgnoreCase("-g")) {
                            updateGlobal = true;
                            break;
                        }
                    }

                    Game game = plugin.getGameManager().getCurrentGame();
                    if (game == null) {
                        if (!updateGlobal) {
                            player.sendMessage(ManiaUtils.color("&cThere is no active game."));
                            return true;
                        }
                    }
                    GameSettings currentSettings = game.getGameSettings(), globalSettings = plugin.getSettingsManager().getCurrentSettings();
                    field.setAccessible(true);
                    try {
                        if (currentSettings != null) {
                            field.set(currentSettings, value);
                        }
                        if (updateGlobal) {
                            field.set(globalSettings, value);
                        }
                    } catch (Exception e) {
                        player.sendMessage(ManiaUtils.color("&cCould not update that setting: " + e.getMessage()));
                        return true;
                    }

                    if (updateGlobal) {
                        plugin.getManiaCore().getDatabase().pushRecord(new GameSettingsRecord(globalSettings));
                    }

                    player.sendMessage(ManiaUtils.color("&aYou have updated the game setting &b" + field.getName().toLowerCase() + " &ato &b" + value));
                    break;
                }
            }
        }

        return true;
    }
}
