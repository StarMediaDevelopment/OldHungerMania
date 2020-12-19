package net.hungermania.maniacore.spigot.user;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.channel.Channel;
import net.hungermania.maniacore.api.leveling.Level;
import net.hungermania.maniacore.api.logging.entry.ChatEntry;
import net.hungermania.maniacore.api.logging.entry.CmdEntry;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.records.ChatEntryRecord;
import net.hungermania.maniacore.api.records.CmdEntryRecord;
import net.hungermania.maniacore.api.redis.Redis;
import net.hungermania.maniacore.api.server.ServerType;
import net.hungermania.maniacore.api.skin.Skin;
import net.hungermania.maniacore.api.stats.Stats;
import net.hungermania.maniacore.api.user.*;
import net.hungermania.maniacore.api.user.toggle.Toggles;
import net.hungermania.maniacore.api.util.Utils;
import net.hungermania.maniacore.plugin.ManiaPlugin;
import net.hungermania.maniacore.spigot.events.UserJoinEvent;
import net.hungermania.maniacore.spigot.updater.UpdateEvent;
import net.hungermania.maniacore.spigot.updater.UpdateType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.*;

public class SpigotUserManager extends UserManager implements Listener {
    
    private ManiaPlugin plugin;
    
    private Map<UUID, User> users = new HashMap<>();
    
    public SpigotUserManager(ManiaPlugin plugin) {
        this.plugin = plugin;
        
        plugin.runTaskTimer(() -> {
            Map<UUID, User> users = new HashMap<>(SpigotUserManager.this.users);
            Set<UUID> offlinePlayers = new HashSet<>();
            for (User value : users.values()) {
                if (!value.isOnline()) {
                    Redis.pushUser(value);
                    Redis.sendCommand("saveUserData " + value.getUniqueId());
                    offlinePlayers.add(value.getUniqueId());
                }
            }
            
            for (UUID offlinePlayer : offlinePlayers) {
                SpigotUserManager.this.users.remove(offlinePlayer);
            }
        }, 20L, 20L);
    }
    
    @SuppressWarnings("DuplicatedCode")
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        SpigotUser user = (SpigotUser) getUser(player.getUniqueId());
        
        if (e.getMessage().startsWith("@") || e.getMessage().startsWith("$")) {
            char channelChar = e.getMessage().charAt(0);
            e.setCancelled(true);
            e.setMessage(e.getMessage().substring(1));
            Channel channel = Channel.GLOBAL;
            
            if (channelChar == '@') {
                channel = Channel.STAFF;
            } else if (channelChar == '$') {
                channel = Channel.ADMIN;
            }
            
            String message = user.getDisplayName() + "&8: " + channel.getColor() + e.getMessage();
            if ((channel == Channel.STAFF && user.hasPermission(Rank.HELPER)) || (channel == Channel.ADMIN && user.hasPermission(Rank.ADMIN))) {
                ManiaCore.getInstance().getMessageHandler().sendChannelMessage(player.getUniqueId(), channel, message);
            }
            return;
        }
        
        if (ManiaCore.getInstance().getServerManager().getCurrentServer().getType() != ServerType.HUNGER_GAMES) {
            Channel channel = user.getChannel();
            String channelPrefix = "";
            e.setCancelled(true);
            if (StringUtils.isNotEmpty(channel.getPrefix())) {
                channelPrefix = channel.getSymbolColor() + "[" + channel.getColor() + channel.getPrefix() + channel.getSymbolColor() + "] ";
            }
            
            String format;
            if (channel == Channel.GLOBAL) {
                format = "&8[{levelcolor}{level}&8] {displayname}&8: {chatcolor}{message}";
            } else {
                format = "{prefix} {displayname}&8: {chatcolor}{message}";
            }
            
            format = format.replace("{displayname}", user.getDisplayName());
            format = format.replace("{prefix}", channelPrefix);
            Rank rank = user.getRank();
            if (StringUtils.isNotEmpty(rank.getChatColor()) && channel == Channel.GLOBAL) {
                if (rank.getChatColor() == null) {
                    format = format.replace("{chatcolor}", "&f");
                } else {
                    format = format.replace("{chatcolor}", rank.getChatColor());
                }
            } else {
                format = format.replace("{chatcolor}", channel.getColor());
            }
            
            Level level = ManiaCore.getInstance().getLevelManager().getLevel(user.getStat(Stats.EXPERIENCE).getValueAsInt());
            
            format = format.replace("{levelcolor}", level.getNumberColor().toString());
            format = format.replace("{level}", level.getNumber() + "");
            format = format.replace("%", "%%");
            format = format.replace("{message}", e.getMessage());
            for (Player p : Bukkit.getOnlinePlayers()) {
                User u = ManiaCore.getInstance().getUserManager().getUser(p.getUniqueId());
                boolean send = true;
                for (IgnoreInfo ignoredPlayer : u.getIgnoredPlayers()) {
                    if (ignoredPlayer.getIgnored().equals(player.getUniqueId())) {
                        if (!user.hasPermission(Rank.HELPER)) {
                            send = false;
                            break;
                        }
                    }
                }
                
                if (send) {
                    p.sendMessage(Utils.color(format));
                }
            }
        }
        
