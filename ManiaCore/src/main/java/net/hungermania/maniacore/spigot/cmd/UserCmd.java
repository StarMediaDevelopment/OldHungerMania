package net.hungermania.maniacore.spigot.cmd;

import net.hungermania.maniacore.ManiaCorePlugin;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.records.UserRecord;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.util.SpigotUtils;
import net.hungermania.manialib.sql.IRecord;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserCmd implements CommandExecutor {
    
    private ManiaCorePlugin plugin;

    public UserCmd(ManiaCorePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = SpigotUtils.getRankFromSender(sender);
        if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
            sender.sendMessage(ManiaUtils.color("&cYou are not allowed to use that command."));
            return true;
        }
        
        if (!(args.length > 0)) {
            sender.sendMessage(ManiaUtils.color("&cUsage: /user <name> <subcommand>"));
            return true;
        }
        
        if (ManiaUtils.checkCmdAliases(args, 0, "file")) {
            new BukkitRunnable() {
                public void run() {
                    Set<User> users = new HashSet<>();
                    sender.sendMessage(ManiaUtils.color("&aProcessing your request..."));
                    List<IRecord> records = ManiaCore.getInstance().getDatabase().getRecords(UserRecord.class, null, null);
                    sender.sendMessage(ManiaUtils.color("&aFound a total of " + records.size() + " entries in the database"));
                    for (IRecord record : records) {
                        if (record instanceof UserRecord) {
                            users.add(((UserRecord) record).toObject());
                        }
                    }
                    sender.sendMessage(ManiaUtils.color("&aFound a total of " + users.size() + " users in the database"));
                    File folder = new File(plugin.getDataFolder() + File.separator + "output");
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    File output = new File(folder + File.separator + "users-" + System.currentTimeMillis() + ".txt");
                    if (!output.exists()) {
                        try {
                            output.createNewFile();
                        } catch (IOException e) {
                            sender.sendMessage(ManiaUtils.color("&cCould not create the output file: " + e.getMessage()));
                            return;
                        }
                    }

                    try (FileOutputStream fileOut = new FileOutputStream(output); PrintWriter out = new PrintWriter(fileOut)) {
                        for (User user : users) {
                            out.println(user.getUniqueId() + " - " + user.getName() + " - " + user.getRank().name());
                        }
                        out.flush();
                    } catch (Exception e) {
                        sender.sendMessage(ManiaUtils.color("&cThere was an error saving user data to the file " + e.getMessage()));
                    } 
                    sender.sendMessage(ManiaUtils.color("&aSuccessfully saved to the file " + output.getName()));
                }
            }.runTaskAsynchronously(plugin);
        }
        return true;
    }
}
