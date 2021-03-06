package net.yzimroni.extremeparkour.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import net.yzimroni.commandmanager.command.Command;
import net.yzimroni.commandmanager.command.SubCommand;
import net.yzimroni.commandmanager.command.args.ArgumentData;
import net.yzimroni.commandmanager.command.args.arguments.BooleanArgument;
import net.yzimroni.commandmanager.command.args.arguments.EnumArgument;
import net.yzimroni.commandmanager.command.args.arguments.IntegerArgument;
import net.yzimroni.commandmanager.command.args.arguments.PotionEffectArgument;
import net.yzimroni.commandmanager.command.args.arguments.StringArgument;
import net.yzimroni.commandmanager.command.methodexecutor.MethodExecutor;
import net.yzimroni.commandmanager.command.methodexecutor.MethodExecutorClass;
import net.yzimroni.commandmanager.command.methodexecutor.MethodId;
import net.yzimroni.commandmanager.manager.CommandManager;
import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.ParkourLeaderboard;
import net.yzimroni.extremeparkour.parkour.manager.player.ParkourPlayer;
import net.yzimroni.extremeparkour.parkour.point.Checkpoint;
import net.yzimroni.extremeparkour.parkour.point.Endpoint;
import net.yzimroni.extremeparkour.parkour.point.Point;
import net.yzimroni.extremeparkour.parkour.point.PointEffect;
import net.yzimroni.extremeparkour.parkour.point.PointMode;
import net.yzimroni.extremeparkour.parkour.point.Startpoint;
import net.yzimroni.extremeparkour.utils.DataStatus;

public class ExtremeParkourCommands implements MethodExecutorClass {

	private ExtremeParkourPlugin plugin;
	
	private HashMap<UUID, Parkour> selections = new HashMap<UUID, Parkour>();

	public ExtremeParkourCommands(ExtremeParkourPlugin plugin) {
		this.plugin = plugin;
		createPakourCommand();
	}
	
	public void onPlayerQuit(UUID player) {
		selections.remove(player);
	}
	
	public void onPlayerDelete(Parkour p) {
		if (selections.containsValue(p)) {
			List<UUID> toRemove = new ArrayList<UUID>();
			for (Entry<UUID, Parkour> e : selections.entrySet()) {
				if (e.getValue().equals(p)) {
					toRemove.add(e.getKey());
				}
			}
			
			if (!toRemove.isEmpty()) {
				for (UUID key : toRemove) {
					selections.remove(key);
				}
			}
			toRemove.clear();
		}
	}
	
	protected Parkour getSelection(CommandSender sender) {
		if (sender instanceof Player) {
			return selections.get(((Player) sender).getUniqueId());
		} else if (sender instanceof ConsoleCommandSender) {
			return selections.get(null);
		}
		return null;
	}
	
	private boolean isParkourBlock(Location l) {
		return plugin.getParkourManager().isParkourBlock(l.getBlock(), false);
	}
	
	private PointMode getPointMode(PointMode e) {
		return e != null ? e : PointMode.BLOCK;
	}
	
