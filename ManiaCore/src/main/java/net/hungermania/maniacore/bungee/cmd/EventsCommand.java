package net.hungermania.maniacore.bungee.cmd;

import net.hungermania.maniacore.ManiaCoreProxy;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.events.EventInfo;
import net.hungermania.maniacore.api.records.EventInfoRecord;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.Utils;
import net.hungermania.maniacore.bungee.util.BungeeUtils;
import net.hungermania.manialib.util.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@SuppressWarnings("DuplicatedCode")
public class EventsCommand extends Command {
    
    private ManiaCoreProxy plugin;
    
    public EventsCommand(ManiaCoreProxy plugin) {
        super("events", "maniacore.bungee.command.events");
        this.plugin = plugin;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        BaseComponent[] notEnoughArguments = new ComponentBuilder("You do not have enough arguments").color(ChatColor.RED).create();
        
        if (!(args.length > 0)) {
            sender.sendMessage(notEnoughArguments);
            return;
        }
        
        if (Utils.checkCmdAliases(args, 0, "create")) {
            if (!(args.length > 2)) {
                sender.sendMessage(notEnoughArguments);
                return;
            }
            
            String name = args[1];
            long startDate;
            try {
                startDate = Utils.parseCalendarDate(StringUtils.join(args, " ", 2, args.length)).getTimeInMillis();
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(new ComponentBuilder("You provided an invalid date value.").color(ChatColor.RED).create());
                return;
            }
            
            EventInfo eventInfo = new EventInfo(name, startDate);
            ManiaCore.getInstance().getDatabase().pushRecord(new EventInfoRecord(eventInfo));
            if (eventInfo.getId() == 0) {
                sender.sendMessage(new ComponentBuilder("An error occured while saving the information").color(ChatColor.RED).create());
                return;
            }
            
            ManiaCore.getInstance().getEventManager().getEvents().put(eventInfo.getId(), eventInfo);
            sender.sendMessage(new ComponentBuilder("Created an event with the name " + name).color(ChatColor.GREEN).create());
        } else if (Utils.checkCmdAliases(args, 0, "edit")) {
            if (!(args.length > 3)) {
                sender.sendMessage(notEnoughArguments);
                return;
            }
            
            int id;
            try {
                id = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(new ComponentBuilder("You provided an invalid number").color(ChatColor.RED).create());
                return;
            }
            
            EventInfo eventInfo = ManiaCore.getInstance().getEventManager().getEvents().get(id);
            if (eventInfo == null) {
                sender.sendMessage(new ComponentBuilder("The event id you provided is invalid").color(ChatColor.RED).create());
                return;
            }
            
            if (Utils.checkCmdAliases(args, 2, "setname")) {
                eventInfo.setName(StringUtils.join(args, 3, args.length));
                sender.sendMessage(new ComponentBuilder("You have set the name of the event to " + eventInfo.getName()).color(ChatColor.GREEN).create());
            } else if (Utils.checkCmdAliases(args, 2, "setactive")) {
                boolean value;
                try {
                    value = Boolean.parseBoolean(args[3]);
                } catch (Exception e) {
                    sender.sendMessage(new ComponentBuilder("You provided an invalid value, must be true or false").color(ChatColor.RED).create());
                    return;
                }
    
                EventInfo activeEvent = ManiaCore.getInstance().getEventManager().getActiveEvent();
                if (value) {
                    if (activeEvent != null) {
                        if (activeEvent.isActive()) {
                            sender.sendMessage(new ComponentBuilder("There already is an active event").color(ChatColor.RED).create());
                            return;
                        }
                    }
                }
    
                eventInfo.setActive(value);
                ManiaCore.getInstance().getEventManager().setActiveEvent(eventInfo);
                plugin.getManiaCore().getMessageHandler().sendMessage("maniacore:mania", "EventStatus", null, eventInfo.getId() + "", eventInfo.isActive() + "");
                sender.sendMessage(new ComponentBuilder("You have set status of the event to " + eventInfo.isActive()).color(ChatColor.GREEN).create());
            } else if (Utils.checkCmdAliases(args, 2, "setstarttime")) {
                long startDate;
                try {
                    startDate = Utils.parseCalendarDate(StringUtils.join(args, " ", 2, args.length)).getTimeInMillis();
                } catch (Exception e) {
                    sender.sendMessage(new ComponentBuilder("You provided an invalid date value.").color(ChatColor.RED).create());
                    return;
                }
                
                eventInfo.setStartTime(startDate);
                sender.sendMessage(new ComponentBuilder("You have set the start date of the event to " + Constants.DATE_FORMAT.format(new Date(startDate))).color(ChatColor.GREEN).create());
            } else if (Utils.checkCmdAliases(args, 2, "addplayer", "addserver")) {
                List<String> outputMessages = new ArrayList<>();
                String successOutputFormat = "Added {value} as a {type}";
                String alreadyExistsFormat = "Could not add {value} as a {type} because it already exists.";
                String couldNotFindFormat = "Could not find {value}";
                for (int i = 3; i < args.length; i++) {
                    String value = args[i];
                    if (args[2].equalsIgnoreCase("addplayer")) {
                        User user = ManiaCore.getInstance().getUserManager().getUser(value);
                        if (user == null) {
                            outputMessages.add(couldNotFindFormat.replace("{value}", value));
                            continue;
                        }
                        
                        if (eventInfo.getPlayers().contains(user.getId())) {
                            outputMessages.add(alreadyExistsFormat.replace("{value}", value).replace("{type}", "player"));
                            continue;
                        }
                        
                        eventInfo.getPlayers().add(user.getId());
                        outputMessages.add(successOutputFormat.replace("{value}", value).replace("{type}", "player"));
                    } else if (args[2].equalsIgnoreCase("addserver")) {
                        //TODO
//                        ManiaServer server = ManiaCore.getInstance().getServerManager().getServer(value);
//                        if (server == null) {
//                            outputMessages.add(couldNotFindFormat.replace("{value}", value));
//                            continue;
//                        }
//    
//                        if (eventInfo.getServers().contains(server.getId())) {
//                            outputMessages.add(alreadyExistsFormat.replace("{value}", value).replace("{type}", "server"));
//                            continue;
//                        }
//    
//                        eventInfo.getServers().add(server.getId());
//                        outputMessages.add(successOutputFormat.replace("{value}", value).replace("{type}", "server"));
                    }
                }
    
                for (String outputMessage : outputMessages) {
                    sender.sendMessage(new ComponentBuilder(outputMessage).color(ChatColor.GREEN).create());
                }
            } else if (Utils.checkCmdAliases(args, 2, "setsettings")) {
                int settingsId;
                try {
                    settingsId = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(new ComponentBuilder("You provided an invalid number").color(ChatColor.RED).create());
                    return;
                }
                
                eventInfo.setSettingsId(settingsId);
                BungeeUtils.sendMessage(sender, "Set the game settings id for the event to " + settingsId, ChatColor.GREEN);
            }
            ManiaCore.getInstance().getDatabase().pushRecord(new EventInfoRecord(eventInfo));
        } else if (Utils.checkCmdAliases(args, 0, "delete")) {
            if (!(args.length > 1)) {
                sender.sendMessage(notEnoughArguments);
                return;
            }
    
            int id;
            try {
                id = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(new ComponentBuilder("You provided an invalid number").color(ChatColor.RED).create());
                return;
            }
    
            EventInfo eventInfo = ManiaCore.getInstance().getEventManager().getEvents().get(id);
            if (eventInfo == null) {
                sender.sendMessage(new ComponentBuilder("The event id you provided is invalid").color(ChatColor.RED).create());
                return;
            }
            
            ManiaCore.getInstance().getEventManager().getEvents().remove(id);
            sender.sendMessage(new ComponentBuilder("Deleted event " + id).color(ChatColor.GREEN).create());
        }
    }
}