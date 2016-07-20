package net.yzimroni.extremeparkour.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.ParkourLeaderboard;
import net.yzimroni.extremeparkour.parkour.manager.player.ParkourPlayerScore;
import net.yzimroni.extremeparkour.parkour.point.Checkpoint;
import net.yzimroni.extremeparkour.parkour.point.Endpoint;
import net.yzimroni.extremeparkour.parkour.point.Point;
import net.yzimroni.extremeparkour.parkour.point.PointEffect;
import net.yzimroni.extremeparkour.parkour.point.Startpoint;
import net.yzimroni.extremeparkour.utils.DataStatus;
import net.yzimroni.extremeparkour.utils.MCSQL;
import net.yzimroni.extremeparkour.utils.Utils;

public class SQLData {
	
	MCSQL sql = null;
	private String prefix = null;
	private SQLType type = null;
	private  ExtremeParkourPlugin plugin;
	
	public SQLData(ExtremeParkourPlugin plugin, String prefix) {
		this.plugin = plugin;
		if (prefix == null) {
			prefix = "";
		}
		this.prefix = prefix;
		this.sql = new MCSQL();
	}
	
	public void openMySQL(String host, String port, String database, String username, String password) throws Exception {
		sql.openMySQLConnection(host, port, database, username, password);
		type = SQLType.MySQL;
		createTables();
	}
	
	public void openSQLite(String file) throws Exception {
		sql.openSQLiteConnection(file);
		type = SQLType.SQLite;
		createTables();
	}
	
	private void createTables() throws Exception {
		/*
		 * To get a file to use here:
		 * Export from phpmyadmin the table structure (using the template 'structure')
		 * Remove the phpmyadmin header from it
		 * add %prefix% before each table name
		 * 
		 * To add compat with sqlite:
		 * Remove all auto increment field and put "`ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
		 * Remove "PRIMARY KEY (`ID`)" and the "," the line above
		 * Remove "ENGINE=InnoDB DEFAULT CHARSET=utf8" from the last line
		 */
		InputStream stream = plugin.getResource("sql/" + type.name().toLowerCase() + ".sql");
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String query = "";
        while ((line = reader.readLine()) != null) {
        	if (line.isEmpty() || line.startsWith("#") || line.startsWith("--")) {
        		continue;
        	}
        	line = line.replaceAll("%prefix%", prefix);
        	query += line;
        	if (line.endsWith(";")) {
        		sql.set(query);
        		query = "";
        	}
        }
        reader.close();
		
	}
	
	public void disable() {
		sql.disable();
		sql = null;
	}
	

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
					
