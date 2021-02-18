package net.hungermania.hungergames.game.gui;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.game.Game;
import net.hungermania.hungergames.game.team.GameTeam;
import net.hungermania.maniacore.spigot.gui.GUIButton;
import net.hungermania.maniacore.spigot.gui.Gui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayersGui extends Gui {
    public PlayersGui(Game game, GameTeam team) {
        super(HungerGames.getInstance(), team.getName(), false, 27);
    
        for (UUID p : team.getMembers()) {
            GUIButton button = new GUIButton(game.getPlayer(p).getSkull());
            button.setListener(e -> {
                Player player = Bukkit.getPlayer(p);
                e.getWhoClicked().teleport(player);
            });
            addButton(button);
        }
    }
}
