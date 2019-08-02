package me.Devee1111.Bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;


/*
 * CREATED BY - Devee1111 on 8/2/19
 * purpose - Admin chat (command)
 * Permissions:
 * - staff.chat.admin
 * - staff.chat.admin.toggle
 */
public class StaffPowersCmdAc extends Command {

	//Permissions
	String admin = "staff.chat.admin";
	String toggle = "staff.chat.admin.toggle";
	String channel = "admin";
	
	private StaffPowers inst = StaffPowers.getInstance();
	
	public StaffPowersCmdAc() {
		super("ac", "", "a");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!sender.hasPermission(admin)) {
			sender.sendMessage(inst.nopermission());
			return;
		}
		//toggle command
		if(args.length == 0) {
			//Can't toggle for console, so make sure it's player
			if(!(sender instanceof ProxiedPlayer)) {
				sender.sendMessage(new TextComponent(inst.createMessage("chat.messages.playersonly")));
				return;
			}
			//Only players with permission can toggle
			ProxiedPlayer pp = (ProxiedPlayer) sender;
			if(!pp.hasPermission(toggle)) {
				pp.sendMessage(inst.nopermission());
			}
			//toggles for the chat
			if(inst.getChannel(pp).equals(channel)) {
				inst.unToggle(pp);
				return;
			}
			inst.setToggled(channel, pp);
			return;
		}
		//converting arguments into the message
		String message = "";
		for(String word : args) {
			message = message+word+" ";
		}
		//Remove space from the end of the message
		message = message.substring(0, message.length() - 1);
		//formatting the message
		String format = inst.getString("chat.format.admin");
		format = inst.prefix(format);
		format = format.replace("%player%", sender.getName());
		format = format+message;
		format = inst.color(format);
		//converting to text component to send to players
		BaseComponent[] tosend = TextComponent.fromLegacyText(format);
		//Logging to console as well
		inst.logChat(format);
		//Sending message to online players
		for(ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
			if(pp.hasPermission(admin)) {
				pp.sendMessage(tosend);
			}
		}
	}

}