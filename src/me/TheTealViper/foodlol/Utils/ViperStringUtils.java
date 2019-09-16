package me.TheTealViper.foodlol.Utils;

import java.nio.charset.Charset;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class ViperStringUtils {

	// String constants. TODO Change them to something unique to avoid conflict with other plugins!
	private static final String SEQUENCE_HEADER = "" + ChatColor.RESET + ChatColor.UNDERLINE + ChatColor.RESET;
	private static final String SEQUENCE_FOOTER = "" + ChatColor.RESET + ChatColor.ITALIC + ChatColor.RESET;
	
	public static String convertToInvisibleString(String s) {
        String hidden = "";
        for (char c : s.toCharArray()) hidden += ChatColor.COLOR_CHAR+""+c;
        return hidden;
    }
    public static String convertBack(String s){
        //String converted = ChatColor.stripColor(s);
        String converted = s.replaceAll("�", "");
        return converted;
    }
    public static String makeColors(String s){
        String replaced = s
                .replaceAll("&0", "" + ChatColor.BLACK)
                .replaceAll("&1", "" + ChatColor.DARK_BLUE)
                .replaceAll("&2", "" + ChatColor.DARK_GREEN)
                .replaceAll("&3", "" + ChatColor.DARK_AQUA)
                .replaceAll("&4", "" + ChatColor.DARK_RED)
                .replaceAll("&5", "" + ChatColor.DARK_PURPLE)
                .replaceAll("&6", "" + ChatColor.GOLD)
                .replaceAll("&7", "" + ChatColor.GRAY)
                .replaceAll("&8", "" + ChatColor.DARK_GRAY)
                .replaceAll("&9", "" + ChatColor.BLUE)
                .replaceAll("&a", "" + ChatColor.GREEN)
                .replaceAll("&b", "" + ChatColor.AQUA)
                .replaceAll("&c", "" + ChatColor.RED)
                .replaceAll("&d", "" + ChatColor.LIGHT_PURPLE)
                .replaceAll("&e", "" + ChatColor.YELLOW)
                .replaceAll("&f", "" + ChatColor.WHITE)
                .replaceAll("&r", "" + ChatColor.RESET)
                .replaceAll("&l", "" + ChatColor.BOLD)
                .replaceAll("&o", "" + ChatColor.ITALIC)
                .replaceAll("&k", "" + ChatColor.MAGIC)
                .replaceAll("&m", "" + ChatColor.STRIKETHROUGH)
                .replaceAll("&n", "" + ChatColor.UNDERLINE)
                .replaceAll("\\\\", " ");
        return replaced;
    }
    
    public static String toLocString(Location loc, boolean detailed, boolean extended, String[] args){
    	String locString = loc.getWorld().getName() + "_";
    	if(detailed)
    		locString += loc.getX() + "_" + loc.getY() + "_" + loc.getZ();
    	else
    		locString += loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
    	if(extended){
    		if(detailed)
    			locString += "_" + loc.getYaw() + "_" + loc.getPitch();
    		else
    			locString += "_" + ((int) loc.getYaw()) + "_" + ((int) loc.getPitch());
    	}
    	if(args != null){
	    	for(String s : args)
	    		locString += "_" + s;
    	}
    	return locString;
    }
    public static Location fromLocString(String locString, boolean extended){
    	String[] s = locString.split("_");
    	if(!extended)
    		return new Location(Bukkit.getWorld(s[0]), Double.valueOf(s[1]), Double.valueOf(s[2]), Double.valueOf(s[3]));
    	else
    		return new Location(Bukkit.getWorld(s[0]), Double.valueOf(s[1]), Double.valueOf(s[2]), Double.valueOf(s[3]), Float.valueOf(s[4]), Float.valueOf(s[5]));
    }
	
	public static String encodeString(String hiddenString) {
		return quote(stringToColors(hiddenString));
	}
	
	public static boolean hasHiddenString(String input) {
		if (input == null) return false;
		
		return input.indexOf(SEQUENCE_HEADER) > -1 && input.indexOf(SEQUENCE_FOOTER) > -1;
	}	
	
	public static String extractHiddenString(String input) {
		return colorsToString(extract(input));
	}
	
	
	public static String replaceHiddenString(String input, String hiddenString) {
		if (input == null) return null;
		
		int start = input.indexOf(SEQUENCE_HEADER);
		int end = input.indexOf(SEQUENCE_FOOTER);
		
		if (start < 0 || end < 0) {
			return null;
		}
		
		return input.substring(0, start + SEQUENCE_HEADER.length()) + stringToColors(hiddenString) + input.substring(end, input.length());
	}
	
	/**
	 * Internal stuff.
	 */
	private static String quote(String input) {
		if (input == null) return null;
		return SEQUENCE_HEADER + input + SEQUENCE_FOOTER;
	}
	
	private static String extract(String input) {
		if (input == null) return null;
		
		int start = input.indexOf(SEQUENCE_HEADER);
		int end = input.indexOf(SEQUENCE_FOOTER);
		
		if (start < 0 || end < 0) {
			return null;
		}
		
		return input.substring(start + SEQUENCE_HEADER.length(), end);
	}
	
	private static String stringToColors(String normal) {
		if (normal == null) return null;
		
		byte[] bytes = normal.getBytes(Charset.forName("UTF-8"));
		char[] chars = new char[bytes.length * 4];
		
		for (int i = 0; i < bytes.length; i++) {
			char[] hex = byteToHex(bytes[i]);
			chars[i * 4] = ChatColor.COLOR_CHAR;
			chars[i * 4 + 1] = hex[0];
			chars[i * 4 + 2] = ChatColor.COLOR_CHAR;
			chars[i * 4 + 3] = hex[1];
		}
		
		return new String(chars);
	}
	
	private static String colorsToString(String colors) {
		if (colors == null) return null;
		
		colors = colors.toLowerCase().replace("" + ChatColor.COLOR_CHAR, "");
		
		if (colors.length() % 2 != 0) {
			colors = colors.substring(0, (colors.length() / 2) * 2);
		}
		
		char[] chars = colors.toCharArray();
		byte[] bytes = new byte[chars.length / 2];
		
		for (int i = 0; i < chars.length; i += 2) {
			bytes[i / 2] = hexToByte(chars[i], chars[i + 1]);
		}

		return new String(bytes, Charset.forName("UTF-8"));
	}
	
	private static int hexToUnsignedInt(char c) {
		if (c >= '0' && c <= '9') {
			return c - 48;
		} else if (c >= 'a' && c <= 'f') {
			return c - 87;
		} else {
			throw new IllegalArgumentException("Invalid hex char: out of range");
		}
	}
	
	private static char unsignedIntToHex(int i) {
		if (i >= 0 && i <= 9) {
			return (char) (i + 48);
		} else if (i >= 10 && i <= 15) {
			return (char) (i + 87);
		} else {
			throw new IllegalArgumentException("Invalid hex int: out of range");
		}
	}

	private static byte hexToByte(char hex1, char hex0) {
		return (byte) (((hexToUnsignedInt(hex1) << 4) | hexToUnsignedInt(hex0)) + Byte.MIN_VALUE);
	}
	
	private static char[] byteToHex(byte b) {
		int unsignedByte = (int) b - Byte.MIN_VALUE;
		return new char[]{unsignedIntToHex((unsignedByte >> 4) & 0xf), unsignedIntToHex(unsignedByte & 0xf)};
	}
	
}