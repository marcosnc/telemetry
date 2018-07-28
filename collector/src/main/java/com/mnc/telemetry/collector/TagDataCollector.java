package com.mnc.telemetry.collector;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Component
public class TagDataCollector implements TagDataConsumer {

	private ConcurrentMap<String, List<SensorData>> tagsData = new ConcurrentHashMap<>();

	@Override
	public void consumeData(String tag, SensorData sensorData) {
		storeData(tag, sensorData);
	}

	public void storeData(String tag, SensorData sensorData) {
		getTagData(tag).add(sensorData);
	}

	public List<SensorData> getTagData(String tag) {
		return tagsData.computeIfAbsent(tag, p -> new LinkedList<>());
	}

	public Map<String, List<SensorData>> getAllSensorData() {
		return new HashMap<>(tagsData);
	}

	public Map<String, SensorData> getLastSensorsData() {
		return tagsData.entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						e -> e.getValue().get(e.getValue().size()-1)));
	}

}
