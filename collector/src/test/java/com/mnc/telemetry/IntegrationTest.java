package com.mnc.telemetry;

import com.google.common.collect.ImmutableMap;
import com.mnc.telemetry.collector.PositioningSensorConsumerFactory;
import com.mnc.telemetry.collector.TagDataConsumer;
import com.mnc.telemetry.collector.UDPClient;
import com.mnc.telemetry.collector.UDPServer;
import com.mnc.telemetry.position.Point;
import com.mnc.telemetry.position.PositionTracker;

import java.util.Map;

public class IntegrationTest extends Thread {

	private Map<String, Point> sensorPositions = new ImmutableMap.Builder<String, Point>()
			.put("2", new Point(0.0,0.0))
			.put("3", new Point(6.4,0.0))
			.put("1", new Point(4.1,3.4))
			.build();

	private Map<String, Double> sensorCalibration = new ImmutableMap.Builder<String, Double>()
			.put("1", -0.6)
			.put("2", -0.6)
			.put("3", -0.6)
			.build();

	private PositionTracker positionTracker = new PositionTracker(sensorPositions);

//	private Point bottomLeftCorner = new Point( 5, 5);
//	private Point topRightCorner   = new Point(25,15);
//	private GameField gameField = new GameField(bottomLeftCorner, topRightCorner, sensorPositions);

//	private GameSimulation gameSimulation = new GameSimulation(gameField, 10);

	private int serverPort = 5555;
	private UDPServer udpServer;

	private static boolean performHandshake = false;

	public static void main(String[] args) {
		try {
//			((Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)).setLevel(Level.INFO);

			if (performHandshake) {
				// Initial handshaking
				UDPClient client = new UDPClient("192.168.0.8", 5555, true);
				System.out.println(client.send("INIT").orElse("NO-RESPONSE"));
				System.out.println(client.send("2").orElse("NO-RESPONSE"));
				System.out.println(client.send("12").orElse("NO-RESPONSE"));
				client.close();
			}

			IntegrationTest integrationTest = new IntegrationTest();
			integrationTest.start();

			Thread.sleep(5 * 60 * 1000);

			integrationTest.interrupt();
			integrationTest.join();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Double dist1;
	private Double dist2;
	private Double dist3;
	private long lastLog;

	public IntegrationTest() {
		// Data Collection initialization
		TagDataConsumer tagDataConsumer = (tag, sensorData) -> {
			double measuredDistance = Double.valueOf(sensorData.getData());
			double correctedDistance = measuredDistance + sensorCalibration.get(sensorData.getSensorId());

			if(sensorData.getSensorId().equals("1")) { dist1 = correctedDistance; }
			if(sensorData.getSensorId().equals("2")) { dist2 = correctedDistance; }
			if(sensorData.getSensorId().equals("3")) { dist3 = correctedDistance; }
			if (System.currentTimeMillis()-lastLog > 500) {
				System.out.printf("%.3f, %.3f, %.3f\n", dist1, dist2, dist3);
				lastLog = System.currentTimeMillis();
			}

			positionTracker.newDistance(
					tag,
					sensorData.getSensorId(),
					System.currentTimeMillis(),
					correctedDistance);
		};
		PositioningSensorConsumerFactory positioningSensorConsumerFactory = new PositioningSensorConsumerFactory(tagDataConsumer);

		this.udpServer = new UDPServer(serverPort, positioningSensorConsumerFactory);
		udpServer.start();
	}

	public void run() {
		while(!this.isInterrupted()) {
			System.out.println("---------------------------------------------");
			positionTracker.getPositions().forEach((tag, point) -> {
				System.out.printf("   %s: %s\n", tag, point);
			});
			System.out.println("---------------------------------------------");
			System.out.println();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		udpServer.terminate();
		try {
			udpServer.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}




}
