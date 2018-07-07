package com.mnc.telemetry;

import com.mnc.telemetry.collector.SensorData;
import com.mnc.telemetry.collector.TagDataCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class PositionReporterService {

	private final TagDataCollector tagDataCollector;

	@Autowired
	public PositionReporterService(TagDataCollector tagDataCollector) {
		this.tagDataCollector = tagDataCollector;
	}

	@RequestMapping("/positions")
	public Map<String, List<SensorData>> getPositions() {
		return tagDataCollector.getAllSensorData();
	}
}
