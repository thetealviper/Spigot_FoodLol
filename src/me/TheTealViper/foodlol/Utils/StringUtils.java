package me.TheTealViper.foodlol.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class StringUtils {
	
	public String makeColors(String s){
		//&x is a new "random color" variable
        return ChatColor.translateAlternateColorCodes('&', s).replaceAll("&x", randomColor(new ArrayList<Integer>()));
    }
    
    public String randomColor(List<Integer> blacklistedColors) {
    	//10 = a
    	//11 = b
    	//12 = c
    	//13 = d
    	//14 = e
    	//15 = f
		Random random = new Random();
		int i = 0;
		while(blacklistedColors.contains(i))
			i = random.nextInt(16);
		String cdata = i + "";
		if(i == 10)
			cdata = "a";
		else if(i == 11)
			cdata = "b";
		else if(i == 12)
			cdata = "c";
		else if(i == 13)
			cdata = "d";
		else if(i == 14)
			cdata = "e";
		String color = ChatColor.translateAlternateColorCodes('&', "&" + cdata);
		return color;
	}
    
    public String toLocString(Location loc, boolean detailed, boolean extended, String[] args){
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
    
    public Location fromLocString(String locString, boolean extended){
    	String[] s = locString.split("_");
    	if(!extended)
    		return new Location(Bukkit.getWorld(s[0]), Double.valueOf(s[1]), Double.valueOf(s[2]), Double.valueOf(s[3]));
    	else
    		return new Location(Bukkit.getWorld(s[0]), Double.valueOf(s[1]), Double.valueOf(s[2]), Double.valueOf(s[3]), Float.valueOf(s[4]), Float.valueOf(s[5]));
    }
    
}
