package net.hungermania.hungergames.game;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.Getter;
import lombok.Setter;
import me.libraryaddict.disguise.DisguiseAPI;
import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.game.death.*;
import net.hungermania.hungergames.game.team.*;
import net.hungermania.hungergames.lobby.Lobby;
import net.hungermania.hungergames.map.HGMap;
import net.hungermania.hungergames.records.GameRecord;
import net.hungermania.hungergames.scoreboard.GameBoard;
import net.hungermania.hungergames.settings.GameSettings;
import net.hungermania.hungergames.util.Messager;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.redis.Redis;
import net.hungermania.maniacore.api.stats.Statistic;
import net.hungermania.maniacore.api.stats.Stats;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.api.util.Position;
import net.hungermania.maniacore.api.util.State;
import net.hungermania.maniacore.memory.MemoryHook;
import net.hungermania.maniacore.memory.MemoryHook.Task;
import net.hungermania.maniacore.spigot.mutations.Mutation;
import net.hungermania.maniacore.spigot.mutations.MutationType;
import net.hungermania.maniacore.spigot.perks.Perks;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import net.hungermania.maniacore.spigot.util.SpigotUtils;
import net.hungermania.manialib.data.model.IRecord;
import net.hungermania.manialib.util.Pair;
import net.hungermania.manialib.util.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"DuplicatedCode", "unused"})
public class Game implements IRecord {
    @Getter private long deathmatchPlayingStart;
    @Getter @Setter private int id = -1, serverId;
    @Getter private HGMap map;
    @Getter private PlayerTrackerTask playerTrackerTask;
    private Map<UUID, GamePlayer> players = new HashMap<>();
    @Getter private long gameStart;
    @Getter private long gracePeriodEnd;
    @Getter private long gameEnd;
    @Getter private long countdownStart;
    @Getter private long deathmatchCountdownStart;
    @Getter private long deathmatchStart;
    @Getter private State state;
    @Getter private GameTask task;
    private GameSettings gameSettings;
    @Getter @Setter private boolean archived = false;
    @Getter private Set<Integer> profiles = new HashSet<>();
    @Getter private Map<Integer, UUID> spawns = new HashMap<>();
    @Getter private Set<Location> lootedChests = new HashSet<>();
    @Getter private boolean playerTrackers = true;
    @Getter private UUID firstKiller = null;
    @Getter private Map<Location, UUID> suicideLocations = new HashMap<>();
    @Getter private Map<UUID, Long> endermanPearlLastUse = new HashMap<>();
    @Getter private Messager messager;
    private Map<UUID, GamePlayer> cachedPlayers = new HashMap<>();
    @Getter @Setter private int currentMapVotes = 0;

    @Getter private GameTeam tributesTeam, spectatorsTeam, hiddenStaffTeam, mutationsTeam;

    public Game(HGMap map, GameSettings settings) {
        this.map = map;
        this.gameSettings = settings;
    }

    public Game(int id, HGMap map, long gameStart, long gameEnd, boolean archived, Set<Integer> profiles, int serverId) {
        this.id = id;
        this.map = map;
        this.gameStart = gameStart;
        this.gameEnd = gameEnd;
        this.profiles = profiles;
        this.archived = archived;
        this.serverId = serverId;
    }

