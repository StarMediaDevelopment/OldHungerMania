package net.hungermania.maniacore.spigot.util;

import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.experimental.UtilityClass;
import net.hungermania.maniacore.api.skin.Skin;
import net.hungermania.maniacore.api.util.Position;
import net.hungermania.maniacore.api.util.Utils;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class SpigotUtils {

    public static GameProfile skinToProfile(Skin skin) {
        GameProfile gameProfile = new GameProfile(skin.getUuid(), skin.getName());
        gameProfile.getProperties().clear();
        gameProfile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
        return gameProfile;
    }
    
    public Position locationToPosition(Location location) {
        return new Position(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
    
    public static void sendActionBar(Player player, String text) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(Utils.color(text)), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static Location positionToLocation(World world, Position position) {
        return new Location(world, position.getX(), position.getY(), position.getZ(), position.getYaw(), position.getPitch());
    }
    
    public static List<String> getCompletions(String[] arguments, List<String> input) {
        return getCompletions(arguments, input, 80);
    }

    public static List<String> getCompletions(String[] arguments, List<String> input, int limit) {
        Preconditions.checkNotNull(arguments);
        Preconditions.checkArgument(arguments.length != 0);
        String argument = arguments[arguments.length - 1];
        return input.stream().filter(string -> string.regionMatches(true, 0, argument, 0, argument.length())).limit(limit).collect(Collectors.toList());
    }
    public static String niceTime(int seconds) {
        int hours = seconds / 3600;
        seconds -= hours * 3600;
        int minutes = seconds / 60;
        seconds -= minutes * 60;
        return niceTime(hours, minutes, seconds);
    }

    public static String niceTime(int seconds, boolean showEmptyHours) {
        int hours = seconds / 3600;
        seconds -= hours * 3600;
        int minutes = seconds / 60;
        seconds -= minutes * 60;
        return niceTime(hours, minutes, seconds, showEmptyHours);
    }

    public static String niceTime(int hours, int minutes, int seconds) {
        return niceTime(hours, minutes, seconds, true);
    }

    public static String niceTime(int hours, int minutes, int seconds, boolean showEmptyHours) {
        StringBuilder builder = new StringBuilder();

        // Skip hours
        if (hours > 0) {
            if (hours < 10) {
                builder.append('0');
            }
            builder.append(hours);
            builder.append(':');
        } else if (showEmptyHours) {
            builder.append("00:");
        }

        if (minutes < 10 && hours != -1) {
            builder.append('0');
        }
        builder.append(minutes);
        builder.append(':');

        if (seconds < 10) {
            builder.append('0');
        }
        builder.append(seconds);

        return builder.toString();
    }
}