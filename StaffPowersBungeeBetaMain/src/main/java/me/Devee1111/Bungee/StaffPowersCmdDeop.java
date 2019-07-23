package me.Devee1111.Bungee;


import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class StaffPowersCmdDeop extends Command {
	/*
	 * Created by - Devee1111 on 9/23/19
	 * Permissions:
	 * - staff.power.op
	 */
	public StaffPowersCmdDeop() {
		super("deopme");
	}
	
	private StaffPowers inst = StaffPowers.getInstance();
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(new TextComponent(inst.playersonly()));
			return;
		}
		if(!sender.hasPermission("staff.power.op")) {
			sender.sendMessage(inst.nopermission());
			return;
		}
		ProxiedPlayer pp = (ProxiedPlayer) sender;
		inst.sendPluginCommand(pp, "CommandDeop");
		//TODO add deop failure message
	}
}
