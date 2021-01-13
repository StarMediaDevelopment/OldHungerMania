package net.hungermania.manialib.util;

import net.hungermania.manialib.exceptions.InvalidDateFormatException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public final class Utils {
    public static void printCurrentStack() {
        System.out.println(Arrays.toString(new Throwable().getStackTrace()));
    }
    
    private static Map<Class<?>, Set<Field>> cachedFields = new HashMap<>();
    
    public static Set<Field> getClassFields(Class<?> clazz) {
        if (cachedFields.containsKey(clazz)) {
            return cachedFields.get(clazz);
        }
        Set<Field> fields = new HashSet<>(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            getClassFields(clazz.getSuperclass(), fields);
        }
        if (cachedFields.containsKey(clazz)) {
            cachedFields.get(clazz).addAll(fields);
        } else {
            cachedFields.put(clazz, fields);
        }
        return fields;
    }
    
    public static Set<Field> getClassFields(Class<?> clazz, Set<Field> fields) {
        if (cachedFields.containsKey(clazz)) {
            fields.addAll(cachedFields.get(clazz));
            return fields;
        }
        if (fields == null) {
            fields = new HashSet<>();
        }
        
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            getClassFields(clazz.getSuperclass(), fields);
        }
        if (cachedFields.containsKey(clazz)) {
            cachedFields.get(clazz).addAll(fields);
        } else {
            cachedFields.put(clazz, fields);
        }
        return fields;
    }
    
    public static Calendar parseCalendarDate(String rawDate) throws InvalidDateFormatException {
        short[] dateValues = Utils.parseDate(rawDate);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, dateValues[0]);
        calendar.set(Calendar.DAY_OF_MONTH, dateValues[1]);
        calendar.set(Calendar.YEAR, dateValues[2]);
        calendar.set(Calendar.HOUR, dateValues[3]);
        calendar.set(Calendar.MINUTE, dateValues[4]);
        calendar.set(Calendar.SECOND, dateValues[5]);
        return calendar;
    }
    
    public static short[] parseDate(String rawDate) throws InvalidDateFormatException {
        String[] rawDateArray = rawDate.split("/");
        if (!(rawDateArray.length >= 3)) {
            throw new InvalidDateFormatException("Invalid day of year arguments");
        }
        short month = Utils.parseTimeArgument(rawDateArray[0], "month"), day = Utils.parseTimeArgument(rawDateArray[1], "day"), year = Utils.parseTimeArgument(rawDateArray[2], "year");
        
        if (month == -2 || day == -2 || year == -2) {
            return null;
        }
        
        short hour, minute, second;
        
        if (rawDateArray.length == 4) {
            String[] rawTimeArray = rawDateArray[3].split(":");
            
            hour = Utils.parseTimeArgument(rawTimeArray[0], "hour");
            minute = Utils.parseTimeArgument(rawTimeArray[1], "minute");
            second = Utils.parseTimeArgument(rawTimeArray[2], "second");
            
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
    
    public static String formatDate(long time) {
        if (time == 0) {
            return "0";
        }
        return Constants.DATE_FORMAT.format(new Date(time));
    }
    
    public static String formatNumber(Number number) {
        return Constants.NUMBER_FORMAT.format(number);
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