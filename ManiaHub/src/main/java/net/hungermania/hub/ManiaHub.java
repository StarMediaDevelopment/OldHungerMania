package net.hungermania.hub;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.Getter;
import net.hungermania.hub.leaderboard.Leaderboard;
import net.hungermania.hub.leaderboard.LeaderboardManager;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.channel.Channel;
import net.hungermania.maniacore.api.events.EventInfo;
import net.hungermania.maniacore.api.leveling.Level;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.server.ManiaServer;
import net.hungermania.maniacore.api.server.ServerType;
import net.hungermania.maniacore.api.stats.Stats;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.memory.MemoryHook;
import net.hungermania.maniacore.memory.MemoryHook.Task;
import net.hungermania.maniacore.plugin.ManiaPlugin;
import net.hungermania.maniacore.plugin.ManiaTask;
import net.hungermania.maniacore.spigot.gui.GUIButton;
import net.hungermania.maniacore.spigot.gui.Gui;
import net.hungermania.maniacore.spigot.plugin.SpigotManiaTask;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import net.hungermania.maniacore.spigot.util.ItemBuilder;
import net.hungermania.manialib.util.Range;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;

public final class ManiaHub extends JavaPlugin implements Listener, ManiaPlugin {

    @Getter
    private LeaderboardManager leaderboardManager;

