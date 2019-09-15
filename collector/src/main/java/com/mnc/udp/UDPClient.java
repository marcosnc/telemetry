package com.mnc.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Optional;

public class UDPClient {
	private DatagramSocket socket;
	private InetAddress address;

	private byte[] dataToReceive = new byte[256];
	private int port;
	private boolean waitForResponse;

	public UDPClient(int port, boolean waitForResponse) {
		this("localhost", port, waitForResponse);
	}

	public UDPClient(String addressName, int port, boolean waitForResponse) {
		this.port = port;
		this.waitForResponse = waitForResponse;

		try {
			socket = new DatagramSocket();
			address = InetAddress.getByName(addressName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized Optional<String> send(String msg) throws IOException {
		byte[] dataToSend = msg.getBytes();
		socket.send(new DatagramPacket(dataToSend, dataToSend.length, address, port));
		if (waitForResponse) {
			DatagramPacket packet = new DatagramPacket(dataToReceive, dataToReceive.length);
			socket.receive(packet);
			return Optional.of(new String(packet.getData(), 0, packet.getLength()));
		}
		return Optional.empty();
	}

	public void close() {
		socket.close();
	}
}
