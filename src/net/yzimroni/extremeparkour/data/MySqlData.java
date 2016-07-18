package net.yzimroni.extremeparkour.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.manager.player.ParkourPlayerScore;
import net.yzimroni.extremeparkour.parkour.point.Checkpoint;
import net.yzimroni.extremeparkour.parkour.point.Endpoint;
import net.yzimroni.extremeparkour.parkour.point.Point;
import net.yzimroni.extremeparkour.parkour.point.Startpoint;
import net.yzimroni.extremeparkour.utils.MCSQL;
import net.yzimroni.extremeparkour.utils.Utils;

public class MySqlData extends ExtremeParkourData {
	
	MCSQL sql = null;
	private String prefix = null;
	
	public MySqlData(ExtremeParkourPlugin plugin, String host, String port, String database, String username, String password, String prefix) {
		super(plugin);
		sql = new MCSQL(host, port, database, username, password);
		if (prefix == null) {
			prefix = "";
		}
		this.prefix = prefix;
	}
	
	@Override
	public void init() {
		try {
			sql.openConnecting();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save() {
		
	}

	@Override
	public void disable() {
		sql.disable();
		sql = null;
	}

	@Override
	public List<Parkour> getAllParkours() {
		ResultSet rs = sql.get("SELECT * FROM " + prefix + "parkours");
		List<Parkour> parkours = new ArrayList<Parkour>();
		try {
			while (rs.next()) {
				int id = rs.getInt("ID");
				String name = rs.getString("name");
				UUID owner = UUID.fromString(rs.getString("owner"));
				long createdTimestamp = rs.getLong("createdTimestamp");
				Parkour p = new Parkour(plugin ,id, name, owner, createdTimestamp);
				
				
				ResultSet points_rs = sql.get("SELECT * FROM " + prefix + "points WHERE parkour_id=" + id + " ORDER BY point_index ASC");
				List<Checkpoint> checkpoints = new ArrayList<Checkpoint>();
				while (points_rs.next()) {
					int point_id = points_rs.getInt("ID");
					Location location = Utils.deserializeLocation(points_rs.getString("location"));
					int index = points_rs.getInt("point_index");
					Point point = null;
					if (index == -1) {
						//Start
						Startpoint start = new Startpoint(point_id, p, location);
						p.setStartPoint(start);
						point = start;
					} else if (index == -2) {
						//End
						Endpoint end = new Endpoint(point_id, p, location);
						p.setEndPoint(end);
						point = end;
					} else {
						//Checkpoint
						Checkpoint checkpoint = new Checkpoint(point_id, p, location, index);
						checkpoints.add(checkpoint);
						point = checkpoint;
					}
					point.setChanged(false);
					//TODO effects
				}
				p.setCheckpoints(checkpoints);
				//TODO points, ladderboard etc
				
				p.setChanged(false);
				parkours.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return parkours;
	}
	
	@Override
	public void insertParkour(Parkour p) {
		try {
			PreparedStatement pre = sql.getPrepareAutoKeys("INSERT INTO " + prefix + "parkours (name,owner,createdTimestamp) VALUES(?,?,?)");
			pre.setString(1, p.getName());
			pre.setString(2, p.getOwner().toString());
			pre.setLong(3, p.getCreatedTimestamp());
			
			pre.executeUpdate();
			p.setChanged(false);
			
			int id = sql.getIdFromPrepared(pre);
			p.setId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveParkour(Parkour p) {

		if (p.hasChanged()) {
			//If the parkour settings/options were changed we need to updated them
			try {
				PreparedStatement pre = sql
						.getPrepare("UPDATE " + prefix + "parkours SET name = ?, owner = ?, createdTimestamp = ?");
				pre.setString(1, p.getName());
				pre.setString(2, p.getOwner().toString());
				pre.setLong(3, p.getCreatedTimestamp());

				pre.executeUpdate();
				p.setChanged(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// Delete from the database the points that were deleted in-game
		if (p.getRemovedPoints() != null && !p.getRemovedPoints().isEmpty()) {
			String ids = "";
			for (Integer id : p.getRemovedPoints()) {
				if (!ids.isEmpty()) {
					ids += ",";
				}
				ids += id.intValue();
			}
			sql.set("DELETE FROM " + prefix + "points WHERE ID IN (" + ids + ")");
		}

		// Update/insert the new/changed points
		List<Point> changed = new ArrayList<Point>();
		if (p.getStartPoint() != null && p.getStartPoint().hasChanged()) {
			changed.add(p.getStartPoint());
		}

		if (p.getEndPoint() != null && p.getEndPoint().hasChanged()) {
			changed.add(p.getEndPoint());
		}

		for (Checkpoint checkpoint : p.getCheckpoints()) {
			if (checkpoint.hasChanged()) {
				changed.add(checkpoint);
			}
		}

		for (Point point : changed) {
			try {
				PreparedStatement pre = sql.getPrepare("UPDATE " + prefix + "points SET parkour_id=?, location=?, point_index=? WHERE ID = " + point.getId());
				pre.setInt(1, point.getParkour().getId());
				pre.setString(2, Utils.serializeLocation(point.getLocation()));
				pre.setInt(3, point.getIndex());
				
				pre.executeUpdate();

				point.setChanged(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void insertPoint(Point point) {
		try {
			PreparedStatement pre = sql.getPrepareAutoKeys("INSERT INTO " + prefix + "points (parkour_id,point_index,location) VALUES(?,?,?)");
			pre.setInt(1, point.getParkour().getId());
			pre.setInt(2, point.getIndex());
			pre.setString(3, Utils.serializeLocation(point.getLocation()));
			
			pre.executeUpdate();
			
			int id = sql.getIdFromPrepared(pre);
			point.setId(id);
			
			point.setChanged(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public ParkourPlayerScore getBestPlayerScore(Player p, Parkour parkour) {
		try {
			PreparedStatement pre = sql.getPrepare("SELECT * FROM " + prefix + "playerscore WHERE UUID = ? AND parkourId = ? ORDER BY timeTook ASC LIMIT 1");
			pre.setString(1, p.getUniqueId().toString());
			pre.setInt(2, parkour.getId());
			
			ResultSet rs = pre.executeQuery();
			if (rs.next()) {
				UUID player = UUID.fromString(rs.getString("UUID"));
				int parkourId = rs.getInt("parkourId");
				long date = rs.getLong("date");
				long timeTook = rs.getLong("timeTook");
				return new ParkourPlayerScore(player, parkourId, date, timeTook);
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void insertPlayerScore(ParkourPlayerScore score) {
		try {
			PreparedStatement pre = sql.getPrepare("INSERT INTO " + prefix + "playerscore (UUID,parkourId,date,timeTook) VALUES(?,?,?,?)");
			pre.setString(1, score.getPlayer().toString());
			pre.setInt(2, score.getParkourId());
			pre.setLong(3, score.getDate());
			pre.setLong(4, score.getTimeTook());
			
			pre.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
