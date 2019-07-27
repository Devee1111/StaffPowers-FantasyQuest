package me.Devee1111.Spigot;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StaffCmdTest implements CommandExecutor {

	/*
	 * Created by - Devee1111 on 7/26/19
	 */
	StaffPowers inst;
	public StaffCmdTest(StaffPowers p) {
		inst = p;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("staff.power.testspigot")) {
			String version = inst.getDescription().getVersion();
			sender.sendMessage(ChatColor.GREEN+"Spigot is active! Version: "+version);
			return true;
		} else {
			sender.sendMessage(ChatColor.RED + "You do not have permission to run this command");
			return true;
		}
	}

}
