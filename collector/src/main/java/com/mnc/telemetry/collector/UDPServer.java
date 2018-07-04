package com.mnc.telemetry.collector;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class UDPServer extends Thread {

	private int port;
	private PacketConsumerFactory taskFactory;
	private boolean running;
	private byte[] buf = new byte[256];
	private ExecutorService executor;
	private DatagramSocket socket;

	public UDPServer(int port, PacketConsumerFactory taskFactory) {
		super();
		this.port = port;
		this.taskFactory = taskFactory;
		this.executor = Executors.newFixedThreadPool(5);
	}

	public void terminate() {
		this.running = false;
		this.interrupt();
		this.socket.close();
		executor.shutdown();
	}

	public void run() {
		running = true;
		try {
			socket = new DatagramSocket(port);
			while (running) {
				DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
				socket.receive(receivedPacket);

				String receivedData = new String(receivedPacket.getData(), receivedPacket.getOffset(), receivedPacket.getLength());

				executor.execute(new ExecutableTask(
						socket,
						receivedPacket.getSocketAddress(),
						receivedData,
						taskFactory.createConsumer(receivedData)));
			}
		} catch (Exception e) {
			if (running) {
				e.printStackTrace();
			}
		}
	}

	private static class ExecutableTask implements Runnable {

		private final DatagramSocket socket;
		private final SocketAddress socketAddress;
		private final String receivedData;
		private final Supplier<Optional<String>> consumer;

		ExecutableTask(DatagramSocket socket, SocketAddress socketAddress, String receivedData, Supplier<Optional<String>> consumer) {
			this.socket = socket;
			this.socketAddress = socketAddress;
			this.receivedData = receivedData;
			this.consumer = consumer;
		}

		@Override
		public void run() {
			Optional<String> response = consumer.get();
			if (response.isPresent()) {
//				System.out.printf("Server: <%s> -> <%s> (%s)\n", receivedData, response.get(), socketAddress);
				byte[] responseData = response.get().getBytes();
				DatagramPacket responsePacket = new DatagramPacket(responseData, 0, responseData.length, socketAddress);
				try {
					socket.send(responsePacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

}
