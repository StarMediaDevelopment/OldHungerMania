package net.hungermania.maniacore.spigot.mutations;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.stats.Stats;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.gui.GUIButton;
import net.hungermania.maniacore.spigot.gui.Gui;
import net.hungermania.maniacore.spigot.util.ItemBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public class MutateGui extends Gui {

    public MutateGui(UUID mutator) {
        super(Bukkit.getPluginManager().getPlugin("ManiaCore"), "Purchase Mutations", false, 27);

        GUIButton unlockedWool = new GUIButton(ItemBuilder.start(Material.WOOL, 1, (short) 13).setDisplayName("&a&lAvailable").build());
        GUIButton purchasableWool = new GUIButton(ItemBuilder.start(Material.WOOL, 1, (short) 4).setDisplayName("&e&lPurchasable").build());
        GUIButton lockedWool = new GUIButton(ItemBuilder.start(Material.WOOL, 1, (short) 14).setDisplayName("&c&lLocked").build());

        GUIButton unlockedPane = new GUIButton(ItemBuilder.start(Material.STAINED_GLASS_PANE, 1, (short) 5).setDisplayName("&a").build());
        GUIButton purchasablePane = new GUIButton(ItemBuilder.start(Material.STAINED_GLASS_PANE, 1, (short) 4).setDisplayName("&e").build());
        GUIButton lockedPane = new GUIButton(ItemBuilder.start(Material.STAINED_GLASS_PANE, 1, (short) 14).setDisplayName("&c").build());

        setButton(0, unlockedWool);
        setButton(1, unlockedPane);
        setButton(9, purchasableWool);
        setButton(10, purchasablePane);
        setButton(18, lockedWool);
        setButton(19, lockedPane);

        Mutation[] mutations = Mutations.MUTATIONS.values().toArray(new Mutation[0]);
        Map<MutationStatus, Map<MutationType, ItemStack>> mutationStacks = new HashMap<>();
        User user = ManiaCore.getInstance().getUserManager().getUser(mutator);
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
                case LOCKED:
                    break;
                case PURCHASABLE:
                    itemBuilder.addLoreLine("&6&lRight Click &fto purchase &a" + mutation.getName() + " &ffor &e" + mutation.getUnlockCost() + " &fcoins.");
                    break;
            }
            if (mutationStacks.containsKey(status)) {
                mutationStacks.get(status).put(mutation.getType(), itemBuilder.build());
            } else {
                mutationStacks.put(status, new HashMap<MutationType, ItemStack>() {{
                    put(mutation.getType(), itemBuilder.build());
                }});
            }
        }

        AtomicInteger availableCounter = new AtomicInteger(2), purchasableCounter = new AtomicInteger(11), lockedCounter = new AtomicInteger(20);
        mutationStacks.forEach((status, items) -> {
            if (status == MutationStatus.AVAILABLE) {
                for (Entry<MutationType, ItemStack> entry : items.entrySet()) {
                    setButton(availableCounter.get(), new GUIButton(entry.getValue()).setListener((e) -> {
                        e.getWhoClicked().sendMessage(ManiaUtils.color("&cYou have already purchased that mutation."));
                        e.getWhoClicked().closeInventory();
                    }));
                    availableCounter.getAndIncrement();
                }
            } else if (status == MutationStatus.PURCHASABLE) {
                for (Entry<MutationType, ItemStack> entry : items.entrySet()) {
                    setButton(purchasableCounter.get(), new GUIButton(entry.getValue()).setListener(e -> {
                        if (e.getClick() == ClickType.RIGHT) {
                            Mutation mutation = Mutations.MUTATIONS.get(entry.getKey());
                            if (user.getStat(Stats.COINS).getValueAsInt() >= mutation.getUnlockCost()) {
                                user.getStat(Stats.COINS).setValue((user.getStat(Stats.COINS).getValueAsInt() - mutation.getUnlockCost()) + "");
                                unlockedTypes.add(entry.getKey());
                                user.setStat(Stats.HG_UNLOCKED_MUTATIONS, StringUtils.join(unlockedTypes, ";"));
                                e.getWhoClicked().closeInventory();
                                refreshInventory(e.getWhoClicked());
                            } else {
                                user.sendMessage("&cYou do not have enough funds to purchase that mutation type.");
                            }
                        }
                    }));
                    purchasableCounter.getAndIncrement();
                }
            } else if (status == MutationStatus.LOCKED) {
                for (Entry<MutationType, ItemStack> entry : items.entrySet()) {
                    setButton(lockedCounter.get(), new GUIButton(entry.getValue()));
                    lockedCounter.getAndIncrement();
                }
            }
        });
    }
}
