package net.yzimroni.extremeparkour.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.point.Checkpoint;
import net.yzimroni.extremeparkour.parkour.point.Endpoint;
import net.yzimroni.extremeparkour.parkour.point.Point;
import net.yzimroni.extremeparkour.parkour.point.Startpoint;
import net.yzimroni.extremeparkour.utils.MCSQL;
import net.yzimroni.extremeparkour.utils.Utils;

public class MySqlData extends ExtremeParkourData {
	
	MCSQL sql = null;
	private String prefix = null;
	
	public MySqlData(String host, String port, String database, String username, String password, String prefix) {
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
				Parkour p = new Parkour(id, name, owner, createdTimestamp);
				
				
				ResultSet points_rs = sql.get("SELECT * FROM " + prefix + "points WHERE parkour_id=" + id + " ORDER BY point_index DESC");
				List<Checkpoint> checkpoints = new ArrayList<Checkpoint>();
				while (points_rs.next()) {
					int point_id = rs.getInt("ID");
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
					//TODO effects
				}
				p.setCheckPoints(checkpoints);
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
	public void saveParkour(Parkour p) {
		//TODO points etc
		if (p.getId() == -1) {
			//If the parkour is not on the db we need to insert the parkour (instead of update it)
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
		} else {
			if (p.hasChanged()) {
				// The parkour is already in the db, we need to update it
				try {
					PreparedStatement pre = sql.getPrepare("UPDATE " + prefix + "parkours SET name = ?, owner = ?, createdTimestamp = ?");
					pre.setString(1, p.getName());
					pre.setString(2, p.getOwner().toString());
					pre.setLong(3, p.getCreatedTimestamp());

					pre.executeUpdate();
					p.setChanged(false);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
