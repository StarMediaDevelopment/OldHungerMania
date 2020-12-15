package net.hungermania.maniacore.bungee.util;


import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class BungeeUtils {
    public static void sendMessage(CommandSender sender, String text, ChatColor color) {
        sender.sendMessage(new ComponentBuilder(text).color(color).create());
    }
}
