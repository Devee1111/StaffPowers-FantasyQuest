package me.Devee1111.Bungee;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
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
	//Creating API object
	private LuckPermsApi luckperms;
	//Storing data for other classes
	HashMap<String, String> chat = new HashMap<String, String>();
	
	@Override
	public void onEnable() {
		//creating out instance
		setInstance(this);
		
		//Setting up luck perms API
		if(!setupLuckPerms()) {
			failedLuckApi();
		}
		
		//Loading our configuration file
		loadConfig();
		
		//Alerting that we're still under beta
		alertDevelopment();
		
		//Setting up our SQL data
		StaffSql.loadSqlFile();

		//Clearing our data on plugin startup
		chat.clear();
		
		//registering a plugin channel for messaging our spigot plugins
		getProxy().registerChannel("staff:powers");
		
		//Creating our command executers TODO this section needs to be recoded
		getProxy().getPluginManager().registerCommand(this, new StaffPowersCmdOp());
		getProxy().getPluginManager().registerCommand(this, new StaffPowersCmdDeop());
		getProxy().getPluginManager().registerCommand(this, new StaffPowersCmdTest());
		getProxy().getPluginManager().registerCommand(this, new StaffPowersCmdReload());
		getProxy().getPluginManager().registerCommand(this, new StaffPowersCmdGod());
		//Newly created (no redo should be needed)
		getProxy().getPluginManager().registerCommand(this, new StaffPowersCmdSc());
		getProxy().getPluginManager().registerCommand(this, new StaffPowersCmdAc());
		//TODO finish writing demote
		getProxy().getPluginManager().registerCommand(this, new StaffCmdDemote());
		getProxy().getPluginManager().registerCommand(this, new StaffCmdOnduty());
		getProxy().getPluginManager().registerCommand(this, new StaffCmdOffduty());
		
		//Creating our Listener TODO this listener needs to be redone
		getProxy().getPluginManager().registerListener(this, new StaffPowersListener());
		//Listeners past this point are up to date
		getProxy().getPluginManager().registerListener(this, new StaffPowersLsnChat());
		getProxy().getPluginManager().registerListener(this, new StaffLsnRankSync());
		
	}
	
	/*
	 * Our default values, at the top for easier access
	 */
	public void defaults() {
		newDefault("version", curver);
		newDefault("options.prefix","&8[&dStaff&8]");
		//OnJoin leave section
		newDefault("options.onjoinleave.enabled.join",true);
		newDefault("options.onjoinleave.enabled.leave",true);
		newDefault("options.onjoinleave.enabled.change",true);
		newDefault("options.onjoinleave.messages.join","&8[&a+&8] (&cStaff&8) &d%player% &ehas joined &a%server%&e.");
		newDefault("options.onjoinleave.messages.leave","&8[&c-&8] (&cStaff&8) &d%player% &eleft the network.");
		newDefault("options.onjoinleave.messages.change","&8[&e*&8] &8(&cStaff&8) &d%player% &eswitched to &a%server%&e.");
		//Make Op
		newDefault("MakeOp.sendMessageOnAttempt",true);
		newDefault("MakeOp.message","%prefix% &aAttempting to make operator...");
		//Messages
		newDefault("messages.nopermission","prefix% &cYou do not have permission to do this.");
		newDefault("messages.reloadedConfig","%prefix% &aYou have reloaded the staffpowers (bungee) configuration!");
		newDefault("messages.playersonly","%prefix% &cOnly players can send this command.");
		newDefault("messages.wrongargs","%prefix% &cError! Wrong arguements given!");
		//Make God
		newDefault("MakeGod.sendMessageOnAttempt",false);
		newDefault("MakeGod.message","%prefix% &aAttempting to apply god mode.");
		//Chat section
		newDefault("chat.format.staff","&8(&cStaff&8) &f%player% &7» &e");
		newDefault("chat.format.admin","&8(&bAdmin&8) &f%player% &7» &e");
		newDefault("chat.messages.toggled-on.staff","%prefix% &3Staff chat &aenabled&c.");
		newDefault("chat.messages.toggled-on.admin","%prefix% &3Admin chat &aenabled&c.");
		newDefault("chat.messages.toggled-off.staff","%prefix% &3Staff chat &cdisabled&3.");
		newDefault("chat.messages.toggled-off.admin","%prefix% &3Admin chat &cdisabled&3.");
		newDefault("chat.messages.playersonly","%prefix% &cOnly players can toggle chat.");
		newDefault("chat.messages.nopermtoggle","%prefix% &cYou do not have permission to toggle the staff chat.");
		//TODO add rank priority here
		//TODO add staff on duty / off messages here
	}
	
	/*
	 * SECTION: LuckPermsAPI
	 * Created by - Devee1111 on 8/5/19
	 */
	
	public void failedLuckApi() {
		getLogger().log(Level.SEVERE,"##################################");
		getLogger().log(Level.SEVERE,"#                                #");
		getLogger().log(Level.SEVERE,"#          ERROR REPORT          #");
		getLogger().log(Level.SEVERE,"#                                #");
		getLogger().log(Level.SEVERE,"#          > Details <           #");
		getLogger().log(Level.SEVERE,"#-> FAILED TO LOAD LUCKPERMS API #");
		getLogger().log(Level.SEVERE,"#                                #");
		getLogger().log(Level.SEVERE,"##################################");
	}
	
	public boolean setupLuckPerms() {
		try {
			//Gets the API loaded before it's used, and get's it ready.
			luckperms = LuckPerms.getApi();
			return true;
		} catch(IllegalStateException ex) {
			luckperms = null;
			return false;
		}
	}
	
	public LuckPermsApi getLuckApi() {
		return luckperms;
	}
	
	public String getRank(String uuid) {
		String rank = "default";
		if(luckperms != null) {
			rank = luckperms.getUser(UUID.fromString(uuid)).getPrimaryGroup();
		}
		//TODO include priority system for multigrouped players
		return rank;
	}
	
	public Boolean getDutyStatus(String uuid) {
		return(StaffSql.onDuty(uuid));
	}
	//TODO setup off duty perms
	public String getRealRank(String uuid) {
		String rank = "";
		if(luckperms != null) {
			rank = luckperms.getUser(UUID.fromString(uuid)).getPrimaryGroup();
			if(StaffSql.onDuty(uuid)) {
			//	rank = rank + "od";
			}
		}
		return rank;
	}
	
	
	/*
	 * SECTION: Staff chat <Data>
	 * CREATED BY - Devee1111 on 7/20/19
	 * DETAILS:
	 * - Channels: general, admin
	 * - Permisssions are in classes
	 */
	String general = "general";
	String admin = "admin";
	
	public HashMap<String, String> getChatData() {
		return chat;
	}
	
	public String getChannel(ProxiedPlayer pp) {
		String channel = ""; 
		String uuid = pp.getUniqueId().toString();
		if(chat.containsKey(uuid)) {
			channel = chat.get(uuid);
		}
		return channel;
	}
	
	public boolean isToggled(ProxiedPlayer pp) {
		boolean toggled = false;
		String uuid = pp.getUniqueId().toString();
		if(chat.containsKey(uuid)) {
			toggled = true;
		}
		return toggled;
		
	}
	
	public void setToggled(String channel, ProxiedPlayer pp) {
		String uuid = pp.getUniqueId().toString();
		String path = "chat.messages.toggled-on.";
		if(channel.equals(general)) {
			path = path + "staff";
		} else {
			path = path + "admin";
		}
		String alert = getString(path);
		alert = prefix(alert);
		TextComponent tosend = new TextComponent(color(alert));
		pp.sendMessage(tosend);
		chat.put(uuid, channel);
	}
	
	public void unToggle(ProxiedPlayer pp) {
		String uuid = pp.getUniqueId().toString();
		String path = "chat.messages.toggled-off.";
		String channel = chat.get(uuid);
		if(channel.equals(general)) {
			path = path + "staff";
		} else {
			path = path + "admin";
		}
		String alert = getString(path);
		alert = prefix(alert);
		TextComponent tosend = new TextComponent(color(alert));
		pp.sendMessage(tosend);
		chat.remove(uuid);
	}
	
	
	
	
	/*
	 * SECTION: configAid
	 * Created - Devee1111 7/19/20
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
		getLogger().log(Level.SEVERE,"#-> PATH DOES NOT EXIST          #");
		getLogger().log(Level.SEVERE,"#          > Section <           #");
		getLogger().log(Level.SEVERE,"#-> configAid."+method+"()");
		getLogger().log(Level.SEVERE,"#           > Path <             #");
		getLogger().log(Level.SEVERE,"#-> "+path);
		getLogger().log(Level.SEVERE,"##################################");
	}
	
	//Saves the config to disk
	public void saveConfig() {
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
		if(!config.contains(path)) {
			configError(path,"createMessage");
		}
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
	
	public TextComponent nopermission() {
		String tosend = config.getString("messages.nopermission");
		tosend = tosend.replace("%prefix%", config.getString("options.prefix"));
		tosend = ChatColor.translateAlternateColorCodes('&', tosend);
		TextComponent mes = new TextComponent(tosend);
		return mes;
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
	
	public void sendRankMessage(ProxiedPlayer pp, String rank) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("GiveRank");
		out.writeUTF(pp.getUniqueId().toString());
		out.writeUTF(rank);
		pp.getServer().sendData("staff:powers",out.toByteArray());
	}
	
	public void log(String message) {
		getLogger().log(Level.WARNING, message);
	}
	
	public void logChat(String tosend) {
		getLogger().log(Level.INFO,tosend);
		//TODO [Later] - Have it save staff chat to a local txt file
	}
	
	/*
	 * Startup methods stored here along with method that aid in the matter
	 */
	
	//Message to console with clear instructions && warnings
	public void alertDevelopment() {
		//56 spaces between each # in empty lines
		getLogger().log(Level.INFO,"##########################################################");
		getLogger().log(Level.INFO,"#                                                        #");
		getLogger().log(Level.INFO,"#                    > Staff Powers <                    #");
		getLogger().log(Level.INFO,"# Be aware this plugin is still under heavy development! #");
		getLogger().log(Level.INFO,"# Please also install this plugin on all spigot servers  #");
		getLogger().log(Level.INFO,"# connected for it to fully work and keep it up to date  #");
		getLogger().log(Level.INFO,"# with the latest jar file found withen the bungee ftp.  #");
		getLogger().log(Level.INFO,"#                       - Daddy Dev                      #");
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
				InputStream def = getResourceAsStream("bungeeconfig.yml");
				//File defaultFile = new File(getClass().getResource("bungeeconfig.yml").getPath());
				OutputStream out = new FileOutputStream(configFile);
				ByteStreams.copy(def, out);
				//defaultFile.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
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
		//Creating a default configuraiton if we're not saving a new .yml (Already existed)
		loadDefaultConfig();
	}
	//Used to create our default config, for simplicy we use some recreated spigot methods
	public void loadDefaultConfig() {
		if(config.contains("version")) {
			version = config.getDouble("version");
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
