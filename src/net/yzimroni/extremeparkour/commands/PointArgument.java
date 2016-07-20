package net.yzimroni.extremeparkour.commands;

import java.util.ArrayList;
import java.util.List;

import net.yzimroni.commandmanager.command.args.ArgumentParseData;
import net.yzimroni.commandmanager.command.args.ArgumentValidCheck;
import net.yzimroni.commandmanager.command.args.CommandArgument;
import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.point.Point;
import net.yzimroni.extremeparkour.utils.Utils;

public class PointArgument extends CommandArgument<Point> {

	private ExtremeParkourPlugin plugin;
	
	public PointArgument(ExtremeParkourPlugin plugin, String name) {
		super(name);
		this.plugin = plugin;
	}

	@Override
	public String getInputType() {
		return "Start/End/[Checkpoint index]";
	}

	@Override
	public String getValidInputs() {
		return "Start/End/[Checkpoint index]";
	}

	@Override
	public ArgumentValidCheck isValidInput(ArgumentParseData data) {
		Parkour p = plugin.getCommands().getSelection(data.getCommandSender());
		if (p == null) {
			return ArgumentValidCheck.create(false, "You need to choose parkour!");
		}
		if (data.getInput().equalsIgnoreCase("start")) {
			return ArgumentValidCheck.create(p.getStartPoint() != null, "Point not found");
		}
		if (data.getInput().equalsIgnoreCase("end")) {
			return ArgumentValidCheck.create(p.getEndPoint() != null, "Point not found");
		}
		if (Utils.isInt(data.getInput())) {
			int index = Utils.getInt(data.getInput()) - 1;
			return ArgumentValidCheck.create(index >= 0 && index < p.getChestpointsCount(), "Point not found");
		} else {
			return ArgumentValidCheck.create(false, "Invalid index");
		}
	}

	@Override
	public Point getInput(ArgumentParseData data) {
		Parkour p = plugin.getCommands().getSelection(data.getCommandSender());
		if (data.getInput().equalsIgnoreCase("start")) {
			return p.getStartPoint();
		} else if (data.getInput().equalsIgnoreCase("end")) {
			return p.getEndPoint();
		} else {
			int index = Utils.getInt(data.getInput()) - 1;
			return p.getCheckpoint(index);
		}
	}

	@Override
	public List<String> getTabCompleteOptions(ArgumentParseData data) {
		Parkour p = plugin.getCommands().getSelection(data.getCommandSender());
		List<String> options = new ArrayList<String>();
		if (p != null) {
			options.add("start");
			options.add("end");
			for (int i = 0; i < p.getChestpointsCount(); i++) {
				options.add("" + (i + 1));
			}
		}
		return options;
	}

	@Override
	public boolean isVarArgs() {
		return false;
	}

}
