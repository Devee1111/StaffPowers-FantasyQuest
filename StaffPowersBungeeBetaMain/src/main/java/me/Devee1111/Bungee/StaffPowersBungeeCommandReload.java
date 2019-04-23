package me.Devee1111.Bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class StaffPowersBungeeCommandReload extends Command {

	private StaffPowersBungeeMain inst = StaffPowersBungeeMain.getInstance();
	
	public StaffPowersBungeeCommandReload() {
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
