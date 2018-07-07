package com.mnc.telemetry.collector;

public class SensorData {
	private final long time;
	private final String sensorId;
	private final String data;

	public SensorData(long time, String sensorId, String data) {
		this.time = time;
		this.sensorId = sensorId;
		this.data = data;
 	}

	public long getTime() {
		return time;
	}

	public String getSensorId() {
		return sensorId;
	}

	public String getData() {
		return data;
	}

	@Override
	public String toString() {
		return String.format("(%d, %s, %s)", time, sensorId, data);
	}
}
