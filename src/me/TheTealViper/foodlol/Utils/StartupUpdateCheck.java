package me.TheTealViper.foodlol.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class StartupUpdateCheck {
	
	public StartupUpdateCheck(JavaPlugin plugin, String spigotID) {
		Bukkit.getPluginManager().registerEvents((Listener) plugin, plugin);
		plugin.saveDefaultConfig();
		
		if(!spigotID.equals("-1"))
			updatePlugin(plugin, spigotID);
		
		updateConfig(plugin);
		plugin.getLogger().info(plugin.getDescription().getName() + " from TheTealViper initializing!");
	}
	
	private void updatePlugin(JavaPlugin plugin, String spigotID){
		String installed = plugin.getDescription().getVersion();
		String[] installed_Arr = installed.split("[.]");
		String posted = getSpigotVersion(spigotID);
		if(posted == null)
			return;
		String[] posted_Arr = posted.split("[.]");
		for(int i = 0;i < posted_Arr.length;i++){
			if(installed_Arr.length <= i || Integer.valueOf(installed_Arr[i]) < Integer.valueOf(posted_Arr[i])){
				plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + plugin.getDescription().getName() + " has an update ready [" + installed + " -> " + posted + "]!");
				break;
			}
		}
	}
	private void updateConfig(JavaPlugin plugin){
		YamlConfiguration compareTo = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("config.yml")));
		boolean update = false;
		if(!plugin.getConfig().contains("VERSION"))
			update = true;
		String oldVersion = plugin.getConfig().getString("VERSION");
		String[] oldVersion_Arr = oldVersion.split("[.]");
		String newVersion = compareTo.getString("VERSION");
		String[] newVersion_Arr = newVersion.split("[.]");
		for(int i = 0;i < newVersion_Arr.length;i++){
			if(oldVersion_Arr.length <= i || Integer.valueOf(oldVersion_Arr[i]) < Integer.valueOf(newVersion_Arr[i])){
				update = true;
				break;
			}
		}
		if(update){
			File file = new File("plugins/" + plugin.getDescription().getName() + "/config.yml");
			try {
				com.google.common.io.Files.copy(file, new File("plugins/" + plugin.getDescription().getName() + "/configBACKUP_" + oldVersion + ".yml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(file.exists())
				file.delete();
			plugin.saveDefaultConfig();
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + plugin.getDescription().getName() + " config.yml has been updated [" + oldVersion + " -> " + newVersion + "] and a backup created of old configuration!");
		}
	}
	private String getSpigotVersion(String spigotID) {
        try {
        	HttpClient client = HttpClient.newHttpClient();
        	HttpRequest request = HttpRequest.newBuilder()
        		    .uri(URI.create("https://api.spiget.org/v2/resources/73487/versions/latest"))
        		    .build();
        	HttpResponse<String> response = client.send(request,
        		    HttpResponse.BodyHandlers.ofString());

        	JSONObject object = (JSONObject) JSONValue.parse(response.body());
        	return (String) object.get("name");
        } catch (Exception ex) {
            
        }
        return null;
    }
}
