package net.hungermania.maniacore.spigot.test;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.util.ManiaUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class TestCmd implements CommandExecutor {
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            DatabaseTest.test(ManiaCore.getInstance());
        } catch (IOException e) {
            sender.sendMessage(ManiaUtils.color("&cAn error occurred: " + e.getMessage()));
        }
        return true;
    }
}
