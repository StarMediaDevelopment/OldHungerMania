package net.hungermania.hungergames.game.team;

import me.libraryaddict.disguise.DisguiseAPI;
import net.hungermania.hungergames.game.Game;
import net.hungermania.hungergames.game.enums.PlayerType;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import net.hungermania.maniacore.spigot.util.SpigotUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;


public abstract class GameTeam implements Iterable<UUID> {
    protected String name, color;
    protected PlayerType playerType;
    protected Set<UUID> members = new HashSet<>();
    protected Game game;
    
    protected GameMode gameMode;
    protected Map<Perms, Boolean> permissons = new HashMap<>();
    
    public enum Perms {
        ALLOWED_TO_DROP, OPEN_ENCHANT, ALLOWED_TO_PICKUP, ALWAYS_MAX_FOOD, DAMAGE, BREAK_BLOCKS
    }
    
    public GameTeam(String name, String color, PlayerType playerType, Game game) {
        this.name = name;
        this.color = color;
        this.playerType = playerType;
        this.game = game;
    }
    
    public boolean getPermissionValue(Perms perms) {
        if (this.permissons.containsKey(perms)) {
            return this.permissons.get(perms);
        }
        return false;
    }
    
    public Set<UUID> getMembers() {
        return new HashSet<>(this.members);
    }
    
    public String getName() {
        return color + name;
    }
    
    public String getJoinMessage() {
        return "&d&l>> &7You joined " + getName();
    }
    
    public String getLeaveMessage() {
        return "&c&l<< &7You left " + getName();
    }
    
    public boolean isMember(UUID uuid) {
        return this.members.contains(uuid);
    }
    
    protected Location getSpawn(User user, Location spawn) {
        for (Entry<Integer, UUID> entry : game.getSpawns().entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getValue().equals(user.getUniqueId())) {
                    spawn = SpigotUtils.positionToLocation(game.getMap().getWorld(), game.getMap().getSpawns().get(entry.getKey()));
                    break;
                }
            }
        }
        return spawn;
    }
    
    public void teleportAll(Location location) {
        for (UUID p : this.members) {
            Player player = Bukkit.getPlayer(p);
            player.teleport(location);
        }
    }
    
    protected void setPlayerStats(Player player, boolean collides, boolean allowFlight, boolean flying) {
        User user = ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
        player.setGameMode(this.gameMode);
        player.setMaxHealth(game.getGameSettings().getMaxHealth());
        player.setHealth(game.getGameSettings().getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(10);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setLevel(0);
        player.setTotalExperience(0);
        player.setExp(0);
        player.setAllowFlight(allowFlight);
        player.setFlying(flying);
        player.spigot().setCollidesWithEntities(collides);
        for (PotionEffect activePotionEffect : new ArrayList<>(player.getActivePotionEffects())) {
            player.removePotionEffect(activePotionEffect.getType());
        }
        DisguiseAPI.undisguiseToAll(player);
        setPlayerListName((SpigotUser) user);
    }
    
    public void setPlayerListName(SpigotUser user) {
        Rank rank;
        String name;
        if (user.getNickname() != null && user.getNickname().isActive()) {
            rank = user.getNickname().getRank();
            name = user.getNickname().getName();
        } else {
            rank = user.getRank();
            name = user.getName();
        }
        user.getBukkitPlayer().setPlayerListName(ManiaUtils.color(rank.getPrefix() + " " + getColor() + name));
    }
    
    public abstract void join(UUID uuid);
    
    public void leave(UUID uuid) {
        if (this.members.contains(uuid)) {
            this.members.remove(uuid);
            User user = ManiaCore.getInstance().getUserManager().getUser(uuid);
            user.sendMessage(getLeaveMessage());
        }
    }
    
    public Iterator<UUID> iterator() {
        return members.iterator();
    }
    
    public void forEach(Consumer<? super UUID> action) {
        this.members.forEach(action);
    }
    
    public Spliterator<UUID> spliterator() {
        return this.members.spliterator();
    }
    
    public int size() {
        return members.size();
    }
    
    public boolean isEmpty() {
        return this.members.isEmpty();
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public PlayerType getPlayerType() {
        return playerType;
    }
    
    public void setPlayerType(PlayerType playerType) {
        this.playerType = playerType;
    }
    
    public void setMembers(Set<UUID> members) {
        this.members = members;
    }
    
    public Game getGame() {
        return game;
    }
    
    public void setGame(Game game) {
        this.game = game;
    }
    
    public GameMode getGameMode() {
        return gameMode;
    }
    
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }
    
    public Map<Perms, Boolean> getPermissons() {
        return permissons;
    }
    
    public void setPermissons(Map<Perms, Boolean> permissons) {
        this.permissons = permissons;
    }
}