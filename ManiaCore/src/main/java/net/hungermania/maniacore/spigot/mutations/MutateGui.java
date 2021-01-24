package net.hungermania.maniacore.spigot.mutations;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.stats.Stats;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.gui.GUIButton;
import net.hungermania.maniacore.spigot.gui.Gui;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import net.hungermania.maniacore.spigot.util.ItemBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MutateGui extends Gui {
    public MutateGui(UUID mutator) {
        super(Bukkit.getPluginManager().getPlugin("ManiaCorePlugin"), "Purchase Mutations", false, 27);
        Mutation[] mutations = Mutations.MUTATIONS.values().toArray(new Mutation[0]);
        SpigotUser user = (SpigotUser) ManiaCore.getInstance().getUserManager().getUser(mutator);
        String[] rawUnlocked = user.getStat(Stats.HG_UNLOCKED_MUTATIONS).getValueAsString().split(";");
        Set<MutationType> unlockedTypes = new HashSet<>();
        for (String s : rawUnlocked) {
            unlockedTypes.add(MutationType.valueOf(s.toUpperCase()));
        }
        for (Mutation mutation : mutations) {
            MutationStatus status;
            
            if (unlockedTypes.contains(mutation.getType())) {
                status = MutationStatus.AVAILABLE;
            } else {
                if (user.getStat(Stats.COINS).getValueAsInt() >= mutation.getUnlockCost()) {
                    status = MutationStatus.PURCHASABLE;
                } else {
                    status = MutationStatus.LOCKED;
                }
            }
            
            String availableLine = "";
            switch (status) {
                case AVAILABLE:
                    availableLine = "&a&oAvailable";
                    break;
                case PURCHASABLE:
                    availableLine = "&e&oPurchasable";
                    break;
                case LOCKED:
                    availableLine = "&c&oLocked";
                    break;
            }
            
            ItemBuilder itemBuilder = ItemBuilder.start(mutation.getIcon()).setDisplayName("&a&l" + mutation.getName().toUpperCase()).withLore(availableLine, "", "&2&lBuffs&a&l:");
            for (String buff : mutation.getBuffs()) {
                itemBuilder.addLoreLine("&8- &a" + buff);
            }
            itemBuilder.addLoreLine("&4&lDEBUFFS&c:");
            for (String debuff : mutation.getDebuffs()) {
                itemBuilder.addLoreLine("&8- &c" + debuff);
            }
            itemBuilder.addLoreLine("").addLoreLine("&7Max HP: &e" + mutation.getMaxHP()).addLoreLine("&7Defense: &e" + mutation.getDefenseType().name()).addLoreLine("");
            switch (status) {
                case AVAILABLE:
                    itemBuilder.addLoreLine("&6&lLeft Click &7to use &a" + mutation.getName() + " &efor &a" + mutation.getUseCost() + " &fcoins.");
                    break;
                case PURCHASABLE:
                    itemBuilder.addLoreLine("&6&lRight Click &fto purchase &a" + mutation.getName() + " &ffor &e" + mutation.getUnlockCost() + " &fcoins.");
                    break;
                case LOCKED:
                    break;
            }
            addButton(new GUIButton(itemBuilder.build()).setListener((e) -> {
                if (status == MutationStatus.AVAILABLE) {
                    e.getWhoClicked().sendMessage(ManiaUtils.color("&cYou have already purchased that mutation."));
                } else if (status == MutationStatus.PURCHASABLE) {
                    if (e.getClick() == ClickType.RIGHT) {
                        if (user.getStat(Stats.COINS).getValueAsInt() >= mutation.getUnlockCost()) {
                            user.getStat(Stats.COINS).setValue((user.getStat(Stats.COINS).getValueAsInt() - mutation.getUnlockCost()) + "");
                            unlockedTypes.add(mutation.getType());
                            user.setStat(Stats.HG_UNLOCKED_MUTATIONS, StringUtils.join(unlockedTypes, ";"));
                            e.getWhoClicked().sendMessage(ManiaUtils.color("&aYou purchased the mutation " + mutation.getName()));
                        } else {
                            user.sendMessage("&cYou do not have enough funds to purchase that mutation type.");
                        }
                    }
                } else {
                    e.getWhoClicked().sendMessage(ManiaUtils.color("&cYou do not have enough funds to purchase that mutation."));
                }
            }));
        }
    }
}
