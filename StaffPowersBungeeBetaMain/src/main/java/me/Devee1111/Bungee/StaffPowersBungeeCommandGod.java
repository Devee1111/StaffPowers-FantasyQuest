package me.Devee1111.Bungee;


import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class StaffPowersBungeeCommandGod extends Command {

	private StaffPowersBungeeMain inst = StaffPowersBungeeMain.getInstance();
	
	public StaffPowersBungeeCommandGod() {
		super("godme");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;
			if(p.hasPermission("staff.command.god")) {
				if(inst.config.getBoolean("MakeGod.sendMessageOnAttempt") == true) {
					inst.sendMessage(p, "MakeGod.message");
				}
				inst.sendPluginCommand(p, "CommandGod");
			} else {
				inst.sendMessage(p, "messages.nopermission");
			}
		} else {
			inst.sendMessage(sender, "messages.playersonly");
		}
	}
}
