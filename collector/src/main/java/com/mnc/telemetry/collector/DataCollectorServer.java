package com.mnc.telemetry.collector;

import com.mnc.udp.UDPServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataCollectorServer extends UDPServer {

	public static final int PORT = 8081;

	@Autowired
	public DataCollectorServer(PositioningSensorConsumerFactory taskFactory) {
		super(PORT, taskFactory);
	}
}