	private void createPakourCommand() {
		Command parkour = new Command("parkour", "ExetremePakour command", MethodExecutor.createByMethodId(this, "parkourMain"));
		parkour.setAliases("p", "eparkour", "extremeparkour", "ep");
		
		SubCommand checkpoint = new SubCommand("checkpoint", "Teleport to your latest checkpoint", MethodExecutor.createByMethodId(this, "parkourCheckpoint"));
		checkpoint.setAliases("check", "cp");
		checkpoint.setOnlyPlayer(true);
		parkour.addSubCommand(checkpoint);
		
		SubCommand reset = new SubCommand("reset", "Teleport to the parkour start point", MethodExecutor.createByMethodId(this, "parkourReset"));
		reset.setOnlyPlayer(true);
		parkour.addSubCommand(reset);
		
		SubCommand select = new SubCommand("select", "Select a parkour", MethodExecutor.createByMethodId(this, "parkourSelect"));
		select.addArgument(new IntegerArgument("parkourId", false));
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
		
		EnumArgument<PointMode> pointModes = new EnumArgument<PointMode>("pointMode", PointMode.values());
		pointModes.setRequire(false);
		
		SubCommand start = new SubCommand("start", "Set the start of the parkour", MethodExecutor.createByMethodId(this, "parkourPointSetStart"));
		start.setOnlyPlayer(true);
		start.addArgument(pointModes);
		set.addSubCommand(start);
		
		SubCommand end = new SubCommand("end", "Set the end of the parkour", MethodExecutor.createByMethodId(this, "parkourPointSetEnd"));
		end.setOnlyPlayer(true);
		end.addArgument(pointModes);
		set.addSubCommand(end);
		
		SubCommand check = new SubCommand("check", "Add a checkpoint to the parkour", MethodExecutor.createByMethodId(this, "parkourPointSetCheck"));
		check.setAliases("checkpoint");
		check.setOnlyPlayer(true);
		check.addArgument(pointModes);
		check.addArgument(new IntegerArgument("index", false, 0, null));
		set.addSubCommand(check);
		
		point.addSubCommand(set);
		
		SubCommand remove = new SubCommand("remove", "Remove a point", MethodExecutor.createByMethodId(this, "parkourMain")); //Just need to send help
		remove.setAliases("rem", "delete", "del");
		
		SubCommand remcheck = new SubCommand("check", "Remove a checkpoint", MethodExecutor.createByMethodId(this, "parkourPointRemoveCheck"));
		remcheck.setAliases("checkpoint");
		remcheck.addArgument(new IntegerArgument("index", true, 0, null));
		remove.addSubCommand(remcheck);
		point.addSubCommand(remove);
		
		SubCommand effect = new SubCommand("effect", "Potion effect", MethodExecutor.createByMethodId(this, "parkourMain")); //Just need to send help
		
		SubCommand addeffect = new SubCommand("add", "Add a new effect", MethodExecutor.createByMethodId(this, "parkourPointEffectAdd"));
		addeffect.setAliases("new");
		addeffect.addArgument(new PointArgument(plugin, "point"));
		addeffect.addArgument(new PotionEffectArgument("effect", true));
		addeffect.addArgument(new IntegerArgument("duration", true));
		addeffect.addArgument(new IntegerArgument("amplifier", false));
		addeffect.addArgument(new BooleanArgument("showParticles", false));
		effect.addSubCommand(addeffect);
		
		SubCommand listeffect = new SubCommand("list", "List of effects on a point", MethodExecutor.createByMethodId(this, "parkourPointEffectList"));
		listeffect.addArgument(new PointArgument(plugin, "point"));
		effect.addSubCommand(listeffect);
		
		SubCommand removeeffect = new SubCommand("remove", "Remove an effect from a point", MethodExecutor.createByMethodId(this, "parkourPointEffectRemove"));
		removeeffect.setAliases("rem", "delete", "del");
		removeeffect.addArgument(new PointArgument(plugin, "point"));
		removeeffect.addArgument(new PotionEffectArgument("effect", true));
		effect.addSubCommand(removeeffect);
		
		point.addSubCommand(effect);
		
		parkour.addSubCommand(point);
		
		SubCommand leaderboard = new SubCommand("leaderboard", "Parkour leaderboard", MethodExecutor.createByMethodId(this, "parkourMain")); //Just need to send help
		leaderboard.setAliases("leader", "board");
		
		SubCommand add = new SubCommand("add", "Add a leaderboard", MethodExecutor.createByMethodId(this, "parkourLeaderboardAdd"));
		add.setAliases("create", "new");
		add.setOnlyPlayer(true);
		add.addArgument(new IntegerArgument("players", false, 1, 150, true));
		leaderboard.addSubCommand(add);
		
		SubCommand removeLeader = new SubCommand("remove", "Remove a leaderboard", MethodExecutor.createByMethodId(this, "parkourLeaderboardRemove"));
		removeLeader.setAliases("rem", "delete", "del");
		removeLeader.setOnlyPlayer(true);
		leaderboard.addSubCommand(removeLeader);
		
		SubCommand resetleader = new SubCommand("reset", "Reset the times in this parkour for all players", MethodExecutor.createByMethodId(this, "parkourLeaderboardReset"));
		leaderboard.addSubCommand(resetleader);
		
		parkour.addSubCommand(leaderboard);
		
		SubCommand removeParkour = new SubCommand("remove", "remove a parkour", MethodExecutor.createByMethodId(this, "parkourRemove"));
		removeParkour.setAliases("rem", "delete", "del");
		parkour.addSubCommand(removeParkour);
		
		SubCommand editMode = new SubCommand("editmode", "Toggle edit mode", MethodExecutor.createByMethodId(this, "parkourEditMode"));
		editMode.setAliases("edit");
		editMode.setOnlyPlayer(true);
		parkour.addSubCommand(editMode);
		
		
		CommandManager.get().registerCommand(plugin, parkour);
	}
	
