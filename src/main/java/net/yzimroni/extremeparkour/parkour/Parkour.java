package net.yzimroni.extremeparkour.parkour;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.point.Checkpoint;
import net.yzimroni.extremeparkour.parkour.point.Endpoint;
import net.yzimroni.extremeparkour.parkour.point.Point;
import net.yzimroni.extremeparkour.parkour.point.Startpoint;
import net.yzimroni.extremeparkour.utils.DataStatus;

public class Parkour {

	private ExtremeParkourPlugin plugin;

	private int id;
	private String name;

	private UUID owner;
	private long createdTimestamp;

	private Startpoint startPoint;
	private List<Checkpoint> checkpoints = new ArrayList<Checkpoint>();
	private Endpoint endPoint;

	private List<ParkourLeaderboard> leaderboards = new ArrayList<ParkourLeaderboard>();

	private boolean changed;

	private List<Integer> removedPoints;
	private List<Integer> removedLeaderboards;

	/*
	 * TODO rewards hologram
	 */

	public Parkour(ExtremeParkourPlugin plugin, int id, String name, UUID owner, long createdTimestamp) {
		this.plugin = plugin;
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.createdTimestamp = createdTimestamp;
	}

	public boolean isComplete() {
		return startPoint != null && endPoint != null;
	}

	public void markPointAsRemoved(Point p) {
		if (p == null) {
			return;
		}
		plugin.getParkourManager().removePoint(p);
		if (removedPoints == null) {
			removedPoints = new ArrayList<Integer>();
		}
		removedPoints.add(p.getId());
	}

	public void initPoint(Point p) {
		plugin.getData().insertPoint(p);
		plugin.getParkourManager().initPoint(p);
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
		if (this.startPoint != null) {
			markPointAsRemoved(this.startPoint);
		}
		this.startPoint = startPoint;
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
		if (this.endPoint != null) {
			markPointAsRemoved(this.endPoint);
		}
		this.endPoint = endPoint;
	}

	/**
	 * @return the checkpoints
	 */
	public List<Checkpoint> getCheckpoints() {
		return checkpoints;
	}

	public boolean hasCheckpoints() {
		return !checkpoints.isEmpty();
	}

	public int getCheckpointsCount() {
		return checkpoints.size();
	}

	public Checkpoint getCheckpoint(int index) {
		return checkpoints.get(index);
	}

	public void addCheckpoint(Checkpoint point) {
		initPoint(point);
		checkpoints.add(point);
	}

	public void insertCheckpoint(int index, Checkpoint point) {
		initPoint(point);
		checkpoints.add(index, point);
		plugin.getParkourManager().fixCheckpoints(this);
	}

	public void removeCheckpoint(int index) {
		Checkpoint p = getCheckpoint(index);
		removeCheckpoint(p);
	}

	public void removeCheckpoint(Checkpoint point) {
		if (checkpoints.contains(point)) {
			markPointAsRemoved(point);
			checkpoints.remove(point);
			plugin.getParkourManager().fixCheckpoints(this);
		}
	}

	/**
	 * @param checkpoints
	 *            the checkpoints to set
	 */
	public void setCheckpoints(List<Checkpoint> checkpoints) {
		this.checkpoints = checkpoints;
	}

	/**
	 * @return the leaderboards
	 */
	public List<ParkourLeaderboard> getLeaderboards() {
		return leaderboards;
	}

	/**
	 * @param leaderboards
	 *            the leaderboards to set
	 */
	public void setLeaderboards(List<ParkourLeaderboard> leaderboards) {
		this.leaderboards = leaderboards;
	}

	public boolean removeLeaderboard(ParkourLeaderboard leaderboard) {
		if (leaderboards.contains(leaderboard)) {
			leaderboards.remove(leaderboard);
			plugin.getParkourManager().removeLeaderboard(leaderboard);
			if (leaderboard.getStatus() != DataStatus.CREATED) {
				// If the leaderboard just created and not yet inserted to the db, we can just
				// remove it, but if it is inserted, we
				// need you remove it from the database as well
				if (removedLeaderboards == null) {
					removedLeaderboards = new ArrayList<Integer>();
				}
				removedLeaderboards.add(leaderboard.getId());
				return true;
			}
		}
		return false;
	}

	public Point getPointByIndex(int index) {
		if (index == -1) {
			return getStartPoint();
		} else if (index == -2) {
			return getEndPoint();
		} else {
			return getCheckpoint(index);
		}
	}

	/**
	 * @return the changed
	 */
	public boolean hasChanged() {
		return changed;
	}

	/**
	 * @param changed
	 *            the changed to set
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	/**
	 * @return the removedPoints
	 */
	public List<Integer> getRemovedPoints() {
		return removedPoints;
	}

	/**
	 * @param removedPoints
	 *            the removedPoints to set
	 */
	public void setRemovedPoints(List<Integer> removedPoints) {
		this.removedPoints = removedPoints;
	}

	/**
	 * @return the removedLeaderboards
	 */
	public List<Integer> getRemovedLeaderboards() {
		return removedLeaderboards;
	}

	/**
	 * @param removedLeaderboards
	 *            the removedLeaderboards to set
	 */
	public void setRemovedLeaderboards(List<Integer> removedLeaderboards) {
		this.removedLeaderboards = removedLeaderboards;
	}

}
