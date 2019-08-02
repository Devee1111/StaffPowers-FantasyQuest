package me.Devee1111.Bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class StaffPowersListener  implements Listener {
	
/*
 * Created by - Devee1111 on ?
 * Recoded by - Devee1111 on 8/2/19
 */
	
	private StaffPowers inst = StaffPowers.getInstance();
	//It seems switch event is called even when they just join, so we use this as a get around atm
	private String justjoined = "";
	
	@EventHandler
	public void onPlayerJoin(PostLoginEvent e) {
		if(!e.getPlayer().hasPermission("staff.power.onjoinleave.alert")) {
			return;
		}
		if(inst.config.getBoolean("options.onjoinleave.enabled.join") == false) {
			return;
		}
		
		justjoined = e.getPlayer().getName();

		String message = inst.config.getString("options.onjoinleave.messages.join");
		message = message.replace("%player%", e.getPlayer().getName());
		for(ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
			if(online.hasPermission("staff.power.onjoinleave.notify")) {
				online.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));
			}
		}

		
		//Storing for playerdata 
		if(StaffSql.playerExists(e.getPlayer()) == false) {
			StaffSql.newPlayer(e.getPlayer());
		} else {
			StaffSql.updateName(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerDisconnectEvent e) {
		if(!e.getPlayer().hasPermission("staff.power.onjoinleave.alert")) {
			return;
		}
		if(inst.config.getBoolean("options.onjoinleave.enabled.leave") == false) {
			return;
		}
		
		String message = inst.config.getString("options.onjoinleave.messages.leave");
		message = message.replace("%player%", e.getPlayer().getName());
		for(ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
			if(online.hasPermission("staff.power.onjoinleave.notify")) {
				online.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));
			}
		}
	}
	
	@EventHandler
	public void onServerChange(ServerConnectedEvent e) { 
		if(!e.getPlayer().hasPermission("staff.power.onjoinleave.alert")) {
			return;
		}
		if(!inst.config.getBoolean("options.onjoinleave.enabled.change") == true) {
			return;
		}
		if(justjoined.equals(e.getPlayer().getName())) {
			justjoined = "";
			return;
		}

		String tomake = inst.config.getString("options.onjoinleave.messages.change");
		tomake = tomake.replace("%player%", e.getPlayer().getName());
		tomake = tomake.replace("%server%", e.getPlayer().getServer().getInfo().getName());
		tomake = ChatColor.translateAlternateColorCodes('&', tomake);
		TextComponent tosend = new TextComponent(tomake);
		
		for(ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
			if(online.hasPermission("staff.power.onjoinleave.notify")) {
				online.sendMessage(tosend);
			}
		}
	}

}