	@MethodId("parkourMain")
	public void parkourMain(CommandSender sender, Command command, ArgumentData args) {
		command.printHelp(sender, 2);
	}
	
	
	
	@MethodId("parkourCheckpoint")
	public void parkourCheckpoint(CommandSender sender, Command command, ArgumentData args) {
		ParkourPlayer p = plugin.getParkourManager().getPlayerManager().getPlayer((Player) sender);
		if (p != null) {
			p.teleportLatestCheckpoint();
		} else {
			sender.sendMessage(ChatColor.RED + "You aren't in a parkour");
		}
	}
	
	
	@MethodId("parkourReset")
	public void parkourReset(CommandSender sender, Command command, ArgumentData args) {
		ParkourPlayer p = plugin.getParkourManager().getPlayerManager().getPlayer((Player) sender);
		if (p != null) {
			p.teleportStart();
		} else {
			sender.sendMessage(ChatColor.RED + "You aren't in a parkour");
		}
	}
	
	@MethodId("parkourSelect")
	public void parkourSelect(CommandSender sender, Command command, ArgumentData args) {
		if (args.has("parkourId")) {
			int id = args.get("parkourId", Integer.class);
			Parkour p = plugin.getParkourManager().getParkourById(id);
			if (p != null) {
				if (sender instanceof Player) {
					selections.put(((Player) sender).getUniqueId(), p);
				} else if (sender instanceof ConsoleCommandSender) {
					selections.put(null, p);
				} else {
					sender.sendMessage("Invalid sender");
					return;
				}
				sender.sendMessage("Parkour selected: " + p.getName() + " (" + p.getId() + ")");
			} else {
				sender.sendMessage("Parkour not found");
			}
		} else {
			Parkour p = getSelection(sender);
			if (p == null) {
				sender.sendMessage("You didn't select a parkour");
			} else {
				sender.sendMessage("Parkour selected: " + p.getName() + " (" + p.getId() + ")");
			}
		}
	}
	
	
	@MethodId("parkourCreate")
	public void parkourCreate(CommandSender sender, Command command, ArgumentData args) {
		Parkour parkour = plugin.getParkourManager().createParkour((Player) sender, args.get("name", String.class));
		sender.sendMessage("Secsessfully created parkour id " + parkour.getId() + " '" + parkour.getName() + "'");
	}
	
	@MethodId("parkourList")
	public void parkourList(CommandSender sender, Command command, ArgumentData args) {
		List<Parkour> parkours = plugin.getParkourManager().getParkours();
		sender.sendMessage("Parkours (" + parkours.size() + "):");
		for (Parkour parkour : parkours) {
			sender.sendMessage(String.format("%s | %s | %s", parkour.getId(), parkour.getName(), parkour.getOwner().toString()));
		}
	}
	
	@MethodId("parkourPointSetStart")
	public void parkourPointSetStart(CommandSender sender, Command command, ArgumentData args) {
		Player p = (Player) sender;
		Parkour parkour = getSelection(p);
		if (parkour == null) {
			p.sendMessage("Parkour is null");
			return;
		}
		if (isParkourBlock(p.getLocation())) {
			p.sendMessage("This block is already a parkour point");
			return;
		}
		PointMode mode = getPointMode(args.get("pointMode", PointMode.class));
		Startpoint start = new Startpoint(plugin, -1, parkour, p.getLocation(), mode, 5);
		parkour.setStartPoint(start);
		parkour.initPoint(start);
		p.sendMessage("Start point set!");
	}
	
