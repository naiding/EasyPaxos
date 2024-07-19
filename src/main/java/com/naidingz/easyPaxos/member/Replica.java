package com.naidingz.easyPaxos.member;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.naidingz.easyPaxos.message.HeartBeatMessage;
import com.naidingz.easyPaxos.network.*;
import com.naidingz.easyPaxos.util.FileHelper;
import com.naidingz.easyPaxos.util.LoggerHelper;
import com.naidingz.easyPaxos.util.MemberHelper;
import com.naidingz.easyPaxos.util.Serializer;

public class Replica {
	
	/*
	 * logger
	 */
	private Logger logger;
	
	/*
	 * Config filename
	 */
	private String configFilename;
	
	/*
	 * Replica Configuration
	 */
	private ReplicaConfig config;
	
	/*
	 * Replicas Map
	 */
	private Map<Integer, ReplicaNode> replicaMap;
	
	/*
	 * Client for sending message
	 */
	private Client client;
	
	/*
	 * Proposer for this replica
	 */
	private Proposer proposer;
	
	/*
	 * Acceptor for this replica
	 */
	private Acceptor acceptor;
	
	/*
	 * View of Primary Id among replicas
	 */
	private volatile int view;
	
	/*
	 * The time of last view received
	 */
	private volatile long lastViewTime;
	
	public Replica(String configFilename) {
		this.configFilename = configFilename;
		System.out.println("Read config file from: " + this.configFilename);
		this.config = FileHelper.readJsonFile(this.configFilename, ReplicaConfig.class);
		System.out.println(this.config);
		
		setupLogger();
		
		this.replicaMap = new HashMap<>(this.config.getNodes().size());
		for (ReplicaNode node : this.config.getNodes()) {
			this.replicaMap.put(node.getId(), node);
		}
		this.client = new ClientUDPImpl();
		this.acceptor = new Acceptor(config, logger, client, replicaMap);
		this.proposer = new Proposer(config, logger, client, replicaMap, this.acceptor);
	}
	
	/**
	 * Setup logger
	 */
	private void setupLogger() {
		logger = Logger.getLogger("replicaLogger" + config.getId());
		logger.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
//		ConsoleHandler consoleHandler = new ConsoleHandler();
//		consoleHandler.setLevel(Level.FINE);
//		logger.addHandler(consoleHandler);

		logger.addHandler(LoggerHelper.getFileHandler(config.getDebugFilename(), config.isAllowPersistence(), Level.FINER));
		logger.addHandler(LoggerHelper.getFileHandler(config.getLogFilename(), config.isAllowPersistence(), Level.SEVERE));
	}
	
	/**
	 * Replica Start !
	 */
	public void start() {
		runHeartBeat();
		Server server = new ServerUDPImpl(config.getPort());
		new Thread(() -> {
			while (true) {
				byte[] message = server.receive();
				Packet packet = Serializer.deserialize(message, Packet.class);
				if (packet == null) {
					continue;
				}
				switch (packet.getReceiver()) {
				case SERVER:
					handleMyRequest(packet);
					break;
				case REPLICA:
					handleMyPacket(packet.getContent());
					break;
				case PROPOSER:
					this.proposer.sendToMessageQueue(packet.getContent());
					break;
				case ACCEPTOR:
					this.acceptor.sendToMessageQueue(packet.getContent());
					break;
				default:
					break;
				}
			}
		}).start(); 
	}
	
	/**
	 * Handle the request from clients.
	 * If this is not primary, redirect packet to primary.
	 * @param packet
	 */
	private void handleMyRequest(Packet packet) {
		if (getPrimaryId() != config.getId()) {
			ReplicaNode primary = replicaMap.get(getPrimaryId());
			client.sendPacketTo(primary.getHost(), primary.getPort(), packet);
		} else {
			proposer.sendToMessageQueue(packet.getContent());
		}
	}
	
	/**
	 * Handle message sent by other replicas.
	 * Typically it is heart beat message.
	 * @param packetContent
	 */
	private void handleMyPacket(PacketContent packetContent) {
		switch (packetContent.getType()) {
		case HEARTBEAT:
			HeartBeatMessage msg = (HeartBeatMessage) packetContent.getMessage();
			logger.finest(msg.toString());
			if (msg.getView() >= view) {
				setView(msg.getView());
				lastViewTime = System.currentTimeMillis();
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * Heart Beat Thread
	 * Send heart beat message to all other replicas every T ms
	 */
	private void runHeartBeat() {
		setView(config.getNodes().get(0).getId());
		lastViewTime = System.currentTimeMillis();

		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(config.getTimeout() / 2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				long currentTime = System.currentTimeMillis();
				if (getPrimaryId() == config.getId()) {
					lastViewTime = System.currentTimeMillis();
				}
				if (currentTime - lastViewTime > 1.5 * config.getTimeout()) {
					setView(view + 1);
					lastViewTime = currentTime;
				}
			}
		}).start();
		
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(config.getTimeout());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if (getPrimaryId() == config.getId()) {
					for (ReplicaNode node : config.getNodes()) {
						if (node.getId() != config.getId()) {
							MemberHelper.sendReplicaMessage(client, new HeartBeatMessage(config.getId(), view), node);
						}
					}
				}
			}
		}).start();	
	}

	private void setView(int newView) {
		view = newView;
		proposer.setIsPrimary(getPrimaryId() == config.getId());
	}
	
	private int getPrimaryId() {
		return view % (config.getNodes().size());
	}
}