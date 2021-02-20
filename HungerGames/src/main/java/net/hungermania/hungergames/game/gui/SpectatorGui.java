package net.hungermania.hungergames.game.gui;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.game.GamePlayer;
import net.hungermania.maniacore.spigot.gui.Gui;

public class SpectatorGui extends Gui {
    public SpectatorGui(GamePlayer player, GamePlayer target) {
        super(HungerGames.getInstance(), target.getUser().getName(), false, 9);
    }
}
