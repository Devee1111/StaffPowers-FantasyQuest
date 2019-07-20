package me.Devee1111.Bungee;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

public class StaffPowers extends Plugin {
	
	private static StaffPowers instance;
	public Configuration config;
	private double version;
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
		
		//Creating our command executers TODO this section needs to be recoded
		getProxy().getPluginManager().registerCommand(this, new StaffPowersCmdOp());
		getProxy().getPluginManager().registerCommand(this, new StaffPowersCmdTest());
		getProxy().getPluginManager().registerCommand(this, new StaffPowersCmdReload());
		getProxy().getPluginManager().registerCommand(this, new StaffPowersCmdGod());
		//Newly created (no redo should be needed)
		
		//Creating our Listener TODO this listener needs to be redone
		getProxy().getPluginManager().registerListener(this, new StaffPowersListener());
		
	}
	
	/*
	 * Our default values, at the top for easier access
	 */
	public void defaults() {
		newDefault("version", curver);
		newDefault("options.prefix","&8[&dStaff&8]");
		newDefault("options.onjoinleave.enabled.join",true);
		newDefault("options.onjoinleave.enabled.leave",true);
		newDefault("options.onjoinleave.messages.join","&8[&a+&8] (&cStaff&8) &d%player% &ehas joined the network.");
		newDefault("options.onjoinleave.messages.leave","&8[&c-&8] (&cStaff&8) &d%player% &eleft the network.");
		newDefault("MakeOp.sendMessageOnAttempt",true);
		newDefault("MakeOp.message","%prefix% &aAttempting to make operator...");
		newDefault("messages.nopermission","prefix% &cYou do not have permission to do this.");
		newDefault("messages.reloadedConfig","%prefix% &aYou have reloaded the staffpowers (bungee) configuration!");
		newDefault("messages.playersonly","%prefix% &cOnly players can send this command.");
		newDefault("messages.wrongargs","%prefix% &cError! Wrong arguements given!");
		newDefault("MakeGod.sendMessageOnAttempt",false);
		newDefault("MakeGod.message","%prefix% &aAttempting to apply god mode.");
		//New values go here to add to yml
	}
	
	/*
	 * Section dedicated to making the config easier to use
	 */
	public boolean getBoolean(String path) {
		boolean tosend = false;
		if(config.contains(path)) {
			tosend = config.getBoolean(path);
		} else {
			configError(path,"getBoolean");
		}
		return tosend;
	}
	
	public String getString(String path) {
		String tosend = "";
		if(config.contains(path)) {
			tosend = config.getString(path);
		} else {
			configError(path,"getBoolean");
		}
		return tosend;
	}
	
	
	public void configError(String path, String method) {
		getLogger().log(Level.SEVERE,"##################################");
		getLogger().log(Level.SEVERE,"#                                #");
		getLogger().log(Level.SEVERE,"#          ERROR REPORT          #");
		getLogger().log(Level.SEVERE,"#                                #");
		getLogger().log(Level.SEVERE,"#          > Details <           #");
		getLogger().log(Level.SEVERE,"# -> PATH DOES NOT EXIST         #");
		getLogger().log(Level.SEVERE,"#          > Section <           #");
		getLogger().log(Level.SEVERE,"#-> configAid."+method+"()");
		getLogger().log(Level.SEVERE,"#           > Path <             #");
		getLogger().log(Level.SEVERE,"#-> "+path);
		getLogger().log(Level.SEVERE,"##################################");
	}
	
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
	 * Message modification / Shortcuts - Methods designed to make messages easier
	 */
	public String createMessage(String path) {
		String tosend = config.getString(path);
		tosend = prefix(tosend);
		tosend = ChatColor.translateAlternateColorCodes('&', tosend);
		return tosend;
	}
	
	public String color(String message) {
		message = ChatColor.translateAlternateColorCodes('&', message);
		return message;
	}
	
	public String prefix(String message) {
		message = message.replace("%prefix%", config.getString("options.prefix"));
		return message;
	}
	
	public String nopermission() {
		String tosend = config.getString("messages.nopermission");
		tosend = tosend.replace("%prefix%", config.getString("options.prefix"));
		tosend = ChatColor.translateAlternateColorCodes('&', tosend);
		return tosend;
	}
	
	public String playersonly() {
		String tosend = config.getString("messages.playersonly");
		tosend = tosend.replace("%prefix%", config.getString("options.prefix"));
		tosend = ChatColor.translateAlternateColorCodes('&', tosend);
		return tosend;
	}
	
	public String wrongargs() {
		String tosend = config.getString("messages.wrongargs");
		tosend = tosend.replace("%prefix%", config.getString("options.prefix"));
		tosend = ChatColor.translateAlternateColorCodes('&', tosend);
		return tosend;
	}
	
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
	
	/*
	 * General help/work methods, used by other classes that need the main class
	 */
	
	public void sendPluginCommand(ProxiedPlayer p, String data) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(data);
		out.writeUTF(p.getName());
		p.getServer().sendData("staff:powers", out.toByteArray());
	}
	
	public void log(String message) {
		getLogger().log(Level.WARNING, message);
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
	public static void setInstance(StaffPowers instance) {
		StaffPowers.instance = instance;
	}
	//Used for instancing the main class allowing other classes to access our methods, and configuration.
	public static StaffPowers getInstance() {
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
			/*
			 * TODO File had error - delete and save our yml
			 */
		}
		//Getting current configuration from OUR jar file, this an input stream since it's from our resoures and no disk
		InputStream in = getResourceAsStream("bungeeconfig.yml");
		Configuration defaultconfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(in);
		curver = defaultconfig.getDouble("version");
		//Just a little message to console that configuration will be changed.
		if(curver != version) {
			log("New configuration version, adding new values. If problems occur please reset the config.yml in staffpowers folder.");
			defaults();
		}
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