					ResultSet effects_rs = sql.get("SELECT * FROM " + prefix + "point_effects WHERE pointId=" + point_id);
					List<PointEffect> effects = new ArrayList<PointEffect>();
					while (effects_rs.next()) {
						int effect_id = effects_rs.getInt("ID");
						PotionEffectType type = PotionEffectType.getByName(effects_rs.getString("type"));
						int duration = effects_rs.getInt("duration");
						int amplifier = effects_rs.getInt("amplifier");
						boolean particles = effects_rs.getBoolean("particles");
						PointEffect effect = new PointEffect(effect_id, type, duration, amplifier, particles);
						effects.add(effect);
					}
					point.setEffects(effects);
				}
				p.setCheckpoints(checkpoints);
				
				ResultSet leaderboard_rs = sql.get("SELECT * FROM " + prefix + "parkour_leaderboards WHERE parkourId=" + id);
				List<ParkourLeaderboard> leaderboards = new ArrayList<ParkourLeaderboard>();
				while (leaderboard_rs.next()) {
					int leaderboard_id = leaderboard_rs.getInt("ID");
					Location location = Utils.deserializeLocation(leaderboard_rs.getString("location"));
					int playerCount = leaderboard_rs.getInt("players_count");
					int page = leaderboard_rs.getInt("page");
					
					ParkourLeaderboard leaderboard = new ParkourLeaderboard(leaderboard_id, p, location, playerCount, page);
					leaderboards.add(leaderboard);
				}
				p.setLeaderboards(leaderboards);
				//TODO points, ladderboard etc
				
				p.setChanged(false);
				parkours.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return parkours;
	}
	
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
			p.setRemovedPoints(null);
		}

		// Update/insert the new/changed points
		List<Point> points = new ArrayList<Point>();
		if (p.getStartPoint() != null) {
			points.add(p.getStartPoint());
		}

		if (p.getEndPoint() != null) {
			points.add(p.getEndPoint());
		}

		for (Checkpoint checkpoint : p.getCheckpoints()) {
			points.add(checkpoint);
		}

		for (Point point : points) {
			if (point.hasChanged()) {
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
			if (point.getRemovedEffects() != null && !point.getRemovedEffects().isEmpty()) {
				String ids = "";
				for (Integer id : point.getRemovedEffects()) {
					if (!ids.isEmpty()) {
						ids += ",";
					}
					ids += id.intValue();
				}
				sql.set("DELETE FROM " + prefix + "point_effects WHERE ID IN (" + ids + ")");
				point.setRemovedEffects(null);
			}
			
			for (PointEffect effect : point.getEffects()) {
				if (effect.getStatus() != null) {
					if (effect.getStatus() == DataStatus.CREATED) {
						try {
							PreparedStatement pre = sql.getPrepareAutoKeys("INSERT INTO " + prefix 
									+ "point_effects (pointId,type,duration,amplifier,particles) VALUES(?,?,?,?,?)");
							pre.setInt(1, point.getId());
							pre.setString(2, effect.getType().getName());
							pre.setInt(3, effect.getDuration());
							pre.setInt(4, effect.getAmplifier());
							pre.setBoolean(5, effect.isShowParticles());
							
							pre.executeUpdate();
							
							int id = sql.getIdFromPrepared(pre);
							effect.setId(id);
							effect.setStatus(null);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (effect.getStatus() == DataStatus.UPDATED) {
						try {
							PreparedStatement pre = sql.getPrepare("UPDATE " + prefix
									+ "parkour_leaderboards SET pointId=?, type=?, duration=?, amplifier=?, particles=? WHERE ID = "
									+ effect.getId());

							pre.setInt(1, point.getId());
							pre.setString(2, effect.getType().getName());
							pre.setInt(3, effect.getDuration());
							pre.setInt(4, effect.getAmplifier());
							pre.setBoolean(5, effect.isShowParticles());

							pre.executeUpdate();
							
							effect.setStatus(null);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} 
		
		// Delete from the database the leaderboards that were deleted in-game
		if (p.getRemovedLeaderboards() != null && !p.getRemovedLeaderboards().isEmpty()) {
			String ids = "";
			for (Integer id : p.getRemovedLeaderboards()) {
				if (!ids.isEmpty()) {
					ids += ",";
				}
				ids += id.intValue();
			}
			sql.set("DELETE FROM " + prefix + "parkour_leaderboards WHERE ID IN (" + ids + ")");
			p.setRemovedLeaderboards(null);
		}
		
		for (ParkourLeaderboard leaderboard : p.getLeaderboards()) {
			if (leaderboard.getStatus() != null) {
				try {
					if (leaderboard.getStatus() == DataStatus.CREATED) {
						PreparedStatement pre = sql.getPrepareAutoKeys("INSERT INTO " + prefix
								+ "parkour_leaderboards (parkourId,location,players_count,page) VALUES(?,?,?,?)");
						pre.setInt(1, leaderboard.getParkour().getId());
						pre.setString(2, Utils.serializeLocation(leaderboard.getLocation()));
						pre.setInt(3, leaderboard.getPlayerCount());
						pre.setInt(4, leaderboard.getPage());

						pre.executeUpdate();

						int id = sql.getIdFromPrepared(pre);
						leaderboard.setId(id);
						leaderboard.setStatus(null);
					} else if (leaderboard.getStatus() == DataStatus.UPDATED) {
						PreparedStatement pre = sql.getPrepare("UPDATE " + prefix + "parkour_leaderboards SET parkourId=?, location=?, players_count=?, page=? WHERE ID = " + leaderboard.getId());
						pre.setInt(1, leaderboard.getParkour().getId());
						pre.setString(2, Utils.serializeLocation(leaderboard.getLocation()));
						pre.setInt(3, leaderboard.getPlayerCount());
						pre.setInt(4, leaderboard.getPage());
						
						pre.executeUpdate();
						leaderboard.setStatus(null);
					} else {
						// TODO debug
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

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
	
	public List<ParkourPlayerScore> getTopPlayerScore(Parkour parkour, int playercount, int page) {
		/*
		 * TODO take page into account
		 */
		try {
			ResultSet rs = sql.get("SELECT * FROM " + prefix + "playerscore WHERE parkourId=" + parkour.getId() + " ORDER BY timeTook ASC LIMIT " + playercount);
			List<ParkourPlayerScore> players = new ArrayList<ParkourPlayerScore>();
			while (rs.next()) {
				ParkourPlayerScore player = new ParkourPlayerScore(UUID.fromString(rs.getString("UUID")), parkour.getId(), rs.getLong("date"), rs.getLong("timeTook"));
				players.add(player);
			}
			return players;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void resetLeaderboard(Parkour parkour) {
		sql.set("DELETE FROM " + prefix + "playerscore WHERE parkourId=" + parkour.getId());
		plugin.getParkourManager().initLeaderboard(parkour);
	}
	
	public void deleteParkour(Parkour parkour) {
		sql.set("DELETE FROM " + prefix + "point_effects WHERE pointId IN (SELECT ID FROM " + prefix + "points WHERE parkour_id=" + parkour.getId() + ")");
		sql.set("DELETE FROM " + prefix + "points WHERE parkour_id=" + parkour.getId());
		sql.set("DELETE FROM " + prefix + "playerscore WHERE parkourId=" + parkour.getId());
		sql.set("DELETE FROM " + prefix + "parkour_leaderboards WHERE parkourId=" + parkour.getId());
		sql.set("DELETE FROM " + prefix + "parkours WHERE ID=" + parkour.getId());
	}

}
