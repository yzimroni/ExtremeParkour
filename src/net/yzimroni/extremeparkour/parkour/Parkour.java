package net.yzimroni.extremeparkour.parkour;

import java.util.List;
import java.util.UUID;

import net.yzimroni.extremeparkour.parkour.point.Checkpoint;
import net.yzimroni.extremeparkour.parkour.point.Endpoint;
import net.yzimroni.extremeparkour.parkour.point.Startpoint;

public class Parkour {

	private int id;
	private String name;
	
	private UUID owner;
	private long createdTimestamp;
	
	private Startpoint startPoint;
	private List<Checkpoint> checkPoints;
	private Endpoint endPoint;
	
	/*
	 * TODO 
	 * rewards
	 * hologram
	 */
	
}
