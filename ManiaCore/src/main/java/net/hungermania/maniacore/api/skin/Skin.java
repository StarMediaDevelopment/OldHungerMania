package net.hungermania.maniacore.api.skin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.hungermania.maniacore.api.util.ManiaUtils;
import org.bukkit.Bukkit;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

@Getter @Setter
public class Skin implements Serializable {
    private static final String skinUrlString = "https://sessionserver.mojang.com/session/minecraft/profile/{uuid}?unsigned=false";
    private static final long serialVersionUID = 1L;
    
    private int id;
    private UUID uuid;
    private String name;
    private String signature;
    private String value;

    public Skin(){}

    public Skin(UUID uuid) {
        this.uuid = uuid;
        String profileURL = skinUrlString.replace("{uuid}", uuid.toString().replace("-", ""));

        try {
            JsonObject json = ManiaUtils.getJsonObject(profileURL);
            JsonArray properties = (JsonArray) json.get("properties");
    
            JsonObject property = (JsonObject) properties.get(0);
            name = property.get("name").getAsString();
            value = property.get("value").getAsString();
            signature = property.get("signature").getAsString();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Invalid name/UUID provided, using default skin");
        }
    }

    public Skin(UUID uuid, String name, String signature, String value) {
        this.uuid = uuid;
        this.name = name;
        this.signature = signature;
        this.value = value;
    }
    
    public Skin(int id, UUID uuid, String name, String signature, String value) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.signature = signature;
        this.value = value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skin skin = (Skin) o;
        return Objects.equals(uuid, skin.uuid);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
    
    public String toString() {
        return "name:" + name + " value:" + value + " signature:" + signature;
    }
}