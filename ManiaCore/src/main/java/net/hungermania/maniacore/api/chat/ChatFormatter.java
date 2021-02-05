package net.hungermania.maniacore.api.chat;

import net.hungermania.maniacore.api.channel.Channel;
import net.hungermania.maniacore.api.leveling.Level;
import net.hungermania.maniacore.api.nickname.Nickname;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.user.User;

public class ChatFormatter {
    
    public static final String CHANNEL_HEADER = "{symbolcolor}&l[{channelcolor}{symbolcolor}&l]"; //Space and color added during processing
    public static final String LEVEL_FORMAT = "&8[{levelcolor}{level}&8]";
    public static final String PLAYER_NAME_FORMAT = "{prefix}{name}{suffix}"; //Spaces added during processing
    public static final String MESSAGE_FORMAT = "{chatcolor}{message}";
    
    protected String format;

    public ChatFormatter(String format) {
        this.format = format;
    }
    
    public String format(User user, String message) {
        String format = this.format;
        Channel channel = user.getChannel();
        Level level = user.getLevel();
        Rank rank = user.getRank();
        Nickname nickname = user.getNickname();
        String username = user.getName();
        if (nickname != null && nickname.isActive()) {
            rank = nickname.getRank();
            username = nickname.getName();
        }
        format = format.replace("{symbolcolor}", channel.getSymbolColor());
        format = format.replace("{channelcolor}", channel.getColor());
        format = format.replace("{levelcolor}", level.getNumberColor().toString());
        format = format.replace("{level}", level.getNumber() + "");
        format = format.replace("{prefix}", rank.getPrefix() + " ");
        format = format.replace("{suffix}", "");
        format = format.replace("{chatcolor}", rank.getChatColor());
        format = format.replace("{message}", message);
        format = format.replace("{name}", username);
        format = format.replace("{trueName}", user.getName());
        format = format.replace("{truePrefix}", user.getRank().getPrefix());
        return format;
    }
}
