package me.TheTealViper.foodlol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.TheTealViper.foodlol.Utils.PluginFile;
import me.TheTealViper.foodlol.Utils.UtilityEquippedJavaPlugin;
import net.md_5.bungee.api.ChatColor;

public class FoodLol extends UtilityEquippedJavaPlugin implements Listener{
//	public Map<String, ItemStack> foodInfo = new HashMap<String, ItemStack>();
	Map<String, Double> healthInfo = new HashMap<String, Double>();
	Map<String, Double> hungerInfo = new HashMap<String, Double>();
	Map<String, ItemStack> returnInfo = new HashMap<String, ItemStack>();
	Map<String, List<String>> effectsInfo = new HashMap<String, List<String>>();
	Map<String, List<String>> commandInfo = new HashMap<String, List<String>>();
	Map<String, Long> cooldownData = new HashMap<String, Long>();
	boolean overrideMaxHealth = false;
	
	public void onEnable(){
		StartupPlugin(this, "49852");
		
		custPlugin = this;
		overrideMaxHealth = getConfig().getBoolean("Override_Max_Health");
		loadRecipes();
	}
	
	public void onDisable(){
		//Bukkit.getLogger().info(makeColors("FoodLol from TheTealViper shutting down. Bshzzzzzz"));
	}
	
	private void loadRecipes(){
		//Default config
		ConfigurationSection allFoodSec = getConfig().getConfigurationSection("Food");
		for(String foodID : allFoodSec.getKeys(false)){
			ConfigurationSection foodSec = allFoodSec.getConfigurationSection(foodID);
//			ItemStack foodItem = ItemCreator.createItemFromConfiguration(foodID, foodSec);
			getLoadEnhancedItemstackFromConfig().loadItem(foodID, foodSec);
			healthInfo.put(foodID, foodSec.getDouble("health"));
			hungerInfo.put(foodID, foodSec.getDouble("hunger"));
			if(foodSec.contains("effects"))
				effectsInfo.put(foodID, foodSec.getStringList("effects"));
			if(foodSec.contains("return")) {
//				returnInfo.put(foodID, ItemCreator.createItemFromConfiguration("null", foodSec.getConfigurationSection("return")));
				getLoadEnhancedItemstackFromConfig().loadItem(foodID + "-return", foodSec.getConfigurationSection("return"));
				returnInfo.put(foodID, getLoadEnhancedItemstackFromConfig().getItem(foodID + "-return"));
			}
			if(foodSec.contains("commands"))
				commandInfo.put(foodID, foodSec.getStringList("commands"));
			else
				commandInfo.put(foodID, new ArrayList<String>());
		}
//		//In game config
//		allFoodSec = inGameMadeRecipes.getConfigurationSection("Food");
//		if(allFoodSec != null){
//			for(String foodID : allFoodSec.getKeys(false)){
//				ConfigurationSection foodSec = allFoodSec.getConfigurationSection(foodID);
////				ItemStack foodItem = ItemCreator.createItemFromConfiguration(foodID, foodSec);
//				getLoadEnhancedItemstackFromConfig().loadItem(foodID, foodSec);
//				healthInfo.put(foodID, foodSec.getDouble("health"));
//				hungerInfo.put(foodID, foodSec.getDouble("hunger"));
//				if(foodSec.contains("effects"))
//					effectsInfo.put(foodID, foodSec.getStringList("effects"));
//				else
//					effectsInfo.put(foodID, new ArrayList<String>());
//				if(foodSec.contains("return")) {
////					returnInfo.put(foodID, ItemCreator.createItemFromConfiguration("null", foodSec.getConfigurationSection("return")));
//					getLoadEnhancedItemstackFromConfig().loadItem(foodID + "-return", foodSec.getConfigurationSection("return"));
//					returnInfo.put(foodID, getLoadEnhancedItemstackFromConfig().getItem(foodID + "-return"));
//				}
//				if(foodSec.contains("commands"))
//					commandInfo.put(foodID, foodSec.getStringList("commands"));
//				else
//					commandInfo.put(foodID, new ArrayList<String>());
//			}
//		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			if(onRightClick(e.getPlayer()))
				e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent e){
		if(onRightClick(e.getPlayer()))
			e.setCancelled(true);
	}
	
