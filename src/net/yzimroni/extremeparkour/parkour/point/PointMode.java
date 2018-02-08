package net.yzimroni.extremeparkour.parkour.point;

public enum PointMode {

	BLOCK(false), DISTANCE(true), TRIPWIRE(true);

	private boolean radius;

	PointMode(boolean radius) {
		this.radius = radius;
	}

	public boolean isRadiusPoint() {
		return radius;
	}

}
