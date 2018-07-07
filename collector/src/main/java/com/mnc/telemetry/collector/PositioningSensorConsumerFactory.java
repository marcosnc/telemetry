package com.mnc.telemetry.collector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PositioningSensorConsumerFactory extends NonResponsePacketConsumerFactory {

	private static Logger logger = LoggerFactory.getLogger(PositioningSensorConsumerFactory.class);

	private static final int SENSOR_GROUP = 1;
	private static final int TAG_GROUP = 2;
	private static final int DISTANCE_GROUP = 3;

	private static final Pattern PACKET_PATTERN = Pattern.compile("Faro ([0-9]{1,2}), Tag ([0-9]{1,2}), ([0-9]{0,5})");

	private final TagDataCollector tagDataCollector;

	@Autowired
	public PositioningSensorConsumerFactory(TagDataCollector tagDataCollector) {
		this.tagDataCollector = tagDataCollector;
	}

	@Override
	protected Consumer<String> createConsumer() {
		final long receivedTime = System.currentTimeMillis();
		return data -> {
			Matcher matcher = PACKET_PATTERN.matcher(data);
			if (matcher.matches()) {
				final String sensor   = matcher.group(SENSOR_GROUP);
				final String tag      = matcher.group(TAG_GROUP);
				final String distance = matcher.group(DISTANCE_GROUP);
				SensorData sensorData = new SensorData(receivedTime, sensor, distance);
				tagDataCollector.storeData(tag, sensorData);
				logger.info("Data stored: {} -> {}", tag, sensorData);
			} else {
				logger.warn("Input does not match expected format: {}", data);
			}

		};
	}

}