    public void setup(Lobby lobby) {
        TimoCloudAPI.getBukkitAPI().getThisServer().setState("INGAME");
        TimoCloudAPI.getBukkitAPI().getThisServer().setExtra("map:" + this.map.getName() + ";time:0");
        this.state = State.SETUP;
        HungerGames.getInstance().getMapManager().copyMap(this.map.getName());
        tributesTeam = new TributesTeam(this);
        spectatorsTeam = new SpectatorsTeam(this);
        hiddenStaffTeam = new HiddenStaffTeam(this);
        mutationsTeam = new MutationsTeam(this);

        map.getWorld().setGameRuleValue("naturalRegeneration", "" + gameSettings.isRegeneration());
        map.getWorld().setGameRuleValue("doDaylightCycle", "" + gameSettings.isTimeProgression());
        map.getWorld().setGameRuleValue("doWeatherCycle", "" + gameSettings.isWeatherProgression());
        map.getWorld().setGameRuleValue("doMobSpawning", "false");
        map.getWorld().setGameRuleValue("announceAdvancements", "false");
        map.getWorld().setGameRuleValue("doFireTick", "false");
        map.getWorld().setGameRuleValue("keepInventory", "false");
        map.getWorld().setDifficulty(Difficulty.EASY);

        List<Entity> entities = map.getWorld().getEntities();
        map.getWorld().setGameRuleValue("doEntityDrops", "false");
        for (Entity entity : entities) {
            if (entity instanceof Monster) {
                entity.remove();
            }
        }
        map.getWorld().setGameRuleValue("doEntityDrops", "true");

        switch (gameSettings.getWeather()) {
            case RAIN:
                map.getWorld().setStorm(true);
                break;
            case STORM:
                map.getWorld().setStorm(true);
                map.getWorld().setThundering(true);
                break;
            case CLEAR:
                map.getWorld().setStorm(false);
                break;
        }

        map.getWorld().setWeatherDuration(Integer.MAX_VALUE);
        map.getWorld().setTime(gameSettings.getTime().getStart());

        for (Integer id : this.map.getSpawns().keySet()) {
            this.spawns.put(id, null);
        }

        for (SpigotUser player : lobby.getPlayers()) {
            this.tributesTeam.join(player.getUniqueId());
        }

        for (SpigotUser hidden : lobby.getHiddenStaff()) {
            this.hiddenStaffTeam.join(hidden.getUniqueId());
            new BukkitRunnable() {
                public void run() {
                    hidden.sendMessage("&6&l>> &cYou are spectating the game because you are incognito.");
                }
            }.runTaskLater(HungerGames.getInstance(), 100L);
        }

        this.playerTrackerTask = new PlayerTrackerTask(this);

        MemoryHook spectatorUpdate = new MemoryHook("Game Spectator Update");
        ManiaCore.getInstance().getMemoryManager().addMemoryHook(spectatorUpdate);
        HungerGames plugin = HungerGames.getInstance();
        new BukkitRunnable() {
            public void run() {
                if (archived)
                    cancel();
                Task task = spectatorUpdate.task().start();
                List<UUID> players = new ArrayList<>(getSpectatorsTeam().getMembers());
                players.addAll(getMutationsTeam().getMembers());
                players.addAll(getHiddenStaffTeam().getMembers());
                for (UUID spectator : players) {
                    Player player = Bukkit.getPlayer(spectator);
                    player.setFoodLevel(20);
                    player.setSaturation(20);
                }
                task.end();
            }
        }.runTaskTimer(HungerGames.getInstance(), 20L, 20L);

        MemoryHook endermanDamage = new MemoryHook("Enderman Mutation Damage");
        ManiaCore.getInstance().getMemoryManager().addMemoryHook(endermanDamage);
        new BukkitRunnable() {
            public void run() {
                if (archived)
                    cancel();
                Task task = endermanDamage.task().start();
                if (getMutationsTeam().isEmpty()) {
                    return;
                }
                for (UUID mutation : getMutationsTeam()) {
                    GamePlayer gamePlayer = getPlayer(mutation);
                    if (gamePlayer.getMutationType() == MutationType.ENDERMAN) {
                        Player player = gamePlayer.getUser().getBukkitPlayer();
                        Location location = player.getLocation();
                        if (location.getBlock().getType() == Material.WATER || location.getBlock().getType() == Material.STATIONARY_WATER) {
                            player.damage(4);
                        }
                    }
                }
                task.end();
            }
        }.runTaskTimer(HungerGames.getInstance(), 20L, 20L);

        new BukkitRunnable() {
            public void run() {
                if (archived) {
                    cancel();
                    return;
                }

                if (state == State.PLAYING || state == State.PLAYING_DEATHMATCH || state == State.DEATHMATCH) {
                    Set<UUID> members = new HashSet<>(spectatorsTeam.getMembers());
                    members.addAll(hiddenStaffTeam.getMembers());

                    for (UUID member : members) {
                        Player player = Bukkit.getPlayer(member);
                        player.sendMessage("");
                        player.sendMessage(ManiaUtils.color("&6&l>> &eYou might be out of the game, but &f&lDON'T QUIT&e!"));
                        player.sendMessage(ManiaUtils.color("&6&l>> &eAnother game will be &f&lSTARTING SOON&e!"));
                        ComponentBuilder builder = new ComponentBuilder(">>").color(net.md_5.bungee.api.ChatColor.GOLD).bold(true)
                                .append(" Or, ").color(net.md_5.bungee.api.ChatColor.YELLOW).append("CLICK HERE").color(net.md_5.bungee.api.ChatColor.WHITE).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nextgame")).bold(true)
                                .append(" to go to the next available game.").color(net.md_5.bungee.api.ChatColor.YELLOW);
                        player.spigot().sendMessage(builder.create());
                        player.sendMessage("");
                    }
                }
            }
        }.runTaskTimer(HungerGames.getInstance(), 0L, 6000);

        new BukkitRunnable() {
            public void run() {
                if (archived)
                    cancel();
                for (GamePlayer value : players.values()) {
                    getGameTeam(value.getUniqueId()).setPlayerListName(value.getUser());
                }
            }
        }.runTaskTimer(HungerGames.getInstance(), 20L, 20L);

        messager = new GameMessager(this);
    }

    public GamePlayer getPlayer(UUID uuid) {
        return this.players.get(uuid);
    }

    public GameSettings getGameSettings() {
        if (gameSettings == null) {
            gameSettings = HungerGames.getInstance().getSettingsManager().getCurrentSettings();
        }
        return gameSettings;
    }

    public void resetPlayer(Player player) {
        player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        DisguiseAPI.undisguiseToAll(player);
    }

    public void teleportStart() {
        List<UUID> tributes = new LinkedList<>(tributesTeam.getMembers());
        this.spawns.entrySet().forEach(entry -> entry.setValue(null));
        List<Entry<Integer, UUID>> spawns = new ArrayList<>(this.spawns.entrySet());
        int spawnIndex = 0;
        for (UUID tribute : tributes) {
            Player player = Bukkit.getPlayer(tribute);
            if (player != null) {
                boolean spawnFound = false;
                int index = 0;
                if (this.tributesTeam.size() > 0) {
                    index = spawnIndex;
                    spawnIndex += Math.max(1, (int) Math.floor(spawns.size() / this.tributesTeam.size()));
                } else {
                    for (int i = 0; i < spawns.size(); i++) {
                        if (spawns.get(i) == null) {
                            index = i;
                        }
                    }
                }

                Location spawn = SpigotUtils.positionToLocation(map.getWorld(), this.map.getSpawns().get(index));
                player.teleport(spawn);
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                player.setSaturation(20);
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                this.spawns.put(index, player.getUniqueId());
            }
        }

        Location spawn = SpigotUtils.positionToLocation(map.getWorld(), map.getCenter());
        this.spectatorsTeam.teleportAll(spawn);
        this.hiddenStaffTeam.teleportAll(spawn);
        for (GamePlayer value : this.players.values()) {
            SpigotUser user = value.getUser();
            user.setScoreboard(null);
            new GameBoard(this, user);
        }
    }

