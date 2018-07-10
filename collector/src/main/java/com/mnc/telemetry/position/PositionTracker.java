package com.mnc.telemetry.position;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PositionTracker {

	private Map<String, Point> sensorPositions; // sensorId -> sensorPosition (as a point)

	private ConcurrentMap<String, PositionHolder> tagPositions = new ConcurrentHashMap<>(); // tagId -> PositionHolder

	public PositionTracker(Map<String, Point> sensorPositions) {
		this.sensorPositions = sensorPositions;
	}

	public void newDistance(String tagId, String sensorId, long time, double distance) {
		tagPositions
				.computeIfAbsent(tagId, p -> new PositionHolder())
				.addDistance(sensorPositions.get(sensorId), time, distance);
	}

	public Map<String, Point> getPositions() {
		return tagPositions.entrySet().stream()
				.filter(entry -> entry.getValue().getPosition()!=null)
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						e -> e.getValue().getPosition()));
	}

	private static class PositionHolder {
		private static final double TOLERANCE = 1.0;

		Point lastPosition;
		long lastPositionTime;
		Map<Point, Double> newDistances = new HashMap<>(); // sensorPosition -> distance to that sensor

		public void addDistance(Point sensorPosition, long time, double distance) {
			newDistances.put(sensorPosition, distance);
			Optional<Point> newPosition = calculatePositionFromDistances();
			if (newPosition.isPresent()) {
				lastPosition = newPosition.get();
				lastPositionTime = time;
				newDistances.clear();
			}
		}

		private Optional<Point> calculatePositionFromDistances() {
			if (newDistances.size() < 3) {
				return Optional.empty();
			}
			Point[]  centers = new Point[3];
			Double[] radious = new Double[3];
			int i = 0;
			for(Map.Entry<Point, Double> distance : newDistances.entrySet()) {
				centers[i] = distance.getKey();
				radious[i] = distance.getValue();
				i++;
			}
			Intersection i01 = intersection(centers[0], radious[0], centers[1], radious[1]);
			Intersection i02 = intersection(centers[0], radious[0], centers[2], radious[2]);

			return firstPresent(
					() -> isSamePoint(i01.getP1(), i02.getP1()),
					() -> isSamePoint(i01.getP1(), i02.getP2()),
					() -> isSamePoint(i01.getP2(), i02.getP1()),
					() -> isSamePoint(i01.getP2(), i02.getP2()));
		}

		private <T> Optional<T> firstPresent(Supplier<Optional<T>> ... suppliers) {
			for(Supplier<Optional<T>> supplier : suppliers) {
				Optional<T> value = supplier.get();
				if(value.isPresent()) {
					return value;
				}
			}
			return Optional.empty();
		}

		private Optional<Point> isSamePoint(Optional<Point> a, Optional<Point> b) {
			if (a.isPresent() && b.isPresent()) {
				return isSamePoint(a.get(), b.get());
			}
			return Optional.empty();
		}

		private Optional<Point> isSamePoint(Point a, Point b) {
			double distance = a.distance(b);
			if (distance < TOLERANCE) {
				return Optional.of(a.add(b).divide(2.0));
			}
			return Optional.empty();
		}

		private Intersection intersection(Point p0, double r0, Point p1, double r1) {
			if (r0 < r1) {
				Point pAux = p0;
				p0 = p1;
				p1 = pAux;
				double rAux = r0;
				r0 = r1;
				r1 = rAux;
			}
			double d = p1.substract(p0).modulus();

			if (d > r0+r1 || d < Math.abs(r0-r1)) {
				// No solution
				return Intersection.EMPTY;
			}

			double a = (r0*r0 - r1*r1 + d*d) / (2.0*d);

			Point p2 = p0.add(p1.substract(p0).times(a).divide(d));

			double h = Math.sqrt(r0*r0 - a*a);

			double xAux = (h * (p1.getY()-p0.getY())) / d;
			double yAux = (h * (p1.getX()-p0.getX())) / d;

			Point p3a = new Point(p2.getX() + xAux, p2.getY() - yAux);
			Point p3b = new Point(p2.getX() - xAux, p2.getY() + yAux);

			Optional<Point> p3 = isSamePoint(p3a, p3b);
			return p3.map(point -> new Intersection(point, null))
					.orElseGet(() -> new Intersection(p3a, p3b));

		}

		public Point getPosition() {
			return lastPosition;
		}

	}

	private static class Intersection {
		public static final Intersection EMPTY = new Intersection(null, null);

		private final Point p1;
		private final Point p2;

		private Intersection(Point p1, Point p2) {
			this.p1 = p1;
			this.p2 = p2;
		}

		public Optional<Point> getP1() {
			return Optional.ofNullable(p1);
		}

		public Optional<Point> getP2() {
			return Optional.ofNullable(p2);
		}
	}

}

