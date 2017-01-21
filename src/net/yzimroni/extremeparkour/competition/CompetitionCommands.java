package net.yzimroni.extremeparkour.competition;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.yzimroni.commandmanager.command.Command;
import net.yzimroni.commandmanager.command.SubCommand;
import net.yzimroni.commandmanager.command.args.ArgumentData;
import net.yzimroni.commandmanager.command.args.arguments.PlayerArgument;
import net.yzimroni.commandmanager.command.methodexecutor.MethodExecutor;
import net.yzimroni.commandmanager.command.methodexecutor.MethodExecutorClass;
import net.yzimroni.commandmanager.manager.CommandManager;
import net.yzimroni.extremeparkour.ExtremeParkourPlugin;

public class CompetitionCommands implements MethodExecutorClass {
	
	private ExtremeParkourPlugin plugin;
	private CompetitionManager manager;
	
	public CompetitionCommands(ExtremeParkourPlugin plugin, CompetitionManager manager) {
		this.plugin = plugin;
		this.manager = manager;
		initCommands();
	}
	
	private void initCommands() {
		MethodExecutor helpExe = MethodExecutor.createByMethodName(this, "help");
		Command competition = new Command("competition", "ExtremeParkour competition command", helpExe);
		
		SubCommand create = new SubCommand("create", "Create a competition", MethodExecutor.createByMethodName(this, "create"));
		create.setOnlyPlayer(true);
		competition.addSubCommand(create);
		
		SubCommand join = new SubCommand("join", "Join a competition", MethodExecutor.createByMethodName(this, "join"));
		join.setOnlyPlayer(true);
		join.addArgument(new PlayerArgument("player", true));
		competition.addSubCommand(join);
		
		SubCommand start = new SubCommand("start", "Start a competition", MethodExecutor.createByMethodName(this, "start"));
		start.setOnlyPlayer(true);
		competition.addSubCommand(start);
		
		CommandManager.get().registerCommand(plugin, competition);
	}
	
	private Competition getCompetition(Player p) {
		return manager.getCompetition(p);
	}
	
	public void help(CommandSender sender, Command command, ArgumentData args) {
		command.printHelp(sender, 2);
	}
	
	public void create(CommandSender sender, Command command, ArgumentData args) {
		Player p = (Player) sender;
		if (manager.createCompetition(p) != null) {
			p.sendMessage("You have created a new competition");
		}
		
	}
	
	public void join(CommandSender sender, Command command, ArgumentData args) {
		Player p = (Player) sender;
		Player target = args.get("player", Player.class);
		Competition c = getCompetition(target);
		if (c == null) {
			p.sendMessage("Competition not found!");
			return;
		}
		c.join(p);
	}

	public void start(CommandSender sender, Command command, ArgumentData args) {
		Player p = (Player) sender;
		Competition c = getCompetition(p);
		if (c == null) {
			p.sendMessage("You aren't in a competition!");
			return;
		}
		
		if (!c.getLeader().equals(p.getUniqueId())) {
			p.sendMessage("You must be the competition leader to start it");
			return;
		}
		c.startCountdown(p);
	}

}
