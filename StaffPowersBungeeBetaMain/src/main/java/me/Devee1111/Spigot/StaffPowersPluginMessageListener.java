package me.Devee1111.Spigot;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;


public class StaffPowersPluginMessageListener  implements PluginMessageListener {
	
	
	/*
	 * Created by - Devee1111 on 9/22/19
	 * TODO make plugin messages use UUID
	 * TODO make messages use message aid methods instead
	 */

	private StaffPowers inst = StaffPowers.getInstance();
	
	
	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] message) {
		//We only care about our channel
		if(!channel.equals("staff:powers")) {
			return;
		}
		//Simplify the data
		ByteArrayDataInput input = ByteStreams.newDataInput(message);
		String subchannel = input.readUTF();
		
		//TODO Check for online player better
		if(subchannel.equals("CommandOp")) {
			String playerName = input.readUTF();
			for(Player online : Bukkit.getOnlinePlayers()) {
				if(online.getName().equals(playerName)) {
					op(online);
					break;
				}
			}
		}
		
		//TODO check for online player better
		if(subchannel.equals("CommandDeop")) {
			String playerName = input.readUTF();
			for(Player online : Bukkit.getOnlinePlayers()) {
				if(online.getName().equals(playerName)) {
					deop(online);
					break;
				}
			}
		}
		
		//TODO check for online player better
		if(subchannel.equals("CommandGod")) {
			String playerName = input.readUTF();
			for(Player online : Bukkit.getOnlinePlayers()) {
				if(online.getName().equals(playerName)) {
					setGod(online);
					break;
				}
			}
			
		}
		
		//TODO add more commands
		
	}
	
	public void setGod(Player p) {
		if(inst.isGod(p)) {
			inst.setGod(p, false);
		} else {
			inst.setGod(p, true);
			p.setSaturation(30);
			p.setHealth(p.getHealthScale());
			p.setFireTicks(0);
			//TODO remove harmful effects
		}
	}
	
	//TODO alert console of player opping/deopping
	public void op(Player p) {
		if(p.isOp() == true) {
			inst.sendMessage(p, "alreadyOp");
		} else {
			p.setOp(true);
			inst.sendMessage(p, "madeOp");
		}
	}
	
	public void deop(Player p) {
		if(p.isOp() == false) {
			inst.sendMessage(p, "alreadyDeop");
		} else {
			p.setOp(false);
			inst.sendMessage(p, "madeDeop");
		}
	}
	
	//TODO Find out what this was used for 
	//This is for connecting a player to another server
	public void connect(Player p, String server) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(server);
		p.sendPluginMessage(inst, "BungeeCord", out.toByteArray());
	}
	
	
}