	@SuppressWarnings("deprecation")
	private boolean onRightClick(Player p){
		ItemStack hand = p.getInventory().getItemInMainHand();
		boolean found = false;
		for(ItemStack dummy : getLoadEnhancedItemstackFromConfig().enhancedItemInfo.values()){
//			if(hand.isSimilar(dummy)){
			if(getLoadEnhancedItemstackFromConfig().isSimilar(hand, dummy)){
				found = true;
				break;
			}
		}
		if(!found) {
			return false;
		}
		if(p.getFoodLevel() == 20)
			return true;
		String foodID = "";
		//This second loop through is necessary to set the foodID
		for(String dummy : getLoadEnhancedItemstackFromConfig().enhancedItemInfo.keySet()){
//			if(getLoadEnhancedItemstackFromConfig().enhancedItemInfo.get(dummy).isSimilar(hand)){
			if(getLoadEnhancedItemstackFromConfig().isSimilar(getLoadEnhancedItemstackFromConfig().enhancedItemInfo.get(dummy), hand)) {
				foodID = dummy;
				break;
			}
		}
		//Now we know they have a food item
		if(!cooldownData.containsKey(p.getUniqueId().toString()))
			cooldownData.put(p.getUniqueId().toString(), 0L);
		long lastEat = cooldownData.get(p.getUniqueId().toString());
		long delta = System.currentTimeMillis() - lastEat;
		int requiredTime = getConfig().getInt("Cooldown") * 1000;
		if(delta >= requiredTime){
			//Now we know they pass cooldown
			Double health = healthInfo.get(foodID);
			Double maxHealth = p.getMaxHealth();
			if(health > 0){
				if(p.getHealth() + health > maxHealth && overrideMaxHealth)
					p.setMaxHealth(p.getHealth() + health);
				if(p.getHealth() + health > p.getMaxHealth())
					p.setHealth(p.getMaxHealth());
				else
					p.setHealth(p.getHealth() + health);
			}else if(health < 0){
				if(p.getHealth() + health < 0)
					p.setHealth(0);
				else
					p.setHealth(p.getHealth() + health);
			}
			
			int hunger = (hungerInfo.get(foodID)).intValue();
			int maxHunger = 20;
			if(hunger > 0){
				if(p.getFoodLevel() + hunger > maxHunger)
					p.setFoodLevel(maxHunger);
				else
					p.setFoodLevel(p.getFoodLevel() + hunger);
			}else if(hunger < 0){
				if(p.getFoodLevel() + hunger < 0)
					p.setFoodLevel(0);
				else
					p.setFoodLevel(p.getFoodLevel() + hunger);
			}
			
			if(effectsInfo.containsKey(foodID)) {
				for(String effectString : effectsInfo.get(foodID)){
					String[] effectArray = effectString.split(":");
					String effectName = effectArray[0];
					int effectLevel = Integer.valueOf(effectArray[1]);
					double effectDuration = Double.valueOf(effectArray[2]);
					PotionEffectType pot = null;
					for(PotionEffectType type : PotionEffectType.values()){
						if(type.getName().equalsIgnoreCase(effectName)){
							pot = type;
						}
					}
					if(pot != null){
						p.addPotionEffect(new PotionEffect(pot, (int) (effectDuration * 20d), effectLevel));
					}
				}
			}
			
			for(String command : commandInfo.get(foodID)) {
				if(!command.startsWith("/"))
					command = "/" + command;
				boolean isOp = p.isOp();
				p.setOp(true);
				p.chat(command);
				p.setOp(isOp);
			}
			
			cooldownData.put(p.getUniqueId().toString(), System.currentTimeMillis());
			hand.setAmount(hand.getAmount() - 1);
			String FINALFOODID = foodID;
			for(int i = 0;i < getConfig().getInt("Cooldown") * 2;i++){
				final int index = i;
				Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {public void run() {
					if(getConfig().contains("Sound." + FINALFOODID)){
//						p.getWorld().playSound(p.getLocation(), Sound.valueOf(getConfig().getString("Sound." + FINALFOODID)).bukkitSound(), 1F, (float) (Math.random() * 2));
						p.getWorld().playSound(p.getLocation(), Sound.valueOf(getConfig().getString("Sound." + FINALFOODID)), 1F, (float) (Math.random() * 2));
					}else{
//						p.getWorld().playSound(p.getLocation(), Sound.valueOf(getConfig().getString("Sound.default")).bukkitSound(), 1F, (float) (Math.random() * 2));
						p.getWorld().playSound(p.getLocation(), Sound.valueOf(getConfig().getString("Sound.default")), 1F, (float) (Math.random() * 2));
					}
					if(index == getConfig().getInt("Cooldown") * 2 - 1 && returnInfo.containsKey(FINALFOODID))
						p.getInventory().setItem(p.getInventory().firstEmpty(), returnInfo.get(FINALFOODID));
				}}, 10L * i);
			}
			return true;
		}else
			p.sendMessage(makeColors(getConfig().getString("Cooldown_Message")));
		return true;
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Player){
			Player p = (Player) e.getDamager();
			if(p.getInventory().getItemInMainHand() != null && !p.getInventory().getItemInMainHand().getType().equals(Material.AIR)){
				for(ItemStack i : getLoadEnhancedItemstackFromConfig().enhancedItemInfo.values()){
					if(i.isSimilar(p.getInventory().getItemInMainHand())){
						e.setDamage(1);
						break;
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		boolean handleCommand = false;
		if(sender instanceof Player && sender.hasPermission("foodlol.use"))
			handleCommand = true;
		else if(!(sender instanceof Player))
			handleCommand = true;
		if(!handleCommand)
			return false;
		boolean explain = false;
		if(label.equalsIgnoreCase("foodlol")){
			if(args.length == 0){
				explain = true;
			}else if(args.length == 1){
				if(args[0].equalsIgnoreCase("list")){
					String message = "";
					int index = 0;
					for(String s : getLoadEnhancedItemstackFromConfig().enhancedItemInfo.keySet()){
						if(index != getLoadEnhancedItemstackFromConfig().enhancedItemInfo.size() - 1)
							message += "- " + s + "\n";
						else
							message += "- " + s;
					}
					if(sender instanceof Player){
						Player p = (Player) sender;
						p.sendMessage(message);
					}else{
						sender.sendMessage(message);
					}
				}else if(args[0].equalsIgnoreCase("reload")){
					reloadConfig();
					overrideMaxHealth = getConfig().getBoolean("Override_Max_Health");
					
					WipeItemstackFromConfigCache();
					healthInfo = new HashMap<String, Double>();
					hungerInfo = new HashMap<String, Double>();
					returnInfo = new HashMap<String, ItemStack>();
					cooldownData = new HashMap<String, Long>();
					loadRecipes();
					sender.sendMessage("Reloaded.");
				}else
					explain = true;
			}else if(args.length == 2){
				explain = true;
			}else if(args.length == 3){
				if(args[0].equalsIgnoreCase("give")){
					String playerName = args[1];
					String foodID = args[2];
					if(Bukkit.getOfflinePlayer(playerName).isOnline()){
						if(getLoadEnhancedItemstackFromConfig().enhancedItemInfo.containsKey(foodID)){
							Player oP = Bukkit.getPlayer(playerName);
							ItemStack item = getLoadEnhancedItemstackFromConfig().enhancedItemInfo.get(foodID).clone();
							//
							ItemStack forceStack = null;
//							ItemStack keyItem = null;
							for(ItemStack i : getLoadEnhancedItemstackFromConfig().forceStackInfo.keySet()){
								if(getLoadEnhancedItemstackFromConfig().isSimilar(item, i)){
									forceStack = item;
//									keyItem = i;
								}
							}
							if(forceStack != null){
								getLoadEnhancedItemstackFromConfig().giveCustomItem(oP, item);
							}
							//
//							oP.getInventory().addItem(foodInfo.get(foodID));
						}else{
							if(sender instanceof Player){
								Player p = (Player) sender;
								p.sendMessage("That food does not exist.");
							}else{
								sender.sendMessage("That food does not exist.");
							}
						}
					}else{
						if(sender instanceof Player){
							Player p = (Player) sender;
							p.sendMessage("That player is not online.");
						}else{
							sender.sendMessage("That player is not online.");
						}
					}
				}else{
					explain = true;
				}
			}else if(args.length == 4){
				if(args[0].equalsIgnoreCase("give")){
					String playerName = args[1];
					String foodID = args[2];
					if(Bukkit.getOfflinePlayer(playerName).isOnline()){
						if(getLoadEnhancedItemstackFromConfig().enhancedItemInfo.containsKey(foodID)){
							Player oP = Bukkit.getPlayer(playerName);
							ItemStack foodItem = getLoadEnhancedItemstackFromConfig().enhancedItemInfo.get(foodID).clone();
							try{
								foodItem.setAmount(foodItem.getAmount() * Integer.valueOf(args[3]));
								getLoadEnhancedItemstackFromConfig().giveCustomItem(oP, foodItem);
//								oP.getInventory().addItem(foodItem);
							}catch(Exception e){
								e.printStackTrace();
								if(sender instanceof Player){
									Player p = (Player) sender;
									p.sendMessage("That is not a number.");
								}else{
									sender.sendMessage("That is not a number.");
								}
							}
						}else{
							if(sender instanceof Player){
								Player p = (Player) sender;
								p.sendMessage("That food does not exist.");
							}else{
								sender.sendMessage("That food does not exist.");
							}
						}
					}else{
						if(sender instanceof Player){
							Player p = (Player) sender;
							p.sendMessage("That player is not online.");
						}else{
							sender.sendMessage("That player is not online.");
						}
					}
				}else{
					explain = true;
				}
			}else{
				explain = true;
			}
		}
		
		if(explain){
			String message = "[FoodLol Command]" + "\n"
					+ "/foodlol give <player> <foodID> (#)" + ChatColor.GRAY + " - Give a player a food item.\n"
							+ ChatColor.RESET + "/foodlol list" + ChatColor.GRAY + " - List possible foodID's.\n"
							+ ChatColor.RESET + "/foodlol reload" + ChatColor.GRAY + " - I refuse to explain this.";
			if(sender instanceof Player){
				Player p = (Player) sender;
				p.sendMessage(message);
			}else{
				sender.sendMessage(message);
			}
		}
		return false;
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
	
	public String getUUIDFromFoodName(String foodName) {
		PluginFile pf = new PluginFile(this, "uuidbase");
		if(pf.getKeys(false) == null || pf.getKeys(false).size() == 0) {
			pf.save();
		}
		
		if(pf.contains(foodName)) {
			return pf.getString(foodName);
		}else {
			String uuid = UUID.randomUUID().toString();
			pf.set(foodName, uuid);
			pf.save();
			return uuid;
		}
	}
}
