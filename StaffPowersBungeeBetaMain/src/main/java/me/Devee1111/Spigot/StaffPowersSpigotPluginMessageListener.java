package me.Devee1111.Spigot;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;


public class StaffPowersSpigotPluginMessageListener  implements PluginMessageListener {
	

	private StaffPowersSpigotMain inst = StaffPowersSpigotMain.getInstance();
	
	
	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] message) {
		//We only care about our channel
		if(!channel.equals("staff:powers")) {
			return;
		}
		//Some stuff to help on how we process the data
		ByteArrayDataInput input = ByteStreams.newDataInput(message);
		String subchannel = input.readUTF();
		//If the message said to process it as CommandOp (permissions were checked on bungee)
		if(subchannel.equals("CommandOp")) {
			String playerName = input.readUTF();
			for(Player online : Bukkit.getOnlinePlayers()) {
				if(online.getName().equals(playerName)) {
					makeOp(online);
					break;
				}
			}
		}
		//Giving the user god
		if(subchannel.equals("CommandGod")) {
			String playerName = input.readUTF();
			for(Player online : Bukkit.getOnlinePlayers()) {
				if(online.getName().equals(playerName)) {
					makeGod(online);
					break;
				}
			}
			
		}
		//More commands coming soon
		if(subchannel.equals("giveRank")) {
			String playerName = input.readUTF();
			String rank = input.readUTF();
			for(Player online : Bukkit.getOnlinePlayers()) {
				if(online.getName().equals(playerName)) {
					giveRank(online);
					break;
				}
			}
		}
		
	}
	
	public void giveRank(Player p) {
		
	}
	
	public void makeGod(Player p) {
		HashMap<String, String[]> data = inst.getData();
		for(Entry<String, String[]> en : data.entrySet()) {
			if(en.getKey().equals("god")) {
				if(en.getValue().equals(p.getUniqueId().toString())) {
					en.setValue(null);
					inst.sendMessage(p,"messages.madeMortal");
					return;
				}
			}
		}
		//I'm not certain if this will work
		String[] newlist = data.get("god");
		newlist[newlist.length] = p.getUniqueId().toString();
		data.put("god", newlist);
		inst.sendMessage(p, "messages.madeGod");
	}
	
	public void makeOp(Player p) {
		if(p.isOp() == true) {
			String toSend = inst.config.getString("messages.alreadyOp");
			toSend = toSend.replace("%prefix%", inst.config.getString("options.prefix"));
			toSend = ChatColor.translateAlternateColorCodes('&', toSend);
			p.sendMessage(toSend);
		} else {
			p.setOp(true);
			String message = inst.config.getString("messages.madeOp");
			message = message.replace("%prefix%", inst.getConfig().getString("options.prefix"));
			message = ChatColor.translateAlternateColorCodes('&', message);
			p.sendMessage(message);
		}
	}
	
	//This is for connecting a player to another server
	public void connect(Player p, String server) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(server);
		p.sendPluginMessage(inst, "BungeeCord", out.toByteArray());
	}
	
	
}
