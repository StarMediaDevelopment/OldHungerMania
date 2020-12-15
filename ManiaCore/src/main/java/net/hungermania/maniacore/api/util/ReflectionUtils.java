package net.hungermania.maniacore.api.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ReflectionUtils {
    
    private ReflectionUtils() {
    }
    
    private static Map<String, Class<?>> cache = new HashMap<>();
    
    private static final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    
    public static Class<?> getNMSClass(String nmsClassString) {
        try {
            String name = "net.minecraft.server." + version + "." + nmsClassString;
    
            if (cache.containsKey(name)) {
                return cache.get(name);
            }
    
            Class<?> nmsClass = Class.forName(name);
            cache.put(name, nmsClass);
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Class<?> getCraftClass(String craftClassString) {
        try {
            String name = "org.bukkit.craftbukkit." + version + "." + craftClassString;
            
            if (cache.containsKey(name)) {
                return cache.get(name);
            }
            
            Class<?> craftClass = Class.forName(name);
            cache.put(name, craftClass);
            return craftClass;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static String getVersion() {
        return version;
    }
    
    public static void setField(Object target, String field, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
        }
    }
    
    public static <T> Field getField(Class<?> target, String name, Class<T> fieldType, int index) {
        for (final Field field : target.getDeclaredFields()) {
            if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
                field.setAccessible(true);
                return field;
            }
        }
        
        // Search in parent classes
        if (target.getSuperclass() != null) return getField(target.getSuperclass(), name, fieldType, index);
        throw new IllegalArgumentException("Cannot find field with type " + fieldType);
    }
    
    public static void sendPacket(Object packet, Player player) {
        try {
            Class<?> craftPlayerClass = getCraftClass("entity.CraftPlayer");
            Class<?> entityPlayerClass = getNMSClass("EntityPlayer");
            Object craftPlayer = Objects.requireNonNull(craftPlayerClass).cast(player);
            Object handle = craftPlayerClass.getMethod("getHandle").invoke(craftPlayer);
            Object playerConnection = entityPlayerClass.getField("playerConnection").get(handle);
            Method sendPacket = playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet"));
            sendPacket.invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}