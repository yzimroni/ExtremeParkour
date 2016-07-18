package net.yzimroni.extremeparkour.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.yzimroni.commandmanager.command.Command;
import net.yzimroni.commandmanager.command.MethodExecutor;
import net.yzimroni.commandmanager.command.SubCommand;
import net.yzimroni.commandmanager.command.args.ArgumentData;
import net.yzimroni.commandmanager.command.args.arguments.IntegerArgument;
import net.yzimroni.commandmanager.command.args.arguments.StringArgument;
import net.yzimroni.commandmanager.manager.CommandManager;
import net.yzimroni.commandmanager.utils.MethodId;
import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.point.Checkpoint;
import net.yzimroni.extremeparkour.parkour.point.Endpoint;
import net.yzimroni.extremeparkour.parkour.point.Startpoint;

public class ExtremeParkourCommands {

	private ExtremeParkourPlugin plugin;
	
	private Parkour parkourSel = null; /* TODO THIS IS TEMP */

	public ExtremeParkourCommands(ExtremeParkourPlugin plugin) {
		this.plugin = plugin;
		createPakourCommand();
	}
	
	public void disable() {
		
	}
	
	private void createPakourCommand() {
		Command parkour = new Command("parkour", "ExetremePakour command", MethodExecutor.createByMethodId(this, "parkourMain"));
		parkour.setAliases("p", "eparkour", "extremeparkour", "ep");
		
		SubCommand select = new SubCommand("select", "Select a parkour", MethodExecutor.createByMethodId(this, "parkourSelect"));
		select.addArgument(new IntegerArgument("parkourId", true));
		parkour.addSubCommand(select);
		
		SubCommand create = new SubCommand("create", "Create a new parkour", MethodExecutor.createByMethodId(this, "parkourCreate"));
		create.setAliases("new", "add");
		create.addArgument(new StringArgument("name", true, true));
		create.setOnlyPlayer(true);
		parkour.addSubCommand(create);
		
		SubCommand list = new SubCommand("list", "List of parkours", MethodExecutor.createByMethodId(this, "parkourList"));
		parkour.addSubCommand(list);
		
		SubCommand point = new SubCommand("point", "Point", MethodExecutor.createByMethodId(this, "parkourMain")); //Just need to send help
		
		SubCommand set = new SubCommand("set", "Set a point", MethodExecutor.createByMethodId(this, "parkourMain")); //^^
		set.setAliases("add");
		
		SubCommand start = new SubCommand("start", "Set the start of the parkour", MethodExecutor.createByMethodId(this, "parkourPointSetStart"));
		start.setOnlyPlayer(true);
		set.addSubCommand(start);
		
		SubCommand end = new SubCommand("end", "Set the end of the parkour", MethodExecutor.createByMethodId(this, "parkourPointSetEnd"));
		end.setOnlyPlayer(true);
		set.addSubCommand(end);
		
		SubCommand check = new SubCommand("check", "Add a checkpoint to the parkour", MethodExecutor.createByMethodId(this, "parkourPointSetCheck"));
		check.setAliases("checkpoint");
		check.setOnlyPlayer(true);
		check.addArgument(new IntegerArgument("index", false, 0, null));
		
		set.addSubCommand(check);
		
		SubCommand remove = new SubCommand("remove", "Remove a point", MethodExecutor.createByMethodId(this, "parkourMain")); //Just need to send help
		remove.setAliases("rem", "delete", "del");
		
		SubCommand remcheck = new SubCommand("check", "Remove a checkpoint", MethodExecutor.createByMethodId(this, "parkourPointRemoveCheck"));
		remcheck.setAliases("checkpoint");
		remcheck.addArgument(new IntegerArgument("index", true, 0, null));
		remove.addSubCommand(remcheck);
		
		set.addSubCommand(remove);
		
		point.addSubCommand(set);
		parkour.addSubCommand(point);
		
		CommandManager.get().registerCommand(plugin, parkour);
	}
	
	@MethodId("parkourMain")
	public boolean parkourMain(CommandSender sender, Command command, ArgumentData args) {
		command.printHelp(sender, 2);
		return true;
	}
	
	
	@MethodId("parkourSelect")
	public boolean parkourSelect(CommandSender sender, Command command, ArgumentData args) {
		/*
		 * TODO THIS IS TEMP
		 */
		int id = args.get("parkourId", Integer.class);
		Parkour p = plugin.getParkourManager().getParkourById(id);
		if (p != null) {
			parkourSel = p;
			sender.sendMessage("Selected parkour: " + p.getId());
		} else {
			sender.sendMessage("Parkour not found");
		}
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
	
	@MethodId("parkourPointSetStart")
	public boolean parkourPointSetStart(CommandSender sender, Command command, ArgumentData args) {
		Player p = (Player) sender;
		if (parkourSel == null) {
			p.sendMessage("Parkour is null");
			return true;
		}
		Startpoint start = new Startpoint(-1, parkourSel, p.getLocation());
		parkourSel.setStartPoint(start);
		parkourSel.initPoint(start);
		p.sendMessage("Start point set!");
		return true;
	}
	
	@MethodId("parkourPointSetEnd")
	public boolean parkourPointSetEnd(CommandSender sender, Command command, ArgumentData args) {
		Player p = (Player) sender;
		if (parkourSel == null) {
			p.sendMessage("Parkour is null");
			return true;
		}
		Endpoint end = new Endpoint(-1, parkourSel, p.getLocation());
		parkourSel.setEndPoint(end);
		parkourSel.initPoint(end);
		p.sendMessage("End point set!");
		return true;
	}
	
	
	@MethodId("parkourPointSetCheck")
	public boolean parkourPointSetCheck(CommandSender sender, Command command, ArgumentData args) {
		Player p = (Player) sender;
		if (parkourSel == null) {
			p.sendMessage("Parkour is null");
			return true;
		}
		int index = parkourSel.getChestpointsCount();
		boolean hasIndex = false;
		if (args.has("index", Integer.class)) {
			index = args.get("index", Integer.class) - 1;
			hasIndex = true;
		}
		Checkpoint check = new Checkpoint(-1, parkourSel, p.getLocation(), index);
		if (hasIndex) {
			parkourSel.insertCheckpoint(index, check);
		} else {
			parkourSel.addCheckpoint(check);
		}
		return true;
	}
	
	
	@MethodId("parkourPointRemoveCheck")
	public boolean parkourPointRemoveCheck(CommandSender sender, Command command, ArgumentData args) {
		if (parkourSel == null) {
			sender.sendMessage("Parkour is null");
			return true;
		}
		int index = args.get("index", Integer.class) - 1;
		if (parkourSel.getChestpointsCount() >= index) {
			sender.sendMessage("Checkpoint not found");
			return false;
		}
		parkourSel.removeCheckpoint(index);
		sender.sendMessage("Removed checkpoint #" + (index + 1));
		return true;
	}
}