        ChatEntry chatEntry = new ChatEntry(System.currentTimeMillis(), ManiaCore.getInstance().getServerManager().getCurrentServer().getName(), user.getId(), e.getMessage().replace("'", "\\'"), user.getChannel().name());
        ChatEntryRecord chatEntryRecord = new ChatEntryRecord(chatEntry);
        ManiaCore.getInstance().getDatabase().addRecordToQueue(chatEntryRecord);
    }
    
    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        User user = this.users.get(player.getUniqueId());
        CmdEntry cmdEntry = new CmdEntry(System.currentTimeMillis(), "test", user.getId(), e.getMessage());
        CmdEntryRecord cmdEntryRecord = new CmdEntryRecord(cmdEntry);
        ManiaCore.getInstance().getDatabase().addRecordToQueue(cmdEntryRecord);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        User user = this.users.get(e.getPlayer().getUniqueId());
        Redis.pushUser(user);
        this.users.remove(user.getUniqueId());
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        Player player = e.getPlayer();
        GameProfile gameProfile = ((CraftPlayer) player).getProfile();
        String value = null, signature = null;
        for (Property property : gameProfile.getProperties().get("textures")) {
            value = property.getValue();
            signature = property.getSignature();
        }
        
        Skin skin = new Skin(player.getUniqueId(), player.getName(), value, signature);
        ManiaCore.getInstance().getSkinManager().addSkin(skin);
        
        plugin.runTaskLaterAsynchronously(() -> {
            User user = getUser(player.getUniqueId());
            
            plugin.runTaskLater(() -> {
                users.put(user.getUniqueId(), user);
                if (user.getToggle(Toggles.INCOGNITO).getAsBoolean()) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!p.hasPermission("mania.incognito.see")) {
                            p.hidePlayer(player);
                        }
                    }
                }
                if (user.hasPermission(Rank.OWNER)) {
                    if (!e.getPlayer().isOp()) {
                        e.getPlayer().setOp(true);
                    }
                }
                
                if (user.hasPermission(Rank.HELPER)) {
                    if (!e.getPlayer().isWhitelisted()) {
                        e.getPlayer().setWhitelisted(true);
                    }
                }
                
                UserJoinEvent event = new UserJoinEvent((SpigotUser) user);
                Bukkit.getServer().getPluginManager().callEvent(event);
            }, 1L);
        }, 5L);
    }
    
    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (e.getType() != UpdateType.SECOND) { return; }
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = getUser(player.getUniqueId());
            user.incrementOnlineTime();
        }
    }
    
    public User constructUser(UUID uuid, String name) {
        return new SpigotUser(uuid, name);
    }
    
    public User constructUser(Map<String, String> data) {
        return new SpigotUser(data);
    }
    
    public User constructUser(User user) {
        return new SpigotUser(user);
    }
    
    public User getUser(UUID uuid) {
        if (this.users.containsKey(uuid)) {
            return this.users.get(uuid);
        }
        User user = super.getUser(uuid);
        this.users.put(user.getUniqueId(), user);
        return user;
    }
    
    public User getUser(int userId) {
        for (User value : this.users.values()) {
            if (value.getId() == userId) {
                return value;
            }
        }
        User user = super.getUser(userId);
        this.users.put(user.getUniqueId(), user);
        return user;
    }
    
    public User getUser(String name) {
        for (User value : this.users.values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        User user = super.getUser(name);
        this.users.put(user.getUniqueId(), user);
        return user;
    }
}