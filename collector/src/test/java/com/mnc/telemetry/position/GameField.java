package com.mnc.telemetry.position;

import com.google.common.collect.Streams;

import java.util.Map;
import java.util.stream.DoubleStream;

public class GameField {

	private final Point bottomLeftCorner;
	private final Point topRightCorner;
	private final Map<String, Point> sensors; // sensorId -> sensorPosition

	private final double xSize;
	private final double ySize;

	public GameField(Point bottomLeftCorner, Point topRightCorner, Map<String, Point> sensors) {
		this.bottomLeftCorner = bottomLeftCorner;
		this.topRightCorner = topRightCorner;
		this.sensors = sensors;
		this.xSize = topRightCorner.getX() - bottomLeftCorner.getX();
		this.ySize = topRightCorner.getY() - bottomLeftCorner.getY();
	}

	public Point getPosition(double xFactor, double yFactor) {
		return bottomLeftCorner.add(new Point(xSize*xFactor, ySize*yFactor));
	}

	public boolean isInside(Point p) {
		return  p.getX() > bottomLeftCorner.getX() &&
				p.getY() > bottomLeftCorner.getY() &&
				p.getX() < topRightCorner.getX()   &&
				p.getY() < topRightCorner.getY();
	}

	public Point getBottomLeftmost() {
		double minX = Streams.concat(
				DoubleStream.of(bottomLeftCorner.getX()),
				sensors.values().stream().mapToDouble(Point::getX))
				.min()
				.getAsDouble();
		double minY = Streams.concat(
				DoubleStream.of(bottomLeftCorner.getY()),
				sensors.values().stream().mapToDouble(Point::getY))
				.min()
				.getAsDouble();
		return new Point(minX, minY);
	}

	public Point getTopRightmost() {
		double maxX = Streams.concat(
				DoubleStream.of(topRightCorner.getX()),
				sensors.values().stream().mapToDouble(Point::getX))
				.max()
				.getAsDouble();
		double maxY = Streams.concat(
				DoubleStream.of(topRightCorner.getY()),
				sensors.values().stream().mapToDouble(Point::getY))
				.max()
				.getAsDouble();
		return new Point(maxX, maxY);
	}
}
