package net.yzimroni.extremeparkour.parkour;

import java.util.ArrayList;
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
	private List<Checkpoint> checkPoints = new ArrayList<Checkpoint>();
	private Endpoint endPoint;

	/*
	 * TODO rewards hologram
	 */

	public Parkour(int id, String name, UUID owner, long createdTimestamp) {
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.createdTimestamp = createdTimestamp;
	}
	
	
	public boolean isComplete() {
		return startPoint != null && endPoint != null;
	}
	

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the owner
	 */
	public UUID getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	/**
	 * @return the createdTimestamp
	 */
	public long getCreatedTimestamp() {
		return createdTimestamp;
	}

	/**
	 * @param createdTimestamp
	 *            the createdTimestamp to set
	 */
	public void setCreatedTimestamp(long createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	/**
	 * @return the startPoint
	 */
	public Startpoint getStartPoint() {
		return startPoint;
	}

	/**
	 * @param startPoint
	 *            the startPoint to set
	 */
	public void setStartPoint(Startpoint startPoint) {
		this.startPoint = startPoint;
	}

	/**
	 * @return the checkPoints
	 */
	public List<Checkpoint> getCheckPoints() {
		return checkPoints;
	}

	/**
	 * @param checkPoints
	 *            the checkPoints to set
	 */
	public void setCheckPoints(List<Checkpoint> checkPoints) {
		this.checkPoints = checkPoints;
	}

	/**
	 * @return the endPoint
	 */
	public Endpoint getEndPoint() {
		return endPoint;
	}

	/**
	 * @param endPoint
	 *            the endPoint to set
	 */
	public void setEndPoint(Endpoint endPoint) {
		this.endPoint = endPoint;
	}

}
