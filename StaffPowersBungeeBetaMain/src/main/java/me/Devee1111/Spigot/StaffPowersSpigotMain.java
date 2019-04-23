package me.Devee1111.Spigot;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class StaffPowersSpigotMain extends JavaPlugin {
	
	public static StaffPowersSpigotMain instance;
	public FileConfiguration config;
	private HashMap<String, String[]> data = new HashMap<>();
	
	@Override
	public void onEnable() {
		//Saving our default config if none, and setting our config variable
		saveDefaultConfig();
		config = getConfig();
		if(config.getDouble("version") != 1.1) {
			saveResource("config.yml", true);
		}
		
		//Creating an instance, so that the config can be loaded easily && isntance can be passed easier
		setInstance(this);
		
		//Creating our Listener Class
		new StaffPowersSpigotListener(this);
		
		
		//This is were we're setting up channels so that we can do stuffy stuffs that annoy the piss outta me
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "staff:powers");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "staff:powers", new StaffPowersSpigotPluginMessageListener());
		
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public HashMap<String, String[]> getData() {
		return data;
	}
	
	public String color(String toColor) {
		String tosend = ChatColor.translateAlternateColorCodes('&', toColor);
		return tosend;
	}
	
	public String createMessage(String path) {
		String tosend = config.getString(path);
		tosend = tosend.replace("%prefix%", config.getString("options.prefix"));
		return tosend;
	}
	
	public void sendMessage(Player p, String path) {
		String tosend = config.getString(path);
		tosend = tosend.replace("%prefix%", config.getString("options.prefix"));
		tosend = tosend.replace("%player%", p.getName());
		tosend = ChatColor.translateAlternateColorCodes('&', tosend);
		p.sendMessage(tosend);
	}
	
	public static void setInstance(StaffPowersSpigotMain instance) {
		StaffPowersSpigotMain.instance = instance;
	}
	
	public void saveConfiguration() {
		saveConfig();
	}
	
	public void reloadConfiguration() {
		reloadConfig();
	}
	
	public void log(String msg) {
		getLogger().log(Level.INFO, msg);
	}
	
	public static StaffPowersSpigotMain getInstance() {
		return instance;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("testspigot")) {
			if(sender.hasPermission("staff.power.testspigot")) {
				sender.sendMessage(ChatColor.GREEN+"Spigot is active!");
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to run this command");
			}
		}
		return false;
	}

}
