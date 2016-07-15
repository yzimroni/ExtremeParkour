package net.yzimroni.extremeparkour.data;

import java.util.List;

import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.utils.MCSQL;

public class MySqlData extends ExtremeParkourData {
	
	MCSQL sql = null;
	private String prefix = null;
	
	public MySqlData(String host, String port, String database, String username, String password, String prefix) {
		sql = new MCSQL(host, port, database, username, password);
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
		//TODO
		return null;
	}

}
