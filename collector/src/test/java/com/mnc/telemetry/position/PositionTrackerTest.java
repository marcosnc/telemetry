package com.mnc.telemetry.position;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class PositionTrackerTest {

	@Test
	public void simulation() {
		Point bottomLeftCorner = new Point( 5, 5);
		Point topRightCorner   = new Point(25,15);
		Map<String, Point> sensorPositions = new ImmutableMap.Builder<String, Point>()
				.put("sensor-1", new Point(10,4))
				.put("sensor-2", new Point(20,4))
				.put("sensor-3", new Point(15,16))
				.build();
		GameField gameField = new GameField(bottomLeftCorner, topRightCorner, sensorPositions);
		GameSimulation gameSimulation = new GameSimulation(gameField, 2);

		System.out.println("Initial Position:");
		for(Player player : gameSimulation.getPlayerPositions()) {
			Assert.assertTrue(gameField.isInside(player.getPosition()));
			System.out.println("  Player: " + player.getId() + "  -> " + player.getPosition());
		}
		System.out.println("------------------------------");
		for(int step=0; step<100; step++) {
			gameSimulation.advanceSimulation();
			System.out.println("Step: " + step);
			for(Player player : gameSimulation.getPlayerPositions()) {
				Assert.assertTrue(gameField.isInside(player.getPosition()));
				System.out.println("  Player: " + player.getId() + "  -> " + player.getPosition());
			}
			System.out.println("------------------------------");
		}

	}

}