    public void teleportDeathmatch() {
        this.spawns.entrySet().forEach(entry -> entry.setValue(null));
        List<Entry<Integer, UUID>> spawns = new ArrayList<>(this.spawns.entrySet());
        Collections.shuffle(spawns);

        for (UUID tribute : this.tributesTeam) {
            Player player = Bukkit.getPlayer(tribute);
            for (Entry<Integer, UUID> entry : spawns) {
                if (entry.getValue() == null) {
                    Position position = this.map.getSpawns().get(entry.getKey());
                    Location spawn = new Location(this.map.getWorld(), position.getX(), position.getY(), position.getZ(), position.getYaw(), position.getPitch());
                    player.teleport(spawn);
                    entry.setValue(player.getUniqueId());
                    break;
                }
            }
        }

        Iterator<UUID> mutationIterator = this.mutationsTeam.iterator();
        while (mutationIterator.hasNext()) {
            UUID mutation = mutationIterator.next();
            resetMutation(mutation);
            Player player = Bukkit.getPlayer(mutation);
            player.sendMessage(ManiaUtils.color("&d&l<< &7You left &dMutations"));
            spectatorsTeam.join(mutation);
            mutationIterator.remove();
        }

        Location center = SpigotUtils.positionToLocation(map.getWorld(), map.getCenter());
        this.spectatorsTeam.teleportAll(center);
        this.hiddenStaffTeam.teleportAll(center);
    }

    @SuppressWarnings("unused")
    public void resetMutation(UUID mutation) {
        Player player = Bukkit.getPlayer(mutation);
        player.getInventory().clear();
        User user = ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
        user.getStat(Stats.COINS).setValue(user.getStat(Stats.COINS).getAsInt() + 100);
        try {
            player.getInventory().setArmorContents(null);
        } catch (Exception e) {
        }
        DisguiseAPI.undisguiseToAll(player);
    }

    public void beginCountdown() {
        this.state = State.COUNTDOWN;
        this.countdownStart = System.currentTimeMillis();
        this.task = new GameTask(this);
        this.task.start();

        GameManager gameManager = HungerGames.getInstance().getGameManager();
        if (gameManager.getGameCounter() + 1 >= gameSettings.getMaxGames()) {
            Set<UUID> players = new HashSet<>(tributesTeam.getMembers());
            players.addAll(spectatorsTeam.getMembers());
            players.addAll(hiddenStaffTeam.getMembers());

            for (UUID u : players) {
                Player player = Bukkit.getPlayer(u);
                player.sendMessage(ManiaUtils.color("&4&l>> &c&lTHE SERVER WILL RESTART AFTER THIS GAME FOR CLEANUP!"));
            }
        }
    }

    public void startGame() {
        this.state = State.PLAYING;
        this.playerTrackerTask.start();
        this.gameStart = System.currentTimeMillis();
        if (gameSettings.getGracePeriodLength() > 0) {
            this.gracePeriodEnd = this.gameStart + TimeUnit.SECONDS.toMillis(gameSettings.getGracePeriodLength());
            sendMessage("&6&l>> &2&lTHERE IS A " + gameSettings.getGracePeriodLength() + " SECOND GRACE PERIOD!");
        }
        for (UUID tribute : this.tributesTeam) {
            GamePlayer player = this.players.get(tribute);
            player.getUser().incrementStat(Stats.HG_GAMES);
            try {
                Perks.SPEED_RACER.activate(player.getUser());
            } catch (Exception e) {
            }
        }
    }

    public void sendMessage(String message) {
        sendMessage(message, null);
    }

    public List<Integer> getArchivedProfiles() {
        for (GamePlayer value : this.players.values()) {
            this.profiles.add(value.getUser().getId());
        }
        return new ArrayList<>(profiles);
    }

    public void addPlayer(UUID uuid) {
        if (!this.players.containsKey(uuid)) {
            if (this.cachedPlayers.containsKey(uuid)) {
                this.players.put(uuid, this.cachedPlayers.get(uuid));
                this.cachedPlayers.remove(uuid);
            } else {
                this.players.put(uuid, new GamePlayer(uuid));
            }
        }
    }

    public ReviveResult revivePlayer(GamePlayer gamePlayer, CommandSender sender) {
        SpigotUser user = gamePlayer.getUser();
        if (!gamePlayer.isSpectatorByDeath()) {
            return ReviveResult.WAS_NOT_A_TRIBUTE;
        }

        if (!(getState() == State.PLAYING || getState() == State.PLAYING_DEATHMATCH)) {
            return ReviveResult.INVALID_STATE;
        }

        spectatorsTeam.leave(gamePlayer.getUniqueId());
        tributesTeam.join(gamePlayer.getUniqueId());
        if (!tributesTeam.isMember(gamePlayer.getUniqueId())) {
            return ReviveResult.SPAWN_ERROR;
        } else {
            String senderName;
            if (sender instanceof Player) {
                senderName = user.getColoredName();
            } else {
                senderName = "&4&lCONSOLE";
            }
            sendMessage("&5&l>> " + user.getColoredName() + " &6has been &6&lREVIVED &6by " + senderName);
        }
        gamePlayer.setRevivedInfo(true, sender);
        return ReviveResult.SUCCESS;
    }

