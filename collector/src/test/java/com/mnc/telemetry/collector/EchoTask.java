package com.mnc.telemetry.collector;

import java.util.Optional;
import java.util.function.Supplier;

class EchoTask {

	static Supplier<Optional<String>> consume(String data) {
		return () -> Optional.of(data);
	}
}