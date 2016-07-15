package net.yzimroni.extremeparkour.data;

import net.yzimroni.extremeparkour.utils.MCSQL;

public class MySqlData extends ExtremeParkourData {
	
	MCSQL sql = null;
	
	public MySqlData(String host, String port, String database, String username, String password) {
		sql = new MCSQL(host, port, database, username, password);
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

}
