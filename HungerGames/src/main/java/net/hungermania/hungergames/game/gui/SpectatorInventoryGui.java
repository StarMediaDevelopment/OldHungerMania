package net.hungermania.hungergames.game.gui;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.game.Game;
import net.hungermania.hungergames.game.GamePlayer;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.gui.GUIButton;
import net.hungermania.maniacore.spigot.gui.Gui;
import net.hungermania.maniacore.spigot.util.ItemBuilder;
import net.hungermania.manialib.util.Constants;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class SpectatorInventoryGui extends Gui {
    public SpectatorInventoryGui(Game game, GamePlayer player, GamePlayer target) {
        super(HungerGames.getInstance(), ManiaUtils.color(target.getUser().getName() + "'s Inventory &c&lWIP"), false, 54);

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
        setButton(5, new GUIButton(ItemBuilder.start(Material.GLASS_BOTTLE).setDisplayName("&ePotion Effects &c&lWIP").build()));
        setButton(8, new GUIButton(ItemBuilder.start(Material.ARROW).setDisplayName("&eBack").build()).setListener(e -> new SpectatorGui(game, player, target).openGUI(player.getUser().getBukkitPlayer())));
        
        int offset = 9;
        for (int i = 0; i < 36; i++) {
            setButton(offset + i, new GUIButton(targetPlayer.getInventory().getItem(i)).setListener(e -> e.setCancelled(true)));
        }
        
        setButton(50, new GUIButton(targetPlayer.getInventory().getHelmet()).setListener(e -> e.setCancelled(true)));
        setButton(51, new GUIButton(targetPlayer.getInventory().getChestplate()).setListener(e -> e.setCancelled(true)));
        setButton(52, new GUIButton(targetPlayer.getInventory().getLeggings()).setListener(e -> e.setCancelled(true)));
        setButton(53, new GUIButton(targetPlayer.getInventory().getBoots()).setListener(e -> e.setCancelled(true)));
    }
}
