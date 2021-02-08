package net.hungermania.maniacore.api.skin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.records.SkinRecord;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.manialib.data.model.IRecord;
import org.bukkit.Bukkit;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

@Getter @Setter
public class Skin implements Serializable, IRecord {
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
        updateValues();
    }
    
    public void updateValues() {
        System.out.println("Updating values for skin " + uuid.toString());
        String profileURL = skinUrlString.replace("{uuid}", uuid.toString().replace("-", ""));

        try {
            JsonObject json = ManiaUtils.getJsonObject(profileURL);
            JsonArray properties = (JsonArray) json.get("properties");

            JsonObject property = (JsonObject) properties.get(0);
            String newName = property.get("name").getAsString();
            boolean updated = false;
            if (this.name != null && !this.name.equals(newName)) {
                System.out.println("Name changed");
                this.name = newName;
                updated = true;
            }
            String newValue = property.get("value").getAsString();
            if (this.value != null && !this.value.equals(newValue)) {
                System.out.println("Value changed");
                this.value = newValue;
                updated = true;
            }
            String newSignature = property.get("signature").getAsString();
            if (this.signature != null && !this.signature.equals(newSignature)) {
                System.out.println("Signature changed");
                this.signature = newSignature;
                updated = true;
            }
            
            if (updated) {
                if (this.name != null && this.value != null && this.signature != null) {
                    new SkinRecord(this).push(ManiaCore.getInstance().getDatabase());
                }
            }
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