    public ForceAddResult forceAddPlayer(GamePlayer gamePlayer, CommandSender sender) {
        if (gamePlayer.isSpectatorByDeath()) {
            sender.sendMessage(ManiaUtils.color("&cThat player was a tribute originally. Please use the command /hungergames revive instead."));
            return ForceAddResult.WAS_TRIBUTE;
        }

        if (!(getState() == State.PLAYING || getState() == State.PLAYING_DEATHMATCH || getState() == State.COUNTDOWN)) {
            sender.sendMessage(ManiaUtils.color("&cInvalid game state to add a tribute."));
            return ForceAddResult.INVALID_STATE;
        }

        spectatorsTeam.leave(gamePlayer.getUniqueId());
        tributesTeam.join(gamePlayer.getUniqueId());
        if (!tributesTeam.isMember(gamePlayer.getUniqueId())) {
            sender.sendMessage(ManiaUtils.color("&cThere was a problem finding a spawn for that player. They were set as a spectator as a result."));
            return ForceAddResult.SPAWN_ERROR;
        } else {
            String senderName;
            if (sender instanceof Player) {
                SpigotUser user = (SpigotUser) ManiaCore.getInstance().getUserManager().getUser(((Player) sender).getUniqueId());
                senderName = user.getColoredName();
            } else {
                senderName = "&4&lCONSOLE";
            }
            sendMessage("&5&l>> " + gamePlayer.getUser().getColoredName() + " &6has been &6&lADDED &6to the game by " + senderName);
        }

        gamePlayer.setForcefullAddedInfo(true, sender);
        return ForceAddResult.SUCCESS;
    }

    public void beginDeathmatch() {
        if (this.state == State.PLAYING) {
            this.state = State.PLAYING_DEATHMATCH;
            this.deathmatchPlayingStart = System.currentTimeMillis();
            sendMessage("&6&l>> &4&lTHE DEATHMATCH COUNTDOWN HAS STARTED!");
        }
    }

