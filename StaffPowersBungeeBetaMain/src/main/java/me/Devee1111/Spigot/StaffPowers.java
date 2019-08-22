package me.Devee1111.Spigot;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.permission.Permission;


public class StaffPowers extends JavaPlugin {
	
	//Hardcoded config versino check - redo default check later
	double configVersion = 1.3;
	//Plugin variables
	public static StaffPowers instance;
	public FileConfiguration config;
	//Vault API - Devee 8/6/19
	private static Permission perms = null;
	
	
	
	//Our datas
	private HashMap<String, String> dataGod = new HashMap<>();
	
	@Override
	public void onEnable() {
		//TODO - Redo the config function, Version detection from default
		saveDefaultConfig();
		config = getConfig();
		if(config.getDouble("version") != configVersion) {
			//TODO say we're resetting config
			saveResource("config.yml", true);
		}
		
		//Creating our instance
		setInstance(this);
		
		//Setting up our Vault API
		setupPermissions();
		
		
		//Creating our Command classes
		getCommand("testspigot").setExecutor(new StaffCmdTest(this));;
		getCommand("staffreloadspigot").setExecutor(new StaffCmdReload(this));
		
		//Creating our Listener Class
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new StaffPowersLsnGod(this), this);
		if(config.getBoolean("options.essentials") == true) {
			pm.registerEvents(new StaffLsnAfk(this), this);
		}
		
		//TODO Clear out data on restart
		dataGod.clear();
		
		//Setting up plugin messenger 
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "staff:powers");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "staff:powers", new StaffPowersPluginMessageListener());
		
	}
	
	/*
	 * SECTION - VAULT API
	 * Created by - Devee1111 on 8/6/19
	 * Adjusted - 8/21/19 by Devee1111
	 */
	
	//Adjusted
	public boolean hasRank(String rank, Player p) {
		for(String r : perms.getPlayerGroups(p)) {
			if(r.equals(rank)) {
				return true;
			}
		}
		return false;
	}
	
	public void giveRank(String uuid, String rank) {
		perms.playerAddGroup(null, Bukkit.getOfflinePlayer(UUID.fromString(uuid)), rank);
	}
	
	public void takeRank(String uuid, String rank) {
		perms.playerRemoveGroup(null, Bukkit.getOfflinePlayer(UUID.fromString(uuid)), rank);
	}
	
	private boolean setupPermissions() {
		if(getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
			//TODO inform that vault isn't active
		}
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
	}
	
	/*
	 * SECTION - GOD DATA
	 * Created by - Devee1111 on 7/22/19
	 */
	
	public HashMap<String, String> getGodData() {
		return dataGod;
	}
	
	//TODO add messages to enable/disable
	public void setGod(Player p, boolean status) {
		if(status == true) {
			dataGod.put(p.getUniqueId().toString(), p.getName());
			sendMessage(p,"madeGod");
		} else {
			dataGod.remove(p.getUniqueId().toString());
			sendMessage(p,"madeMortal");
		}
	}
	
	public boolean isGod(Player p) {
		if(dataGod.containsKey(p.getUniqueId().toString())) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	/*
	 * SECTION - Message assist
	 * Created by - Devee1111 on 7/22/19
	 */
	
	public String color(String toColor) {
		String tosend = ChatColor.translateAlternateColorCodes('&', toColor);
		return tosend;
	}
	
	public String nopermission() {
		String tosend = config.getString("messages.nopermission");
		tosend = tosend.replace("%prefix%", config.getString("options.prefix"));
		tosend = ChatColor.translateAlternateColorCodes('&', tosend);
		return tosend;
	}
	
	public String createMessage(String path) {
		path = "messages."+path;
		if(!config.contains(path)) {
			error("crateMessage",path);
			return path;
		}
		String tosend = config.getString(path);
		tosend = tosend.replace("%prefix%", config.getString("options.prefix"));
		return tosend;
	}
	
	public String prefix(String toPrefix) {
		String tosend = toPrefix.replace("%prefix%", config.getString("options.prefix"));
		return tosend;
	}
	
	public String prefixcolor(String todo) {
		String tosend = prefix(todo);
		tosend = color(tosend);
		return tosend;
	}
	
	public void sendMessage(Player p, String path) {
		path = "messages."+path;
		if(!config.contains(path)) {
			error("crateMessage",path);
			p.sendMessage(path);
			return;
		}
		String tosend = config.getString(path);
		tosend = tosend.replace("%prefix%", config.getString("options.prefix"));
		tosend = tosend.replace("%player%", p.getName());
		tosend = ChatColor.translateAlternateColorCodes('&', tosend);
		p.sendMessage(tosend);
	}
	
	public void sendSMessage(Player p, String path) {
		if(!config.contains(path)) {
			error("createMessage",path);
			p.sendMessage(path);
			return;
		}
		String tosend = config.getString(path);
		tosend = tosend.replace("%prefix%", config.getString("options.prefix"));
		tosend = tosend.replace("%player%", p.getName());
		tosend = ChatColor.translateAlternateColorCodes('&', tosend);
		p.sendMessage(tosend);
	}
	
	private void error(String method, String path) {
		getLogger().log(Level.SEVERE,"##################################");
		getLogger().log(Level.SEVERE,"#                                #");
		getLogger().log(Level.SEVERE,"#          ERROR REPORT          #");
		getLogger().log(Level.SEVERE,"#                                #");
		getLogger().log(Level.SEVERE,"#          > Details <           #");
		getLogger().log(Level.SEVERE,"#-> PATH DOES NOT EXIST          #");
		getLogger().log(Level.SEVERE,"#          > Section <           #");
		getLogger().log(Level.SEVERE,"#-> messageAssist."+method+"()");
		getLogger().log(Level.SEVERE,"#           > Path <             #");
		getLogger().log(Level.SEVERE,"#-> "+path);
		getLogger().log(Level.SEVERE,"##################################");
	}
	
	/*
	 * SECTION - General use
	 * Created by - Devee1111 on 7/22/19
	 */
	
	public void log(String msg) {
		getLogger().log(Level.INFO, msg);
	}
	
	/*
	 * SECTION - Function
	 * Created by - Devee1111 on ?
	 */
	
	public static void setInstance(StaffPowers instance) {
		StaffPowers.instance = instance;
	}
	
	public void saveConfiguration() {
		saveConfig();
	}
	
	public void reloadConfiguration() {
		reloadConfig();
	}
	
	public static StaffPowers getInstance() {
		return instance;
	}
}