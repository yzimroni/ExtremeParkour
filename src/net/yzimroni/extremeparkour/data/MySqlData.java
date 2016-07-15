package net.yzimroni.extremeparkour.data;

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

}
