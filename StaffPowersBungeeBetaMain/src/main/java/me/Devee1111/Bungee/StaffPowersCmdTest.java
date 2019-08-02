package me.Devee1111.Bungee;


import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class StaffPowersCmdTest extends Command {

	private StaffPowers inst = StaffPowers.getInstance();
	
	public StaffPowersCmdTest() {
		super("testbungee");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("staff.power.testbungee")) {
			sender.sendMessage(new TextComponent(ChatColor.GREEN + "Bungee is active! Version: "+inst.getDescription().getVersion()));
		} else {
			inst.sendMessage(sender, "messages.nopermission");
		}
	}
	

}
