package net.hungermania.maniacore.api.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import net.hungermania.maniacore.api.exceptions.InvalidDateFormatException;
import net.hungermania.maniacore.memory.MemoryHook;
import net.hungermania.manialib.util.Constants;
import net.md_5.bungee.api.ChatColor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@UtilityClass
public class Utils {
    public static String color(String uncolored) {
        return ChatColor.translateAlternateColorCodes('&', uncolored);
    }
    
    public static Calendar parseCalendarDate(String rawDate) throws InvalidDateFormatException {
        short[] dateValues = parseDate(rawDate);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, dateValues[0]);
        calendar.set(Calendar.DAY_OF_MONTH, dateValues[1]);
        calendar.set(Calendar.YEAR, dateValues[2]);
        calendar.set(Calendar.HOUR, dateValues[3]);
        calendar.set(Calendar.MINUTE, dateValues[4]);
        calendar.set(Calendar.SECOND, dateValues[5]);
        return calendar;
    }
    
    public static UUID getUUIDFromName(String username) {
        String s = "https://api.mojang.com/users/profiles/minecraft/" + username + "?at=" + (System.currentTimeMillis() / 1000);
        
        JsonObject json = getJsonObject(s);
        if (json == null) {
            return null;
        }
        JsonElement idObject = json.get("id");
        if (idObject == null) {
            return null;
        }
        
        return formatUUID(idObject.getAsString());
    }
    
    private static UUID formatUUID(String uuidString) {
        String finalUUIDString = uuidString.substring(0, 8) + "-";
        finalUUIDString += uuidString.substring(8, 12) + "-";
        finalUUIDString += uuidString.substring(12, 16) + "-";
        finalUUIDString += uuidString.substring(16, 20) + "-";
        finalUUIDString += uuidString.substring(20, 32);
        return UUID.fromString(finalUUIDString);
    }
    
    public static short[] parseDate(String rawDate) throws InvalidDateFormatException {
        String[] rawDateArray = rawDate.split("/");
        if (!(rawDateArray.length >= 3)) {
            throw new InvalidDateFormatException("Invalid day of year arguments");
        }
        short month = parseTimeArgument(rawDateArray[0], "month"), day = parseTimeArgument(rawDateArray[1], "day"), year = parseTimeArgument(rawDateArray[2], "year");
        
        if (month == -2 || day == -2 || year == -2) {
            return null;
        }
        
        short hour, minute, second;
        
        if (rawDateArray.length == 4) {
            String[] rawTimeArray = rawDateArray[3].split(":");
            
            hour = parseTimeArgument(rawTimeArray[0], "hour");
            minute = parseTimeArgument(rawTimeArray[1], "minute");
            second = parseTimeArgument(rawTimeArray[2], "second");
            
            if (hour == -2 || minute == -2 || second == -2) {
                return null;
            }
        } else {
            hour = -1;
            minute = -1;
            second = -1;
        }
        
        return new short[]{month, day, year, hour, minute, second};
    }
    
    private static short parseTimeArgument(String arg, String type) {
        try {
            return Short.parseShort(arg);
        } catch (NumberFormatException e) {
            return -2;
        }
    }
    
    public static void updateMemoryHook(MemoryHook memoryHook, long start, long end) {
        long totalTime = end - start;
        if (memoryHook.getLowest() > totalTime) {
            memoryHook.setLowest((int) totalTime);
        }
        
        memoryHook.addRecentRun((int) totalTime);
        
        if (memoryHook.getHighest() < totalTime) {
            memoryHook.setHighest((int) totalTime);
        }
    }
    
    public static String getNameFromUUID(UUID uuid) {
        String profileURL = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "");
        JsonObject json = getJsonObject(profileURL);
        if (json == null) { return null; }
        JsonElement element = json.get("name");
        if (element == null) { return null; }
        return element.getAsString();
    }
    
    public static String formatDate(long time) {
        if (time == 0) {
            return "0";
        }
        return Constants.DATE_FORMAT.format(new Date(time));
    }
    
    public static String formatNumber(Number number) {
        return Constants.NUMBER_FORMAT.format(number);
    }
    
    public static JsonObject getJsonObject(String urlString) {
        try {
            StringBuilder buffer = getJsonBuffer(urlString);
            return (JsonObject) new JsonParser().parse(buffer.toString());
        } catch (Exception e) {
        }
        
        return null;
    }
    
    private static StringBuilder getJsonBuffer(String urlString) throws IOException {
        URL url = new URL(urlString);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder buffer = new StringBuilder();
        int read;
        char[] chars = new char[256];
        while ((read = reader.read(chars)) != -1) {
            buffer.append(chars, 0, read);
        }
        
        reader.close();
        return buffer;
    }
    
    public static String capitalizeEveryWord(String string) {
        string = string.toLowerCase();
        String[] words = string.split("_");
        StringBuilder name = new StringBuilder();
        for (int w = 0; w < words.length; w++) {
            String word = words[w];
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < word.length(); i++) {
                if (i == 0) {
                    sb.append(Character.toUpperCase(word.charAt(i)));
                } else {
                    sb.append(word.charAt(i));
                }
            }
            name.append(sb.toString());
            if (w < (words.length - 1)) {
                name.append(" ");
            }
        }
        
        return name.toString();
    }
    
    public static String join(Collection collection, String separator) {
        Iterator iterator = collection.iterator();
        if (iterator == null) {
            return null;
        } else if (!iterator.hasNext()) {
            return "";
        } else {
            Object first = iterator.next();
            if (!iterator.hasNext()) {
                return first.toString();
            } else {
                StringBuilder buf = new StringBuilder();
                if (first != null) {
                    buf.append(first);
                }
                
                while (iterator.hasNext()) {
                    if (separator != null) {
                        buf.append(separator);
                    }
                    
                    Object obj = iterator.next();
                    if (obj != null) {
                        buf.append(obj);
                    }
                }
                
                return buf.toString();
            }
        }
    }
    
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    
    public static void copyFolder(Path src, Path dest) {
        try (Stream<Path> stream = Files.walk(src)) {
            stream.forEach(source -> copy(source, dest.resolve(src.relativize(source))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String formatTime(long time) {
        Duration remainingTime = Duration.ofMillis(time);
        long days = remainingTime.toDays();
        remainingTime = remainingTime.minusDays(days);
        long hours = remainingTime.toHours();
        remainingTime = remainingTime.minusHours(hours);
        long minutes = remainingTime.toMinutes();
        remainingTime = remainingTime.minusMinutes(minutes);
        long seconds = remainingTime.getSeconds();
        
        StringBuilder sb = new StringBuilder();
        if (days > 0) { sb.append(days).append("d"); }
        if (hours > 0) { sb.append(hours).append("h"); }
        if (minutes > 0) { sb.append(minutes).append("m"); }
        if (seconds > 0) { sb.append(seconds).append("s"); }
        
        if (sb.toString().isEmpty()) {
            sb.append("0s");
        }
        //String st = sb.toString();
//        if (st != null || !st.isEmpty()) {
//            sb.append("0s");
//        }
        return sb.toString();
    }
    
    private static void copy(Path source, Path dest) {
        try {
            Files.copy(source, dest, REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public static boolean checkCmdAliases(String[] args, int index, String... aliases) {
        if (aliases == null) { return false; }
        for (String s : aliases) {
            try {
                if (args[index].equalsIgnoreCase(s)) { return true; }
            } catch (Exception e) {
                return false;
            }
        }
        
        return false;
    }
    
    public static void purgeDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) { purgeDirectory(file); }
                file.delete();
            }
        }
        dir.delete();
    }
}