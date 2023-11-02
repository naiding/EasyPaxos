package com.naidingz.easyPaxos.member;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.naidingz.easyPaxos.message.ClientRequestMessage;
import com.naidingz.easyPaxos.message.ClientResponseMessage;
import com.naidingz.easyPaxos.message.Value;
import com.naidingz.easyPaxos.network.*;
import com.naidingz.easyPaxos.util.FileHelper;
import com.naidingz.easyPaxos.util.PacketFactory;
import com.naidingz.easyPaxos.util.Serializer;

public class User {
	
	private Logger logger;
	
	private Client client;
	
	private Server server;
	
	private UserConfig config;
			
	private BlockingQueue<Value> requestQueue = new LinkedBlockingQueue<>();

	private volatile Packet lastPacket;
	
	private volatile long lastPacketSentTime;

	private volatile int serverNum = 0;

	private volatile int sequenceNumber = 111;
	
	public User (String configFilename) {
		
		System.out.println("Read config file from: " + configFilename);
		this.config = FileHelper.readJsonFile(configFilename, UserConfig.class);
		System.out.println(this.config);
				
		client = new ClientUDPImpl();
		server = new ServerUDPImpl(config.getPort());

		configLogger();
		
		new Thread(() -> {
			while (true) {
				if (lastPacket == null) {
					try {
						Value value = requestQueue.take();
						sequenceNumber++;
						value.setCsn(getCsn());
						Packet packet = PacketFactory.getPacket(PacketReceiverEnum.SERVER, PacketContentTypeEnum.CLIENT_REQUEST, 
								new ClientRequestMessage(config.getHost(), config.getPort(), value));
						sendRequestPacket(packet);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					long currentTime = System.currentTimeMillis();
					if (currentTime - lastPacketSentTime > config.getTimeout()) {
						serverNum = ((serverNum + 1) % config.getServers().size());
						sendRequestPacket(lastPacket);
					}
				}
			}
		}).start();
		
		
		new Thread(() -> {
			while (true) {
				byte[] message = server.receive();
				Packet packet = Serializer.deserialize(message, Packet.class);
				PacketContent packetContent = packet.getContent();
				if (packet.getReceiver() == PacketReceiverEnum.CLIENT && 
					packetContent.getType() == PacketContentTypeEnum.SERVER_RESPONSE) {
					ClientResponseMessage responseMessage = (ClientResponseMessage) packetContent.getMessage();
					logger.info("Received packet " + responseMessage.getCsn());
//					System.out.println("Received packet " + responseMessage.getCsn());
					if (responseMessage.getCsn() == getCsn()) {
						lastPacket = null;
					}
				}
			}
		}).start();
	}
	
	public void start() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Scanner running...");
		while (true) {
			System.out.print("Input message: ");
			String data = scanner.nextLine();
			if (data.equals("^")) {
				scanner.close();
				System.out.println("Scanner stopped.");
				break;
			} else {
				requestQueue.add(new Value(0, data));
			}
		}
	}
	
	public void startBatch(int startSequenceNumber, int start, int end) {
		this.sequenceNumber = startSequenceNumber;
		for (int i = start; i <= end; i++) {
			requestQueue.add(new Value(0, String.valueOf(i)));
		}
	}
	
	private void sendRequestPacket(Packet packet) {
		ReplicaNode node = config.getServers().get(serverNum);
		client.sendTo(node.getHost(), node.getPort(), Serializer.serialize(packet));
		lastPacket = packet;
		lastPacketSentTime = System.currentTimeMillis();
		logger.info("Sent to " + node + " with packet " + packet);
//		System.out.println("Sent to " + node + " with packet " + packet);
	}
	
	private int getCsn() {
		final int prime = 31;
		int result = 1;
		result = prime * result + config.getId();
		result = prime * result + sequenceNumber;
		return result;
	}
	
	private void configLogger() {
		logger = Logger.getLogger("userLogger" + config.getId());
		logger.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
		
		try {
			String filename = config.getLogFilename();
			File file = new File(filename);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			FileHandler fileHandler = new FileHandler(filename, false);
			fileHandler.setFormatter(new SimpleFormatter());
			fileHandler.setFormatter(new Formatter() {
				@Override
				public String format(LogRecord lr) {
					return lr.getMessage() + System.lineSeparator();
				}
			});
			fileHandler.setLevel(Level.INFO);
			logger.addHandler(fileHandler);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}
}
