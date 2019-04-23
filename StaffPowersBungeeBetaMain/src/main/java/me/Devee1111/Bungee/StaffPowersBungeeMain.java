package me.Devee1111.Bungee;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class StaffPowersBungeeMain extends Plugin {
	
	private static StaffPowersBungeeMain instance;
	public Configuration config;
	private static HashMap<String,String[]> data = new HashMap<>();
	private double version = 0;
	private double curver;
	
	@Override
	public void onEnable() {
		//creating out instance
		setInstance(this);
		//Loading our configuration file
		loadConfig();
		//Alerting that we're still under beta
		alertDevelopment();
		//registering a plugin channel for messaging our spigot plugins
		getProxy().registerChannel("staff:powers");
		//Creating our command executers
		getProxy().getPluginManager().registerCommand(this, new StaffPowersBungeeCommandOp());
		getProxy().getPluginManager().registerCommand(this, new StaffPowersBungeeCommandTest());
		getProxy().getPluginManager().registerCommand(this, new StaffPowersBungeeCommandReload());
		getProxy().getPluginManager().registerCommand(this, new StaffPowersBungeeCommandGod());
		//Creating our Listener
		getProxy().getPluginManager().registerListener(this, new StaffPowersBungeeListener());
		
	}
	/*
	 * General methods, used for general fuctionality
	 */
	//Saves the config to disk
	public void saveConfig() {
		//Now saving it
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder().getAbsolutePath(), "config.yml"));
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	//Loads the configuration from the current disk, if loaded incorrectly console with spit out errors when they occur
	public void reloadConfig() {
		try {
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder().getAbsolutePath(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
			getLogger().log(Level.SEVERE,"##################################");
			getLogger().log(Level.SEVERE,"#                                #");
			getLogger().log(Level.SEVERE,"# Failed to reload configuration #");
			getLogger().log(Level.SEVERE,"#                                #");
			getLogger().log(Level.SEVERE,"##################################");
		}
	}
	
	/*
	 * General help/work methods, used by other classes
	 */
	//This is a hashmap for data, might remove later, thought it would be helpful since we have on in the spigot side
	public static HashMap<String, String[]> getData() {
		return data;
	}
	/*
	 * sendMessage() method is for simplicity, preventing us from needing to repeat the same code.
	 */
	//This is unique, just in case we need to add more placeholders to the message it just gets the jist of the message completed
	public String createMessage(String path) {
		String tosend = config.getString(path);
		tosend = prefix(tosend);
		tosend = ChatColor.translateAlternateColorCodes('&', tosend);
		return tosend;
	}
	//These arguements are met in our command classes, this prevents us from having to add 4 extra lines of code for cleaner looks
	public void sendMessage(CommandSender sender, String path) {
		String tosend = config.getString(path);
		tosend = prefix(tosend);
		if(sender instanceof ProxiedPlayer) {
			tosend = tosend.replace("%player%", sender.getName());
		}
		tosend = ChatColor.translateAlternateColorCodes('&', tosend);
		sender.sendMessage(new TextComponent(tosend));
	}
	public void sendMessage(ProxiedPlayer p, String path) {
		String tosend = config.getString(path);
		tosend = prefix(tosend);
		tosend = tosend.replace("%player%",p.getName());
		tosend = ChatColor.translateAlternateColorCodes('&', tosend);
		p.sendMessage(new TextComponent(tosend));
	}
	
	public void sendPluginCommand(ProxiedPlayer p, String data) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(data);
		out.writeUTF(p.getName());
		p.getServer().sendData("staff:powers", out.toByteArray());
	}
	public void log(String message) {
		getLogger().log(Level.WARNING, message);
	}
	
	public String color(String message) {
		message = ChatColor.translateAlternateColorCodes('&', message);
		return message;
	}
	
	public String prefix(String message) {
		message = message.replace("%prefix%", config.getString("options.prefix"));
		return message;
	}
	/*
	 * Startup methods stored here along with method that aid in the matter
	 */
	//Message to console with clear instructions && warnings
	public void alertDevelopment() {
		getLogger().log(Level.INFO,"##########################################################");
		getLogger().log(Level.INFO,"#                                                        #");
		getLogger().log(Level.INFO,"#                                                        #");
		getLogger().log(Level.INFO,"# Be aware this plugin is still under heavy development! #");
		getLogger().log(Level.INFO,"# Please also install this plugin on all spigot servers  #");
		getLogger().log(Level.INFO,"# connected for it to fully work and keep it up to date  #");
		getLogger().log(Level.INFO,"# with the latest jar file found withen the bungee ftp.  #");
		getLogger().log(Level.INFO,"#                                                        #");
		getLogger().log(Level.INFO,"#                                                        #");
		getLogger().log(Level.INFO,"##########################################################");
	}
	//Create an instance for us to later reference
	public static void setInstance(StaffPowersBungeeMain instance) {
		StaffPowersBungeeMain.instance = instance;
	}
	//Used for instancing the main class allowing other classes to access our methods, and configuration.
	public static StaffPowersBungeeMain getInstance() {
		return instance;
	}
	//Load the configuration, if needed, create and setup the configuration as well
	public void loadConfig() {
		//Creating our default configuration - It will still be empty
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		File configFile = new File(getDataFolder().getAbsolutePath(), "config.yml");
		if(!configFile.exists()) {
			try {
				File defaultFile = new File(getClass().getResource("bungeeconfig.yml").getPath());
				defaultFile.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		//Creating a default configuraiton if we're not saving a new .yml (Already existed)
		loadDefaultConfig();
		//loading our configuration
		try {
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder().getAbsolutePath(), "config.yml"));
			
		} catch (IOException e) {
			getLogger().log(Level.SEVERE,"################################");
			getLogger().log(Level.SEVERE,"#                              #");
			getLogger().log(Level.SEVERE,"# Failed to load configuration #");
			getLogger().log(Level.SEVERE,"#                              #");
			getLogger().log(Level.SEVERE,"################################");
			e.printStackTrace();
		}
	}
	//Used to create our default config, for simplicy we use some recreated spigot methods
	public void loadDefaultConfig() {
		if(config.contains("version")) {
			version = config.getInt("version");
		} else {
			getLogger().log(Level.WARNING,"Failed to detect config version, resetting.");
		}
		//Getting current configuration from OUR jar file, this an input stream since it's from our resoures and no disk
		InputStream in = getResourceAsStream("bungeeconfig.yml");
		Configuration defaultconfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(in);
		curver = defaultconfig.getDouble("version");
		//Just a little message to console that configuration will be changed.
		if(curver != version) {
			log("New configuration version, adding new values. If problems occur please reset the config.yml in staffpowersbungeebeta folder.");
		}
		//Making our actual values
		newDefault("version", curver);
		newDefault("options.prefix","&8[&dStaffPowers&8]");
		newDefault("options.onjoinleave.enabled.join",true);
		newDefault("options.onjoinleave.enabled.leave",true);
		newDefault("options.onjoinleave.messages.join","&8[&a+&8] (&cStaff&8) &d%player% &ehas joined the network.");
		newDefault("options.onjoinleave.messages.leave","&8[&c-&8] (&cStaff&8) &d%player% &eleft the network.");
		newDefault("MakeOp.sendMessageOnAttempt",true);
		newDefault("MakeOp.message","%prefix% &aAttempting to make operator...");
		newDefault("messages.nopermission","prefix% &cYou do not have permission to do this.");
		newDefault("messages.reloadedConfig","%prefix% &aYou have reloaded the bungee staffpowers configuration!");
		newDefault("messages.playersonly","%prefix% &cOnly players can send this command.");
		newDefault("MakeGod.sendMessageOnAttempt",false);
		newDefault("MakeGod.message","%prefix% &aAttempting to apply god mode.");
	}
	//NewDefaults help create new paths that don't exist yet in an already generatoed config, if a new one is not generated
	public void newDefault(String path, String value) {
		if(!config.contains(path) || version != curver) {
			config.set(path, value);
			saveConfig();
		}
	}
	
	public void newDefault(String path, boolean value) {
		if(!config.contains(path) || version != curver) {
			config.set(path, value);
			saveConfig();
		}
	}
	
	public void newDefault(String path, double value) {
		if(!config.contains(path) || version != curver) {
			config.set(path, value);
			saveConfig();
		}
	}
}
/*
 * Note 1 - Get defaultconfig from our resources
 *		InputStream in = getResourceAsStream("bungeeconfig.yml");
 *		Configuration defaultconfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(in);
 * */
