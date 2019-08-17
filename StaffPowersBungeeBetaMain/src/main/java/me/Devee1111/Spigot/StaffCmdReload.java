package me.Devee1111.Spigot;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StaffCmdReload implements CommandExecutor {

	
	
	/*
	 * Created by - Devee1111 on 8/17/19
	 */
	StaffPowers inst;
	public StaffCmdReload(StaffPowers p) {
		inst = p;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!sender.hasPermission("staff.power.reload")) {
			sender.sendMessage(inst.nopermission());
			return true;
		}
		
		inst.reloadConfig();
		sender.sendMessage(inst.createMessage("reloadedConfig"));
		
		return true;
	}

}
