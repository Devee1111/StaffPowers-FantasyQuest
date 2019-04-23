package me.Devee1111.Bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class StaffPowersBungeeListener  implements Listener {
	
	/*
	 * This class has yet to be redone after the rework on the code, however it should still work fine.
	 */
	
	private StaffPowersBungeeMain inst = StaffPowersBungeeMain.getInstance();
	
	@EventHandler
	public void onPlayerJoin(ServerConnectEvent e) {
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
	public void onPlayerLeave(ServerDisconnectEvent e) {
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

}