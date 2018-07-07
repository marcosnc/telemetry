package com.mnc.telemetry.collector;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class NonResponsePacketConsumerFactory implements PacketConsumerFactory {

	@Override
	public Supplier<Optional<String>> createConsumer(String data) {
		return () -> {
			createConsumer().accept(data);
			return Optional.empty();
		};
	}

	protected abstract Consumer<String> createConsumer();
}
