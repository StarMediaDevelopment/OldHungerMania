package net.hungermania.hungergames.game.gui;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.game.Game;
import net.hungermania.hungergames.game.GamePlayer;
import net.hungermania.hungergames.game.sponsoring.SponsorType;
import net.hungermania.maniacore.api.stats.Statistic;
import net.hungermania.maniacore.api.stats.Stats;
import net.hungermania.maniacore.spigot.gui.GUIButton;
import net.hungermania.maniacore.spigot.gui.Gui;
import net.hungermania.maniacore.spigot.util.ItemBuilder;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;

public class SponsorGui extends Gui {
    public SponsorGui(Game game, GamePlayer actor, GamePlayer target) {
        super(HungerGames.getInstance(), "Sponsor " + target.getUser().getName(), false, 9);

        for (SponsorType type : SponsorType.values()) {
            addButton(new GUIButton(ItemBuilder.start(type.getDisplay()).setDisplayName("&e" + type.getName()).setLore(Arrays.asList("&7" + type.getDescription(), "", "&6&lLeft click &fto sponsor with Points!", "&6&lRight click &fto sponsor with Coins!")).build()).setListener(e -> {
                if (actor.hasSponsored()) {
                    e.getWhoClicked().closeInventory();
                    actor.sendMessage("&cYou have already sponsored this game.");
                } else {
                    if (e.getClick().equals(ClickType.LEFT)) {
                        Statistic stat = actor.getUser().getStat(Stats.HG_SCORE);
                        stat.setValue(stat.getAsInt() - type.getPriceScore());
                    } else if (e.getClick().equals(ClickType.RIGHT)) {
                        Statistic stat = actor.getUser().getStat(Stats.COINS);
                        stat.setValue(stat.getAsInt() - type.getPriceCoins());
                    } else {
                        return;
                    }
                    
                    game.sponsorPlayer(actor, target, type);
                }
            }));
        }
    }
}
