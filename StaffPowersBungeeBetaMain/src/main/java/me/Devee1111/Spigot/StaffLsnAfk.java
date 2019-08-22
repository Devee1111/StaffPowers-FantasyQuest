package me.Devee1111.Spigot;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.earth2me.essentials.Essentials;
import com.lishid.openinv.OpenInv;

import net.ess3.api.events.AfkStatusChangeEvent;
import net.ess3.api.events.VanishStatusChangeEvent;

public class StaffLsnAfk implements Listener {
	
	/*
	 * Created by - Devee1111 on 8/21/19
	 * TODO add multigroup support
	 * TODO look into adding messages
	 * TODO remove phantoms when vanished
	 */
	
	StaffPowers inst;
	public StaffLsnAfk(StaffPowers inst) {
		this.inst = inst;
	}
	
	//Getting our APIs
	Essentials ess = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
	OpenInv oi = (OpenInv) Bukkit.getServer().getPluginManager().getPlugin("OpenInv");

	//Storing users we changed
	List<String> changed = new ArrayList<String>();
	
	//Storing users we toggled silentchest for
	List<String> silented = new ArrayList<String>();
	
	@EventHandler
	public void onAfk(AfkStatusChangeEvent e) {
		
		if(inst.getConfig().getBoolean("essentials.vanishAfkStaff") == false) {
			//this is if they change the config value during runtime, removed changed players
			for(String player : changed) {
				ess.getUser(player).setVanished(false);
			}
			return;
		}
		
		if(e.getValue() == false) {
			if(!changed.contains(e.getAffected().getBase().getUniqueId().toString())) {
				return;
			}
			e.getAffected().setVanished(false);
			changed.remove(e.getAffected().getBase().getUniqueId().toString());
			return;
		}
		
		if(e.getAffected().isVanished()) {
			return;
		}
		
		for(String rank : inst.config.getStringList("ranks")) {
			if(inst.hasRank(rank, e.getAffected().getBase())) {
				e.getAffected().setVanished(true);
				changed.add(e.getAffected().getBase().getUniqueId().toString());
				break;
			}
		}
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onVanish(VanishStatusChangeEvent e) {
		
		if(e.getValue() == false) {
			if(silented.contains(e.getAffected().getBase().getUniqueId().toString())) {
				silented.remove(e.getAffected().getBase().getUniqueId().toString());
				oi.setPlayerSilentChestStatus((OfflinePlayer) e.getAffected().getBase(), false);
			}
			return;
		}
		
		if(oi.getPlayerSilentChestStatus((OfflinePlayer) e.getAffected().getBase()) == true) {
			//Removing changed players if config is reloaded or they toggled during vanish
			if(silented.contains(e.getAffected().getBase().getUniqueId().toString())) {
				silented.remove(e.getAffected().getBase().getUniqueId().toString());
			}
			return;
		}
		
		oi.setPlayerSilentChestStatus((OfflinePlayer) e.getAffected().getBase(), true);
		silented.add(e.getAffected().getBase().getUniqueId().toString());
		
		
	}

}
