package net.hungermania.hub;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.hungermania.maniacore.api.server.ManiaServer;
import net.hungermania.maniacore.api.util.Utils;
import net.hungermania.maniacore.spigot.gui.GUIButton;
import net.hungermania.maniacore.spigot.gui.Gui;
import net.hungermania.maniacore.spigot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.Map.Entry;

public class HungerGamesGui extends Gui {
    public HungerGamesGui(Hub plugin) {
        super(plugin, "&3&lHUNGER GAMES &8- &rServer Select", false, 54);
        Map<Integer, ItemStack> serverStacks = new TreeMap<>();
        int onlinePlayers = 0, maximumPlayers = 0;
        
        for (ServerObject server : TimoCloudAPI.getUniversalAPI().getServerGroup("HG").getServers()) {
            Material itemMaterial = null;
            List<String> lore = new LinkedList<>();
            if (server.getState().equalsIgnoreCase("online") || server.getState().equalsIgnoreCase("ingame") || server.getState().equalsIgnoreCase("lobby")) {
                if (server.getState().equalsIgnoreCase("online") || server.getState().equalsIgnoreCase("lobby")) {
                    itemMaterial = Material.EMERALD_BLOCK;
                } else if (server.getState().equalsIgnoreCase("ingame")) {
                    itemMaterial = Material.GOLD_BLOCK;
                }
                
                lore.add("");
                lore.add(Utils.color("&d&lStatus &f" + server.getState()));
                String time = "", map = "";
                String extra = server.getExtra();
                if (extra != null && !extra.isEmpty()) {
                    String[] extraSplit = extra.split(";");
                    if (extraSplit.length > 0) {
                        for (String es : extraSplit) {
                            String[] optionSplit = es.split(":");
                            if (optionSplit.length == 2) {
                                if (optionSplit[0].equalsIgnoreCase("map")) {
                                    map = optionSplit[1];
                                } else if (optionSplit[0].equalsIgnoreCase("time")) {
                                    time = optionSplit[1];
                                }
                            }
                        }
                    }
                }
                lore.add(Utils.color("&d&lMap &f" + map));
                lore.add(Utils.color("&d&lTime &f" + time));
                lore.add("");
                lore.add(Utils.color("&3&l" + server.getOnlinePlayerCount() + "/" + server.getMaxPlayerCount()));
            } else if (server.getState().equalsIgnoreCase("starting")) {
                itemMaterial = Material.DIAMOND_BLOCK;
                lore.addAll(Arrays.asList("", Utils.color("&c&lSERVER IS STARTING")));
            } else if (server.getState().equalsIgnoreCase("restarting")) {
                itemMaterial = Material.REDSTONE_BLOCK;
                lore.addAll(Arrays.asList("", Utils.color("&c&lSERVER IS RESTARTING")));
            } else {
                itemMaterial = Material.BEDROCK;
                lore.addAll(Arrays.asList("", Utils.color("&c&lSERVER IS OFFLINE")));
            }
            
            ItemStack itemStack = new ItemStack(itemMaterial);
            
            onlinePlayers += server.getOnlinePlayerCount();
            maximumPlayers += server.getMaxPlayerCount();
            
            ItemMeta itemMeta = itemStack.getItemMeta();
            int number = Integer.parseInt(server.getName().split("-")[1]);
            itemMeta.setDisplayName(Utils.color("&a&lSERVER " + number));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            serverStacks.put(number, itemStack);
        }
        
        
        ItemStack back = ItemBuilder.start(Material.COMPASS).setDisplayName("&c&lBACK TO GAME SELECTOR").build();
        ItemStack totalServers = ItemBuilder.start(Material.ENCHANTED_BOOK).setDisplayName("&e&lTOTAL SERVERS").withLore("&7Servers&8: &6" + serverStacks.size(), " &7In Lobby&8: &a&c&oNot Implemented", " &7Running&8: &e&c&oNot Implemented", " &7Restarting&8: &c&c&oNot Implemented").build();
        ItemStack hungerGames = ItemBuilder.start(Material.DIAMOND_SWORD).setDisplayName("&e&lHUNGER GAMES").withLore("&7Compete in a free-for-all deathmatch", "&7where the last player standing is", "&7declared the victor. Twists and turns", "&7at every corner!").build();
        ItemStack totalPlayers = ItemBuilder.start(Material.SKULL_ITEM, 1, (byte) 3).setDisplayName("&e&lTOTAL PLAYERS").withLore("&6" + onlinePlayers + "&8/&6" + maximumPlayers).build();
        ItemStack allServers = ItemBuilder.start(Material.GOLD_BLOCK).setDisplayName("&e&lALL SERVERS").withLore("&7View all servers, including ones already playing", "&6&lClick &fto view all servers.", "&c&oNOT IMPLEMENTED").build();
        ItemStack autoJoin = ItemBuilder.start(Material.ENDER_PEARL).setDisplayName("&3&lAUTO-JOIN").withLore("&6&lClick &fto join the best lobby!", "&c&oNOT IMPLEMENTED").build();
        
        setButton(0, new GUIButton(back).setListener(e -> new GameBrowserGui(plugin).openGUI(e.getWhoClicked())));
        setButton(3, new GUIButton(totalServers));
        setButton(4, new GUIButton(hungerGames));
        setButton(5, new GUIButton(totalPlayers));
        setButton(8, new GUIButton(allServers));
        setButton(13, new GUIButton(autoJoin));
        
        if (plugin.getConfig().getBoolean("testinfo.active")) {
            ItemStack hgTestStack = ItemBuilder.start(Material.ANVIL).setDisplayName("&a&lPublic Test").build();
            GUIButton hgTestButton = new GUIButton(hgTestStack);
            hgTestButton.setListener(e -> {
                Player player = (Player) e.getWhoClicked();
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF(plugin.getConfig().getString("testinfo.server"));
                player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
            });
            setButton(22, hgTestButton);
        }
        
        for (Entry<Integer, ItemStack> entry : serverStacks.entrySet()) {
            ManiaServer maniaServer = null;
            for (ServerObject hg : TimoCloudAPI.getUniversalAPI().getServerGroup("HG").getServers()) {
                if (hg.getName().split("-")[1].equals(entry.getKey() + "")) {
                    maniaServer = new ManiaServer(hg.getName(), hg.getPort());
                }
            }
            ManiaServer finalManiaServer = maniaServer;
            setButton(26 + entry.getKey(), new GUIButton(entry.getValue()).setListener(event -> {
                if (finalManiaServer != null) {
                    Player player = (Player) event.getWhoClicked();
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Connect");
                    out.writeUTF(finalManiaServer.getName());
                    player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                }
            }));
        }
    }
}