    public static ManiaHub instance;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            if (!e.getPlayer().hasPermission("hungermania.hub.protection.bypass")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            if (!e.getPlayer().hasPermission("hungermania.hub.protection.bypass")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (e.getItem() == null) {
            return;
        }
        ItemStack itemStack = e.getItem();
        if (!itemStack.hasItemMeta()) {
            return;
        }
        if (!itemStack.getItemMeta().hasDisplayName()) {
            return;
        }
        if (!itemStack.getItemMeta().getDisplayName().contains("GAME BROWSER")) {
            return;
        }
        new GameBrowserGui(this).openGUI(e.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof PlayerInventory) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.getPlayer().sendMessage(ManiaUtils.color("&6&l>> &bWelcome to &3&lHungerMania&b!"));
        if (getConfig().getBoolean("testinfo.active")) {
            e.getPlayer().sendMessage(ManiaUtils.color("&6&l>> &bThe HG Test is currently active use &3/hgtest &bto go to the server!"));
        }
        if (e.getPlayer().hasPermission(Channel.STAFF.getPermission())) {
            e.getPlayer().sendMessage(ManiaUtils.color("&6&l>> &9Use &3@ &9in front of messages to talk in staff chat."));
        }
        if (e.getPlayer().hasPermission(Channel.ADMIN.getPermission())) {
            e.getPlayer().sendMessage(ManiaUtils.color("&6&l>> &9Use &3$ &9in front of messages to talk in admin chat."));
        }
        e.getPlayer().setGameMode(GameMode.ADVENTURE);
        e.getPlayer().getInventory().clear();
        ItemStack gameBrowser = new ItemStack(Material.COMPASS);
        ItemMeta browserMeta = gameBrowser.getItemMeta();
        browserMeta.setDisplayName(ManiaUtils.color("&e&lGAME BROWSER &7&o(Right Click)"));
        gameBrowser.setItemMeta(browserMeta);
        e.getPlayer().getInventory().setItem(4, gameBrowser);
        e.getPlayer().updateInventory();
        e.getPlayer().teleport(getServer().getWorld("world").getSpawnLocation());

        String[] motd = new String[]{"&6&l>> &bWelcome to HungerMania!", "", "----Server Info----", "&6&l>> &eDiscord: &fhttps://discord.gg/Z95xgD7", "&6&l>> &eWebsite: &fhttps://hungermania.net/", "&6&l>> &eStore: &fhttps://hunger-mania.tebex.io/", "&6&l>> &eRules: &f/rules"};

        for (String message : motd) {
            e.getPlayer().sendMessage(ManiaUtils.color(message));
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("testadmin")) {
            if (!sender.hasPermission("hungermania.hub.testadmin")) {
                sender.sendMessage(ManiaUtils.color("&cYou do not have permission to use that command."));
                return true;
            }

            if (!(args.length > 0)) {
                sender.sendMessage(ManiaUtils.color("&cYou must provide a sub command"));
                return true;
            }

            if (ManiaUtils.checkCmdAliases(args, 0, "info")) {
                sender.sendMessage(ManiaUtils.color("&6&l>> &eHG Test Information"));
                sender.sendMessage(ManiaUtils.color("&6&l> &bActive&8: &e" + getConfig().getBoolean("testinfo.active")));
                sender.sendMessage(ManiaUtils.color("&6&l> &bServer&8: &e" + getConfig().getString("testinfo.server")));
                return true;
            }

            if (!(args.length > 1)) {
                sender.sendMessage(ManiaUtils.color("&cYou must provide a value"));
                return true;
            }

            if (ManiaUtils.checkCmdAliases(args, 0, "setactive")) {
                try {
                    boolean value = Boolean.parseBoolean(args[1]);
                    getConfig().set("testinfo.active", value);
                    saveConfig();
                    sender.sendMessage(ManiaUtils.color("&6&l>> &aYou set the HG Test to " + value));
                } catch (Exception e) {
                    sender.sendMessage(ManiaUtils.color("&4&l>> &cYou must provide the values true or false"));
                }
            } else if (ManiaUtils.checkCmdAliases(args, 0, "setserver")) {
                getConfig().set("testinfo.server", args[1]);
                saveConfig();
                sender.sendMessage(ManiaUtils.color("&6&l>> &aYou set the HG Test Server to " + args[1]));
                sender.sendMessage(ManiaUtils.color("&6&l>> &cNote: This server must exist to work. This command does not check if it does."));
            }
        } else if (cmd.getName().equalsIgnoreCase("spawn")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ManiaUtils.color("&cOnly players may use that command."));
                return true;
            }

            Player player = (Player) sender;
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        } else if (cmd.getName().equalsIgnoreCase("fly")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ManiaUtils.color("&cOnly players may use that command."));
                return true;
            }

            Player player = (Player) sender;
            User user = ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
            if (!user.hasPermission(Rank.HELPER)) {
                player.sendMessage(ManiaUtils.color("&cYou do not have permission to use that command."));
                return true;
            }

            player.setAllowFlight(!player.getAllowFlight());
            if (player.getAllowFlight()) {
                player.sendMessage(ManiaUtils.color("&6&l>> &fFly mode &a&lENABLED&f."));
            } else {
                player.sendMessage(ManiaUtils.color("&6&l>> &fFly mode &c&lDISABLED&f."));
            }
        } else if (cmd.getName().equalsIgnoreCase("leaderboard")) {
            if (!(args.length > 1)) {
                sender.sendMessage(ManiaUtils.color("&cYou did not provide enough arguments"));
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(ManiaUtils.color("&cOnly players can use that command."));
                return true;
            }

            int start, end;
            try {
                start = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ManiaUtils.color("&cYou provided an invalid number for the starting number."));
                return true;
            }

            try {
                end = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ManiaUtils.color("&cYou provided an invalid number for the ending number."));
                return true;
            }

            for (Range<Leaderboard> range : leaderboardManager.getLeaderboards()) {
                if (range.contains(start) || range.contains(end)) {
                    sender.sendMessage(ManiaUtils.color("&cA leaderboard already exists with one of those numbers."));
                    return true;
                }
            }

            Leaderboard leaderboard = new Leaderboard(((Player) sender).getLocation(), start, end);
            leaderboard.spawn();
            leaderboardManager.addLeaderboard(leaderboard);
        }
        return true;
    }

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();

        ManiaCore.getInstance().getServerManager().getCurrentServer().setType(ServerType.HUB);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getWorld("world").setDifficulty(Difficulty.PEACEFUL);

        MemoryHook playerUpdate = new MemoryHook("Hub Player Update");
        ManiaCore.getInstance().getMemoryManager().addMemoryHook(playerUpdate);
        new BukkitRunnable() {
            public void run() {
                Task task = playerUpdate.task().start();
                Map<Integer, ItemStack> serverStacks = new TreeMap<>();
                int onlinePlayers = 0, maximumPlayers = 0;
                EventInfo activeEvent = ManiaCore.getInstance().getEventManager().getActiveEvent();

                Collection<ServerObject> hgServers = TimoCloudAPI.getUniversalAPI().getServerGroup("HG").getServers();
                for (ServerObject server : hgServers) {
                    Material itemMaterial = null;
                    List<String> lore = new LinkedList<>();
                    if (server.getState().equalsIgnoreCase("online") || server.getState().equalsIgnoreCase("ingame") || server.getState().equalsIgnoreCase("lobby")) {
                        if (activeEvent != null) {
                            if (activeEvent.getServers().contains(server.getName())) {
                                itemMaterial = Material.ENDER_STONE;
                                lore.add(ManiaUtils.color("&3&lACTIVE EVENT"));
                            }
                        } else {
                            if (server.getState().equalsIgnoreCase("online") || server.getState().equalsIgnoreCase("lobby")) {
                                itemMaterial = Material.EMERALD_BLOCK;
                            } else if (server.getState().equalsIgnoreCase("ingame")) {
                                itemMaterial = Material.GOLD_BLOCK;
                            }

                            lore.add("");
                            lore.add(ManiaUtils.color("&d&lStatus &f" + server.getState()));
                            String time = "", map = "";
                            String extra = server.getExtra();
                            if (extra != null && !extra.isEmpty()) {
                                String[] extraSplit = extra.split(";");
                                if (extraSplit.length > 0) {
                                    for (String es : extraSplit) {
                                        String[] optionSplit = es.split(":");
                                        if (optionSplit.length == 2) {
                                            if (optionSplit[0].equalsIgnoreCase("map")) {
                                                map = optionSplit[1];
                                            } else if (optionSplit[0].equalsIgnoreCase("time")) {
                                                time = optionSplit[1];
                                            }
                                        }
                                    }
                                }
                            }
                            lore.add(ManiaUtils.color("&d&lMap &f" + map));
                            lore.add(ManiaUtils.color("&d&lTime &f" + time));
                            lore.add("");
                            lore.add(ManiaUtils.color("&3&l" + server.getOnlinePlayerCount() + "/" + server.getMaxPlayerCount()));
                        }
                    } else if (server.getState().equalsIgnoreCase("starting")) {
                        itemMaterial = Material.DIAMOND_BLOCK;
                        lore.addAll(Arrays.asList("", ManiaUtils.color("&c&lSERVER IS STARTING")));
                    } else if (server.getState().equalsIgnoreCase("restarting")) {
                        itemMaterial = Material.REDSTONE_BLOCK;
                        lore.addAll(Arrays.asList("", ManiaUtils.color("&c&lSERVER IS RESTARTING")));
                    } else {
                        itemMaterial = Material.BEDROCK;
                        lore.addAll(Arrays.asList("", ManiaUtils.color("&c&lSERVER IS OFFLINE")));
                    }

                    ItemStack itemStack = new ItemStack(itemMaterial);

                    onlinePlayers += server.getOnlinePlayerCount();
                    maximumPlayers += server.getMaxPlayerCount();

                    ItemMeta itemMeta = itemStack.getItemMeta();
                    int number;
                    try {
                        number = Integer.parseInt(server.getName().split("-")[1]);
                    } catch (Exception e) {
                        number = 1;
                    }
                    itemMeta.setDisplayName(ManiaUtils.color("&a&lSERVER " + number));
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                    serverStacks.put(number, itemStack);
                }
//                ItemStack totalServers = ItemBuilder.start(Material.ENCHANTED_BOOK).setDisplayName("&e&lTOTAL SERVERS").withLore("&7Servers&8: &6" + serverStacks.size(),
//                        " &7In Lobby&8: &a&c&oNot Implemented", " &7Running&8: &e&c&oNot Implemented", " &7Restarting&8: &c&c&oNot Implemented").build();
                ItemStack totalPlayers = ItemBuilder.start(Material.SKULL_ITEM, 1, (byte) 3).setDisplayName("&e&lTOTAL PLAYERS").withLore("&6" + onlinePlayers + "&8/&6" + maximumPlayers).build();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.setHealth(20);
                    player.setFoodLevel(20);

                    if (player.getLocation().getBlockY() < 0) {
                        player.teleport(Bukkit.getWorld("world").getSpawnLocation());
                    }

                    SpigotUser user = (SpigotUser) ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
                    if (user.getScoreboard() == null) {
                        new HubBoard(user);
                    } else {
                        user.getScoreboard().update();
                    }
                    user.getBukkitPlayer().setPlayerListName(ManiaUtils.color(user.getDisplayName()));

                    Level level = ManiaCore.getInstance().getLevelManager().getLevel(user.getStat(Stats.EXPERIENCE).getAsInt());
                    if (level != null) {
                        player.setLevel(level.getNumber());
                        if (level.getNumber() != 0) {
                            Level nextLevel = ManiaCore.getInstance().getLevelManager().getLevels().get(level.getNumber() + 1);
                            float xp = 0;
                            if (nextLevel != null) {
                                int xpToNextLevel = nextLevel.getTotalXp();
                                long currentProgress = user.getStat(Stats.EXPERIENCE).getAsInt() - level.getTotalXp();
                                xp = (currentProgress * 1F) / (xpToNextLevel * 1F);
                            }
                            player.setExp(xp);
                        }
                    }

                    if (player.getOpenInventory() != null) {
                        if (player.getOpenInventory().getTopInventory().getHolder() instanceof HungerGamesGui) {
                            HungerGamesGui gui = (HungerGamesGui) player.getOpenInventory().getTopInventory().getHolder();
                            gui.getButton(5).setItem(totalPlayers);

                            for (Entry<Integer, ItemStack> entry : serverStacks.entrySet()) {
                                GUIButton button = gui.getButton(26 + entry.getKey());
                                button.setItem(entry.getValue());
                            }
                            gui.refreshInventory(player);
                        }
                    }
                }

                for (Range<Leaderboard> range : leaderboardManager.getLeaderboards()) {
                    range.getObject().update();
                }

                task.end();
            }
        }.runTaskTimer(this, 20L, 30L);

        ManiaServer currentServer = ManiaCore.getInstance().getServerManager().getCurrentServer();
        currentServer.setType(ServerType.HUB);

        Gui.prepare(this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            new HubBoard((SpigotUser) ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId()));
        }

        this.leaderboardManager = new LeaderboardManager(this);
        leaderboardManager.loadData();
        new BukkitRunnable() {
            public void run() {
                for (Range<Leaderboard> leaderboard : leaderboardManager.getLeaderboards()) {
                    leaderboard.getObject().update();
                }
            }
        }.runTaskTimer(this, 1200, 1200);

        ManiaCore.getInstance().getMemoryManager().addManiaPlugin(this);
    }

    @Override
    public void onDisable() {
        leaderboardManager.saveData(true);
    }

    public static ManiaHub getInstance() {
        return instance;
    }

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }

    public ManiaTask runTask(Runnable runnable) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTask(this, runnable));
    }

    public ManiaTask runTaskAsynchronously(Runnable runnable) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTaskAsynchronously(this, runnable));
    }

    public ManiaTask runTaskLater(Runnable runnable, long delay) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTaskLater(this, runnable, delay));
    }

    public ManiaTask runTaskLaterAsynchronously(Runnable runnable, long delay) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTaskLaterAsynchronously(this, runnable, delay));
    }

    public ManiaTask runTaskTimer(Runnable runnable, long delay, long period) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTaskTimer(this, runnable, delay, period));
    }

    public ManiaTask runTaskTimerAsynchronously(Runnable runnable, long delay, long period) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTaskTimerAsynchronously(this, runnable, delay, period));
    }
}