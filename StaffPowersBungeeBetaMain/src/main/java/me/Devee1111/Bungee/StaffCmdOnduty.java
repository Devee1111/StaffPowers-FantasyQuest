package me.Devee1111.Bungee;


import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class StaffCmdOnduty extends Command {

	/*
	 * Created by - Devee1111 on 8/8/19
	 * Permissions:
	 * - staff.power.break
	 * - staff.power.break.notify
	 */
	
	StaffPowers inst = StaffPowers.getInstance();
	
	private String perm = "staff.power.break";
	private String notify = "staff.power.break.notify";
	
	public StaffCmdOnduty() {
		super("onduty", "", "od");
	}
	
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!sender.hasPermission(perm)) {
			sender.sendMessage(inst.nopermission());
			return;
		}
		
		//TODO allow console support to toggle for others
		if(!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(new TextComponent(inst.playersonly()));
			return;
		}
		
		final ProxiedPlayer pp = (ProxiedPlayer) sender;
				
		if(StaffSql.onDuty(pp.getUniqueId().toString())) {
			TextComponent tosend = new TextComponent(inst.color(inst.createMessage("break.on-duty.nothing-changed").replace("%player%", pp.getName())));
			pp.sendMessage(tosend);
			return;
		}
		
		
		StaffSql.setDuty(pp.getUniqueId().toString(), true);
		
		//Sending to staff about the duty change
		pp.sendMessage(new TextComponent(inst.color(inst.createMessage("break.on-duty.to-player").replace("%player%", pp.getName()))));
		
		TextComponent tosend = new TextComponent(inst.color(inst.createMessage("break.on-duty.to-staff").replace("%player%", pp.getName())));
		for(ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
			if(online.hasPermission(notify)) {
				if(!online.getName().equals(pp.getName())) {
					online.sendMessage(tosend);
				}
			}
		}
		
		for(ServerInfo server : inst.getProxy().getServers().values()) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("GiveRank");
			out.writeUTF(pp.getUniqueId().toString());
			out.writeUTF(inst.getRealRank(pp.getUniqueId().toString()));
			server.sendData("staff:powers",out.toByteArray());
		}

	}

}
