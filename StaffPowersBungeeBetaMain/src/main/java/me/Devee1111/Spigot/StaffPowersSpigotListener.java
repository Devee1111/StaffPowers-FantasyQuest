package me.Devee1111.Spigot;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class StaffPowersSpigotListener implements Listener {
	
	StaffPowersSpigotMain inst;
	private StaffPowersSpigotMain main = StaffPowersSpigotMain.getInstance();
	
	public StaffPowersSpigotListener(StaffPowersSpigotMain p) {
		this.inst = p;
	}


	@EventHandler
	public void onHunger(FoodLevelChangeEvent e) {
		if(e.getEntity() instanceof Player) {
			if(main.config.getBoolean("god.disableHunger") == true) {
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
			if(main.config.getBoolean("god.disableDamage") == true) {
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
			if(main.config.getBoolean("god.disableDeath") == true) {
				Player p = (Player) e.getEntity();
				if(isGod(p) == true) {
					p.setHealth(p.getHealthScale());
					p.setFoodLevel(20);
					p.setFireTicks(0);
					if(main.config.getBoolean("god.messageGod") == true) {
						main.sendMessage(p, "god.messageToGod");
					}
					if(e.getEntity().getLastDamageCause().getEntity() instanceof Player) {
						if(main.config.getBoolean("god.messageAttacker") == true) {
							Player attacker = (Player) e.getEntity().getLastDamageCause().getEntity();
							main.sendMessage(attacker, "god.messageToAttacker");
						}
					}
					if(main.config.getBoolean("god.messageEveryone") == true) {
						String tosend = main.createMessage("god.messageToEveryone");
						tosend = tosend.replace("%player%", p.getName());
						tosend = main.color(tosend);
						for(Player online : Bukkit.getOnlinePlayers()) {
							online.sendMessage(tosend);
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unlikely-arg-type")
	private boolean isGod(Player p) {
		HashMap<String, String[]> data = main.getData();
		for(Entry<String, String[]> en : data.entrySet()) {
			if(en.getKey().equals("god")) {
				if(en.getValue().equals(p.getUniqueId().toString())) {
					return true;
				}
			}
		}
		return false;
	}

}
