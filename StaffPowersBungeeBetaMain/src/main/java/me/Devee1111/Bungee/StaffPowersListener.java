package me.Devee1111.Bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class StaffPowersListener  implements Listener {
	
	/*
	 * This class has yet to be redone after the rework on the code, however it should still work fine.
	 */
	
	private StaffPowers inst = StaffPowers.getInstance();
	
	@EventHandler
	public void onPlayerJoin(PostLoginEvent e) {
		ProxiedPlayer p = e.getPlayer();
		if(p.hasPermission("staff.power.onjoinleave.alert")) {
			if(inst.config.getBoolean("options.onjoinleave.enabled.join") == true) {
				String message = inst.config.getString("options.onjoinleave.messages.join");
				message = message.replace("%player%", p.getName());
				for(ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
					if(online.hasPermission("staff.power.onjoinleave.notify")) {
						online.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerDisconnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		if(p.hasPermission("staff.power.onjoinleave.alert")) {
			if(inst.config.getBoolean("options.onjoinleave.enabled.leave") == true) {
				String message = inst.config.getString("options.onjoinleave.messages.leave");
				message = message.replace("%player%", p.getName());
				for(ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
					if(online.hasPermission("staff.power.onjoinleave.notify")) {
						online.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));
					}
				}
			}
		}
	}
	
	//Section created by Deveee1111 on 9/28/19
	@EventHandler
	public void onServerChange(ServerSwitchEvent e) { 
		if(!e.getPlayer().hasPermission("staff.power.onjoinleave.alert")) {
			return;
		}
		if(!inst.config.getBoolean("options.onjoinleave.enabled.change") == true) {
			return;
		}
		String tomake = inst.config.getString("options.onjoinleave.messages.change");
		tomake = tomake.replace("%player%", e.getPlayer().getName());
		tomake = tomake.replace("%server%", e.getPlayer().getServer().toString());
		tomake = ChatColor.translateAlternateColorCodes('&', tomake);
		TextComponent tosend = new TextComponent(tomake);
		
		for(ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
			if(online.hasPermission("staff.power.onjoinleave.notify")) {
				online.sendMessage(tosend);
			}
		}
	}

}