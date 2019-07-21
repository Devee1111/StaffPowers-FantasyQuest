package me.Devee1111.Bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;


/*
 * CREATED BY - Devee1111 on 7/20/19
 * purpose - Staff chat (command)
 * Permissions:
 * - staff.chat.general
 * - staff.chat.general.toggle
 */
public class StaffPowersCmdSc extends Command {

	//Permissions
	String general = "staff.chat.general";
	String toggle = "staff.chat.general.toggle";
	String channel = "general";
	
	private StaffPowers inst = StaffPowers.getInstance();
	
	public StaffPowersCmdSc() {
		super("sc");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!sender.hasPermission(general)) {
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
		String format = inst.getString("chat.format.staff");
		format = inst.prefix(format);
		format = format.replace("%player%", sender.getName());
		format = format+message;
		//converting to text component to send to players
		TextComponent tosend = new TextComponent(inst.color(format));
		//Logging to console as well
		inst.logChat(inst.color(format+message));
		//Sending message to online players
		for(ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
			if(pp.hasPermission(general)) {
				pp.sendMessage(tosend);
			}
		}
	}

}