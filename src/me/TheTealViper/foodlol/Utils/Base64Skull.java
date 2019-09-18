package me.TheTealViper.foodlol.Utils;

import java.lang.reflect.Field;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class Base64Skull {
//	public static ItemStack getSkull(String base64) {
//		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
//		SkullMeta meta = (SkullMeta) item.getItemMeta();
//		
//		GameProfile profile = new GameProfile(UUID.randomUUID(), "");
//		profile.getProperties().put("textures", new Property("textures", base64));
//		Field profileField = null;
//		try {
//		    profileField = meta.getClass().getDeclaredField("profile");
//		    profileField.setAccessible(true);
//		    profileField.set(meta, profile);
//		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
//		    e.printStackTrace();
//		}
//		
//		item.setItemMeta(meta);
//		return item;
//	}
	
	public static ItemStack getSkull(ItemStack head, String uuid, String url) {
        if(url == null)return head;
        if(url.isEmpty())return head;
       
       
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.fromString(uuid), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }
       
}
