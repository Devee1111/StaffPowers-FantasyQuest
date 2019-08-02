package me.Devee1111.Bungee;


import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class StaffPowersLsnChat implements Listener {
	
	private StaffPowers inst = StaffPowers.getInstance();
	
	@EventHandler
	public void onChat(ChatEvent e) {
		if(e.isCancelled() == true) {
			return;
		}
		//TODO This is known not to work, might need to just check for a /
		if(e.isCommand() == true) {
			return;
		}
		if(e.getSender() instanceof ProxiedPlayer) {
			ProxiedPlayer pp = (ProxiedPlayer) e.getSender();
			if(inst.isToggled(pp)) {
				e.setCancelled(true);
				String channel = inst.getChannel(pp);
				String permission = "staff.chat."+channel;
				String formatpath = getFormatPath(channel);
				String format = inst.getString(formatpath);
				format = inst.prefix(format);
				format = format.replace("%player%", pp.getName());
				String tosend = format + e.getMessage();
				tosend = inst.color(tosend);
				inst.logChat(tosend);
				BaseComponent [] chat = TextComponent.fromLegacyText(tosend);
				for(ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
					if(online.hasPermission(permission)) {
						online.sendMessage(chat);
					}
				}
			}
		}
	}
	
	private String getFormatPath(String channel) {
		String path = "chat.format.";
		if(channel == "general") {
			path = path + "staff";
		} else {
			path = path + "admin";
		}
				
		return path;
	}
	
	@EventHandler
	public void chatOnJoin(PostLoginEvent e) {
		if(inst.isToggled(e.getPlayer()) == true) {
			inst.unToggle(e.getPlayer());
		}
	}
	
	@EventHandler
	public void chatOnLeave(PlayerDisconnectEvent e) {
		if(inst.isToggled(e.getPlayer()) == true) {
			inst.unToggle(e.getPlayer());
		}
	}
	//TODO Remove player from toggled onjoin/leave

}
