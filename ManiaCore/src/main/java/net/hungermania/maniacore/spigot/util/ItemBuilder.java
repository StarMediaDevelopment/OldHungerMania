package net.hungermania.maniacore.spigot.util;

import com.mojang.authlib.GameProfile;
import lombok.Getter;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.records.SkinRecord;
import net.hungermania.maniacore.api.skin.Skin;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.manialib.sql.IRecord;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.craftbukkit.v1_8_R3.CraftOfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.*;

@Getter
public class ItemBuilder {
    private String displayName;
    private List<String> lore = new LinkedList<>();
    private Map<Enchantment, Integer> enchantments = new HashMap<>(), storedEnchantments = new HashMap<>();
    private Set<ItemFlag> itemFlags = new HashSet<>();
    private boolean unbreakable;
    private DyeColor bannerColor;
    private List<Pattern> bannerPatterns = new ArrayList<>();
    private BlockState blockState;
    private String bookTitle, bookAuthor;
    private List<String> bookPages = new LinkedList<>();
    private Color armorColor;
    private PotionEffectType mainEffect;
    private List<PotionEffect> potionEffects = new ArrayList<>();
    private int repairCost = -1;
    private UUID skullOwner;
    private Skin skullSkin;
    private int amount = 1, durability;
    private Material material;
    private short damage;
    
    public ItemStack build() {
        ItemStack itemStack = new ItemStack(material, amount, damage);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (displayName != null) {
            itemMeta.setDisplayName(ManiaUtils.color(displayName));
        }
        
        if (!lore.isEmpty()) {
            List<String> coloredLore = new LinkedList<>();
            lore.forEach(line -> coloredLore.add(ManiaUtils.color(line)));
            itemMeta.setLore(coloredLore);
        }
        
        if (!enchantments.isEmpty()) {
            enchantments.forEach((enchant, level) -> itemMeta.addEnchant(enchant, level, true));
        }
        
        if (!storedEnchantments.isEmpty()) {
            if (itemMeta instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta enchantStorage = (EnchantmentStorageMeta) itemMeta;
                storedEnchantments.forEach((enchant, level) -> enchantStorage.addStoredEnchant(enchant, level, true));
            }
        }
        
        if (!itemFlags.isEmpty()) {
            itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        }
        
        itemMeta.spigot().setUnbreakable(unbreakable);
    
        if (itemMeta instanceof BannerMeta) {
            BannerMeta bannerMeta = (BannerMeta) itemMeta;
            if (bannerColor != null) bannerMeta.setBaseColor(bannerColor);
            bannerMeta.setPatterns(bannerPatterns);
        }
        
        if (itemMeta instanceof BlockStateMeta) {
            if (blockState != null) {
                BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
                blockStateMeta.setBlockState(blockState);
            }
        }
        
        if (itemMeta instanceof BookMeta) {
            BookMeta bookMeta = (BookMeta) itemMeta;
            if (bookTitle != null) {
                bookMeta.setTitle(ManiaUtils.color(bookTitle));
            }
            if (bookAuthor != null) {
                bookMeta.setTitle(ManiaUtils.color(bookAuthor));
            }
            
            if (!bookPages.isEmpty()) {
                bookMeta.setPages(bookPages);
            }
        }
        
        if (itemMeta instanceof LeatherArmorMeta) {
            if (armorColor != null) {
                LeatherArmorMeta leatherArmorMeta =  (LeatherArmorMeta) itemMeta;
                leatherArmorMeta.setColor(armorColor);
            }
        }
        
        if (itemMeta instanceof PotionMeta) {
            if (mainEffect != null) {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.setMainEffect(mainEffect);
                if (!potionEffects.isEmpty()) {
                    potionEffects.forEach(potionEffect -> potionMeta.addCustomEffect(potionEffect, true));
                }
            }
        }
        
        if (itemMeta instanceof Repairable) {
            if (repairCost != -1) {
                Repairable repairable = (Repairable) itemMeta;
                repairable.setRepairCost(repairCost);
            }
        }
        
        if (itemMeta instanceof SkullMeta) {
            if (this.skullOwner != null) {
                SkullMeta skullMeta = (SkullMeta) itemMeta;
                String skullOwner;
                User user = ManiaCore.getInstance().getUserManager().getUser(this.skullOwner);
                if (user != null) {
                    skullOwner = user.getName();
                } else {
                    skullOwner = ManiaUtils.getNameFromUUID(this.skullOwner);
                }
    
                if (skullOwner != null) {
                    skullMeta.setOwner(skullOwner);
        
                    
                }
    
                GameProfile mcProfile = null;
    
                if (this.skullSkin != null) {
                    mcProfile = SpigotUtils.skinToProfile(skullSkin);
                } else {
                    Player player = Bukkit.getPlayer(this.skullOwner);
                    if (player != null) {
                        mcProfile = ((CraftPlayer) player).getProfile();
                    } else {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(this.skullOwner);
                        if (offlinePlayer != null) {
                            mcProfile = ((CraftOfflinePlayer) offlinePlayer).getProfile();
                            if (mcProfile == null) {
                                List<IRecord> records = ManiaCore.getInstance().getDatabase().getRecords(SkinRecord.class, "uuid", this.skullOwner.toString());
                                for (IRecord record : records) {
                                    if (record instanceof SkinRecord) {
                                        mcProfile = SpigotUtils.skinToProfile(((SkinRecord) record).toObject());
                                    }
                                }
                            }
                        }
                    }
                }
                
                if (mcProfile != null) {
                    try {
                        Field field = skullMeta.getClass().getDeclaredField("profile");
                        field.setAccessible(true);
                        field.set(skullMeta, mcProfile);
                    } catch (Exception e) {}
                }
            }
        }
        
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    public ItemBuilder(Material material) {
        this.material = material;
    }
    
    public ItemBuilder(Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }
    
    public ItemBuilder(Material material, int amount, short data) {
        this.material = material;
        this.amount = amount;
        this.damage = data;
    }
    
    public ItemBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }
    
    
    
