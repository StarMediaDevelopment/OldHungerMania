package net.hungermania.hungergames.game.gui;

import lombok.Getter;
import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.game.Game;
import net.hungermania.hungergames.game.GamePlayer;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.gui.GUIButton;
import net.hungermania.maniacore.spigot.gui.Gui;
import net.hungermania.maniacore.spigot.util.ItemBuilder;
import net.hungermania.manialib.util.Constants;
import net.hungermania.manialib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SpectatorInventoryGui extends Gui {
    
    public static final int OFFSET = 9, HELM = 50, CHEST = HELM + 1, LEGS = CHEST + 1, BOOTS = LEGS + 1; 
    @Getter private GamePlayer player, target;
    
    @Getter private static Set<Inventory> guiInstances = new HashSet<>();
    
    private static BukkitRunnable task;
    
    public SpectatorInventoryGui(Game game, GamePlayer player, GamePlayer target) {
        super(HungerGames.getInstance(), ManiaUtils.color(target.getUser().getName() + "'s Inventory &c&lWIP"), false, 54);

        this.player = player;
        this.target = target;
        
        ItemStack skull = target.getSkull().clone();
        ItemMeta skullMeta = skull.getItemMeta();
        skullMeta.setDisplayName(ManiaUtils.color("&e" + target.getUser().getName()));
        String line = "&7This player is " + target.getTeam().getColor() + "&l";
        if (game.getTributesTeam().isMember(target.getUniqueId())) {
            line += "ALIVE";
        } else if (game.getSpectatorsTeam().isMember(target.getUniqueId())) {
            line += "DEAD";
        } else if (game.getMutationsTeam().isMember(target.getUniqueId())) {
            line += "MUTATED";
        }
        skullMeta.setLore(Collections.singletonList(ManiaUtils.color(line)));
        skull.setItemMeta(skullMeta);
        setButton(0, new GUIButton(skull));
        setButton(1, new GUIButton(ItemBuilder.start(Material.DIAMOND_SWORD).setDisplayName("&eStats &c&lWIP").build()));
        Player targetPlayer = target.getUser().getBukkitPlayer();
        setButton(2, new GUIButton(ItemBuilder.start(Material.GOLDEN_APPLE).setDisplayName("&eHealth: &c&l" + Constants.NUMBER_FORMAT.format(targetPlayer.getHealth()) + "/" + Constants.NUMBER_FORMAT.format(targetPlayer.getMaxHealth())).build()));
        setButton(3, new GUIButton(ItemBuilder.start(Material.COOKED_BEEF).setDisplayName("&eFood: &c&l" + Constants.NUMBER_FORMAT.format(targetPlayer.getFoodLevel()) + "/" + Constants.NUMBER_FORMAT.format(20)).build()));
        setButton(4, new GUIButton(ItemBuilder.start(Material.EXP_BOTTLE).setDisplayName("&eXP Level: &b" + targetPlayer.getLevel()).build()));
        List<String> activeEffects = new ArrayList<>();
        for (PotionEffect effect : targetPlayer.getActivePotionEffects()) {
            activeEffects.add(effect.getType().getName() + " " + Utils.romanNumerals(effect.getAmplifier()));
        }
        setButton(5, new GUIButton(ItemBuilder.start(Material.GLASS_BOTTLE).setDisplayName("&ePotion Effects").setLore(activeEffects).build()));
        setButton(8, new GUIButton(ItemBuilder.start(Material.ARROW).setDisplayName("&eBack").build()).setListener(e -> new SpectatorGui(game, player, target).openGUI(player.getUser().getBukkitPlayer())));
        
        for (int i = 0; i < 36; i++) {
            setButton(OFFSET + i, new GUIButton(targetPlayer.getInventory().getItem(i)).setListener(e -> e.setCancelled(true)));
        }
        
        setButton(HELM, new GUIButton(targetPlayer.getInventory().getHelmet()).setListener(e -> e.setCancelled(true)));
        setButton(CHEST, new GUIButton(targetPlayer.getInventory().getChestplate()).setListener(e -> e.setCancelled(true)));
        setButton(LEGS, new GUIButton(targetPlayer.getInventory().getLeggings()).setListener(e -> e.setCancelled(true)));
        setButton(BOOTS, new GUIButton(targetPlayer.getInventory().getBoots()).setListener(e -> e.setCancelled(true)));
    }
    
    public static void prepareTask() {
        task = new BukkitRunnable() {
            public void run() {
                if (HungerGames.getInstance().getGameManager().getCurrentGame() == null) {
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    InventoryView invView = player.getOpenInventory();
                    if (invView == null) {
                        continue;
                    }

                    Inventory inv = invView.getTopInventory();
                    if (!(inv.getHolder() instanceof SpectatorInventoryGui)) {
                        continue;
                    }
                    
                    SpectatorInventoryGui gui = (SpectatorInventoryGui) inv.getHolder();
                    if (gui == null) {
                        continue;
                    }
                    PlayerInventory targetInv = gui.getTarget().getUser().getBukkitPlayer().getInventory();
                    for (int i = 0; i < 36; i++) {
                        ItemStack item = targetInv.getItem(i);
                        inv.setItem(i + OFFSET, item);
                        gui.getButton(i + OFFSET).setItem(item);
                    }
                    
                    gui.getItems().get(HELM).setItem(targetInv.getHelmet());
                    gui.getItems().get(CHEST).setItem(targetInv.getChestplate());
                    gui.getItems().get(LEGS).setItem(targetInv.getLeggings());
                    gui.getItems().get(BOOTS).setItem(targetInv.getBoots());
                    
                    inv.setItem(HELM, targetInv.getHelmet());
                    inv.setItem(CHEST, targetInv.getChestplate());
                    inv.setItem(LEGS, targetInv.getLeggings());
                    inv.setItem(BOOTS, targetInv.getBoots());
                    //player.updateInventory();
                }
            }
        };
        task.runTaskTimer(HungerGames.getInstance(), 20L, 5L);
    }

    public Inventory openGUI(HumanEntity player) {
        Inventory inventory = super.openGUI(player);
        guiInstances.add(inventory);
        return inventory;
    }
}
