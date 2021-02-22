package net.hungermania.hungergames.game.gui;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.game.GamePlayer;
import net.hungermania.maniacore.spigot.gui.Gui;

public class SponsorGui extends Gui {
    public SponsorGui(GamePlayer actor, GamePlayer target) {
        super(HungerGames.getInstance(), "Sponsor " + target.getUser().getName(), false, 9);
    }
}
