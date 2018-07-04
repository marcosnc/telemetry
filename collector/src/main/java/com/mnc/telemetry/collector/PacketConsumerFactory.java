package com.mnc.telemetry.collector;

import java.util.Optional;
import java.util.function.Supplier;

public interface PacketConsumerFactory {

	Supplier<Optional<String>> createConsumer(String data);
}
