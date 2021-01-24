package net.hungermania.hungergames.lobby;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import me.libraryaddict.disguise.DisguiseAPI;
import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.game.Game;
import net.hungermania.hungergames.game.GamePlayer;
import net.hungermania.hungergames.game.VoteTimer;
import net.hungermania.hungergames.map.HGMap;
import net.hungermania.hungergames.map.MapManager;
import net.hungermania.hungergames.profile.LobbyBoard;
import net.hungermania.hungergames.records.GameRecord;
import net.hungermania.hungergames.settings.GameSettings;
import net.hungermania.hungergames.util.Messager;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.redis.Redis;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.user.toggle.Toggles;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.memory.MemoryHook;
import net.hungermania.maniacore.memory.MemoryHook.Task;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import net.hungermania.maniacore.spigot.util.SpigotUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings("DuplicatedCode")
public class Lobby implements Listener, CommandExecutor {
    private HungerGames plugin;
    private GameSettings gameSettings;
    private Set<SpigotUser> players = new HashSet<>(), hiddenStaff = new HashSet<>();
    private MapOptions mapOptions = new MapOptions();
    private Set<UUID> voteStart = new HashSet<>();
    private Location location;
    private VoteTimer voteTimer;
    private Game game;
    private LobbySigns lobbySigns;
    private Set<HGMap> playedMaps = new HashSet<>();
    private Messager messager;
    
