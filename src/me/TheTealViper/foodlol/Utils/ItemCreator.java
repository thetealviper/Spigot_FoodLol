package me.TheTealViper.foodlol.Utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import me.TheTealViper.foodlol.FoodLol;

public class ItemCreator implements Listener{
	private static List<Material> durMats = new ArrayList<Material>();
	public static Map<ItemStack, Integer> damageInfo = new HashMap<ItemStack, Integer>();
	public static Map<ItemStack, Integer> forceStackInfo = new HashMap<ItemStack, Integer>();
	
	public static ItemStack createItemFromConfiguration(String foodName, ConfigurationSection sec){
		if(durMats.isEmpty())
			loadDurMats();
		ItemStack item = null;
		if(sec == null || !sec.contains("id"))
			return null;
		if(sec.getString("id").startsWith("DIAMOND_SWORD:")) {
			item = new ItemStack(Material.DIAMOND_SWORD);
			item.setDurability(Short.valueOf(sec.getString("id").replace("DIAMOND_SWORD:", "")));
		}else {
			item = new ItemStack(Material.getMaterial(sec.getString("id")));
		}
		List<String> tags = sec.contains("tags") ? sec.getStringList("tags") : new ArrayList<String>();
		if(sec.contains("amount"))
			item.setAmount(sec.getInt("amount"));
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.STICK);
		if(sec.contains("name") && !sec.getString("name").equals("")){
			meta.setDisplayName(ViperStringUtils.makeColors(sec.getString("name")));
			item.setItemMeta(meta);
		}
		List<String> enchantmentStrings = sec.contains("enchantments") ? sec.getStringList("enchantments") : new ArrayList<String>();
		for(String enchantmentString : enchantmentStrings){
			String enchantment = enchantmentString.split(":")[0];
    		int level = Integer.valueOf(enchantmentString.split(":")[1]);
    		if(enchantment.equalsIgnoreCase("arrowdamage")){
                item.addEnchantment(Enchantment.ARROW_DAMAGE, level);
            }else if(enchantment.equalsIgnoreCase("arrowfire")){
                item.addUnsafeEnchantment(Enchantment.ARROW_FIRE, level);
            }else if(enchantment.equalsIgnoreCase("arrowinfinite")){
                item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, level);
            }else if(enchantment.equalsIgnoreCase("arrowknockback")){
                item.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, level);
            }else if(enchantment.equalsIgnoreCase("damage")){
                item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, level);
            }else if(enchantment.equalsIgnoreCase("digspeed")){
                item.addUnsafeEnchantment(Enchantment.DIG_SPEED, level);
            }else if(enchantment.equalsIgnoreCase("durability")){
                item.addUnsafeEnchantment(Enchantment.DURABILITY, level);
            }else if(enchantment.equalsIgnoreCase("fireaspect")){
                item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, level);
            }else if(enchantment.equalsIgnoreCase("knockback")){
                item.addUnsafeEnchantment(Enchantment.KNOCKBACK, level);
            }else if(enchantment.equalsIgnoreCase("lootbonusblock")){
                item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, level);
            }else if(enchantment.equalsIgnoreCase("lootbonusmob")){
                item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, level);
            }else if(enchantment.equalsIgnoreCase("luck")){
                item.addUnsafeEnchantment(Enchantment.LUCK, level);
            }else if(enchantment.equalsIgnoreCase("protectionfall")){
                item.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, level);
            }else if(enchantment.equalsIgnoreCase("protectionfire")){
                item.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, level);
            }else if(enchantment.equalsIgnoreCase("silktouch")){
                item.addUnsafeEnchantment(Enchantment.SILK_TOUCH, level);
            }
		}
		for(String s : tags){
    		if(s.startsWith("skullskin") && item.getType().equals(Material.PLAYER_HEAD)){
    			SkullMeta skull = (SkullMeta) item.getData();
    			skull.setOwner(s.replace("skullskin:", ""));
    			item.setData((MaterialData) skull);
    		}else if(s.startsWith("durability") && durMats.contains(item.getType())){
    			item.getData().setData(Byte.valueOf(s.replace("durability:", "")));
    			item.setDurability(Short.valueOf(s.replace("durability:", "")));
    		}else if(s.equalsIgnoreCase("unbreakable")){
    			meta = item.getItemMeta();
    			meta.setUnbreakable(true);
    			item.setItemMeta(meta);
    		}
    	}
		for(String s : tags) {
			if(s.startsWith("skulltexture") && item.getType().equals(Material.PLAYER_HEAD)){
				item = Base64Skull.getSkull(item, FoodLol.getUUIDFromFoodName(foodName), "http://textures.minecraft.net/texture/" + s.replace("skulltexture:", ""));
//				item = Base64Skull.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTk5NzBkMDg1YmY1YWFlODc1MTY1ZjgzMzc4OTYyNDJjMTJjZDExMjk5ZGY4OTg5MWE4YTgxM2FkZWI4In19fQ==");
			}
		}
		List<String> lore = sec.contains("lore") ? sec.getStringList("lore") : new ArrayList<String>();
		if(!lore.isEmpty()){
			for(int i = 0;i < lore.size();i++){
				lore.set(i, ViperStringUtils.makeColors(lore.get(i)));
			}
			meta = item.getItemMeta();
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		if(sec.contains("flags")){
			meta = item.getItemMeta();
			for(String s : sec.getStringList("flags")){
				meta.addItemFlags(ItemFlag.valueOf(s));
			}
			item.setItemMeta(meta);
		}
		for(String s : tags){//Item modification is complete by now
    		if(s.startsWith("damage")){
    			damageInfo.put(item.clone(), Integer.valueOf(s.replace("damage:", "")));
    		}else if(s.startsWith("forcestack")){
    			forceStackInfo.put(item.clone(), Integer.valueOf(s.replace("forcestack:", "")));
    		}
    	}
		return item;
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Player){
			Player p = (Player) e.getDamager();
			ItemStack item = p.getItemInHand();
			if(p.getItemInHand() != null && !p.getItemInHand().getType().equals(Material.AIR)){
				for(ItemStack i : damageInfo.keySet()){
					if(item.isSimilar(i)){
						e.setDamage(damageInfo.get(i));
						return;
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = false)
	public void onPickup(PlayerPickupItemEvent e){
		Player p = e.getPlayer();
		ItemStack forceStack = null;
		ItemStack keyItem = null;
		for(ItemStack i : forceStackInfo.keySet()){
			if(e.getItem().getItemStack().isSimilar(i)){
				forceStack = e.getItem().getItemStack();
				keyItem = i;
			}
		}
		if(forceStack != null){
			e.setCancelled(true);
			e.getItem().remove();
			giveCustomItem(p, forceStack);
		}
	}
	
	public static void giveCustomItem(Player p, ItemStack item){
		int amount = item.getAmount();//Start at the item's amount because they're picking it up
		for(int i = 0;i < 36;i++){
			if(p.getInventory().getItem(i) != null && p.getInventory().getItem(i).isSimilar(item)){
				amount += p.getInventory().getItem(i).getAmount();
				p.getInventory().getItem(i).setAmount(0);
			}
		}
		int stackSize = forceStackInfo.get(item);
		while(amount > 0){
			if(amount >= stackSize){
				ItemStack temp = item.clone();
				temp.setAmount(stackSize);
				p.getInventory().setItem(p.getInventory().firstEmpty(), temp);
				amount -= stackSize;
			}else{
				ItemStack temp = item.clone();
				temp.setAmount(amount);
				p.getInventory().setItem(p.getInventory().firstEmpty(), temp);
				amount = 0;
			}
		}
	}
	
	private static void loadDurMats(){
		durMats.add(Material.DIAMOND_SHOVEL);durMats.add(Material.GOLDEN_SHOVEL);durMats.add(Material.IRON_SHOVEL);
    	durMats.add(Material.STONE_SHOVEL);durMats.add(Material.WOODEN_SHOVEL);
    	durMats.add(Material.DIAMOND_PICKAXE);durMats.add(Material.GOLDEN_PICKAXE);durMats.add(Material.IRON_PICKAXE);
    	durMats.add(Material.STONE_PICKAXE);durMats.add(Material.WOODEN_PICKAXE);
    	durMats.add(Material.DIAMOND_AXE);durMats.add(Material.GOLDEN_AXE);durMats.add(Material.IRON_AXE);
    	durMats.add(Material.STONE_AXE);durMats.add(Material.WOODEN_AXE);
    	durMats.add(Material.DIAMOND_HOE);durMats.add(Material.GOLDEN_HOE);durMats.add(Material.IRON_HOE);
    	durMats.add(Material.STONE_HOE);durMats.add(Material.WOODEN_HOE);
    	durMats.add(Material.DIAMOND_SWORD);durMats.add(Material.GOLDEN_SWORD);durMats.add(Material.IRON_SWORD);
    	durMats.add(Material.STONE_SWORD);durMats.add(Material.WOODEN_SWORD);
    	durMats.add(Material.CHAINMAIL_HELMET);durMats.add(Material.DIAMOND_HELMET);durMats.add(Material.GOLDEN_HELMET);
    	durMats.add(Material.IRON_HELMET);durMats.add(Material.LEATHER_HELMET);
    	durMats.add(Material.CHAINMAIL_CHESTPLATE);durMats.add(Material.DIAMOND_CHESTPLATE);durMats.add(Material.GOLDEN_CHESTPLATE);
    	durMats.add(Material.IRON_CHESTPLATE);durMats.add(Material.LEATHER_CHESTPLATE);
    	durMats.add(Material.CHAINMAIL_LEGGINGS);durMats.add(Material.DIAMOND_LEGGINGS);durMats.add(Material.LEATHER_LEGGINGS);
    	durMats.add(Material.IRON_LEGGINGS);durMats.add(Material.GOLDEN_LEGGINGS);
    	durMats.add(Material.CHAINMAIL_BOOTS);durMats.add(Material.DIAMOND_BOOTS);durMats.add(Material.GOLDEN_BOOTS);
    	durMats.add(Material.IRON_BOOTS);durMats.add(Material.LEATHER_BOOTS);
    	durMats.add(Material.BOW);
	}
	
	private boolean isSimilar(ItemStack item1, ItemStack item2) {
        if (item2.getType() == item1.getType() && item2.getDurability() == item1.getDurability()) {
            ItemMeta item1Meta = item1.getItemMeta();
            ItemMeta item2Meta = item2.getItemMeta();
            if (item1Meta.hasDisplayName() != item2Meta.hasDisplayName()) {
                return false;
            }
            if (item1Meta.hasDisplayName()) {
                if (!item1Meta.getDisplayName().equals(item2Meta.getDisplayName())) {
                    return false;
                }
            }
            if (item1Meta.hasLore() != item2Meta.hasLore()) {
                return false;
            }
            if (item1Meta.hasLore()) {
                if (item1Meta.getLore().size() != item2Meta.getLore().size()) {
                    return false;
                }
                for (int index = 0; index < item1Meta.getLore().size(); index++) {
                    if (item1Meta.getLore().get(index).equals(item2Meta.getLore().get(index))) {
                        return false;
                    }
                }
            }
            if (item1Meta.hasEnchants() != item2Meta.hasEnchants()) {
                return false;
            }
            if (item1Meta.hasEnchants()) {
                if (item1Meta.getEnchants().size() != item2Meta.getEnchants().size()) {
                    return false;
                }
                for (Entry<Enchantment, Integer> enchantInfo : item1Meta.getEnchants().entrySet()) {
                    if (item1Meta.getEnchantLevel(enchantInfo.getKey()) != item2Meta.getEnchantLevel(enchantInfo.getKey())) {
                        return false;
                    }
                }
            }
            if (item1Meta.getItemFlags().size() != item2Meta.getItemFlags().size()) {
                return false;
            }
            for (ItemFlag flag : item1Meta.getItemFlags()) {
                if (!item2Meta.hasItemFlag(flag)) {
                    return false;
                }
            }
            return true;
        }
        return false;
	}
	
}
