package net.hungermania.hungergames.game;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.profile.GameBoard;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.State;
import net.hungermania.maniacore.api.util.Utils;
import net.hungermania.maniacore.memory.MemoryHook.Task;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import net.hungermania.maniacore.spigot.util.SpigotUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("DuplicatedCode")
public class GameTask extends BukkitRunnable {
    
    private Game game;
    private Set<Integer> announcedCountdownSeconds = new HashSet<>(), announcedMinutes = new HashSet<>(), announcedDeathmatchStartSeconds = new HashSet<>(), announcedDeathmatchSeconds = new HashSet<>(), announcedDeathmatchMinutes = new HashSet<>(), announcedGameSeconds = new HashSet<>(), restocked = new HashSet<>(), announcedPlayers = new HashSet<>(), survivalTime = new HashSet<>();
    private Set<Integer> gameBeginSoundPlayed = new HashSet<>();
    
    private static final Set<Integer> COUNTDOWN_ANNOUNCE = new HashSet<>(Arrays.asList(30, 20, 10, 3, 2, 1, 0));
    private static final Set<Integer> GAME_COUNTDOWN_ANNOUNCE = new HashSet<>(Arrays.asList(60, 45, 30, 15, 10, 5, 4, 3, 2, 1, 0));
    private static final Set<Integer> DEATHMATCH_COUNTDOWN_ANNOUNCE = new HashSet<>(Arrays.asList(10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
    public static final Set<Integer> GAME_END_ANNOUNCE = new HashSet<>(Arrays.asList(60, 45, 30, 15, 10, 5, 4, 3, 2, 1, 0));
    
    private boolean announcedGraceExpire = false;
    
    public GameTask(Game game) {
        this.game = game;
    }
    
    public void run() {
        if (game == null) {
            cancel();
            return;
        }
        Task task = HungerGames.getInstance().getGameTaskHook().task().start();
        long start = game.getGameStart();
        long current = System.currentTimeMillis();
        long elapsedTime = current - start;
        
        int elapsedMinutes = (int) TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
        int remainingGameMinutes = game.getGameSettings().getGameLength() - elapsedMinutes;
        
        long calculatedEnd = start + TimeUnit.MINUTES.toMillis(game.getGameSettings().getGameLength());
        long remainingMilliseconds = calculatedEnd - current;
        new BukkitRunnable() {
            public void run() {
                TimoCloudAPI.getBukkitAPI().getThisServer().setState("INGAME");
                TimoCloudAPI.getBukkitAPI().getThisServer().setExtra("map:" + game.getMap().getName() + ";time:" + Utils.formatTime(remainingMilliseconds));
            }
        }.runTaskAsynchronously(HungerGames.getInstance());
        
        game.checkWin();
        
        for (GamePlayer value : game.getPlayers()) {
            SpigotUser spigotUser = value.getUser();
            if (spigotUser != null) {
                if (spigotUser.getScoreboard() == null) {
                    new GameBoard(game, spigotUser);
                }
            }
            
            spigotUser.getScoreboard().update();
        }
        
        List<UUID> rawPlayers = new ArrayList<>(game.getTributesTeam().getMembers());
        rawPlayers.addAll(game.getSpectatorsTeam().getMembers());
        rawPlayers.addAll(game.getMutationsTeam().getMembers());
        rawPlayers.addAll(game.getHiddenStaffTeam().getMembers());
        Map<Player, Boolean> players = new HashMap<>();
        rawPlayers.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            ItemStack item = player.getInventory().getItemInHand();
            boolean holdingTracker = false;
            if (item != null) {
                if (item.hasItemMeta()) {
                    if (item.getItemMeta().hasDisplayName()) {
                        if (item.getItemMeta().getDisplayName().toLowerCase().contains("player tracker")) {
                            holdingTracker = true;
                        }
                    }
                }
            }
            players.put(player, holdingTracker);
        });
        if (game.getState() == State.COUNTDOWN) {
            long calculatedStart = game.getCountdownStart() + TimeUnit.SECONDS.toMillis(game.getGameSettings().getStartingCountdown());
            int remainingSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(calculatedStart - System.currentTimeMillis());
            if (!this.gameBeginSoundPlayed.contains(remainingSeconds)) {
                game.playSound(Sound.NOTE_BASS);
                this.gameBeginSoundPlayed.add(remainingSeconds);
            }
            if (!this.announcedCountdownSeconds.contains(remainingSeconds)) {
                if (COUNTDOWN_ANNOUNCE.contains(remainingSeconds)) {
                    game.playSound(Sound.CLICK);
                    if (remainingSeconds == 1) {
                        game.sendMessage("&6&l>> &eThe game begins &ein &b" + remainingSeconds + " &esecond!");
                    } else if (remainingSeconds != 0) {
                        game.sendMessage("&6&l>> &eThe game begins &ein &b" + remainingSeconds + " &eseconds!");
                    } else {
                        game.startGame();
                    }
                }
                this.announcedCountdownSeconds.add(remainingSeconds);
            }
            
            players.forEach((player, holding) -> {
                if (!holding) {
                    SpigotUtils.sendActionBar(player, "&fThe game begins in &e" + remainingSeconds + "s&f...");
                }
            });
        } else if (game.getState() == State.PLAYING) {
            if (!this.restocked.contains(elapsedMinutes)) {
                if (elapsedMinutes % 10 == 0) {
                    game.restockChests();
                    this.restocked.add(elapsedMinutes);
                }
            }
            
            if (!this.survivalTime.contains(elapsedMinutes)) {
                if (elapsedMinutes % 5 == 0 && !(elapsedMinutes == 0)) {
                    for (UUID tribute : game.getTributesTeam()) {
                        GamePlayer gamePlayer = game.getPlayer(tribute);
                        gamePlayer.getUser().setCoins(gamePlayer.getUser().getCoins());
                        Rank rank = gamePlayer.getUser().getRank();
                        String multiplierString = "";
                        if (rank.getCoinMultiplier() > 1) {
                            multiplierString = rank.getBaseColor() + "&lx" + rank.getCoinMultiplier() + " " + rank.getName() + " Bonus";
                        }
                        int coins = (int) Math.round(15 * rank.getCoinMultiplier());
                        gamePlayer.getUser().setCoins(gamePlayer.getUser().getCoins());
                        gamePlayer.getUser().sendMessage("&2&l>> &a&l+" + coins + " &3&lCOINS&a! " + multiplierString);
                    }
                    this.survivalTime.add(elapsedMinutes);
                }
            }
            
            if (!this.announcedPlayers.contains(elapsedMinutes)) {
                if (elapsedMinutes % 5 == 0 && !(elapsedMinutes == 0)) {
                    int tributes = game.getTributesTeam().size();
                    int mutations = game.getMutationsTeam().size();
                    int spectators = game.getSpectatorsTeam().size();
                    
                    String message = "&6&l>> &7There are &e" + tributes + " &7alive &a&lTributes&7 with &e" + mutations + " &7vengeful &d&lMutations &7and &e" + spectators + " &7watching &c&lSpectators&7.";
                    game.sendMessage(message);
                    this.announcedPlayers.add(elapsedMinutes);
                }
            }
            
            if (!announcedGraceExpire) {
                int remainingSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(game.getGracePeriodEnd() - System.currentTimeMillis());
                players.forEach((player, holding) -> {
                    if (!holding) {
                        SpigotUtils.sendActionBar(player, "&fThe grace period ends in &e" + remainingSeconds + "s");
                    }
                });
                
                if (game.getGracePeriodEnd() <= System.currentTimeMillis()) {
                    game.sendMessage("&6&l>> &c&lTHE GRACE PERIOD HAS ENDED!");
                    this.announcedGraceExpire = true;
                }
            } else {
                long calculatedDMStart = game.getGameStart() + TimeUnit.MINUTES.toMillis(game.getGameSettings().getGameLength());
                String timeRemaining = Utils.formatTime(calculatedDMStart - System.currentTimeMillis());
                players.forEach((player, holding) -> {
                    if (!holding) {
                        SpigotUtils.sendActionBar(player, "&fThe deathmatch begins in &e" + timeRemaining);
                    }
                });
            }
            
            if (!this.announcedMinutes.contains(remainingGameMinutes)) {
                game.playSound(Sound.CLICK);
                if (remainingGameMinutes == 1) {
                    game.sendMessage("&6&l>> &eThe &c&lDEATHMATCH &ebegins in &b" + remainingGameMinutes + " &eminute!");
                } else if (remainingGameMinutes != 0) {
                    game.sendMessage("&6&l>> &eThe &c&lDEATHMATCH &ebegins in &b" + remainingGameMinutes + " &eminutes!");
                } else {
                    game.beginDeathmatch();
                }
                this.announcedMinutes.add(remainingGameMinutes);
            }
        } else if (game.getState() == State.PLAYING_DEATHMATCH) {
            long calculatedDMStart = game.getDeathmatchPlayingStart() + TimeUnit.MINUTES.toMillis(1);
            int remainingSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(calculatedDMStart - System.currentTimeMillis());
            if (!this.announcedGameSeconds.contains(remainingSeconds)) {
                players.forEach((player, holding) -> {
                    if (!holding) {
                        SpigotUtils.sendActionBar(player, "&fThe deathmatch begins in &e" + remainingSeconds + "s");
                    }
                });
                if (GAME_COUNTDOWN_ANNOUNCE.contains(remainingSeconds)) {
                    game.playSound(Sound.CLICK);
                    if (remainingSeconds == 1) {
                        game.sendMessage("&6&l>> &eThe &c&lDEATHMATCH &ebegins in &b" + remainingSeconds + " &esecond!");
                    } else if (remainingSeconds != 0) {
                        game.sendMessage("&6&l>> &eThe &c&lDEATHMATCH &ebegins in &b" + remainingSeconds + " &eseconds!");
                    } else {
                        game.startDeathmatchCountdown();
                    }
                }
                this.announcedGameSeconds.add(remainingSeconds);
            }
        } else if (game.getState() == State.DEATHMATCH_COUNTDOWN) {
            long calculatedStart = game.getDeathmatchCountdownStart() + TimeUnit.SECONDS.toMillis(10);
            int remainingSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(calculatedStart - System.currentTimeMillis());
            if (!this.announcedDeathmatchStartSeconds.contains(remainingSeconds)) {
                if (DEATHMATCH_COUNTDOWN_ANNOUNCE.contains(remainingSeconds)) {
                    game.playSound(Sound.CLICK);
                    if (remainingSeconds == 1) {
                        game.sendMessage("&6&l>> &eThe &c&lDEATHMATCH &ebegins in &b" + remainingSeconds + " &esecond!");
                    } else if (remainingSeconds != 0) {
                        game.sendMessage("&6&l>> &eThe &c&lDEATHMATCH &ebegins in &b" + remainingSeconds + " &eseconds!");
                    } else {
                        game.startDeathmatch();
                    }
                }
                this.announcedDeathmatchStartSeconds.add(remainingSeconds);
            }
        } else if (game.getState() == State.DEATHMATCH) {
            long calculatedDeathmatchEnd = game.getDeathmatchStart() + TimeUnit.MINUTES.toMillis(game.getGameSettings().getDeathmatchLength());
            int remainingMinutes = (int) TimeUnit.MILLISECONDS.toMinutes(calculatedDeathmatchEnd - System.currentTimeMillis());
            int remainingSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(calculatedDeathmatchEnd - System.currentTimeMillis());
            players.forEach((player, holding) -> {
                if (!holding) {
                    SpigotUtils.sendActionBar(player, "&fThe game ends in &e" + Utils.formatTime(calculatedDeathmatchEnd - System.currentTimeMillis()));
                }
            });
            if (remainingMinutes == 0) {
                if (!this.announcedDeathmatchSeconds.contains(remainingSeconds)) {
                    if (GAME_END_ANNOUNCE.contains(remainingSeconds)) {
                        game.playSound(Sound.CLICK);
                        if (remainingSeconds == 1) {
                            game.sendMessage("&6&l>> &eThe &c&lGAME ENDS &ein &b" + remainingSeconds + " &esecond!");
                        } else if (remainingSeconds != 0) {
                            game.sendMessage("&6&l>> &eThe &c&lGAME ENDS &ein &b" + remainingSeconds + " &eseconds!");
                        } else {
                            game.endGame();
                        }
                    }
                    this.announcedDeathmatchSeconds.add(remainingSeconds);
                }
            } else {
                if (!this.announcedDeathmatchMinutes.contains(remainingMinutes)) {
                    game.playSound(Sound.CLICK);
                    if (remainingMinutes == 1) {
                        game.sendMessage("&6&l>> &eThe &c&lGAME ENDS &ein &b" + remainingMinutes + " &eminute!");
                    } else if (remainingMinutes != 0) {
                        game.sendMessage("&6&l>> &eThe &c&lGAME ENDS &ein &b" + remainingMinutes + " &eminutes!");
                    }
                    this.announcedDeathmatchMinutes.add(remainingMinutes);
                }
            }
        } else if (game.getState() == State.ENDING) {
            long nextGame = game.getGameEnd();
            if (game.getPlayers().length > 0) {
                nextGame = nextGame + TimeUnit.SECONDS.toMillis(game.getGameSettings().getNextGameStart());
            }
    
            boolean restarting = false;
            if (HungerGames.getInstance().getGameManager().getGameCounter() >= game.getGameSettings().getMaxGames()) {
                restarting = true;
            }
            
            long finalNextGame = nextGame;
            boolean finalRestarting = restarting;
            players.forEach((player, holding) -> {
                if (!holding) {
                    String time = Utils.formatTime(finalNextGame - System.currentTimeMillis());
                    if (!finalRestarting) {
                        SpigotUtils.sendActionBar(player, "&fThe next game starts in &e" + time);
                    } else {
                        SpigotUtils.sendActionBar(player, "&fServer restarting in &e" + time);
                    }
                }
            });
            
            if (System.currentTimeMillis() >= nextGame) {
                cancel();
                game.resetServer();
            }
        }
        task.end();
        long totalTime = task.getEnd() - task.getStart();
        if (totalTime > 40) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                User user = ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
                if (user.hasPermission(Rank.ADMIN)) {
                    user.sendMessage("&8Game task took " + totalTime + " milliseconds");
                }
            }
        }
    }
    
    public void start() {
        this.runTaskTimer(HungerGames.getInstance(), 0L, 1L);
    }
}
