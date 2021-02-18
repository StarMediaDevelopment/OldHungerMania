package net.hungermania.hungergames.game.cmd;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.loot.DiamondLoot;
import net.hungermania.hungergames.loot.Loot;
import net.hungermania.hungergames.loot.LootManager;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.manialib.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ProbablityCmd implements CommandExecutor {
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(args.length > 0)) {
            sender.sendMessage(ManiaUtils.color("&cYou must provide an item name or material type"));
            return true;
        }

        LootManager lootManager = HungerGames.getInstance().getLootManager();
        Loot loot = null;

        String rawName = StringUtils.join(args, "", 0, args.length).replace(" ", "_");
        try {
            Material material = Material.valueOf(rawName.toUpperCase());
            for (Loot pl : lootManager.getPossibleLoot().values()) {
                if (pl instanceof DiamondLoot) {
                    DiamondLoot diamondLoot = (DiamondLoot) pl;
                    for (Loot dl : diamondLoot.getLoot()) {
                        if (dl.getMaterial().equals(material)) {
                            loot = dl;
                            break;
                        }
                    }
                } else {
                    if (pl.getMaterial().equals(material)) {
                        loot = pl;
                        break;
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            for (Loot pl : lootManager.getPossibleLoot().values()) {
                if (pl.getName() != null && !pl.getName().equals("")) {
                    if (pl.getName().equalsIgnoreCase(rawName)) {
                        loot = pl;
                        break;
                    }
                }
            }
        }
        
        if (loot == null) {
            sender.sendMessage(ManiaUtils.color("&cCannot find a loot table entry with the name " + rawName));
            return true;
        }
        
        double probability = ((loot.getWeight() * 1.0) / lootManager.getLootChances().size()) * 100.0D;
        sender.sendMessage(ManiaUtils.color("&6&l>> &eYour probability of receiving &l" + loot.getName() + " &eis &b" + Constants.NUMBER_FORMAT.format(probability) + "%&e!"));
        sender.sendMessage(ManiaUtils.color("&6&l>> &7(Or otherwise a &3" + loot.getWeight() + "&7/&3" + lootManager.getLootChances().size()));
        return true;
    }
}
