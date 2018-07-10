package com.mnc.telemetry.position;

import java.util.Random;

public class GameSimulation {
	private static final double PLAYER_MOVEMENT_RADIO = 1.0;
	private final GameField gameField;
	private final Player[] players;
	private final Random random;

	public GameSimulation(GameField gameField, int playersCount) {
		this.gameField = gameField;
		this.players = new Player[playersCount];
		this.random = new Random();
		for(int i=0; i<playersCount; i++) {
			players[i] = new Player(
							String.format("player-%02d", i),
							gameField.getPosition(random.nextDouble(), random.nextDouble()));
		}
	}

	public void advanceSimulation() {
		for (Player player: players) {
			player.setPosition(moveRandomlyInsideTheGameField(player.getPosition()));
		}
	}

	public Player[] getPlayerPositions() {
		return players;
	}

	private Point moveRandomlyInsideTheGameField(Point p) {
		while(true) {
			double newX = p.getX() - PLAYER_MOVEMENT_RADIO + (random.nextDouble() * 2.0 * PLAYER_MOVEMENT_RADIO);
			double newY = p.getY() - PLAYER_MOVEMENT_RADIO + (random.nextDouble() * 2.0 * PLAYER_MOVEMENT_RADIO);
			Point newPosition = new Point(newX, newY);
			if (gameField.isInside(newPosition)) {
				return newPosition;
			}
		}
	}
}