    public ItemBuilder setDamage(short damage) {
        this.damage = damage;
        return this;
    }
    
    public static ItemBuilder start(Material material) {
        return new ItemBuilder(material);
    }
    
    public static ItemBuilder start(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }
    
    public static ItemBuilder start(Material material, int amount, short data) {
        return new ItemBuilder(material, amount, data);
    }
    
    public ItemBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }
    
    public ItemBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }
    
    public ItemBuilder addLoreLine(String line) {
        this.lore.add(line);
        return this;
    }
    
    public ItemBuilder setEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
        return this;
    }
    
    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }
    
    public ItemBuilder setStoredEnchantments(Map<Enchantment, Integer> storedEnchantments) {
        this.storedEnchantments = storedEnchantments;
        return this;
    }
    
    public ItemBuilder addStoredEnchantment(Enchantment enchantment, int level) {
        this.storedEnchantments.put(enchantment, level);
        return this;
    }
    
    public ItemBuilder setItemFlags(Set<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }
    
    public ItemBuilder addItemFlag(ItemFlag... flags) {
        this.itemFlags.addAll(Arrays.asList(flags));
        return this;
    }
    
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }
    
    public ItemBuilder setBannerColor(DyeColor bannerColor) {
        this.bannerColor = bannerColor;
        return this;
    }
    
    public ItemBuilder setBannerPatterns(List<Pattern> bannerPatterns) {
        this.bannerPatterns = bannerPatterns;
        return this;
    }
    
    public ItemBuilder setBlockState(BlockState blockState) {
        this.blockState = blockState;
        return this;
    }
    
    public ItemBuilder setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
        return this;
    }
    
    public ItemBuilder setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
        return this;
    }
    
    public ItemBuilder setBookPages(List<String> bookPages) {
        this.bookPages = bookPages;
        return this;
    }
    
    public ItemBuilder setArmorColor(Color armorColor) {
        this.armorColor = armorColor;
        return this;
    }
    
    public ItemBuilder setMainEffect(PotionEffectType mainEffect) {
        this.mainEffect = mainEffect;
        return this;
    }
    
    public ItemBuilder setPotionEffects(List<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
        return this;
    }
    
    public ItemBuilder addPotionEffects(PotionEffect... potionEffects) {
        this.potionEffects.addAll(Arrays.asList(potionEffects));
        return this;
    }
    
    public ItemBuilder setRepairCost(int repairCost) {
        this.repairCost = repairCost;
        return this;
    }
    
    public ItemBuilder setSkullOwner(UUID skullOwner) {
        this.skullOwner = skullOwner;
        return this;
    }
    
    public ItemBuilder setSkullSkin(Skin skullSkin) {
        this.skullSkin = skullSkin;
        return this;
    }
    
    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }
    
    public ItemBuilder setDurability(int durability) {
        this.durability = durability;
        return this;
    }
    
    public ItemBuilder withLore(String... lore) {
        if (lore != null) {
            for (String s : lore) {
                addLoreLine(s);
            }
        }
        
        return this;
    }
}