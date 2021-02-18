package net.hungermania.hungergames.game.sponsoring;

import lombok.Getter;
import net.hungermania.maniacore.api.util.ManiaUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class SponsorManager {
    @Getter private Map<SponsorType, List<SponsorItem>> sponsorItems = new HashMap<>();
    @Getter private Set<UUID> sponsoredActors = new HashSet<>();
    
    
    public SponsorManager() {
        List<SponsorItem> foodItems = new LinkedList<>(), weaponItems = new LinkedList<>(), armorItems = new LinkedList<>(), potionEffects = new LinkedList<>();
        foodItems.add(new SponsorItem(Material.APPLE, 1, SponsorType.FOOD));
        foodItems.add(new SponsorItem(Material.RAW_FISH, 3, SponsorType.FOOD));
        foodItems.add(new SponsorItem("Carrots", Material.CARROT_ITEM, 2, SponsorType.FOOD));
        foodItems.add(new SponsorItem(Material.MELON, 3, SponsorType.FOOD));
        foodItems.add(new SponsorItem("Potatoes", Material.POTATO_ITEM, 2, SponsorType.FOOD));
        foodItems.add(new SponsorItem(Material.PORK, 2, SponsorType.FOOD));
        foodItems.add(new SponsorItem(Material.RAW_BEEF, 2, SponsorType.FOOD));
        foodItems.add(new SponsorItem(Material.GRILLED_PORK, 1, SponsorType.FOOD));
        foodItems.add(new SponsorItem(Material.COOKED_BEEF, 1, SponsorType.FOOD));
        foodItems.add(new SponsorItem(Material.COOKED_FISH, 1, SponsorType.FOOD));
        
        weaponItems.add(new SponsorItem(Material.WOOD_AXE, 1, SponsorType.WEAPON));
        weaponItems.add(new SponsorItem(Material.WOOD_SWORD, 1, SponsorType.WEAPON));
        weaponItems.add(new SponsorItem(Material.STONE_AXE, 1, SponsorType.WEAPON));
        weaponItems.add(new SponsorItem(Material.STONE_SWORD, 1, SponsorType.WEAPON));
        weaponItems.add(new SponsorItem(Material.IRON_AXE, 1, SponsorType.WEAPON));
        weaponItems.add(new SponsorItem(Material.IRON_SWORD, 1, SponsorType.WEAPON));
        weaponItems.add(new SponsorItem(Material.FISHING_ROD, 1, SponsorType.WEAPON));

        armorItems.add(new SponsorItem(Material.GOLD_HELMET, 1, SponsorType.ARMOR));
        armorItems.add(new SponsorItem(Material.GOLD_CHESTPLATE, 1, SponsorType.ARMOR));
        armorItems.add(new SponsorItem(Material.GOLD_LEGGINGS, 1, SponsorType.ARMOR));
        armorItems.add(new SponsorItem(Material.GOLD_BOOTS, 1, SponsorType.ARMOR));
        armorItems.add(new SponsorItem(Material.CHAINMAIL_HELMET, 1, SponsorType.ARMOR));
        armorItems.add(new SponsorItem(Material.CHAINMAIL_CHESTPLATE, 1, SponsorType.ARMOR));
        armorItems.add(new SponsorItem(Material.CHAINMAIL_LEGGINGS, 1, SponsorType.ARMOR));
        armorItems.add(new SponsorItem(Material.CHAINMAIL_BOOTS, 1, SponsorType.ARMOR));
        armorItems.add(new SponsorItem(Material.IRON_HELMET, 1, SponsorType.ARMOR));
        armorItems.add(new SponsorItem(Material.IRON_CHESTPLATE, 1, SponsorType.ARMOR));
        armorItems.add(new SponsorItem(Material.IRON_LEGGINGS, 1, SponsorType.ARMOR));
        armorItems.add(new SponsorItem(Material.IRON_BOOTS, 1, SponsorType.ARMOR));
        
        potionEffects.add(new PotionSponsorItem(PotionEffectType.POISON, 2, 30));
        potionEffects.add(new PotionSponsorItem(PotionEffectType.POISON, 3, 20));
        potionEffects.add(new PotionSponsorItem(PotionEffectType.HEALTH_BOOST, 1, 30));
        potionEffects.add(new PotionSponsorItem(PotionEffectType.HEALTH_BOOST, 2, 20));
        potionEffects.add(new PotionSponsorItem("Strength", PotionEffectType.INCREASE_DAMAGE, 1, 30));
        potionEffects.add(new PotionSponsorItem("Strength", PotionEffectType.INCREASE_DAMAGE, 2, 20));
        potionEffects.add(new PotionSponsorItem(PotionEffectType.DAMAGE_RESISTANCE, 1, 30));
        potionEffects.add(new PotionSponsorItem(PotionEffectType.DAMAGE_RESISTANCE, 2, 20));
        potionEffects.add(new PotionSponsorItem(PotionEffectType.FIRE_RESISTANCE, 1, 60));
        potionEffects.add(new PotionSponsorItem(PotionEffectType.SPEED, 1, 40));
        potionEffects.add(new PotionSponsorItem(PotionEffectType.SPEED, 2, 30));
        potionEffects.add(new PotionSponsorItem(PotionEffectType.SPEED, 3, 20));
        potionEffects.add(new PotionSponsorItem("Slowness", PotionEffectType.SLOW, 1, 40));
        potionEffects.add(new PotionSponsorItem("Slowness", PotionEffectType.SLOW, 2, 30));
        potionEffects.add(new PotionSponsorItem("Slowness", PotionEffectType.SLOW, 3, 20));
        potionEffects.add(new PotionSponsorItem(PotionEffectType.BLINDNESS, 1, 30));
        potionEffects.add(new PotionSponsorItem(PotionEffectType.BLINDNESS, 2, 20));
        potionEffects.add(new PotionSponsorItem(PotionEffectType.NIGHT_VISION, 1, 60));
        potionEffects.add(new PotionSponsorItem(PotionEffectType.ABSORPTION, 1, 60));
        potionEffects.add(new PotionSponsorItem(PotionEffectType.ABSORPTION, 2, 40));
        
        this.sponsorItems.put(SponsorType.FOOD, foodItems);
        this.sponsorItems.put(SponsorType.WEAPON, weaponItems);
        this.sponsorItems.put(SponsorType.ARMOR, armorItems);
        this.sponsorItems.put(SponsorType.POTION, potionEffects);
    }
    
    public ItemStack getTypeIcon(SponsorType type) {
        List<SponsorItem> items = this.sponsorItems.get(type);
        ItemStack itemStack = new ItemStack(type.getDisplay());
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ManiaUtils.color("&e&l" + type.getName()));
        List<String> lore = new LinkedList<>();
        lore.add(ManiaUtils.color("&7" + type.getDescription()));
        lore.addAll(Arrays.asList("", ManiaUtils.color("&e&lPossible Items (" + items.size() + ")")));
        for (SponsorItem item : items) {
            lore.add(ManiaUtils.color("&8> &f" + item.getLoreLine()));
        }
        lore.addAll(Arrays.asList("", ManiaUtils.color("&6&lLeft Click &fto sponsor with Score!"), ManiaUtils.color("&6&lRight Click &fto sponsor with Coins!")));
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
