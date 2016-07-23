package net.yzimroni.extremeparkour.parkour.edit;

import java.util.UUID;

import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.point.Point;

public class EditData {

	private UUID uuid;
	private Parkour parkour;
	private Point point;

	public EditData(UUID uuid, Parkour parkour) {
		super();
		this.uuid = uuid;
		this.parkour = parkour;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public Parkour getParkour() {
		return parkour;
	}

	public void setParkour(Parkour parkour) {
		this.parkour = parkour;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

}
