package me.Devee1111.Bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class StaffCmdDemote extends Command {

	
	public StaffCmdDemote() {
		super("demote");
	}
	
	/*
	 * Created by - Devee1111 on 8/12/19
	 * Permissions:
	 * - staff.power.demote
	 */
	
	private StaffPowers inst = StaffPowers.getInstance();
	
	private String perm = "staff.power.demote";
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(!sender.hasPermission(perm)) {
			sender.sendMessage(inst.nopermission());
			return;
		}
		
		//TODO setup demoting
		sender.sendMessage(new TextComponent("This feature has not been created yet - Daddy."));

	}

}
