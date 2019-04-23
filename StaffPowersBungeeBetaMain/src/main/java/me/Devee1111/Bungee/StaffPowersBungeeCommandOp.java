package me.Devee1111.Bungee;


import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class StaffPowersBungeeCommandOp extends Command {
	
	public StaffPowersBungeeCommandOp() {
		super("opme");
	}
	
	private StaffPowersBungeeMain inst = StaffPowersBungeeMain.getInstance();
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;
			if(p.hasPermission("staff.power.op")) {
				inst.sendPluginCommand(p, "CommandOp");
				if(inst.config.getBoolean("MakeOp.sendMessageOnAttempt") == true) {
					inst.sendMessage(p, "MakeOp.message");
				}
			} else {
				inst.sendMessage(p, "messages.nopermission");
			}
		} else {
			inst.sendMessage(sender, "messages.playersonly");
		}
	}
}
