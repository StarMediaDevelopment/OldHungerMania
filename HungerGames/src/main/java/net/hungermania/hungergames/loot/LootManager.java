package net.hungermania.hungergames.loot;

import lombok.Getter;
import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.records.LootRecord;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.manialib.sql.IRecord;
import org.bukkit.Material;

import java.util.*;

@SuppressWarnings("DuplicatedCode")
public class LootManager {
    @Getter private Map<Integer, Loot> possibleLoot = new HashMap<>();
    @Getter private List<Loot> lootChances = new ArrayList<>();

    private HungerGames plugin = HungerGames.getInstance();
    
    public void loadFromDatabase() {
        List<IRecord> records = plugin.getManiaCore().getDatabase().getRecords(LootRecord.class, null, null);
        if (!records.isEmpty()) {
            records:
            for (IRecord record : records) {
                if (record instanceof LootRecord) {
                    LootRecord lootRecord = (LootRecord) record;
                    Loot loot = lootRecord.toObject();
                    for (Loot value : this.possibleLoot.values()) {
                        if (value.getMaterial() == loot.getMaterial()) {
                            ManiaCore.getInstance().getDatabase().deleteRecord(lootRecord);
                            continue records;
                        }
                    }
                    this.possibleLoot.put(lootRecord.getId(), lootRecord.toObject());
                }
            }
        }
    
        for (Loot value : this.possibleLoot.values()) {
            for (int i = 0; i < value.getWeight(); i++) {
                lootChances.add(value);
            }
        }
    
        Collections.shuffle(lootChances);
    }
    
