package net.yzimroni.extremeparkour.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.utils.MCSQL;

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
				
				//TODO points etc
				
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
