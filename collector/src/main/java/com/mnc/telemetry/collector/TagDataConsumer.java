package com.mnc.telemetry.collector;

public interface TagDataConsumer {

	void consumeData(String tag, SensorData sensorData);

}
