package net.hungermania.hungergames.game.team;

import lombok.Getter;
import lombok.Setter;
import me.libraryaddict.disguise.DisguiseAPI;
import net.hungermania.hungergames.game.Game;
import net.hungermania.hungergames.game.PlayerType;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.Utils;
import net.hungermania.maniacore.spigot.util.SpigotUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

@Getter
public abstract class GameTeam implements Iterable<UUID> {
    protected String name, color;
    protected PlayerType playerType;
    protected Set<UUID> members = new HashSet<>();
    protected Game game;
    @Setter
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
        return "&c&l>> &7You left " + getName();
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
        player.setPlayerListName(ManiaUtils.color(user.getRank().getPrefix() + " " + getColor() + player.getName()));
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
}