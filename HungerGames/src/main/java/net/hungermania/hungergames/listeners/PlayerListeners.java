package net.hungermania.hungergames.listeners;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.game.Game;
import net.hungermania.hungergames.game.GamePlayer;
import net.hungermania.hungergames.game.MutateGui;
import net.hungermania.hungergames.game.PlayersGui;
import net.hungermania.hungergames.game.death.*;
import net.hungermania.hungergames.game.team.GameTeam;
import net.hungermania.hungergames.game.team.GameTeam.Perms;
import net.hungermania.hungergames.loot.Loot;
import net.hungermania.hungergames.scoreboard.LobbyBoard;
import net.hungermania.hungergames.settings.GameSettings;
import net.hungermania.hungergames.util.Messager;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.redis.Redis;
import net.hungermania.maniacore.api.stats.Stats;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.user.toggle.Toggle;
import net.hungermania.maniacore.api.user.toggle.Toggles;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.api.util.Position;
import net.hungermania.maniacore.api.util.State;
import net.hungermania.maniacore.spigot.events.UserActionBarUpdateEvent;
import net.hungermania.maniacore.spigot.events.UserIncognitoEvent;
import net.hungermania.maniacore.spigot.events.UserJoinEvent;
import net.hungermania.maniacore.spigot.gui.Gui;
import net.hungermania.maniacore.spigot.mutations.MutationType;
import net.hungermania.maniacore.spigot.perks.PerkInfo;
import net.hungermania.maniacore.spigot.perks.PerkInfoRecord;
import net.hungermania.maniacore.spigot.perks.Perks;
import net.hungermania.maniacore.spigot.updater.UpdateEvent;
import net.hungermania.maniacore.spigot.updater.UpdateType;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import net.hungermania.maniacore.spigot.util.NBTWrapper;
import net.hungermania.maniacore.spigot.util.SpigotUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class PlayerListeners extends GameListener {
    
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (gameManager.getCurrentGame() != null) {
            Game game = gameManager.getCurrentGame();
            if (!game.getGameTeam(e.getPlayer().getUniqueId()).getPermissionValue(Perms.ALLOWED_TO_DROP)) {
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerEntityInteract(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked() instanceof ArmorStand || e.getRightClicked() instanceof ItemFrame) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent e) {
        CraftingInventory inv = e.getInventory();
        if (inv.getRecipe().getResult().getType() == Material.FLINT_AND_STEEL) {
            ItemStack itemStack = new ItemStack(Material.FLINT_AND_STEEL);
            itemStack.setDurability((short) (Material.FLINT_AND_STEEL.getMaxDurability() - 4));
            inv.setResult(itemStack);
        }
    }
    
    @EventHandler
    public void onPlayerOpenEnchant(InventoryOpenEvent e) {
        if (gameManager.getCurrentGame() != null) {
            if (e.getInventory() instanceof EnchantingInventory) {
                Game game = gameManager.getCurrentGame();
                if (!game.getGameTeam(e.getPlayer().getUniqueId()).getPermissionValue(Perms.OPEN_ENCHANT)) {
                    e.setCancelled(true);
                    return;
                }
                
                EnchantingInventory enchantingInventory = (EnchantingInventory) e.getInventory();
                enchantingInventory.setSecondary(new ItemStack(Material.INK_SACK, 64, (short) 4));
            }
        } else {
            if (!(e.getInventory().getHolder() instanceof Gui)) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerCloseEnchant(InventoryCloseEvent e) {
        try {
            if (e.getInventory() instanceof EnchantingInventory) {
                EnchantingInventory enchantingInventory = (EnchantingInventory) e.getInventory();
                enchantingInventory.setSecondary(null);
            }
        } catch (Exception ex) {}
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            Game game = gameManager.getCurrentGame();
            if (e.getClickedBlock() != null) {
                Block block = e.getClickedBlock();
                if (block.getType() == Material.DISPENSER || block.getType() == Material.FURNACE || block.getType() == Material.BURNING_FURNACE) {
                    e.setCancelled(true);
                    return;
                }
                
                if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
                    if (game == null) { return; }
                    if (!game.getTributesTeam().isMember(e.getPlayer().getUniqueId())) {
                        e.setCancelled(true);
                        return;
                    }
                    if (game.isLootedChest(block)) { return; }
                    Inventory inv = ((Chest) block.getState()).getBlockInventory();
                    int maxAmount = 6;
                    if (inv instanceof DoubleChestInventory) {
                        maxAmount *= 2;
                    }
                    
                    List<Loot> loot = plugin.getLootManager().generateLoot(ManiaCore.RANDOM.nextInt(maxAmount) + 2);
                    inv.clear();
                    for (Loot l : loot) {
                        int slot;
                        do {
                            slot = ManiaCore.RANDOM.nextInt(inv.getSize());
                        } while (inv.getItem(slot) != null);
                        inv.setItem(slot, l.generateItemStack());
                    }
                    game.addLootedChest(block.getLocation());
                    GamePlayer gamePlayer = game.getPlayer(e.getPlayer().getUniqueId());
                    gamePlayer.getUser().incrementStat(Stats.HG_CHESTS_FOUND);
                } else if (block.getState() instanceof Sign) {
                    int mapPosition = 0;
                    Location loc = block.getLocation();
                    for (Entry<Integer, Position> entry : lobby.getLobbySigns().getMapSigns().entrySet()) {
                        Position pos = entry.getValue();
                        if (pos.getX() == loc.getX() && pos.getY() == loc.getY() && pos.getZ() == loc.getZ()) {
                            mapPosition = entry.getKey();
                            break;
                        }
                    }
                    
                    if (mapPosition == 0) { return; }
                    for (SpigotUser user : lobby.getHiddenStaff()) {
                        if (user.getUniqueId().equals(e.getPlayer().getUniqueId())) {
                            e.getPlayer().sendMessage(ManiaUtils.color("&cYou cannot vote for a map."));
                            return;
                        }
                    }
                    e.getPlayer().performCommand("map " + mapPosition);
                } else if (block.getType() == Material.CAKE_BLOCK) {
                    if (e.getPlayer().getFoodLevel() != 20) {
                        try {
                            SpigotUser user = (SpigotUser) ManiaCore.getInstance().getUserManager().getUser(e.getPlayer().getUniqueId());
                            Perks.FATTY.activate(user);
                        } catch (Exception ex) {}
                    }
                }
            } else {
                ItemStack hand = e.getItem();
                if (hand == null || hand.getType() == Material.AIR) {
                    return;
                }
                if (hand.getType() == Material.ENDER_PEARL) {
                    System.out.println("Hand item is an enderpearl");
                    if (game.getMutationsTeam().isMember(e.getPlayer().getUniqueId())) {
                        GamePlayer gamePlayer = game.getPlayer(e.getPlayer().getUniqueId());
                        if (gamePlayer.getMutationType() == MutationType.ENDERMAN) {
                            Long lastUse = game.getEndermanPearlLastUse().get(gamePlayer.getUniqueId());
                            if (lastUse != null) {
                                long nextUse = lastUse + TimeUnit.SECONDS.toMillis(game.getGameSettings().getPearlCooldown());
                                if (System.currentTimeMillis() < nextUse) {
                                    e.setCancelled(true);
                                    e.getPlayer().sendMessage(ManiaUtils.color("&cThe enderpearl is still on cooldown."));
                                    return;
                                }
                            }
                            game.getEndermanPearlLastUse().put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
                        }
                    }
                } else if (hand.getType() == Material.ENCHANTED_BOOK) {
                    if (!(game.getSpectatorsTeam().isMember(e.getPlayer().getUniqueId()) || game.getHiddenStaffTeam().isMember(e.getPlayer().getUniqueId()))) {
                        return;
                    }
                    if (!hand.hasItemMeta()) {
                        return;
                    }
                    if (!hand.getItemMeta().hasDisplayName()) {
                        return;
                    }
                    String displayName = hand.getItemMeta().getDisplayName();
                    GameTeam gameTeam = null;
                    if (displayName.contains("Tributes")) {
                        gameTeam = game.getTributesTeam();
                    } else if (displayName.contains("Mutations")) {
                        gameTeam = game.getMutationsTeam();
                    } else if (displayName.contains("Spectators")) {
                        gameTeam = game.getSpectatorsTeam();
                    }
                    
                    new PlayersGui(game, gameTeam).openGUI(e.getPlayer());
                } else if (hand.getType() == Material.ROTTEN_FLESH) {
                    if (!(game.getSpectatorsTeam().isMember(e.getPlayer().getUniqueId()) || game.getHiddenStaffTeam().isMember(e.getPlayer().getUniqueId()))) {
                        return;
                    }
                    
                    if (!game.getGameSettings().isMutations()) {
                        e.getPlayer().sendMessage(ManiaUtils.color("&cMutations have been disabled."));
                        return;
                    }
                    
                    if (game.getGameStart() + TimeUnit.MINUTES.toMillis(game.getGameSettings().getMutationDelay()) > System.currentTimeMillis()) {
                        e.getPlayer().sendMessage(ManiaUtils.color("&cYou must wait " + game.getGameSettings().getMutationDelay() + " minute(s) after the game starts to mutate."));
                        return;
                    }
                    
                    GamePlayer gamePlayer = game.getPlayer(e.getPlayer().getUniqueId());
                    if (gamePlayer.hasMutated()) {
                        e.getPlayer().sendMessage(ManiaUtils.color("&6&l>> &cYou have already mutated."));
                        return;
                    }
                    
                    if (!gamePlayer.isSpectatorByDeath()) {
                        e.getPlayer().sendMessage(ManiaUtils.color("&6&l>> &cYou were not killed. No one to take revenge on!"));
                        return;
                    }
                    
                    if (!(game.getState() == State.PLAYING || game.getState() == State.PLAYING_DEATHMATCH)) {
                        e.getPlayer().sendMessage(ManiaUtils.color("&6&l>> &cYou cannot mutate right now."));
                        return;
                    }
                    
                    if (gamePlayer.isMutating()) {
                        e.getPlayer().sendMessage(ManiaUtils.color("&6&l>> &cYou are already mutating."));
                        return;
                    }
                    
                    DeathInfo deathInfo = gamePlayer.getDeathInfo();
                    if (!(deathInfo instanceof DeathInfoPlayerKill)) {
                        e.getPlayer().sendMessage(ManiaUtils.color("&6&l>> &cNo one to take revenge on!"));
                        return;
                    }
                    
                    DeathInfoPlayerKill playerDeath = (DeathInfoPlayerKill) deathInfo;
                    UUID target = playerDeath.getKiller();
                    if (!game.getTributesTeam().isMember(target)) {
                        e.getPlayer().sendMessage(ManiaUtils.color("&6&l>> &cYour target died, you cannot take revenge."));
                        return;
                    }
                    
                    if (playerDeath.isMutationKill()) {
                        e.getPlayer().sendMessage(ManiaUtils.color("&6&l>> &cYou were killed by a mutation, you cannot take revenge."));
                        return;
                    }
                    
                    Set<UUID> previousMutations = new HashSet<>();
                    for (GamePlayer gp : game.getPlayers()) {
                        if (game.getMutationsTeam().isMember(gp.getUniqueId()) || gp.isMutating()) {
                            if (gp.getMutationTarget().equals(target)) {
                                e.getPlayer().sendMessage(ManiaUtils.color("&cYou cannot mutate against that player because there already is a mutation against them."));
                                return;
                            }
                        }
                        
                        if (gp.getMutationTarget() != null) {
                            if (gp.getMutationTarget().equals(target)) {
                                previousMutations.add(gp.getUniqueId());
                            }
                        }
                    }
                    
                    if (previousMutations.size() >= 2) {
                        e.getPlayer().sendMessage(ManiaUtils.color("&cYou cannot mutate against that player because they already had 2 or more mutations against them already."));
                        return;
                    }
                    
                    new MutateGui(gamePlayer.getUniqueId(), target).openGUI(e.getPlayer());
                } else if (hand.getType() == Material.SULPHUR) {
                    if (game.getMutationsTeam().isMember(e.getPlayer().getUniqueId())) {
                        GamePlayer gamePlayer = game.getPlayer(e.getPlayer().getUniqueId());
                        Player player = Bukkit.getPlayer(e.getPlayer().getUniqueId());
                        Location pLoc = player.getLocation().clone();
                        Location loc = new Location(pLoc.getWorld(), pLoc.getBlockX(), pLoc.getBlockY(), pLoc.getBlockZ());
                        if (gamePlayer.getMutationType().equals(MutationType.CREEPER)) {
                            World world = player.getWorld();
                            game.getSuicideLocations().put(loc, gamePlayer.getUniqueId());
                            TNTPrimed tnt = (TNTPrimed) world.spawnEntity(loc.clone().add(0.5, 0, 0.5), EntityType.PRIMED_TNT);
                            tnt.setYield(4F);
                            tnt.setFuseTicks(0);
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                if (game.getTributesTeam().isMember(gamePlayer.getMutationTarget())) {
                                    e.getPlayer().setHealth(0);
                                }
                            }, 10L);
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e) {
        if (gameManager.getCurrentGame() != null) {
            Game game = gameManager.getCurrentGame();
            GameTeam gameTeam = game.getGameTeam(e.getPlayer().getUniqueId());
            e.setCancelled(!gameTeam.getPermissionValue(Perms.ALLOWED_TO_PICKUP));
        } else {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.setDeathMessage(null);
        
        Game game = gameManager.getCurrentGame();
        if (game == null) { return; }
        Player player = e.getEntity();
        if (!(game.getTributesTeam().isMember(player.getUniqueId()) || game.getMutationsTeam().isMember(player.getUniqueId()))) {
            return;
        }
        
        DeathInfo deathInfo = null;
        final Player killer = player.getKiller();
        if (killer != null) {
            GamePlayer killerPlayer = game.getPlayer(killer.getUniqueId());
            deathInfo = new DeathInfoPlayerKill(player.getUniqueId(), killerPlayer.getUniqueId(), killer.getItemInHand(), killer.getHealth(), game.getGameTeam(killer.getUniqueId()).getColor());
            killerPlayer.getUser().incrementStat(Stats.HG_KILLS);
            if (game.getMutationsTeam().isMember(killer.getUniqueId())) {
                ((DeathInfoPlayerKill) deathInfo).setMutationKill(true);
            }
        } else {
            EntityDamageEvent lastDamageCause = player.getLastDamageCause();
            DamageCause damageCause;
            if (lastDamageCause == null) {
                damageCause = DamageCause.VOID;
            } else {
                damageCause = lastDamageCause.getCause();
            }
            
            String teamColor = game.getGameTeam(player.getUniqueId()).getColor();
            if (damageCause == DamageCause.PROJECTILE) {
                if (lastDamageCause instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) lastDamageCause;
                    if (edbee.getDamager() instanceof Projectile) {
                        Projectile projectile = (Projectile) edbee.getDamager();
                        if (projectile.getShooter() instanceof Entity) {
                            Entity entity = ((Entity) projectile.getShooter());
                            String color = "";
                            if (entity instanceof Player) {
                                color = game.getGameTeam(entity.getUniqueId()).getColor();
                            }
                            deathInfo = new DeathInfoProjectile(player.getUniqueId(), ((Entity) projectile.getShooter()), player.getLocation().distance(entity.getLocation()), color);
                        }
                    }
                }
            } else if (damageCause == DamageCause.SUFFOCATION) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.SUFFOCATION, "&4&l>> %playername% &7suffocated to death.", teamColor);
            } else if (damageCause == DamageCause.FALL) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.FALL, "&4&l>> %playername% &7fell to their death.", teamColor);
            } else if (damageCause == DamageCause.LAVA) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.LAVA, "&4&l>> %playername% &7roasted in lava.", teamColor);
            } else if (damageCause == DamageCause.FIRE || damageCause == DamageCause.FIRE_TICK) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.FIRE, "&4&l>> %playername% &7burned to death.", teamColor);
            } else if (damageCause == DamageCause.DROWNING) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.DROWNING, "&4&l>> %playername% &7drowned.", teamColor);
            } else if (damageCause == DamageCause.ENTITY_EXPLOSION || damageCause == DamageCause.BLOCK_EXPLOSION) {
                if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent edbe = ((EntityDamageByEntityEvent) player.getLastDamageCause());
                    Location dLoc = edbe.getDamager().getLocation().clone();
                    Location loc = new Location(dLoc.getWorld(), dLoc.getBlockX(), dLoc.getBlockY(), dLoc.getBlockZ());
                    if (game.getSuicideLocations().containsKey(loc)) {
                        UUID cause = game.getSuicideLocations().get(loc);
                        deathInfo = new DeathInfoKilledSuicide(player.getUniqueId(), cause, game.getGameTeam(cause).getColor());
                    } else {
                        deathInfo = new DeathInfo(player.getUniqueId(), DeathType.EXPLOSION, "&4&l>> %playername% &7exploded.", teamColor);
                    }
                } else {
                    deathInfo = new DeathInfo(player.getUniqueId(), DeathType.EXPLOSION, "&4&l>> %playername% &7exploded.", teamColor);
                }
            } else if (damageCause == DamageCause.VOID) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.VOID, "&4&l>> %playername% &7fell in the void.", teamColor);
            } else if (damageCause == DamageCause.LIGHTNING) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.LIGHTNING, "&4&l>> %playername% &7was struck by lightning and died.", teamColor);
            } else if (damageCause == DamageCause.SUICIDE) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.SUICIDE, "&4&l>> %playername% &7died.", teamColor);
            } else if (damageCause == DamageCause.STARVATION) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.STARVATION, "&4&l>> %playername% &7starved to death.", teamColor);
            } else if (damageCause == DamageCause.POISON) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.POISON, "&4&l>> %playername% &7was poisoned to death.", teamColor);
            } else if (damageCause == DamageCause.MAGIC) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.MAGIC, "&4&l>> %playername% &7was killed by magic.", teamColor);
            } else if (damageCause == DamageCause.WITHER) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.WITHER, "&4&l>> %playername% &7withered away.", teamColor);
            } else if (damageCause == DamageCause.MELTING) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.WITHER, "&4&l>> %playername% &7melted to death.", teamColor);
            } else if (damageCause == DamageCause.FALLING_BLOCK) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.FALLING_BLOCK, "&4&l>> %playername% &7had a block fall on their head.", teamColor);
            } else if (damageCause == DamageCause.THORNS) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.THORNS, "&4&l>> %playername% &7was poked to death by armor.", teamColor);
            } else if (damageCause == DamageCause.ENTITY_ATTACK) {
                if (lastDamageCause instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) lastDamageCause;
                    Entity damager = edbee.getDamager();
                    if (damager instanceof Player) {
                        deathInfo = new DeathInfoSuicide(player.getUniqueId());
                    } else {
                        deathInfo = new DeathInfoEntity(player.getUniqueId(), damager.getType());
                    }
                }
            }
        }
        
        if (deathInfo == null) {
            deathInfo = new DeathInfo(player.getUniqueId(), DeathType.UNKNOWN, "%playername% &7died to unknown reasons.", game.getGameTeam(player.getUniqueId()).getColor());
        }
        
        Location deathLocation = player.getLocation().clone();
        DeathInfo finalDeathInfo = deathInfo;
        if (game.getMutationsTeam().isMember(player.getUniqueId())) {
            e.getDrops().clear();
        }
        new BukkitRunnable() {
            public void run() {
                player.spigot().respawn();
                player.teleport(deathLocation);
                game.killPlayer(e.getEntity().getUniqueId(), finalDeathInfo);
            }
        }.runTaskLater(HungerGames.getInstance(), 2L);
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        //Move to a runnable for spawns and teleporting
        Location from = e.getFrom(), to = e.getTo();
        
        if (plugin.getGameManager().getCurrentGame() != null) {
            if (plugin.getGameManager().getCurrentGame().getState() == State.COUNTDOWN || plugin.getGameManager().getCurrentGame().getState() == State.DEATHMATCH_COUNTDOWN) {
                if (plugin.getGameManager().getCurrentGame().getTributesTeam().isMember(e.getPlayer().getUniqueId())) {
                    if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
                        e.getPlayer().teleport(e.getFrom());
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onUserIncognito(UserIncognitoEvent e) {
        Game game = gameManager.getCurrentGame();
        if (game != null) {
            e.getAffectedPlayers().clear();
        }
        handleRemove(e.getUser());
        Toggle incognito = e.getUser().getToggle(Toggles.INCOGNITO);
        incognito.setValue(e.newValue() + "");
        handleAdd(e.getUser());
        incognito.setValue(e.oldValue() + "");
    }
    
    private void handleRemove(SpigotUser user) {
        lobby.getVoteStart().remove(user.getUniqueId());
        lobby.getMapOptions().removeVote(user.getUniqueId());
        
        Game game = gameManager.getCurrentGame();
        Messager messager;
        GameSettings gameSettings;
        int totalPlayers;
        if (game != null) {
            if (game.getSpectatorsTeam().isMember(user.getUniqueId())) {
                game.getSpectatorsTeam().leave(user.getUniqueId());
            } else if (game.getHiddenStaffTeam().isMember(user.getUniqueId())) {
                game.getHiddenStaffTeam().leave(user.getUniqueId());
            } else {
                game.killPlayer(user.getUniqueId(), new DeathInfo(user.getUniqueId(), DeathType.LEAVE, "&4&l>> %playername% &7died because they left.", game.getGameTeam(user.getUniqueId()).getColor()));
            }
            game.removePlayer(user.getUniqueId());
            messager = game.getMessager();
            gameSettings = game.getGameSettings();
            totalPlayers = game.getPlayers().length;
        } else {
            lobby.getPlayers().remove(user);
            lobby.getHiddenStaff().remove(user);
            messager = lobby.getMessager();
            gameSettings = lobby.getGameSettings();
            totalPlayers = lobby.getPlayers().size();
            
            if (lobby.getPlayers().size() < lobby.getGameSettings().getMinPlayers()) {
                if (lobby.getVoteTimer() != null) {
                    if (!lobby.getVoteTimer().isForceStarted()) {
                        lobby.getVoteTimer().cancel();
                        lobby.setVoteTimer(null);
                    }
                }
            }
        }
        
        
        if (user.getToggle(Toggles.INCOGNITO).getAsBoolean()) {
            messager.sendMessage("&b" + user.getColoredName() + " &eleft &e&osilently&e. ", Rank.HELPER);
        } else {
            messager.sendMessage("&b" + user.getColoredName() + " &eleft. &5(&d" + totalPlayers + "&5/&d" + gameSettings.getMaxPlayers() + "&5)");
        }
    }
    
    private void handleAdd(SpigotUser user) {
        new BukkitRunnable() {
            public void run() {
                Game game = gameManager.getCurrentGame();
                Redis.pushUser(user);
                
                new BukkitRunnable() {
                    public void run() {
                        user.getBukkitPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                        user.setScoreboard(null);
    
                        Messager messager;
                        GameSettings gameSettings;
                        int totalPlayers;
                        if (game != null) {
                            if (user.getToggle(Toggles.INCOGNITO).getAsBoolean()) {
                                game.getHiddenStaffTeam().join(user.getUniqueId());
                            } else {
                                game.getSpectatorsTeam().join(user.getUniqueId());
                            }
                            user.getBukkitPlayer().teleport(SpigotUtils.positionToLocation(game.getMap().getWorld(), game.getMap().getCenter()));
                            messager = game.getMessager();
                            gameSettings = game.getGameSettings();
                            totalPlayers = game.getPlayers().length;
                        } else {
                            if (user.getToggle(Toggles.INCOGNITO).getAsBoolean()) {
                                lobby.getHiddenStaff().add(user);
                            } else {
                                lobby.getPlayers().add(user);
                            }
                            user.getBukkitPlayer().teleport(plugin.getSpawn());

                            if (lobby.getPlayers().size() >= lobby.getGameSettings().getMinPlayers()) {
                                lobby.startTimer();
                                if (lobby.getVoteTimer() == null) {
                                    lobby.sendMessage("&6&l>> &aMinimum player requirement met. Game starting shortly...");
                                }
                            }

                            user.getBukkitPlayer().setPlayerListName(ManiaUtils.color(user.getDisplayName()));
                            new BukkitRunnable() {
                                public void run() {
                                    user.sendMessage("&6&l>> &e&lDid you know that you can use &f&l/votestart &e&lto start a game early?");
                                }
                            }.runTaskLater(HungerGames.getInstance(), 40L);

                            messager = lobby.getMessager();
                            gameSettings = lobby.getGameSettings();
                            totalPlayers = lobby.getPlayers().size();
                            new LobbyBoard(lobby, user);
                        }
                        
                        if (user.getToggle(Toggles.INCOGNITO).getAsBoolean()) {
                            messager.sendMessage("&a&l>> &b" + user.getColoredName() + " &ejoined &e&osilently&e.", Rank.HELPER);
                        } else {
                            messager.sendMessage("&a&l>> &b" + user.getColoredName() + " &ejoined. &5(&d" + totalPlayers + "&5/&d" + gameSettings.getMaxPlayers() + "&5)");
                        }
                    }
                }.runTask(HungerGames.getInstance());
            }
        }.runTaskAsynchronously(HungerGames.getInstance());
    }
    
    @EventHandler
    public void onPlayerJoin(UserJoinEvent e) {
        handleAdd(e.getSpigotUser());
    }
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Game game = gameManager.getCurrentGame();
        if (game == null) return;
        if (e.getCause() == TeleportCause.ENDER_PEARL) {
            e.setCancelled(true);
            e.getPlayer().teleport(e.getTo(), TeleportCause.PLUGIN);
            try {
                SpigotUser user = (SpigotUser) ManiaCore.getInstance().getUserManager().getUser(e.getPlayer().getUniqueId());
                if (user.getPerkInfo(Perks.ENDERMAN).getValue()) {
                    e.getPlayer().damage(5.0);
                }
            } catch (Exception ex) {
            }
        }
    }
    
    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        Game game = gameManager.getCurrentGame();
        if (game == null) {
            if (lobby.getPlayers().size() >= lobby.getGameSettings().getMaxPlayers()) {
                User user = ManiaCore.getInstance().getUserManager().getUser(e.getUniqueId());
                if (!user.hasPermission(Rank.HELPER)) {
                    e.disallow(Result.KICK_FULL, ManiaUtils.color("&cThe game is full"));
                }
            }
        } else {
            if (game.getPlayers().length >= game.getGameSettings().getMaxPlayers()) {
                User user = ManiaCore.getInstance().getUserManager().getUser(e.getUniqueId());
                if (!user.hasPermission(Rank.HELPER)) {
                    e.disallow(Result.KICK_FULL, ManiaUtils.color("&cThe game is full"));
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        SpigotUser user = (SpigotUser) plugin.getManiaCore().getUserManager().getUser(player.getUniqueId());
        this.handleRemove(user);
        plugin.getManiaCore().getDatabase().pushQueue();
        lobby.getVoteStart().remove(player.getUniqueId());
        for (PerkInfo perk : user.getPerks()) {
            ManiaCore.getInstance().getDatabase().pushRecord(new PerkInfoRecord(perk));
        }
    }
    
    private EnumSet<Material> RAW_FOOD_ITEMS = EnumSet.of(Material.RAW_BEEF, Material.RAW_FISH, Material.RAW_CHICKEN, Material.POTATO, Material.MUTTON, Material.RABBIT);
    
    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (e.getType() != UpdateType.SECOND) return;
        Game game = gameManager.getCurrentGame();
        if (game == null) return;
        for (UUID uuid : game.getTributesTeam()) {
            Player player = Bukkit.getPlayer(uuid);
            ItemStack hand = player.getItemInHand();
            if (RAW_FOOD_ITEMS.contains(hand.getType())) {
                try {
                    String rawTime = NBTWrapper.getNBTString(hand, "heldtime");
                    int heldTime = Integer.parseInt(rawTime);
                    if (heldTime >= 7) {
                        switch (hand.getType()) {
                            case RAW_BEEF:
                                hand.setType(Material.COOKED_BEEF);
                                break;
                            case RAW_FISH:
                                hand.setType(Material.COOKED_FISH);
                                break;
                            case RAW_CHICKEN:
                                hand.setType(Material.COOKED_CHICKEN);
                                break;
                            case POTATO:
                                hand.setType(Material.BAKED_POTATO);
                                break;
                            case MUTTON:
                                hand.setType(Material.COOKED_MUTTON);
                                break;
                            case RABBIT:
                                hand.setType(Material.COOKED_RABBIT);
                                break;
                        }
                    }
                } catch (Exception ex) {
                }
            }
        }
    }
    
    @EventHandler
    public void onActionBarUpdateEvent(UserActionBarUpdateEvent e) {
        if (lobby.getVoteTimer() != null && lobby.getVoteTimer().getRemainingSeconds() > 0) {
            e.setCancelled(true);
            return;
        }
        
        if (gameManager.getCurrentGame() != null) {
            e.setCancelled(true);
        }
    }
}
