package me.Devee1111.Spigot;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class StaffPowersLsnGod implements Listener {
	
	/*
	 * Created by - Devee1111 on ?
	 * TODO Redo class messages
	 */
	StaffPowers inst;
	public StaffPowersLsnGod(StaffPowers p) {
		this.inst = p;
	}


	@EventHandler
	public void onHunger(FoodLevelChangeEvent e) {
		if(e.getEntity() instanceof Player) {
			if(inst.config.getBoolean("god.disableHunger") == true) {
				Player p = (Player) e.getEntity();
				if(isGod(p)) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			if(inst.config.getBoolean("god.disableDamage") == true) {
				Player p = (Player) e.getEntity();
				if(isGod(p) == true) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		if(e.getEntity() instanceof Player) {
			if(inst.config.getBoolean("god.disableDeath") == true) {
				Player p = (Player) e.getEntity();
				if(isGod(p) == true) {
					p.setHealth(p.getHealthScale());
					p.setFoodLevel(20);
					p.setFireTicks(0);
					//TODO Remove harmful effects
					if(inst.config.getBoolean("god.messageGod") == true) {
						inst.sendMessage(p, "god.messageToGod");
					}
					if(e.getEntity().getLastDamageCause().getEntity() instanceof Player) {
						if(inst.config.getBoolean("god.messageAttacker") == true) {
							Player attacker = (Player) e.getEntity().getLastDamageCause().getEntity();
							inst.sendMessage(attacker, "god.messageToAttacker");
						}
					}
					if(inst.config.getBoolean("god.messageEveryone") == true) {
						String tosend = inst.createMessage("god.messageToEveryone");
						tosend = tosend.replace("%player%", p.getName());
						tosend = inst.color(tosend);
						for(Player online : Bukkit.getOnlinePlayers()) {
							online.sendMessage(tosend);
						}
					}
				}
			}
		}
	}
	
	
	private boolean isGod(Player p) {
		HashMap<String, String> data = inst.getGodData();
		if(data.containsKey(p.getUniqueId().toString())) {
			return true;
		}
		return false;
	}

}
