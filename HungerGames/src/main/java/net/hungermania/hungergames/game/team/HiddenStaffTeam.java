package net.hungermania.hungergames.game.team;

import net.hungermania.hungergames.game.Game;
import net.hungermania.hungergames.game.enums.PlayerType;
import org.bukkit.GameMode;

public class HiddenStaffTeam extends SpectatorsTeam {
    public HiddenStaffTeam(Game game) {
        super("Hidden Staff", "&b", PlayerType.HIDDEN_STAFF, game);
        setGameMode(GameMode.ADVENTURE);
    }
}