    public Lobby(HungerGames plugin, Location location) {
        this.location = location;
        this.plugin = plugin;
        this.gameSettings = plugin.getSettingsManager().getCurrentSettings();
        this.lobbySigns = new LobbySigns();
        this.messager = new LobbyMessanger(this);
        
        MemoryHook voidDetector = new MemoryHook("Lobby Void Detector");
        ManiaCore.getInstance().getMemoryManager().addMemoryHook(voidDetector);
        new BukkitRunnable() {
            public void run() {
                if (game != null) { return; }
                Task task = voidDetector.task().start();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getLocation().getBlockY() < 0) {
                        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                    }
                }
                task.end();
            }
        }.runTaskTimer(HungerGames.getInstance(), 20L, 20L);
        
        MemoryHook playerCount = new MemoryHook("Lobby Player Count Auto Start");
        ManiaCore.getInstance().getMemoryManager().addMemoryHook(playerCount);
        new BukkitRunnable() {
            public void run() {
                Task task = playerCount.task().start();
                if (game == null) {
                    if (voteTimer == null) {
                        if (players.size() >= gameSettings.getMinPlayers()) {
                            startTimer();
                        }
                    } else {
                        if (voteTimer.getRemainingSeconds() == 0) {
                            voteTimer = null;
                            if (players.size() >= gameSettings.getMinPlayers()) {
                                startTimer();
                            }
                        }
                    }
                }
                task.end();
            }
        }.runTaskTimer(HungerGames.getInstance(), 20L, 20L);
        
        MemoryHook mapAnnounce = new MemoryHook("Map Announcement");
        ManiaCore.getInstance().getMemoryManager().addMemoryHook(mapAnnounce);
        new BukkitRunnable() {
            public void run() {
                Task task = mapAnnounce.task().start();
                if (mapOptions.size() < gameSettings.getMaxMapOptions()) {
                    for (HGMap hgMap : plugin.getMapManager().getMaps().values()) {
                        if (!mapOptions.containsMap(hgMap)) {
                            mapOptions.addMap(hgMap);
                        }
                    }
                }
                
                
                sendMessage("&6&l>> &e&lVOTING OPTIONS - &7Type /map <position> to vote!");
                for (Entry<Integer, HGMap> entry : mapOptions.getMaps().entrySet()) {
                    int votes = mapOptions.getVotes(entry.getValue());
                    
                    String[] creators = entry.getValue().getCreators().toArray(new String[0]);
                    StringBuilder creatorNames = new StringBuilder();
                    
                    for (int i = 0; i < creators.length; i++) {
                        if (i == 0) {
                            creatorNames.append("&3").append(creators[i]);
                        } else if (i == (creators.length - 1)) {
                            creatorNames.append(", &7&oand &3").append(creators[i]);
                        } else {
                            creatorNames.append("&7&o, &3").append(creators[i]);
                        }
                    }
                    
                    sendMessage("&6&l> &c&l" + entry.getKey() + "&4: &b" + entry.getValue().getName() + " &7&oby " + creatorNames.toString() + " &7&o(" + votes + " Votes)");
                }
                if (players.size() < gameSettings.getMinPlayers()) {
                    if (voteTimer == null) {
                        sendMessage("&6&l>> &e&lThe game needs &b&l" + (Math.abs(players.size() - gameSettings.getMinPlayers())) + " more players&e&l to start.");
                    }
                }
                task.end();
            }
        }.runTaskTimer(plugin, 20L, 600L);
        
        MemoryHook worldCheck = new MemoryHook("Lobby World Checker");
        ManiaCore.getInstance().getMemoryManager().addMemoryHook(worldCheck);
        new BukkitRunnable() {
            @Override
            public void run() {
                Task task = worldCheck.task().start();
                World world = Bukkit.getWorld("world");
                
                if (world.hasStorm()) {
                    world.setStorm(false);
                    world.setWeatherDuration(Integer.MAX_VALUE);
                }
                
                if (world.isThundering()) {
                    world.setThundering(false);
                    world.setThunderDuration(Integer.MAX_VALUE);
                }
                
                if (game == null) {
                    if (world.getDifficulty() != Difficulty.PEACEFUL) {
                        world.setDifficulty(Difficulty.PEACEFUL);
                    }
                }
                task.end();
            }
        }.runTaskTimer(HungerGames.getInstance(), 100L, 200L);
        
        MemoryHook playerChecker = new MemoryHook("Lobby Player Checker");
        ManiaCore.getInstance().getMemoryManager().addMemoryHook(playerChecker);
        new BukkitRunnable() {
            public void run() {
                Task task = playerChecker.task().start();
                boolean mapEditing = false;
                
                for (HGMap map : plugin.getMapManager().getMaps().values()) {
                    if (map.isEditmode()) {
                        mapEditing = true;
                        break;
                    }
                }
                
                if (!mapEditing) {
                    Set<SpigotUser> users = new HashSet<>(players);
                    users.addAll(hiddenStaff);
                    if (game == null) {
                        for (SpigotUser user : users) {
                            if (user != null) {
                                if (user.getBukkitPlayer() != null) {
                                    user.getBukkitPlayer().setMaxHealth(20);
                                    user.getBukkitPlayer().setHealth(20);
                                    user.getBukkitPlayer().setFoodLevel(20);
                                    user.getBukkitPlayer().setSaturation(20);
                                    user.getBukkitPlayer().setGameMode(GameMode.SURVIVAL);
                                    user.getBukkitPlayer().getInventory().clear();
                                    user.getBukkitPlayer().getInventory().setArmorContents(null);
                                    user.getBukkitPlayer().setLevel(0);
                                    user.getBukkitPlayer().setTotalExperience(0);
                                    user.getBukkitPlayer().setExp(0);
                                    DisguiseAPI.undisguiseToAll(user.getBukkitPlayer());
                                }
                            }
                        }
                    }
                }
                
                lobbySigns.updateSigns();
                task.end();
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
    
    public Messager getMessager() {
        return messager;
    }
    
    public void generateMapOptions() {
        this.mapOptions.clear();
        if (gameSettings == null) {
            this.gameSettings = plugin.getSettingsManager().getCurrentSettings();
        }
        int totalOptions = this.gameSettings.getMaxMapOptions();
        MapManager mapManager = plugin.getMapManager();
        if (mapManager.getMaps().size() < totalOptions) {
            for (HGMap value : mapManager.getMaps().values()) {
                this.mapOptions.addMap(value);
            }
        } else {
            Random random = ManiaCore.RANDOM;
            List<HGMap> maps = new LinkedList<>(mapManager.getMaps().values());
            Collections.shuffle(maps);
            for (int i = 0; i < totalOptions; i++) {
                HGMap map;
                do {
                    map = maps.get(random.nextInt(maps.size()));
                } while (this.mapOptions.containsMap(map) || this.playedMaps.contains(map));
                this.mapOptions.addMap(map);
                maps.remove(map);
            }
        }
        String time;
        if (this.voteTimer != null) {
            time = this.voteTimer.getRemainingSeconds() + "";
        } else {
            time = gameSettings.getStartTimer() + "";
        }
        TimoCloudAPI.getBukkitAPI().getThisServer().setExtra("map:Undecided;time:" + time + "s");
        TimoCloudAPI.getBukkitAPI().getThisServer().setState("LOBBY");
        Redis.sendCommand("gameReady " + ManiaCore.getInstance().getServerManager().getCurrentServer().getName());
    }
    
    public void startTimer() {
        if (this.voteTimer == null) {
            this.voteTimer = new VoteTimer(this, gameSettings.getStartTimer());
            this.voteTimer.start();
        }
    }
    
    public VoteTimer getVoteTimer() {
        return voteTimer;
    }
    
    public void fromGame(Game game) {
        for (GamePlayer player : game.getPlayers()) {
            SpigotUser user = player.getUser();
            user.getBukkitPlayer().teleport(getLocation());
            user.getBukkitPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            new LobbyBoard(this, user);
            if (!user.getToggle(Toggles.INCOGNITO).getAsBoolean()) {
                this.players.add(user);
            } else {
                this.hiddenStaff.add(user);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.hidePlayer(user.getBukkitPlayer());
                }
            }
            
            Player p = Bukkit.getPlayer(user.getUniqueId());
            p.setFlying(false);
            p.setAllowFlight(false);
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            p.setExp(0);
            p.setLevel(0);
            p.setTotalExperience(0);
            p.setPlayerListName(ManiaUtils.color(user.getDisplayName()));
        }
        generateMapOptions();
        this.game = null;
    }
    
    public void startGame() {
        if (this.game == null) {
            Entry<HGMap, Integer> mostVotedMap = mapOptions.getMostVotedMap();
            if (mostVotedMap == null) {
                sendMessage(ManiaUtils.color("&cThere was an error determining the map to be used."));
                return;
            }
    
            if (mostVotedMap.getKey() == null) {
                sendMessage(ManiaUtils.color("&cThere was an error generating the map"));
                return;
            }
    
            this.game = new Game(mostVotedMap.getKey(), this.gameSettings);
            try {
                plugin.getManiaCore().getDatabase().pushRecord(new GameRecord(game));
            } catch (Exception e) {
                e.printStackTrace();
                handleError(e.getMessage());
                return;
            }
            if (this.game.getId() == -1) {
                handleError("Game does not have a valid id.");
                return;
            }
            this.game.setCurrentMapVotes(mostVotedMap.getValue());
            this.playedMaps.add(mostVotedMap.getKey());
            plugin.getGameManager().setCurrentGame(game);
            this.game.setup(this);
            this.game.teleportStart();
            this.game.beginCountdown();
            this.resetLobby();
        }
    }
    
    public void handleError(String message) {
        sendMessage("&cThere was an error generating the game: " + message);
        this.voteTimer = null;
        this.game = null;
        if (this.players.size() >= gameSettings.getMinPlayers()) {
            startTimer();
        }
    }
    
    public Set<SpigotUser> getHiddenStaff() {
        return hiddenStaff;
    }
    
    public void sendMessage(String message) {
        sendMessage(message, null);
    }
    
    public void sendMessage(String message, Rank permission) {
        getMessager().sendMessage(message, permission);
    }
    
    public LobbySigns getLobbySigns() {
        return lobbySigns;
    }
    
    public boolean isMapEdititing() {
        if (this.game == null) {
            for (HGMap map : plugin.getMapManager().getMaps().values()) {
                if (map.isEditmode()) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public void resetLobby() {
        this.gameSettings = plugin.getSettingsManager().getCurrentSettings();
        if (this.voteTimer != null) {
            this.voteTimer.cancel();
            this.voteTimer = null;
        }
        this.players.clear();
        this.hiddenStaff.clear();
        this.mapOptions.clear();
        this.voteStart.clear();
        this.generateMapOptions();
    }
    
    public void setGameSettings(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }
    
    public GameSettings getGameSettings() {
        return gameSettings;
    }
    
    public Set<SpigotUser> getPlayers() {
        return players;
    }
    
    public MapOptions getMapOptions() {
        return mapOptions;
    }
    
    public Set<UUID> getVoteStart() {
        return voteStart;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public VoteResult addVote(int map, UUID player) {
        User user = plugin.getManiaCore().getUserManager().getUser(player);
        Rank rank = user.getRank();
        
        int weight = 1;
        if (gameSettings.isVoteWeight()) {
            weight = rank.getVoteWeight();
        }
        
        return mapOptions.addVote(map, player, weight);
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("map")) {
            if (!(args.length > 0)) {
                sender.sendMessage(ManiaUtils.color("&cYou must provide a map name"));
                return true;
            }
            
            int map;
            try {
                map = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ManiaUtils.color("&cInvalid number."));
                return true;
            }
            
            HGMap hgMap = mapOptions.getMaps().get(map);
            if (hgMap == null) {
                sender.sendMessage(ManiaUtils.color("&cInvalid map option."));
                return true;
            }
            
            Player player = (Player) sender;
            
            for (SpigotUser user : this.hiddenStaff) {
                if (user.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(ManiaUtils.color("&cYou cannot vote for a map."));
                    return true;
                }
            }
            
            this.addVote(map, player.getUniqueId());
            
            String[] creators = mapOptions.getMaps().get(map).getCreators().toArray(new String[0]);
            StringBuilder creatorNames = new StringBuilder();
            
            for (int i = 0; i < creators.length; i++) {
                if (i == 0) {
                    creatorNames.append("&3").append(creators[i]);
                } else if (i == (creators.length - 1)) {
                    creatorNames.append(", &7&oand &3").append(creators[i]);
                } else {
                    creatorNames.append("&7&o, &3").append(creators[i]);
                }
            }
            
            player.sendMessage(ManiaUtils.color("&6&l>> &eYou voted for&8: &b" + hgMap.getName() + " &7&oby " + creatorNames.toString()));
            User user = plugin.getManiaCore().getUserManager().getUser(player.getUniqueId());
            Rank rank = user.getRank();
            
            int weight = 1;
            if (gameSettings.isVoteWeight()) {
                weight = rank.getVoteWeight();
            }
            
            player.sendMessage(ManiaUtils.color("&6&l>> &eVoting Weight&8: &b" + weight + " Vote(s)&e."));
        } else if (cmd.getName().equalsIgnoreCase("lobby")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ManiaUtils.color("&cOnly players may use that command."));
                return true;
            }
            
            Player player = (Player) sender;
            User user = ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
            if (!user.hasPermission(Rank.ADMIN)) {
                player.sendMessage(ManiaUtils.color("&cYou do not have permission to use that command."));
                return true;
            }
            
            if (!(args.length > 0)) {
                player.sendMessage(ManiaUtils.color("&cYou must provide a sub command."));
                return true;
            }
    
            if (ManiaUtils.checkCmdAliases(args, 0, "signs")) {
                if (!(args.length > 1)) {
                    player.sendMessage(ManiaUtils.color("&cYou must provide a subcommand."));
                    return true;
                }
        
                Block target = player.getTargetBlock((Set<Material>) null, 20);
                if (!(target.getState() instanceof Sign)) {
                    player.sendMessage(ManiaUtils.color("&cYou must be looking at a sign."));
                    return true;
                }
        
                if (ManiaUtils.checkCmdAliases(args, 1, "setvotetitle")) {
                    this.lobbySigns.setVoteTitleSign(SpigotUtils.locationToPosition(target.getLocation()));
                    player.sendMessage(ManiaUtils.color("&aSet the voting title sign to the block you are looking at."));
                } else if (ManiaUtils.checkCmdAliases(args, 1, "setvotinginfo")) {
                    this.lobbySigns.setVotingInfo(SpigotUtils.locationToPosition(target.getLocation()));
                    player.sendMessage(ManiaUtils.color("&aSet the voting info sign to the block you are looking at."));
                } else if (ManiaUtils.checkCmdAliases(args, 1, "setmapsign")) {
                    if (!(args.length > 2)) {
                        sender.sendMessage(ManiaUtils.color("&cYou must provide the map position number for that sign."));
                        return true;
                    }
            
                    int pos;
                    try {
                        pos = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ManiaUtils.color("&cInvalid number"));
                        return true;
                    }
                    
                    if (pos == 0 || pos > gameSettings.getMaxMapOptions()) {
                        player.sendMessage(ManiaUtils.color("&cInvalid position, it must be between 1 and " + gameSettings.getMaxMapOptions()));
                        return true;
                    }
                    
                    this.lobbySigns.getMapSigns().put(pos, SpigotUtils.locationToPosition(target.getLocation()));
                    player.sendMessage(ManiaUtils.color("&aSet the current location to the map sign position &b" + pos));
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("votestart")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ManiaUtils.color("&cOnly players can do that."));
                return true;
            }
            
            Player player = (Player) sender;
            
            if (game != null) {
                player.sendMessage(ManiaUtils.color("&cThere is a game already running."));
                return true;
            }
            
            if (this.voteTimer != null) {
                player.sendMessage(ManiaUtils.color("&cThe vote timer has already started."));
                return true;
            }
            
            if (this.voteStart.contains(player.getUniqueId())) {
                player.sendMessage(ManiaUtils.color("&cYou have already voted to start the game."));
                return true;
            }
            
            for (SpigotUser user : this.hiddenStaff) {
                if (user.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(ManiaUtils.color("&cYou cannot vote for a map."));
                    return true;
                }
            }
            
            int votesNeeded = 2;
            
            this.voteStart.add(player.getUniqueId());
            if (this.voteStart.size() >= votesNeeded) {
                this.startTimer();
                this.voteTimer.setForceStarted(true);
            }
            sendMessage("&6&l>> &b" + player.getName() + " &ehas voted to start the game. &5(&f" + this.voteStart.size() + "&d/&f" + votesNeeded + "&d)");
        }
        
        return true;
    }
    
    public void setVoteTimer(VoteTimer timer) {
        this.voteTimer = timer;
    }
}