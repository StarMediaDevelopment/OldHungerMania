package net.hungermania.hungergames.game;

import com.mojang.authlib.GameProfile;
import net.hungermania.hungergames.game.death.DeathInfo;
import net.hungermania.hungergames.game.team.GameTeam;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.mutations.MutationType;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;


public class GamePlayer {
    private Game game;
    private SpigotUser user;
    private boolean forcefullyAdded;
    private CommandSender forcefullyAddedActor;
    private boolean hasMutated = false;
    private boolean hasSponsored = false;
    private UUID mutationTarget;
    private MutationType mutationType;
    private int killStreak;
    private boolean revived;
    private CommandSender revivedActor;
    private boolean spectatorByDeath = false;
    private ItemStack skull;
    private DeathInfo deathInfo = null;
    private boolean isMutating = false;
    private int kills;
    private int earnedCoins = 0;
    private long revengeTime = 0;
    
    public GamePlayer(Game game, UUID uuid) {
        this.game = game;
        this.user = (SpigotUser) ManiaCore.getInstance().getUserManager().getUser(uuid);
        this.skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = ((SkullMeta) skull.getItemMeta());
        String playerName = Bukkit.getPlayer(uuid).getName();
        Player player = user.getBukkitPlayer();
        GameProfile mcProfile = ((CraftPlayer) player).getProfile();
        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, mcProfile);
        } catch (Exception e) {
        }
        skullMeta.setDisplayName(ManiaUtils.color("&f" + playerName));
        skull.setItemMeta(skullMeta);
    }
    
    public UUID getUniqueId() {
        return user.getUniqueId();
    }
    
    public boolean hasSponsored() {
        return hasSponsored;
    }
    
    public void setSponsored(boolean value) {
        this.hasSponsored = value;
    }
    
    public boolean hasMutated() {
        return hasMutated;
    }
    
    public void setForcefullAddedInfo(boolean value, CommandSender sender) {
        this.forcefullyAdded = value;
        this.forcefullyAddedActor = sender;
    }
    
    public void setRevivedInfo(boolean value, CommandSender sender) {
        this.revived = value;
        this.revivedActor = sender;
    }
    
    public void sendMessage(String s) {
        user.sendMessage(s);
    }
    
    public GameTeam getTeam() {
        return game.getGameTeam(getUniqueId());
    }
    
    public Game getGame() {
        return game;
    }
    
    public void setGame(Game game) {
        this.game = game;
    }
    
    public SpigotUser getUser() {
        return user;
    }
    
    public void setUser(SpigotUser user) {
        this.user = user;
    }
    
    public boolean isForcefullyAdded() {
        return forcefullyAdded;
    }
    
    public void setForcefullyAdded(boolean forcefullyAdded) {
        this.forcefullyAdded = forcefullyAdded;
    }
    
    public CommandSender getForcefullyAddedActor() {
        return forcefullyAddedActor;
    }
    
    public void setForcefullyAddedActor(CommandSender forcefullyAddedActor) {
        this.forcefullyAddedActor = forcefullyAddedActor;
    }
    
    public boolean isHasMutated() {
        return hasMutated;
    }
    
    public void setHasMutated(boolean hasMutated) {
        this.hasMutated = hasMutated;
    }
    
    public boolean isHasSponsored() {
        return hasSponsored;
    }
    
    public void setHasSponsored(boolean hasSponsored) {
        this.hasSponsored = hasSponsored;
    }
    
    public UUID getMutationTarget() {
        return mutationTarget;
    }
    
    public void setMutationTarget(UUID mutationTarget) {
        this.mutationTarget = mutationTarget;
    }
    
    public MutationType getMutationType() {
        return mutationType;
    }
    
    public void setMutationType(MutationType mutationType) {
        this.mutationType = mutationType;
    }
    
    public int getKillStreak() {
        return killStreak;
    }
    
    public void setKillStreak(int killStreak) {
        this.killStreak = killStreak;
    }
    
    public boolean isRevived() {
        return revived;
    }
    
    public void setRevived(boolean revived) {
        this.revived = revived;
    }
    
    public CommandSender getRevivedActor() {
        return revivedActor;
    }
    
    public void setRevivedActor(CommandSender revivedActor) {
        this.revivedActor = revivedActor;
    }
    
    public boolean isSpectatorByDeath() {
        return spectatorByDeath;
    }
    
    public void setSpectatorByDeath(boolean spectatorByDeath) {
        this.spectatorByDeath = spectatorByDeath;
    }
    
    public ItemStack getSkull() {
        return skull;
    }
    
    public void setSkull(ItemStack skull) {
        this.skull = skull;
    }
    
    public DeathInfo getDeathInfo() {
        return deathInfo;
    }
    
    public void setDeathInfo(DeathInfo deathInfo) {
        this.deathInfo = deathInfo;
    }
    
    public boolean isMutating() {
        return isMutating;
    }
    
    public void setMutating(boolean mutating) {
        isMutating = mutating;
    }
    
    public int getKills() {
        return kills;
    }
    
    public void setKills(int kills) {
        this.kills = kills;
    }
    
    public int getEarnedCoins() {
        return earnedCoins;
    }
    
    public void setEarnedCoins(int earnedCoins) {
        this.earnedCoins = earnedCoins;
    }
    
    public long getRevengeTime() {
        return revengeTime;
    }
    
    public void setRevengeTime(long revengeTime) {
        this.revengeTime = revengeTime;
    }
}
