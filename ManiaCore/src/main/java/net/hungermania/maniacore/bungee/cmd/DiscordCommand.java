package net.hungermania.maniacore.bungee.cmd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class DiscordCommand extends Command {
    public DiscordCommand() {
        super("discord");
    }
    
    @Override
    public void execute(CommandSender sender, String[] strings) {
        sender.sendMessage(new ComponentBuilder("Join Our Public Discord! ").color(ChatColor.RED).bold(true).append("https://discord.gg/Z95xgD7").color(ChatColor.AQUA).underlined(true).event(new ClickEvent(Action.OPEN_URL, "https://discord.gg/Z95xgD7")).create());
    }
}