    public void generateDefaultLoot() {
        if (this.possibleLoot.isEmpty()) {
            Loot leather_helmet = new Loot(Material.LEATHER_HELMET, 22);
            Loot leather_chestplate = new Loot(Material.LEATHER_CHESTPLATE, 22);
            Loot leather_leggings = new Loot(Material.LEATHER_LEGGINGS, 22);
            Loot leather_boots = new Loot(Material.LEATHER_BOOTS, 22);
            Loot chain_helmet = new Loot(Material.CHAINMAIL_HELMET, 10);
            Loot chain_chestplate = new Loot(Material.CHAINMAIL_CHESTPLATE, 10);
            Loot chain_leggings = new Loot(Material.CHAINMAIL_LEGGINGS, 10);
            Loot chain_boots = new Loot(Material.CHAINMAIL_BOOTS, 10);
            Loot iron_helmet = new Loot(Material.IRON_HELMET, 7);
            Loot iron_chestplate = new Loot(Material.IRON_CHESTPLATE, 7);
            Loot iron_leggings = new Loot(Material.IRON_LEGGINGS, 7);
            Loot iron_boots = new Loot(Material.IRON_BOOTS, 7);
            Loot gold_helmet = new Loot(Material.GOLD_HELMET, 20);
            Loot gold_chestplate = new Loot(Material.GOLD_CHESTPLATE, 20);
            Loot gold_leggings = new Loot(Material.GOLD_LEGGINGS, 20);
            Loot gold_boots = new Loot(Material.GOLD_BOOTS, 20);
            Loot wood_axe = new Loot(Material.WOOD_AXE, 20);
            Loot wood_sword = new Loot(Material.WOOD_SWORD, 25);
            Loot stone_axe = new Loot(Material.STONE_AXE, 20);
            Loot stone_sword = new Loot(Material.STONE_SWORD, 25);
            Loot bow = new Loot(Material.BOW, 15);
            Loot arrows = new Loot(Material.ARROW, 20, 5);
            Loot cooked_porkchop = new Loot(Material.GRILLED_PORK, "Porkchop", 20);
            Loot steak = new Loot(Material.COOKED_BEEF, "Steak", 30);
            Loot grilled_chicken = new Loot(Material.COOKED_CHICKEN, "Grilled Chicken", 30);
            Loot raw_porkchop = new Loot(Material.PORK, "Raw Porkchop", 20);
            Loot raw_beef = new Loot(Material.RAW_BEEF, 15);
            Loot raw_chicken = new Loot(Material.RAW_CHICKEN, 15);
            Loot carrots = new Loot(Material.CARROT_ITEM, "Carrot", 20);
            Loot potato = new Loot(Material.POTATO_ITEM, "Potato", 15);
            Loot baked_potato = new Loot(Material.BAKED_POTATO, 20);
            Loot egg_of_doom = new Loot(Material.EGG, "Egg of Doom", 15);
            Loot slowball = new Loot(Material.SNOW_BALL, "Slowball", 20);
            Loot iron_ingot = new Loot(Material.IRON_INGOT, 10);
            Loot gold_ingot = new Loot(Material.GOLD_INGOT, 5);
            Loot diamond = new DiamondLoot();
            Loot stick = new Loot(Material.STICK, 15);
            Loot flint_and_steel = new Loot(Material.FLINT_AND_STEEL, 15);
            Loot tnt = new Loot(Material.TNT, "TNT", 15);
            Loot iron_axe = new Loot(Material.IRON_AXE, 20);
            Loot cake = new Loot(Material.CAKE, 20);
            Loot player_tracker = new Loot(Material.COMPASS, "Player Tracker", 15);
            Loot pumpkin_pie = new Loot(Material.PUMPKIN_PIE, 10);
            Loot raw_fish = new Loot(Material.RAW_FISH, 20);
            Loot cooked_fish = new Loot(Material.COOKED_FISH, 20);
            Loot feather = new Loot(Material.FEATHER, 20);
            Loot flint = new Loot(Material.FLINT, 20);
            Loot fishing_rod = new Loot(Material.FISHING_ROD, 20);
            Loot cobweb = new Loot(Material.WEB, "Cobweb", 15);
            Loot enchantment_bottle = new Loot(Material.EXP_BOTTLE, 20, 3);
            Loot golden_apple = new Loot(Material.GOLDEN_APPLE, "Golden Munchie", 2);
            Loot wood_planks = new Loot(Material.WOOD, "Wood Planks", 15);
            Loot ender_pearl = new Loot(Material.ENDER_PEARL, 1);
            Loot wet_noodle = new Loot(Material.ROTTEN_FLESH, "Wet Noodle", 10);
            Loot golden_carrot = new Loot(Material.GOLDEN_CARROT, 15);
    
            plugin.getManiaCore().getDatabase().addRecordsToQueue(new LootRecord(leather_helmet), new LootRecord(leather_chestplate), new LootRecord(leather_leggings), new LootRecord(leather_boots), new LootRecord(chain_helmet), new LootRecord(chain_chestplate), new LootRecord(chain_leggings), new LootRecord(chain_boots), new LootRecord(gold_helmet), new LootRecord(gold_chestplate), new LootRecord(gold_leggings), new LootRecord(gold_boots), new LootRecord(iron_helmet), new LootRecord(iron_chestplate), new LootRecord(iron_leggings), new LootRecord(iron_boots),
                    new LootRecord(wood_axe), new LootRecord(wood_sword), new LootRecord(stone_axe), new LootRecord(stone_sword), new LootRecord(bow), new LootRecord(arrows), new LootRecord(cooked_porkchop), new LootRecord(cooked_porkchop), new LootRecord(steak), new LootRecord(grilled_chicken), new LootRecord(raw_porkchop), new LootRecord(raw_beef), new LootRecord(raw_chicken), new LootRecord(carrots), new LootRecord(potato), new LootRecord(baked_potato), new LootRecord(egg_of_doom), new LootRecord(slowball), new LootRecord(iron_ingot), new LootRecord(gold_ingot),
                    new LootRecord(diamond), new LootRecord(stick), new LootRecord(flint_and_steel), new LootRecord(tnt), new LootRecord(iron_axe), new LootRecord(cake), new LootRecord(player_tracker), new LootRecord(pumpkin_pie), new LootRecord(raw_fish), new LootRecord(cooked_fish), new LootRecord(feather), new LootRecord(flint), new LootRecord(fishing_rod), new LootRecord(cobweb), new LootRecord(enchantment_bottle), new LootRecord(golden_apple), new LootRecord(wood_planks), new LootRecord(ender_pearl), new LootRecord(wet_noodle), new LootRecord(golden_carrot));
            plugin.getManiaCore().getDatabase().pushQueue();
            this.possibleLoot.put(leather_helmet.getId(), leather_helmet);
            this.possibleLoot.put(leather_chestplate.getId(), leather_chestplate);
            this.possibleLoot.put(leather_leggings.getId(), leather_leggings);
            this.possibleLoot.put(leather_boots.getId(), leather_boots);
            this.possibleLoot.put(chain_helmet.getId(), chain_helmet);
            this.possibleLoot.put(chain_chestplate.getId(), chain_chestplate);
            this.possibleLoot.put(chain_leggings.getId(), chain_leggings);
            this.possibleLoot.put(chain_boots.getId(), chain_boots);
            this.possibleLoot.put(iron_helmet.getId(), iron_helmet);
            this.possibleLoot.put(iron_chestplate.getId(), iron_chestplate);
            this.possibleLoot.put(iron_leggings.getId(), iron_leggings);
            this.possibleLoot.put(iron_boots.getId(), iron_boots);
            this.possibleLoot.put(gold_helmet.getId(), gold_helmet);
            this.possibleLoot.put(gold_chestplate.getId(), gold_chestplate);
            this.possibleLoot.put(gold_leggings.getId(), gold_leggings);
            this.possibleLoot.put(gold_boots.getId(), gold_boots);
            this.possibleLoot.put(wood_axe.getId(), wood_axe);
            this.possibleLoot.put(wood_sword.getId(), wood_sword);
            this.possibleLoot.put(stone_axe.getId(), stone_axe);
            this.possibleLoot.put(stone_sword.getId(), stone_sword);
            this.possibleLoot.put(bow.getId(), bow);
            this.possibleLoot.put(arrows.getId(), arrows);
            this.possibleLoot.put(cooked_porkchop.getId(), cooked_porkchop);
            this.possibleLoot.put(steak.getId(), steak);
            this.possibleLoot.put(grilled_chicken.getId(), grilled_chicken);
            this.possibleLoot.put(raw_porkchop.getId(), raw_porkchop);
            this.possibleLoot.put(raw_beef.getId(), raw_beef);
            this.possibleLoot.put(raw_chicken.getId(), raw_chicken);
            this.possibleLoot.put(carrots.getId(), carrots);
            this.possibleLoot.put(potato.getId(), potato);
            this.possibleLoot.put(baked_potato.getId(), baked_potato);
            this.possibleLoot.put(egg_of_doom.getId(), egg_of_doom);
            this.possibleLoot.put(slowball.getId(), slowball);
            this.possibleLoot.put(iron_ingot.getId(), iron_ingot);
            this.possibleLoot.put(gold_ingot.getId(), gold_ingot);
            this.possibleLoot.put(diamond.getId(), diamond);
            this.possibleLoot.put(flint_and_steel.getId(), flint_and_steel);
            this.possibleLoot.put(stick.getId(), stick);
            this.possibleLoot.put(tnt.getId(), tnt);
            this.possibleLoot.put(iron_axe.getId(), iron_axe);
            this.possibleLoot.put(cake.getId(), cake);
            this.possibleLoot.put(player_tracker.getId(), player_tracker);
            this.possibleLoot.put(pumpkin_pie.getId(), pumpkin_pie);
            this.possibleLoot.put(raw_fish.getId(), raw_fish);
            this.possibleLoot.put(cooked_fish.getId(), cooked_fish);
            this.possibleLoot.put(feather.getId(), feather);
            this.possibleLoot.put(flint.getId(), flint);
            this.possibleLoot.put(fishing_rod.getId(), fishing_rod);
            this.possibleLoot.put(cobweb.getId(), cobweb);
            this.possibleLoot.put(enchantment_bottle.getId(), enchantment_bottle);
            this.possibleLoot.put(golden_apple.getId(), golden_apple);
            this.possibleLoot.put(wood_planks.getId(), wood_planks);
            this.possibleLoot.put(ender_pearl.getId(), ender_pearl);
            this.possibleLoot.put(wet_noodle.getId(), wet_noodle);
            this.possibleLoot.put(golden_carrot.getId(), golden_carrot);
        }
    }
    
    public List<Loot> generateLoot(int amount) {
        List<Loot> loot = new ArrayList<>();
        
        for (int i = 0; i < amount; i++) {
            loot.add(lootChances.get(ManiaCore.RANDOM.nextInt(lootChances.size())));
        }
        
        return loot;
    }
    
    public Loot getPlayerTracker() {
        for (Loot value : this.possibleLoot.values()) {
            if (value.getName().toLowerCase().contains("player tracker")) {
                return value;
            }
        }
        
        return null;
    }
}