	@MethodId("parkourPointSetEnd")
	public void parkourPointSetEnd(CommandSender sender, Command command, ArgumentData args) {
		Player p = (Player) sender;
		Parkour parkour = getSelection(p);
		if (parkour == null) {
			p.sendMessage("Parkour is null");
			return;
		}
		if (isParkourBlock(p.getLocation())) {
			p.sendMessage("This block is already a parkour point");
			return;
		}
		PointMode mode = getPointMode(args.get("pointMode", PointMode.class));
		Endpoint end = new Endpoint(plugin, -1, parkour, p.getLocation(), mode, 5);
		parkour.setEndPoint(end);
		parkour.initPoint(end);
		p.sendMessage("End point set!");
	}
	
	
	@MethodId("parkourPointSetCheck")
	public void parkourPointSetCheck(CommandSender sender, Command command, ArgumentData args) {
		Player p = (Player) sender;
		Parkour parkour = getSelection(p);
		if (parkour == null) {
			p.sendMessage("Parkour is null");
			return;
		}
		if (isParkourBlock(p.getLocation())) {
			p.sendMessage("This block is already a parkour point");
			return;
		}
		int index = parkour.getCheckpointsCount();
		boolean hasIndex = false;
		if (args.has("index", Integer.class)) {
			index = args.get("index", Integer.class) - 1;
			hasIndex = true;
		}
		PointMode mode = getPointMode(args.get("pointMode", PointMode.class));
		Checkpoint check = new Checkpoint(plugin, -1, parkour, p.getLocation(), index, mode, 5);
		if (hasIndex) {
			parkour.insertCheckpoint(index, check);
		} else {
			parkour.addCheckpoint(check);
		}
	}
	
	
	@MethodId("parkourPointRemoveCheck")
	public void parkourPointRemoveCheck(CommandSender sender, Command command, ArgumentData args) {
		Parkour parkour = getSelection(sender);
		if (parkour == null) {
			sender.sendMessage("Parkour is null");
			return;
		}
		int index = args.get("index", Integer.class) - 1;
		System.out.println();
		if (index >= parkour.getCheckpointsCount() || index < 0) {
			sender.sendMessage("Checkpoint not found");
			return;
		}
		parkour.removeCheckpoint(index);
		sender.sendMessage("Removed checkpoint #" + (index + 1));
	}
	
	
	@MethodId("parkourPointEffectAdd")
	public void parkourPointEffectAdd(CommandSender sender, Command command, ArgumentData args) {
		Point point = args.get("point", Point.class);
		PotionEffectType type = args.get("effect", PotionEffectType.class);
		if (point.hasEffect(type)) {
			sender.sendMessage("This point already has effect " + type.getName());
			return;
		}
		int duration = args.get("duration", Integer.class);
		int amplifier = args.has("amplifier") ? args.get("amplifier", Integer.class) - 1 : 0;
		boolean showParticles = args.has("showParticles") ? args.get("showParticles", Boolean.class) : true;
		PointEffect effect = new PointEffect(-1, type, duration, amplifier, showParticles);
		effect.setStatus(DataStatus.CREATED);
		point.getEffects().add(effect);
		sender.sendMessage("Effect " + type.getName() + " added to point " + point.getName());
	}
	
	
	@MethodId("parkourPointEffectList")
	public void parkourPointEffectList(CommandSender sender, Command command, ArgumentData args) {
		Point point = args.get("point", Point.class);
		if (point.getEffects().isEmpty()) {
			sender.sendMessage("There is no effects on this point");
			return;
		}
		sender.sendMessage("" + point.getEffects().size() + " effects on point " + point.getName() + ":");
		for (PointEffect effect : point.getEffects()) {
			sender.sendMessage(effect.getType().getName() + "x" + (effect.getAmplifier() + 1) + " for " + effect.getDuration() + " ticks (with" + (effect.shouldShowParticles() ? "" : "out") + " particles)");
		}
	}
	
	
	@MethodId("parkourPointEffectRemove")
	public void parkourPointEffectRemove(CommandSender sender, Command command, ArgumentData args) {
		Point point = args.get("point", Point.class);
		PotionEffectType type = args.get("effect", PotionEffectType.class);
		if (!point.hasEffect(type)) {
			sender.sendMessage("This point doesn't contains the effect " + type.getName());
			return;
		}
		PointEffect ep = null;
		for (PointEffect temp : point.getEffects()) {
			if (temp.getType().equals(type)) {
				ep = temp;
				break;
			}
		}
		if (ep == null) {
			sender.sendMessage("Error: ep is null");
			return;
		}
		point.removeEffect(ep);
		sender.sendMessage("Effect " + type.getName() + " removed from point" + point.getName());
	}
	
