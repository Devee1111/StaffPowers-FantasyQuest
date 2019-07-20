package me.Devee1111.Bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class StaffPowersCmdReload extends Command {

	private StaffPowers inst = StaffPowers.getInstance();
	
	public StaffPowersCmdReload() {
		super("staffreloadbungee");
	}
	
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("staff.power.reload")) {
			inst.reloadConfig();
			inst.sendMessage(sender, "messages.reloadedConfig");
		} else {
			inst.sendMessage(sender, "messages.nopermission");
		}

	}

}
