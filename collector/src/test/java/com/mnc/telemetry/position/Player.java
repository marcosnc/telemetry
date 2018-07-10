package com.mnc.telemetry.position;

public class Player {
	private final String id;
	private Point position;

	Player(String id, Point initialPosition) {
		this.id = id;
		this.position = initialPosition;
	}

	public String getId() {
		return id;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}
}
