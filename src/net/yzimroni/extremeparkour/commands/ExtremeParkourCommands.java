package net.yzimroni.extremeparkour.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.yzimroni.commandmanager.command.Command;
import net.yzimroni.commandmanager.command.MethodExecutor;
import net.yzimroni.commandmanager.command.SubCommand;
import net.yzimroni.commandmanager.command.args.ArgumentData;
import net.yzimroni.commandmanager.command.args.arguments.StringArgument;
import net.yzimroni.commandmanager.manager.CommandManager;
import net.yzimroni.commandmanager.utils.MethodId;
import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;

public class ExtremeParkourCommands {

	private ExtremeParkourPlugin plugin;

	public ExtremeParkourCommands(ExtremeParkourPlugin plugin) {
		this.plugin = plugin;
		createPakourCommand();
	}
	
	public void disable() {
		
	}
	
	private void createPakourCommand() {
		Command parkour = new Command("parkour", "ExetremePakour command", MethodExecutor.createByMethodId(this, "parkourMain"));
		parkour.setAliases("p", "eparkour", "extremeparkour", "ep");
		
		SubCommand create = new SubCommand("create", "Create a new parkour", MethodExecutor.createByMethodId(this, "parkourCreate"));
		create.setAliases("new");
		create.addArgument(new StringArgument("name", true, true));
		create.setOnlyPlayer(true);
		parkour.addSubCommand(create);
		
		SubCommand list = new SubCommand("list", "List of parkours", MethodExecutor.createByMethodId(this, "parkourList"));
		parkour.addSubCommand(list);
		
		CommandManager.get().registerCommand(plugin, parkour);
	}
	
	@MethodId("parkourMain")
	public boolean parkourMain(CommandSender sender, Command command, ArgumentData args) {
		command.printHelp(sender, 2);
		return true;
	}
	
	@MethodId("parkourCreate")
	public boolean parkourCreate(CommandSender sender, Command command, ArgumentData args) {
		Parkour parkour = plugin.getParkourManager().createParkour((Player) sender, args.get("name", String.class));
		sender.sendMessage("Secsessfully created parkour id " + parkour.getId() + " '" + parkour.getName() + "'");
		return true;
	}
	
	@MethodId("parkourList")
	public boolean parkourList(CommandSender sender, Command command, ArgumentData args) {
		List<Parkour> parkours = plugin.getParkourManager().getParkours();
		sender.sendMessage("Parkours (" + parkours.size() + "):");
		for (Parkour parkour : parkours) {
			sender.sendMessage(String.format("%s | %s | %s", parkour.getId(), parkour.getName(), parkour.getOwner().toString()));
		}
		return true;
	}
	

	
}
