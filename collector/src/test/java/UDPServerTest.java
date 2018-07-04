import com.mnc.telemetry.collector.UDPServer;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static org.hamcrest.core.Is.is;

public class UDPServerTest {

	@Test
	public void sendAndReceive() throws InterruptedException {
		int serverPort = 4445;
		int threadsCount = 100;
		int messagesPerThread = 100;

		UDPServer udpServer = new UDPServer(serverPort, EchoTask::consume);
		udpServer.start();

		ClientThread[] clientThread = new ClientThread[threadsCount];
		IntStream.range(0, clientThread.length).forEach(i ->
				clientThread[i] = new ClientThread(new UDPClient(serverPort, true), "Message-"+i+"-", messagesPerThread));

		IntStream.range(0, clientThread.length).parallel().forEach(i ->
				clientThread[i].start());

		IntStream.range(0, clientThread.length).forEach(i -> {
			try {
				clientThread[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}});

		Set<String> allReceivedMessages = new HashSet<>();
		IntStream.range(0, clientThread.length).forEach(i ->
				allReceivedMessages.addAll(clientThread[i].getReceivedMessages()));

		Assert.assertThat(allReceivedMessages.size(), is(clientThread.length*messagesPerThread));
		IntStream.range(0, clientThread.length).forEach(i ->
				IntStream.range(0, messagesPerThread).forEach(j ->
					Assert.assertTrue(allReceivedMessages.contains("Message-"+i+"-"+j))));

		udpServer.terminate();
		udpServer.join();
	}

	private static class ClientThread extends Thread {

		private final UDPClient client;
		private final String messagePrefix;
		private final int messagesCount;
		private final Set<String> receivedMessages;

		ClientThread(UDPClient client, String messagePrefix, int messagesCount) {
			this.client = client;
			this.messagePrefix = messagePrefix;
			this.messagesCount = messagesCount;
			this.receivedMessages = new HashSet<>();
		}

		@Override
		public void run() {
			IntStream.range(0, messagesCount).parallel().forEach(i -> {
				try {
					String sentMessage = messagePrefix + i;
					Optional<String> receivedMessage = client.send(sentMessage);
					if (receivedMessage.isPresent()) {
//					System.out.printf("Client: <%s> -> <%s> (%s)\n", sentMessage, receivedMessage.get(), client.socket.getLocalSocketAddress());
						synchronized (receivedMessages) {
							receivedMessages.add(receivedMessage.get());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			client.close();
		}

		Set<String> getReceivedMessages() {
			return new HashSet<>(receivedMessages);
		}
	}


}
