package net.hungermania.hungergames.listeners;

import net.hungermania.hungergames.game.Game;
import net.hungermania.hungergames.game.GamePlayer;
import net.hungermania.hungergames.game.team.GameTeam.Perms;
import net.hungermania.hungergames.perks.Perks;
import net.hungermania.hungergames.perks.TieredPerk;
import net.hungermania.hungergames.perks.TieredPerk.Tier;
import net.hungermania.hungergames.user.GameUser;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.MutationType;
import net.hungermania.maniacore.api.util.State;
import net.hungermania.maniacore.api.util.Utils;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.concurrent.TimeUnit;

public class EntityListeners extends GameListener {
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (gameManager.getCurrentGame() != null) {
            Game game = gameManager.getCurrentGame();
            if (!(e.getEntity() instanceof Player)) {
                return;
            }
            
            if (!game.getGameTeam(e.getEntity().getUniqueId()).getPermissionValue(Perms.DAMAGE)) {
                e.setCancelled(true);
                e.getEntity().setFireTicks(0);
                return;
            }
    
            State state = game.getState();
            if (state == State.COUNTDOWN || state == State.DEATHMATCH_COUNTDOWN || state == State.ENDING || game.getGracePeriodEnd() > System.currentTimeMillis()) {
                e.setCancelled(true);
                e.getEntity().setFireTicks(0);
                return;
            }
            
            if (game.getMutationsTeam().isMember(e.getEntity().getUniqueId())) {
                GamePlayer gamePlayer = game.getPlayer(e.getEntity().getUniqueId());
                if (e.getCause() == DamageCause.FALL) {
                    if (gamePlayer.getMutationType() == MutationType.CHICKEN || gamePlayer.getMutationType() == MutationType.ENDERMAN) {
                        e.setCancelled(true);
                    }
                } else if (e.getCause() == DamageCause.BLOCK_EXPLOSION || e.getCause() == DamageCause.ENTITY_EXPLOSION) {
                    if (gamePlayer.getMutationType() == MutationType.CREEPER) {
                        e.setCancelled(true);
                    }
                } else if (e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.FIRE_TICK || e.getCause() == DamageCause.LAVA) {
                    if (gamePlayer.getMutationType() == MutationType.PIG_ZOMBIE) {
                        e.setCancelled(true);
                        e.getEntity().setFireTicks(0);
                    }
                }
            } else {
                GameUser user = (GameUser) ManiaCore.getInstance().getUserManager().getUser(e.getEntity().getUniqueId());
                try {
                    if (Perks.FEATHERWEIGHT.activate(user)) {
                        Tier tier = ((TieredPerk) Perks.FEATHERWEIGHT).getTier(user);
                        double damageReduction = 0;
                        switch (tier.getNumber()) {
                            case 1: damageReduction = 0.15; break;
                            case 2: damageReduction = 0.25; break;
                            case 3: damageReduction = 0.50; break;
                            default: break;
                        }
                        if (damageReduction == 0) return;
                        double damage = e.getDamage(DamageModifier.BASE);
                        double finalDamage = damage * (1-damageReduction);
                        e.setDamage(DamageModifier.BASE, finalDamage);
                    }
                } catch (Exception ex) {
                    
                }
            }
        } else {
            e.setCancelled(true);
            e.getEntity().setFireTicks(0);
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (gameManager.getCurrentGame() != null) {
            Game game = gameManager.getCurrentGame();
            
            if (game.getSpectatorsTeam().isMember(e.getDamager().getUniqueId()) || e.getDamager().isDead() || game.getHiddenStaffTeam().isMember(e.getDamager().getUniqueId())) {
                e.setCancelled(true);
                return;
            }
    
            if (game.getGracePeriodEnd() > System.currentTimeMillis()) {
                e.setCancelled(true);
                return;
            }
            
            if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
                Player damager = (Player) e.getDamager();
                Player target = (Player) e.getEntity();
                
                GamePlayer damagerPlayer = game.getPlayer(damager.getUniqueId());
                GamePlayer targetPlayer = game.getPlayer(target.getUniqueId());
                
                if (game.getMutationsTeam().isMember(target.getUniqueId())) {
                    if (!targetPlayer.getMutationTarget().equals(damager.getUniqueId())) {
                        damager.sendMessage(Utils.color("&cThat mutation is not your target. You cannot damage it."));
                        e.setCancelled(true);
                        return;
                    }
                    
                    if (targetPlayer.getMutationType() == MutationType.SKELETON) {
                        e.setDamage(DamageModifier.BASE, e.getDamage(DamageModifier.BASE) * 1.5);
                    }
                }
                
                if (game.getMutationsTeam().isMember(damager.getUniqueId())) {
                    if (!damagerPlayer.getMutationTarget().equals(target.getUniqueId())) {
                        damager.sendMessage(Utils.color("&cYou can only damage your target as a mutation."));
                        e.setCancelled(true);
                    }
                }
                
                long revengeTime = targetPlayer.getRevengeTime() + TimeUnit.SECONDS.toMillis(10);
                if (System.currentTimeMillis() < revengeTime) {
                    e.setCancelled(true);
                    damager.sendMessage(Utils.color("&cThat player recently took revenge. They have a 10 second grace period."));
                }
            } else if (e.getEntity() instanceof ItemFrame || e.getEntity() instanceof ArmorStand) {
                e.setCancelled(true);
            } else if (e.getDamager() instanceof Projectile) {
                if (e.getDamager() instanceof Snowball || e.getDamager() instanceof Egg) {
                    if (e.getEntity() instanceof Player) {
                        Player targetPlayer = (Player) e.getEntity();
                        if (e.getDamager() instanceof Snowball) {
                            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 0));
                        } else {
                            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 160, 1));
                        }
                    }
                } else if (e.getDamager() instanceof Arrow) {
                    Arrow arrow = (Arrow) e.getDamager();
                    ProjectileSource source = arrow.getShooter();
                    if (source instanceof Player) {
                        Player shooter = (Player) source;
                        GamePlayer shooterPlayer = game.getPlayer(shooter.getUniqueId());
                        GamePlayer targetPlayer = game.getPlayer(e.getEntity().getUniqueId());
                        if (game.getMutationsTeam().isMember(e.getEntity().getUniqueId())) {
                            if (!shooterPlayer.getMutationTarget().equals(targetPlayer.getUniqueId())) {
                                e.setCancelled(true);
                                return;
                            }
                        }
        
                        if (game.getMutationsTeam().isMember(shooter.getUniqueId())) {
                            if (!shooterPlayer.getMutationTarget().equals(e.getEntity().getUniqueId())) {
                                e.setCancelled(true);
                                return;
                            }
                        }
    
                        try {
                            Perks.SHARPSHOOTER.activate((GameUser) shooterPlayer.getUser());
                        } catch (Exception ex) {}
                    }
                }
            }
        } else {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent e) {
        if (gameManager.getCurrentGame() != null) {
            Game game = gameManager.getCurrentGame();
            if (e.getEntity() instanceof Player) {
                Player player = ((Player) e.getEntity());
                if (!game.getGameSettings().isRegeneration()) {
                    if (!game.getSpectatorsTeam().isMember(player.getUniqueId())) {
                        e.setCancelled(true);
                    }
                }
        
                GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
                if (game.getMutationsTeam().isMember(gamePlayer.getUniqueId())) {
                    MutationType mutationType = gamePlayer.getMutationType();
                    if (mutationType == MutationType.PIG_ZOMBIE) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        if (gameManager.getCurrentGame() != null) {
            Game game = gameManager.getCurrentGame();
            if (!game.getGameTeam(e.getEntity().getUniqueId()).getPermissionValue(Perms.ALWAYS_MAX_FOOD)) {
                return;
            }
        }
    
        e.setFoodLevel(20);
        e.setCancelled(true);
    }
}
