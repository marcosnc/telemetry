package com.mnc.telemetry.collector;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class TagDataCollector {

	private ConcurrentMap<String, List<SensorData>> tagsData = new ConcurrentHashMap<>();

	public void storeData(String tag, SensorData sensorData) {
		List<SensorData> tagData = getTagData(tag);
		tagData.add(sensorData);
		tagsData.putIfAbsent(tag, tagData);
	}

	public List<SensorData> getTagData(String tag) {
		return tagsData.getOrDefault(tag, new LinkedList<>());
	}

	public Map<String, List<SensorData>> getAllSensorData() {
		return new HashMap<>(tagsData);
	}
}
