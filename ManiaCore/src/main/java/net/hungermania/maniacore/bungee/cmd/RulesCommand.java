package net.hungermania.maniacore.bungee.cmd;

import net.hungermania.maniacore.bungee.util.BungeeUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.LinkedList;

public class RulesCommand extends Command {
    public RulesCommand() {
        super("rules");
    }
    
    public void execute(CommandSender sender, String[] args) {
        LinkedList<String> rulesList = new LinkedList<>(Arrays.asList("1. No Blacklisted Modifications", "2. No chat bypassing", "3. No Advertising Other platforms unrelated to HungerMania", "4. (Unless you are media rank..) No advertising your social medias.", "5. No Disrespecting staff or players.", "6. No Spamming Chat.", "7. No DDos or Dox Threats", "8. No Toxicity (General Threats)", "9. Teaming is NOT allowed", "10. No Cursing", "11. No Boosting", "12. Don't abuse exploits"));
        BungeeUtils.sendMessage(sender, "HungerMania Rules", ChatColor.GOLD);
        for (String rule : rulesList) {
            BungeeUtils.sendMessage(sender, rule, ChatColor.GREEN);
        }
    }
}
