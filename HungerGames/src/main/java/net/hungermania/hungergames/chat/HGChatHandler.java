package net.hungermania.hungergames.chat;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.game.Game;
import net.hungermania.hungergames.game.GamePlayer;
import net.hungermania.maniacore.api.chat.ChatHandler;
import net.hungermania.maniacore.api.util.State;
import net.hungermania.maniacore.spigot.user.SpigotUser;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HGChatHandler extends ChatHandler {
    public Set<UUID> getAllTargets() {
        Set<UUID> targets = new HashSet<>();
        Game game = HungerGames.getInstance().getGameManager().getCurrentGame();
        if (game == null) {
            for (SpigotUser player : HungerGames.getInstance().getLobby().getPlayers()) {
                targets.add(player.getUniqueId());
            }
        } else {
            for (GamePlayer player : game.getPlayers()) {
                targets.add(player.getUniqueId());
            }
        }
        return targets;
    }

    public Set<UUID> getMessageTargets(UUID sender) {
        Set<UUID> targets = super.getMessageTargets(sender);
        Game game = HungerGames.getInstance().getGameManager().getCurrentGame();
        if (game != null) {
            if (game.getState() != State.ENDING) {
                if (game.getSpectatorsTeam().isMember(sender) || game.getHiddenStaffTeam().isMember(sender)) {
                    targets.removeIf(target -> game.getTributesTeam().isMember(target) || game.getMutationsTeam().isMember(target));
                }
            }
        }
        return targets;
    }
}
