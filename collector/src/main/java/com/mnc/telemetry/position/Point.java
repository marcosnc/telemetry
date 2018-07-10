package com.mnc.telemetry.position;

class Point {
	private final double x;
	private final double y;

	public Point(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public Point add(Point p) {
		return new Point(x+p.x, y+p.y);
	}

	public Point substract(Point p) {
		return new Point(x-p.x, y-p.y);
	}

	public Point times(double factor) {
		return new Point(x*factor, y*factor);
	}

	public Point divide(double divisor) {
		return new Point(x/divisor, y/divisor);
	}

	public double modulus() {
		return Math.sqrt(x*x + y*y);
	}

	public double distance(Point p) {
		double distanceX = x - p.x;
		double distanceY = y - p.y;

		return Math.sqrt(distanceX*distanceX + distanceY*distanceY);
	}

	@Override
	public String toString() {
		return "Point (x=" + x + ", y=" + y + ")";
	}
}