	@MethodId("parkourLeaderboardAdd")
	public void parkourLeaderboardAdd(CommandSender sender, Command command, ArgumentData args) {
		Player p = (Player) sender;
		Parkour parkour = getSelection(p);
		if (parkour == null) {
			sender.sendMessage("Parkour is null");
			return;
		}
		int count = args.has("players") ? args.get("players", Integer.class) : 10;
		ParkourLeaderboard leaderboard = new ParkourLeaderboard(-1, parkour, p.getLocation(), count, 1);
		leaderboard.setStatus(DataStatus.CREATED);
		parkour.getLeaderboards().add(leaderboard);
		plugin.getParkourManager().initLeaderboard(parkour);
	}
	
	@MethodId("parkourLeaderboardRemove")
	public void parkourLeaderboardRemove(CommandSender sender, Command command, ArgumentData args) {
		Player p = (Player) sender;
		Parkour parkour = getSelection(p);
		if (parkour == null) {
			sender.sendMessage("Parkour is null");
			return;
		}
		if (parkour.getLeaderboards().isEmpty()) {
			p.sendMessage("The parkour dont have leaderboards");
			return;
		}
		ParkourLeaderboard lb = null;
		double last_dis = -1;
		for (ParkourLeaderboard h : parkour.getLeaderboards()) {
			if (h.getLocation().getWorld().equals(p.getWorld())) {
				double dis = h.getLocation().distanceSquared(p.getLocation());
				if (dis < 10 && (last_dis == -1 || lb == null || dis < last_dis)) {
					lb = h;
					last_dis = dis;
				}
			}
		}
		if (lb == null) {
			p.sendMessage("Leaderboard not found, are you near the board?");
			return;
		}
		
		parkour.removeLeaderboard(lb);
		p.sendMessage("Leaderboard removed!");
	}
	
	
	@MethodId("parkourLeaderboardReset")
	public void parkourLeaderboardReset(CommandSender sender, Command command, ArgumentData args) {
		Parkour parkour = getSelection(sender);
		if (parkour == null) {
			sender.sendMessage("Parkour is null");
			return;
		}
		//TODO accept
		plugin.getData().resetLeaderboard(parkour);
		sender.sendMessage("Leaderboard stats has been reset for parkour " + parkour.getId());
	}
	
	@MethodId("parkourRemove")
	public void parkourRemove(CommandSender sender, Command command, ArgumentData args) {
		//TODO require the sender to accept the deletion
		Parkour parkour = getSelection(sender);
		if (parkour == null) {
			sender.sendMessage("Parkour is null");
			return;
		}
		plugin.getParkourManager().removeParkour(parkour);
		sender.sendMessage(ChatColor.DARK_RED + "Parkour deleted");
	}
	
	
	
	@MethodId("parkourEditMode")
	public void parkourEditMode(CommandSender sender, Command command, ArgumentData args) {
		Parkour parkour = getSelection(sender);
		if (parkour == null) {
			sender.sendMessage("Parkour is null");
			return;
		}
		plugin.getParkourManager().getEditMode().toggle((Player) sender, parkour);
	}
	
}
