package net.yzimroni.extremeparkour.parkour.edit;

import java.util.UUID;

import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.point.Point;
import net.yzimroni.extremeparkour.parkour.point.PointMode;

public class EditData {

	private UUID uuid;
	private Parkour parkour;
	private Point point;
	private PointMode selectedMode;

	public EditData(UUID uuid, Parkour parkour) {
		super();
		this.uuid = uuid;
		this.parkour = parkour;
		this.selectedMode = PointMode.BLOCK;
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

	public PointMode getSelectedMode() {
		return selectedMode;
	}

	public void setSelectedMode(PointMode selectedMode) {
		this.selectedMode = selectedMode;
	}

}
