package me.Devee1111.Bungee;

import java.util.concurrent.TimeUnit;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class StaffLsnRankSync implements Listener {
	
	private StaffPowers inst = StaffPowers.getInstance();
	
	/*
	 * TODO 
	 * - Add support for multigroup users
	 */
	
	
	//Sync the rank for when they login
	@EventHandler
	public void onJoinSync(PostLoginEvent e) {
		//Creating delayed task, login event doesn't like plugin messaging right away
		final String uuid = e.getPlayer().getUniqueId().toString();
		ProxyServer.getInstance().getScheduler().schedule(inst, new Runnable() {
			@Override
			public void run() {
				for(ServerInfo server : inst.getProxy().getServers().values()) {
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					if(StaffSql.onDuty(uuid)) {
						out.writeUTF("GiveRank");
					} else {
						out.writeUTF("TakeRank");
					}
					out.writeUTF(uuid);
					out.writeUTF(inst.getRealRank(uuid));
					server.sendData("staff:powers",out.toByteArray());
				}
			}
		}, 3, TimeUnit.SECONDS);
	}

}
