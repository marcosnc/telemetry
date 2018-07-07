package com.mnc.telemetry;

import com.mnc.telemetry.collector.DataCollectorServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PreDestroy;

@SpringBootApplication
public class Main {

	private final DataCollectorServer dataCollectorServer;

	@Autowired
	public Main(DataCollectorServer dataCollectorServer) {
		this.dataCollectorServer = dataCollectorServer;
	}

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

	@Bean
	public CommandLineRunner schedulingRunner() {
		return args -> dataCollectorServer.start();
	}

	@PreDestroy
	public void finish() {
		dataCollectorServer.terminate();
	}

}