    public void startDeathmatchCountdown() {
        this.state = State.DEATHMATCH_COUNTDOWN;
        this.deathmatchCountdownStart = System.currentTimeMillis();
        sendMessage("&6&l>> &e&lPREPARE FOR THE DEATHMATCH...");
        teleportDeathmatch();
        this.tributesTeam.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 200, -50));
        });
    }

    public void startDeathmatch() {
        this.state = State.DEATHMATCH;
        this.deathmatchStart = System.currentTimeMillis();
        Location center = new Location(map.getWorld(), map.getCenter().getX(), map.getCenter().getY(), map.getCenter().getZ());
        map.getWorld().getWorldBorder().setCenter(center);
        map.getWorld().getWorldBorder().setSize(map.getBorderDistance());
        map.getWorld().getWorldBorder().setSize(10, (TimeUnit.MINUTES.toMillis(gameSettings.getDeathmatchLength()) * 60));
        sendMessage("&6&l>> &a&lLAST PLAYER STANDING CLAIMS VICTORY.");
        restockChests();
        for (UUID tribute : this.tributesTeam) {
            GamePlayer player = this.players.get(tribute);
            player.getUser().incrementStat(Stats.HG_DEATHMATCHES);
            User user = HungerGames.getInstance().getManiaCore().getUserManager().getUser(tribute);
            Pair<Integer, String> result = user.addCoins(50, gameSettings.isCoinMultiplier());
            player.setEarnedCoins(player.getEarnedCoins() + result.getValue1());
            user.sendMessage("&2&l>> &a+" + result.getValue1() + " &3COINS&a! " + result.getValue2());
            user.addNetworkExperience(5);
            for (PotionEffect effect : player.getUser().getBukkitPlayer().getActivePotionEffects()) {
                player.getUser().getBukkitPlayer().removePotionEffect(effect.getType());
            }
        }
    }

    public void restockChests() {
        this.lootedChests.clear();
        sendMessage("&6&l>> &a&lALL CHESTS HAVE BEEN RESTOCKED!");
    }

    public void endGame() {
        if (state != State.ENDING) {
            this.state = State.ENDING;

            this.gameEnd = System.currentTimeMillis();
            this.archived = true;

            for (Player player : Bukkit.getOnlinePlayers()) {
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    player.showPlayer(player1);
                }
                player.setAllowFlight(true);
            }

            String winnerName;
            UUID winner = null;
            if (this.tributesTeam.size() == 1) {
                winner = new ArrayList<>(tributesTeam.getMembers()).get(0);
                Player player = Bukkit.getPlayer(winner);
                GamePlayer gamePlayer = this.players.get(player.getUniqueId());
                gamePlayer.getUser().incrementStat(Stats.HG_WINS);
                User user = HungerGames.getInstance().getManiaCore().getUserManager().getUser(player.getUniqueId());
                Pair<Integer, String> result = user.addCoins(100, gameSettings.isCoinMultiplier());
                gamePlayer.setEarnedCoins(gamePlayer.getEarnedCoins() + result.getValue1());
                user.sendMessage("&2&l>> &a+" + result.getValue1() + " &3COINS&a! " + result.getValue2());
                user.addNetworkExperience(30);
                user.incrementStat(Stats.HG_WINSTREAK);
                winnerName = user.getDisplayName();
            } else {
                winnerName = "&f&lNo one";
            }

            sendMessage("&e&l/  /  /  /  / &6&lHUNGER MANIA &e&l/  /  /  /  /");
            sendMessage("");
            sendMessage("");
            sendMessage(winnerName + "&a&l won the Hunger Games!");
            sendMessage("");
            sendMessage("");

            HungerGames.getInstance().getManiaCore().getDatabase().addRecordToQueue(new GameRecord(this));
            for (GamePlayer gp : this.players.values()) {
                if (!gp.getUser().getUniqueId().equals(winner)) {
                    gp.getUser().getStat(Stats.HG_WINSTREAK).setValue(0);
                    Statistic fakeWinStreak = gp.getUser().getFakedStat(Stats.HG_WINSTREAK);
                    if (fakeWinStreak != null) {
                        fakeWinStreak.setValue(0);
                    }
                }
                Redis.pushUser(gp.getUser());
                gp.getUser().setScoreboard(null);
            }
            HungerGames.getInstance().getManiaCore().getDatabase().pushQueue();
            HungerGames.getInstance().getGameManager().setGameCounter(HungerGames.getInstance().getGameManager().getGameCounter() + 1);
            ManiaCore.getInstance().getMemoryManager().removeMemoryHook("Game Spectator Update");
            ManiaCore.getInstance().getMemoryManager().removeMemoryHook("Enderman Mutation Damage");
        }
    }

    public void resetServer() {
        if (HungerGames.getInstance().getGameManager().getGameCounter() >= gameSettings.getMaxGames()) {
            TimoCloudAPI.getBukkitAPI().getThisServer().setState("RESTARTING");
            Bukkit.getServer().shutdown();
        }

        HungerGames.getInstance().getMapManager().deleteMap(map.getName());
    }

    public void addLootedChest(Location location) {
        this.lootedChests.add(location);
    }

    public boolean isLootedChest(Location location) {
        return this.lootedChests.contains(location);
    }

    public void removePlayer(UUID uuid) {
        GamePlayer gamePlayer = getPlayer(uuid);
        this.cachedPlayers.put(uuid, gamePlayer);

        this.tributesTeam.leave(uuid);
        this.mutationsTeam.leave(uuid);
        this.spectatorsTeam.leave(uuid);
        this.hiddenStaffTeam.leave(uuid);
        this.players.remove(uuid);
    }

    public void killPlayer(UUID uniqueId, DeathInfo deathInfo) {
        if (!this.players.containsKey(uniqueId)) {
            return;
        }
        GamePlayer gamePlayer = this.players.get(uniqueId);
        Player player = Bukkit.getPlayer(gamePlayer.getUniqueId());
        player.getInventory().clear();
        gamePlayer.getUser().incrementStat(Stats.HG_DEATHS);
        gamePlayer.setDeathInfo(deathInfo);
        Statistic points = gamePlayer.getUser().getStat(Stats.HG_SCORE);
        int lost = (int) Math.ceil((float) points.getAsInt() / 8F);
        int gained = lost;
        if (points.getAsInt() > 0) {
            points.setValue(points.getAsInt() - lost);
            Statistic fakedStat = gamePlayer.getUser().getFakedStat(Stats.HG_SCORE);
            if (fakedStat != null) {
                fakedStat.setValue(fakedStat.getAsInt() - lost);
            }
            gamePlayer.getUser().sendMessage("&4&l>> &cYou lost " + lost + " Score for dying.");
        }

        if (tributesTeam.isMember(uniqueId)) {
            sendMessage("&6&l>> &c&l" + (tributesTeam.size() - 1) + " tributes remain.");
            if ((this.tributesTeam.size() - 1) <= gameSettings.getDeathmatchPlayerStart()) {
                this.beginDeathmatch();
            }
        } else if (mutationsTeam.isMember(uniqueId)) {
            sendMessage("&6&l>> &d&l" + (mutationsTeam.size() - 1) + " mutations remain.");
        }

        if (firstKiller != null) {
            if (tributesTeam.isMember(uniqueId)) {
                playSound(Sound.WITHER_SPAWN);
            } else if (mutationsTeam.isMember(uniqueId)) {
                playSound(Sound.ZOMBIE_PIG_DEATH);
            }
        }

        this.tributesTeam.leave(gamePlayer.getUniqueId());
        this.mutationsTeam.leave(gamePlayer.getUniqueId());

        if (deathInfo.getType() != DeathType.LEAVE) {
            spectatorsTeam.join(gamePlayer.getUniqueId());
            gamePlayer = players.get(uniqueId);
            gamePlayer.setSpectatorByDeath(true);
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        GamePlayer killer = null;
        String killerName = null;
        boolean mutationKill = false;
        boolean sendDeathMessage = true;
        double killerHealth = 0;
        GameTeam newTeam = this.spectatorsTeam;
        if (deathInfo instanceof DeathInfoPlayerKill) {
            DeathInfoPlayerKill playerDeath = (DeathInfoPlayerKill) deathInfo;
            killerName = "";
            if (tributesTeam.isMember(playerDeath.getKiller())) {
                killerName += "&a";
            } else if (mutationsTeam.isMember(playerDeath.getKiller())) {
                killerName += "&d";
            } else if (spectatorsTeam.isMember(playerDeath.getKiller())) {
                killerName += "&c";
            }

            killer = this.players.get(playerDeath.getKiller());
            mutationKill = playerDeath.isMutationKill();
            killerHealth = playerDeath.getKillerHealth();
        } else if (deathInfo instanceof DeathInfoSuicide) {
            if (mutationsTeam.isMember(gamePlayer.getUniqueId())) {
                if (gamePlayer.getMutationType() == MutationType.CREEPER) {
                    UUID target = gamePlayer.getMutationTarget();
                    if (!tributesTeam.isMember(target)) {
                        sendDeathMessage = false;
                    }
                }
            }
        } else if (deathInfo instanceof DeathInfoKilledSuicide) {
            DeathInfoKilledSuicide death = (DeathInfoKilledSuicide) deathInfo;
            killerName = "";
            if (tributesTeam.isMember(death.getKiller())) {
                killerName += "&a";
            } else {
                killerName += "&d";
            }

            killer = this.players.get(death.getKiller());
            Player killerPlayer = Bukkit.getPlayer(killer.getUniqueId());
            killerPlayer.setMaxHealth(gameSettings.getMaxHealth());
            newTeam = this.tributesTeam;
            mutationKill = true;
            killerHealth = 20; //TODO Add better tracking of this for this type of death
        }

        if (killer != null) {
            Player killerPlayer = Bukkit.getPlayer(killer.getUniqueId());
            killer.setKillStreak(killer.getKillStreak() + 1);
            killer.setKills(killer.getKills() + 1);
            if (killer.getUser().getStat(Stats.HG_HIGHEST_KILL_STREAK).getAsInt() < killer.getKillStreak()) {
                killer.getUser().setStat(Stats.HG_HIGHEST_KILL_STREAK, killer.getKillStreak());
            }

            SpigotUser killerUser = killer.getUser();

            int coins = 25, experience = 10;

            killer.setKillStreak(killer.getKillStreak() + 1);
            killer.setKills(killer.getKills() + 1);
            if (killer.getUser().getStat(Stats.HG_HIGHEST_KILL_STREAK).getAsInt() < killer.getKillStreak()) {
                gained += (int) Math.ceil(gained / 3);
                killer.getUser().setStat(Stats.HG_HIGHEST_KILL_STREAK, killer.getKillStreak());
            }
            if (killer.getUser().getNickname() != null && killer.getUser().getNickname().isActive()) {
                killerName += killer.getUser().getNickname().getName();

            } else {
                killerName += killer.getUser().getName();
            }
            if (firstKiller == null) {
                sendMessage("&6&l>> &c&l" + (ChatColor.stripColor(ManiaUtils.color(killerName)) + " drew first blood!").toUpperCase());
                playSound(Sound.WOLF_HOWL);
                this.firstKiller = killer.getUniqueId();
                experience += 15;
                coins += 15;
                gained += (int) Math.ceil((gained / 2));
            }

            newTeam.join(uniqueId);

            if (tributesTeam.size() == 1) {
                gained += gained;
            }

            Statistic killerScore = killer.getUser().getStat(Stats.HG_SCORE);
            killerScore.setValue(killerScore.getAsInt() + gained);
            Statistic killerFakeScore = killer.getUser().getFakedStat(Stats.HG_SCORE);
            if (killerFakeScore != null) {
                killerFakeScore.setValue(killerFakeScore.getAsInt() + gained);
            }
            killer.getUser().sendMessage("&6&l>> &a+" + gained + " Score!");

            gamePlayer.getUser().sendMessage("&4&l>> &cYour killer &8(" + killerName + "&8) &chad &4" + Utils.formatNumber(killerHealth) + " HP &cremaining!");

            if (mutationKill) {
                sendMessage("&6&l>> " + killerName + " &ahas taken revenge , and is back in the game!");
                DisguiseAPI.undisguiseToAll(killerPlayer);
                killerPlayer.setMaxHealth(gameSettings.getMaxHealth());
                killerPlayer.setHealth(gameSettings.getMaxHealth());
                killer.setRevengeTime(System.currentTimeMillis());
                PlayerInventory inventory = killerPlayer.getInventory();
                inventory.clear();
                inventory.setArmorContents(null);
            } else {
                try {
                    Perks.SPEED_KILL.activate(killerUser);
                    Perks.RESISTANCE.activate(killerUser);
                    Perks.ABSORPTION.activate(killerUser);
                    Perks.ASSASSIN.activate(killerUser);
                    Perks.REGEN.activate(killerUser);
                    Perks.SURVIVALIST.activate(killerUser);
                    Perks.MIRACLE.activate(killerUser);
                    Perks.ENCHANT_XP_BOOST.activate(killerUser);
                    Perks.BETTY.activate(killerUser);
                } catch (Exception e) {
                }
            }

            gamePlayer.setKillStreak(0);
            killerUser.addNetworkExperience(experience);
            killerUser.sendMessage("&6&l>> &f&lCurrent Streak: &a" + killer.getKillStreak() + "   &f&lPersonal Best: &a" + killer.getUser().getStat(Stats.HG_HIGHEST_KILL_STREAK).getAsInt());
            Pair<Integer, String> result = killerUser.addCoins(coins, gameSettings.isCoinMultiplier());
            killer.setEarnedCoins(killer.getEarnedCoins() + result.getValue1());
            killerUser.sendMessage("&2&l>> &a+" + result.getValue1() + " &3COINS&a! " + result.getValue2());

            for (UUID mutation : this.mutationsTeam) {
                GamePlayer gp = this.players.get(mutation);
                if (gp.getMutationTarget() != null) {
                    if (gp.getMutationTarget().equals(uniqueId)) {
                        gp.setMutationTarget(killerUser.getUniqueId());
                        gp.getUser().sendMessage("&6&l>> &eYour previous target died. Your new target is &a" + killerName);
                    }
                }
            }
        }

        if (sendDeathMessage) {
            this.sendMessage(deathInfo.getDeathMessage(this));
        }

        new BukkitRunnable() {
            public void run() {
                if (state == State.ENDING || state == State.ARCHIVED) {
                    cancel();
                    return;
                }
                checkWin();
            }
        }.runTaskLater(HungerGames.getInstance(), 1L);
    }

    public void checkWin() {
        if (this.state != State.ENDING) {
            if (this.tributesTeam.size() < 2) {
                this.endGame();
            }
        }
    }

    public boolean isLootedChest(Block block) {
        return this.lootedChests.contains(block.getLocation());
    }

    public void setPlayerTrackers(boolean playerTrackers) {
        this.playerTrackers = playerTrackers;

        if (playerTrackers) {
            sendMessage("&6&l>> &ePlayer trackers have been &a&lENABLED&e!");
        } else {
            sendMessage("&6&l>> &ePlayer trackers have been &c&lDISABLED&e!");
        }
    }

    public void sendMessage(String message, Rank permission) {
        getMessager().sendMessage(message, permission);
    }

    public void mutatePlayer(UUID uniqueId, Mutation mutation, UUID target) {
        GamePlayer gamePlayer = this.players.get(uniqueId);
        gamePlayer.setMutating(true);
        gamePlayer.setMutationTarget(target);
        gamePlayer.setMutationType(mutation.getType());
        gamePlayer.getUser().sendMessage("&6&l>> &eYou will mutate as a(n) &e&l" + mutation.getName() + "&e!");
        gamePlayer.getUser().sendMessage("&6&l>> &eYou will mutate in &e&l15 Seconds&e!");
        GamePlayer targetPlayer = this.players.get(target);
        new BukkitRunnable() {
            private int secondsLeft = 15;
            private final Set<Integer> ANNOUNCE = new HashSet<>(Arrays.asList(15, 10, 5, 3, 2, 1));

            public void run() {
                if (state == State.DEATHMATCH || state == State.ENDING || state == State.DEATHMATCH_COUNTDOWN) {
                    cancel();
                    gamePlayer.getUser().sendMessage("&6&l>> State is no longer valid for mutations.");
                    gamePlayer.setMutating(false);
                    return;
                }

                if (!tributesTeam.isMember(gamePlayer.getMutationTarget())) {
                    cancel();
                    gamePlayer.getUser().sendMessage("&6&l>> &cYour target has died. You cannot mutate anymore.");
                    gamePlayer.setMutating(false);
                    return;
                }

                if (!gameSettings.isMutations()) {
                    cancel();
                    gamePlayer.getUser().sendMessage("&6&l>> &cMutations have been disabled.");
                    gamePlayer.setMutating(false);
                    return;
                }

                if (secondsLeft == 0) {
                    int spawn = -1;
                    for (Entry<Integer, UUID> entry : spawns.entrySet()) {
                        if (entry.getValue() != null && entry.getValue().equals(uniqueId)) {
                            spawn = entry.getKey();
                        }
                    }

                    if (spawn == -1) {
                        for (Entry<Integer, UUID> entry : spawns.entrySet()) {
                            if (entry.getValue() == null) {
                                spawn = entry.getKey();
                            }
                        }
                    }

                    if (spawn == -1) {
                        spawn = ManiaCore.RANDOM.nextInt(spawns.size());
                    }

                    Position position = map.getSpawns().get(spawn);
                    Location location = SpigotUtils.positionToLocation(map.getWorld(), position);
                    spectatorsTeam.leave(uniqueId);
                    mutationsTeam.join(uniqueId);
                    Player player = Bukkit.getPlayer(uniqueId);
                    mutation.applyPlayer(player);
                    player.setFallDistance(0);
                    player.teleport(location);
                    sendMessage("&6&l>> &d" + gamePlayer.getUser().getName() + " &6has &6&lMUTATED &6as a(n) &6&l" + mutation.getName() + " &6and seeks revenge on &a" + targetPlayer.getUser().getName());
                    player.getInventory().addItem(HungerGames.getInstance().getLootManager().getPlayerTracker().generateItemStack());
                    gamePlayer.setHasMutated(true);
                    gamePlayer.setMutating(false);
                    cancel();
                } else {
                    secondsLeft--;
                    gamePlayer.getUser().sendMessage("&6&l>> &e&lMUTATING: " + secondsLeft + "s...");
                    if (this.ANNOUNCE.contains(secondsLeft)) {
                        targetPlayer.getUser().sendMessage("&6&l>> &b" + gamePlayer.getUser().getName() + " &e&lis MUTATING against you: " + secondsLeft + "s");
                    }
                }
            }
        }.runTaskTimer(HungerGames.getInstance(), 0L, 20L);
    }

    public void setMutations(boolean value) {
        if (!value) {
            for (UUID mutation : this.mutationsTeam) {
                mutationsTeam.leave(mutation);
            }
        }

        String status;
        if (value) {
            status = "&a&lENABLED";
        } else {
            status = "&c&lDISABLED";
        }
        sendMessage("&6&l>> &eMutations have been " + status);
        gameSettings.setMutations(value);
    }

    public void setGameSettings(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    public void playSound(Sound sound) {
        Set<UUID> players = new HashSet<>(tributesTeam.getMembers());
        players.addAll(spectatorsTeam.getMembers());
        players.addAll(mutationsTeam.getMembers());
        players.addAll(hiddenStaffTeam.getMembers());

        for (UUID p : players) {
            Player player = Bukkit.getPlayer(p);
            player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
        }
    }

    public PlayerType getPlayerType(UUID uuid) {
        if (!this.players.containsKey(uuid)) {
            return PlayerType.UNKNOWN;
        } else if (this.tributesTeam.isMember(uuid)) {
            return PlayerType.TRIBUTE;
        } else if (this.spectatorsTeam.isMember(uuid)) {
            return PlayerType.SPECTATOR;
        } else if (this.mutationsTeam.isMember(uuid)) {
            return PlayerType.MUTATION;
        } else if (this.hiddenStaffTeam.isMember(uuid)) {
            return PlayerType.HIDDEN_STAFF;
        } else {
            return PlayerType.UNKNOWN;
        }
    }

    public GameTeam getGameTeam(UUID player) {
        if (tributesTeam.isMember(player)) {
            return tributesTeam;
        } else if (mutationsTeam.isMember(player)) {
            return mutationsTeam;
        } else if (spectatorsTeam.isMember(player)) {
            return spectatorsTeam;
        } else if (hiddenStaffTeam.isMember(player)) {
            return hiddenStaffTeam;
        }
        return null;
    }

    public GamePlayer[] getPlayers() {
        return this.players.values().toArray(new GamePlayer[0]);
    }

    public GamePlayer getTopKiller() {
        GamePlayer topKiller = null;
        for (GamePlayer p : getPlayers()) {
            if (p.getKills() > 0) {
                if (topKiller == null) {
                    topKiller = p;
                } else {
                    if (p.getKills() > topKiller.getKills()) {
                        topKiller = p;
                    }
                }
            }
        }
        return topKiller;
    }

    public void nextGame() {
        System.out.println("Determining Next game");
        boolean restarting = HungerGames.getInstance().getGameManager().getGameCounter() >= getGameSettings().getMaxGames();
        if (restarting) {
            System.out.println("Server is restarting");
            boolean singleServerFits = false;
            for (ServerObject server : TimoCloudAPI.getUniversalAPI().getServerGroup("HG").getServers()) {
                System.out.println("Checking server " + server.getName());
                if (server.getState().equalsIgnoreCase("lobby") || server.getState().equalsIgnoreCase("online")) {
                    System.out.println("Server is in lobby state");
                    int remainingSlots = gameSettings.getMaxPlayers() - server.getOnlinePlayerCount();
                    System.out.println("Server has " + remainingSlots + " slots left");
                    if (remainingSlots >= Bukkit.getOnlinePlayers().size()) {
                        System.out.println("Remaining slots is higher than current server player count");
                        singleServerFits = true;
                        System.out.println("Moving players to " + server.getName());
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            PlayerObject playerObject = TimoCloudAPI.getUniversalAPI().getPlayer(player.getUniqueId());
                            playerObject.sendToServer(server);
                        }
                        System.out.println("Done");
                        break;
                    }
                }
            }

            if (!singleServerFits) {
                System.out.println("One server does not fit all of the players, moving the remaining players to other servers");
                Iterator<? extends Player> players = new ArrayList<>(Bukkit.getOnlinePlayers()).iterator();
                System.out.println("Player count " + Bukkit.getOnlinePlayers().size());
                for (ServerObject server : TimoCloudAPI.getUniversalAPI().getServerGroup("HG").getServers()) {
                    System.out.println("Checking server " + server.getName());
                    if (server.getState().equalsIgnoreCase("lobby") || server.getState().equalsIgnoreCase("online")) {
                        System.out.println("Server is in lobby state");
                        int freeSlots = gameSettings.getMaxPlayers() - server.getOnlinePlayerCount();
                        System.out.println("Server has " + freeSlots + " free slots");
                        while (players.hasNext() && freeSlots > 0) {
                            Player p = players.next();
                            System.out.println("Moving player " + p.getName() + " to server " + server.getName());
                            PlayerObject playerObject = TimoCloudAPI.getUniversalAPI().getPlayer(p.getUniqueId());
                            playerObject.sendToServer(server);
                            System.out.println("Done");
                            freeSlots--;
                        }
                    }
                }

                if (players.hasNext()) {
                    System.out.println("Players still remain, meaning other HG servers are full, moving to hub");
                    ServerGroupObject hubGroup = TimoCloudAPI.getUniversalAPI().getServerGroup("Hub");
                    List<ServerObject> connectableServers = new ArrayList<>();
                    for (ServerObject server : hubGroup.getServers()) {
                        if (server.getOnlinePlayerCount() < server.getMaxPlayerCount()) {
                            connectableServers.add(server);
                        }
                    }

                    for (ServerObject server : connectableServers) {
                        while (players.hasNext() && server.getOnlinePlayerCount() < server.getMaxPlayerCount()) {
                            Player p = players.next();
                            PlayerObject playerObject = TimoCloudAPI.getUniversalAPI().getPlayer(p.getUniqueId());
                            playerObject.sendToServer(server);
                        }
                    }

                    if (players.hasNext()) {
                        while (players.hasNext()) {
                            Player player = players.next();
                            player.kickPlayer(ManiaUtils.color("&cAll games and hubs are full."));
                        }
                    }
                }
            }
        } else {
            HungerGames.getInstance().getLobby().fromGame(this);
            HungerGames.getInstance().getGameManager().setCurrentGame(null);
        }
    }
}