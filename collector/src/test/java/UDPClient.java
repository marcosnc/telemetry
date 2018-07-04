import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Optional;

class UDPClient {
	private DatagramSocket socket;
	private InetAddress address;

	private byte[] dataToReceive = new byte[256];
	private int port;
	private boolean waitForResponse;

	UDPClient(int port, boolean waitForResponse) {
		this.port = port;
		this.waitForResponse = waitForResponse;

		try {
			socket = new DatagramSocket();
			address = InetAddress.getByName("localhost");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	synchronized Optional<String> send(String msg) throws IOException {
		byte[] dataToSend = msg.getBytes();
		socket.send(new DatagramPacket(dataToSend, dataToSend.length, address, port));
		if (waitForResponse) {
			DatagramPacket packet = new DatagramPacket(dataToReceive, dataToReceive.length);
			socket.receive(packet);
			return Optional.of(new String(packet.getData(), 0, packet.getLength()));
		}
		return Optional.empty();
	}

	void close() {
		socket.close();
	}
}
