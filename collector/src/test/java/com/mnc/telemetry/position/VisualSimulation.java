package com.mnc.telemetry.position;

import com.google.common.collect.ImmutableMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class VisualSimulation extends Canvas {

	private static final int xSize = 600;
	private static final int ySize = 600;

	public static void main(String[] args) {
		JFrame frame = new JFrame("Visual Simulation");
		Canvas canvas = new VisualSimulation();
		canvas.setSize(xSize, ySize);
		frame.add(canvas);
		frame.pack();
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				System.exit(0);
			}
		});
	}

	private Point bottomLeftCorner = new Point( 5, 5);
	private Point topRightCorner   = new Point(25,15);
	private Map<String, Point> sensorPositions = new ImmutableMap.Builder<String, Point>()
			.put("sensor-1", new Point(10,4))
			.put("sensor-2", new Point(20,4))
			.put("sensor-3", new Point(15,16))
			.build();
	private GameField gameField = new GameField(bottomLeftCorner, topRightCorner, sensorPositions);
	private GameSimulation gameSimulation = new GameSimulation(gameField, 10);

	private int xMargin = 20;
	private int yMargin = 20;
	private Point bottomLeftmost = gameField.getBottomLeftmost();
	private Point topRightmost   = gameField.getTopRightmost();
	private double gameFieldXSize = topRightmost.getX() - bottomLeftmost.getX();
	private double gameFieldYSize = topRightmost.getY() - bottomLeftmost.getY();

	private PositionTracker positionTracker = new PositionTracker(sensorPositions);
	private Random random = new Random();

	public VisualSimulation() throws HeadlessException {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				gameSimulation.advanceSimulation();
				repaint();
			}
		});
	}


	public void paint(Graphics g) {
		g.setColor(Color.red);
		g.drawRect(
				getX(bottomLeftCorner.getX()),
				getY(bottomLeftCorner.getY()),
				getX(topRightCorner.getX())-getX(bottomLeftCorner.getX()),
				getY(topRightCorner.getY())-getY(bottomLeftCorner.getY()));

		g.setColor(Color.blue);
		sensorPositions.entrySet().stream().forEach(entry -> {
			g.fillRect(
					getX(entry.getValue().getX()) - 3,
					getY(entry.getValue().getY()) - 3,
					6, 6);
			g.drawString(entry.getKey(), getX(entry.getValue().getX()) + 6, getY(entry.getValue().getY()) + 6);

		});

		g.setColor(Color.black);
		Arrays.stream(gameSimulation.getPlayerPositions()).forEach(player -> {
			g.fillOval(
					getX(player.getPosition().getX()) - 3,
					getY(player.getPosition().getY()) - 3,
					6, 6);
			g.drawString(player.getId(), getX(player.getPosition().getX()) + 6, getY(player.getPosition().getY()) + 6);
		});


		System.out.println("------------------------------");
		System.out.println("Distances:");
		System.out.println("------------------------------");
		Arrays.stream(gameSimulation.getPlayerPositions()).forEach(player -> {
			System.out.println("Player: " + player.getId());
			sensorPositions.entrySet().stream().forEach(entry -> {
				double distance = player.getPosition().distance(entry.getValue());
				System.out.printf("  %s: %.3f\n", entry.getKey(), distance);
				double error = random.nextDouble() / 50;
				if (random.nextBoolean()) {
					error = -error;
				}
				positionTracker.newDistance(player.getId(), entry.getKey(), System.currentTimeMillis(), distance + error);
			});
		});
		System.out.println("------------------------------");

		System.out.println("------------------------------");
		System.out.println("Tracking:");
		System.out.println("------------------------------");
		Arrays.stream(gameSimulation.getPlayerPositions()).forEach(player -> {
			Point trackedPoint = positionTracker.getPositions().get(player.getId());
			System.out.printf("  %s: (%02.3f,%2.3f) -> (%02.3f,%2.3f) (%.3f)\n",
					player.getId(),
					player.getPosition().getX(), player.getPosition().getY(),
					trackedPoint.getX(), trackedPoint.getY(),
					player.getPosition().substract(trackedPoint).modulus());

		});
		System.out.println("------------------------------");
	}

	private int getX(double x) {
		// gameFieldXSize          -> xSize-2*xMargin
		// x-bottomLeftmost.getX() -> (x-bottomLeftmost.getX()) * (xSize-2*xMargin) / gameFieldXSize
		//-----
		return (int) ((x-bottomLeftmost.getX()) * (xSize-2.0*xMargin) / gameFieldXSize) + xMargin;
	}

	private int getY(double y) {
		// gameFieldYSize          -> ySize-2*yMargin
		// y-bottomLeftmost.getY() -> (y-bottomLeftmost.getY()) * (ySize-2*yMargin) / gameFieldYSize
		//-----
		return (int) ((y-bottomLeftmost.getY()) * (ySize-2.0*yMargin) / gameFieldYSize) + yMargin;
	}
}
