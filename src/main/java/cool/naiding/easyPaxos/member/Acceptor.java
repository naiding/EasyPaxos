package cool.naiding.easyPaxos.member;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import com.google.gson.Gson;
import cool.naiding.easyPaxos.message.AcceptMessage;
import cool.naiding.easyPaxos.message.AcceptResponseMessage;
import cool.naiding.easyPaxos.message.PrepareMessage;
import cool.naiding.easyPaxos.message.PrepareResponseMessage;
import cool.naiding.easyPaxos.message.ProposalNumber;
import cool.naiding.easyPaxos.message.SuccessMessage;
import cool.naiding.easyPaxos.message.SuccessResponseMessage;
import cool.naiding.easyPaxos.message.Value;
import cool.naiding.easyPaxos.network.Client;
import cool.naiding.easyPaxos.network.PacketContent;
import cool.naiding.easyPaxos.util.FileHelper;
import cool.naiding.easyPaxos.util.MemberHelper;

public class Acceptor {
	
	private final Logger logger;
	
	private final Client client;
	
	private final ReplicaConfig config;
	
	private final Map<Integer, ReplicaNode> replicaMap;
	
	private BlockingQueue<PacketContent> messageQueue = new LinkedBlockingQueue<>();
	
	/**
	 * the set for chosen client sequence number
	 */
	Set<Integer> chosenCsn = new HashSet<>();

	/**
	 * the set for logged client sequence number
	 */
	Set<Integer> loggedCsn = new HashSet<>();
	
	/**
	 * the number of the last proposal the server has accepted for this entry, 
	 * or 0 if it never accepted any, or ∞ if acceptedValue[i] is known to be chosen
	 */
	private Map<Integer, ProposalNumber> acceptedProposal = new HashMap<>();
	
	/**
	 * the value in the last proposal the server accepted for this entry, or null if it never accepted any
	 */
	private Map<Integer, Value> acceptedValue = new HashMap<>();

	/**
	 * The largest entry for which this server has accepted a proposal
	 */
	private volatile int lastAcceptIndex = 0;

	/**
	 *  The smallest log index i > 0 for which acceptedProposal[i] < Infinity
	 */
	private volatile int firstUnchosenIndex = 1;
	
	/**
	 * The number of the smallest proposal this server will accept for any log entry, 
	 * or 0 if it has never received a Prepare request. This applies globally to all entries.
	 */
	private volatile ProposalNumber minProposal = null;
	
	public Acceptor(ReplicaConfig config, Logger logger, Client client, Map<Integer, ReplicaNode> replicaMap) {
		this.client = client;
		this.config = config;
		this.logger = logger;
		this.replicaMap = replicaMap;
		
		recoverFromFile();
		
		new Thread(() -> {
			while (true) {
				try {
					PacketContent packetContent = messageQueue.take();
					handlePacketContent(packetContent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 * Send PacketContent to Message Queue for Future Processing
	 * 
	 * @param packetContent
	 */
	public void sendToMessageQueue(PacketContent packetContent) {
		try {
			messageQueue.put(packetContent);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handle Incoming PacketContent
	 * 
	 * @param packetContent
	 */
	private void handlePacketContent(PacketContent packetContent) {
		switch (packetContent.getType()) {
		case PREPARE:
			handlePrepare((PrepareMessage) packetContent.getMessage());
			break;
		case ACCEPT:
			handleAccept((AcceptMessage) packetContent.getMessage());
			break;
		case SUCCESS:
			handleSuccess((SuccessMessage) packetContent.getMessage());
			break;
		default:
			System.out.println("Unknown packet: " + packetContent);
			break;
		}
	}
	
	/*
	 * Some getters for proposer
	 */
	ProposalNumber getAcceptedProposalAtIndex(int index) {
		return acceptedProposal.get(index);
	}

	Value getAcceptedValueAtIndex(int index) {
		return acceptedValue.get(index);
	}

	int getLastAcceptIndex() {
		return lastAcceptIndex;
	}

	int getFirstUnchosenIndex() {
		return firstUnchosenIndex;
	}

	ProposalNumber getMinProposal() {
		return minProposal;
	}

	/**
	 * Handle incoming prepare message
	 * 
	 * @param prepareMessage
	 */
	private void handlePrepare(PrepareMessage prepareMessage) {
		if (minProposal != null && minProposal.compareTo(prepareMessage.getProposal()) > 0) {
			logger.finer(getMyName() + ": expired prepare message " + prepareMessage);
			PrepareResponseMessage responseMessage = new PrepareResponseMessage(config.getId(), 
				prepareMessage.getIndex(), minProposal, null, false);
			MemberHelper.sendReplicaMessage(client, responseMessage, replicaMap.get(prepareMessage.getSenderId()));
			return;
		}
		
		logger.finer(getMyName() + ": response prepare message " + prepareMessage);
		minProposal = new ProposalNumber(prepareMessage.getProposal());
		
		PrepareResponseMessage responseMessage = new PrepareResponseMessage(config.getId(), prepareMessage.getIndex(),
			acceptedProposal.get(prepareMessage.getIndex()),
			acceptedValue.get(prepareMessage.getIndex()),
			lastAcceptIndex <= prepareMessage.getIndex());
		MemberHelper.sendReplicaMessage(client, responseMessage, replicaMap.get(prepareMessage.getSenderId()));		
	}
	
	/**
	 * Handle incoming acceptMessage
	 * 
	 * @param acceptMessage
	 */
	private void handleAccept(AcceptMessage acceptMessage) {
		if (minProposal != null && minProposal.compareTo(acceptMessage.getProposal()) > 0) {
			logger.finer(getMyName() + ": expired accept message " + minProposal + acceptMessage);
			AcceptResponseMessage responseMessage = new AcceptResponseMessage(config.getId(), 
					acceptMessage.getIndex(), minProposal, firstUnchosenIndex);
			MemberHelper.sendReplicaMessage(client, responseMessage, replicaMap.get(acceptMessage.getSenderId()));
			return;
		}
		
		logger.finer(getMyName() + ": response accept message " + acceptMessage);
		
		acceptProposalAndValue(acceptMessage.getIndex(), acceptMessage.getProposal(), acceptMessage.getValue());
		minProposal = new ProposalNumber(acceptMessage.getProposal());

		for (int index = firstUnchosenIndex; index < acceptMessage.getFirstUnchosenIndex(); index++) {
			ProposalNumber proposal = acceptedProposal.get(index);
			// no way to pass any hole
			if (proposal == null || proposal.compareTo(acceptMessage.getProposal()) != 0) {
				break;
			} else {
				chooseValue(index, acceptedValue.get(index));
			}
		}
		updateFirstUnchosenIndex();

		AcceptResponseMessage responseMessage = new AcceptResponseMessage(config.getId(), 
			acceptMessage.getIndex(), minProposal, firstUnchosenIndex);
		MemberHelper.sendReplicaMessage(client, responseMessage, replicaMap.get(acceptMessage.getSenderId()));		
	}
	
	/**
	 * Handle incoming success message
	 * 
	 * @param successMessage
	 */
	private void handleSuccess(SuccessMessage successMessage) {
		logger.finer(getMyName() + ": response success message " + successMessage);
		chooseValue(successMessage.getIndex(), successMessage.getValue());
		updateFirstUnchosenIndex();
		
		SuccessResponseMessage responseMessage = new SuccessResponseMessage(config.getId(), firstUnchosenIndex);
		MemberHelper.sendReplicaMessage(client, responseMessage, replicaMap.get(successMessage.getSenderId()));
	}

	/**
	 * Update first unchosen index
	 * 
	 */
	synchronized void updateFirstUnchosenIndex() {
		// TODO : if all entries are chosen, what value should firstUnchosenIndex holds?
		// lastAcceptIndex + 1 or 0?
		for (int index = firstUnchosenIndex; ; index++) {
			ProposalNumber proposal = acceptedProposal.get(index);
			if (proposal != null && acceptedValue.get(index) != null) {
				lastAcceptIndex = index > lastAcceptIndex ? index : lastAcceptIndex;
			}
			if (proposal == null || proposal.getRoundNumber() < ProposalNumber.INFINITY) {
				firstUnchosenIndex = index;
				break;
			} else {
				Value value = acceptedValue.get(index);
				if (value.getCsn() != Integer.MIN_VALUE && !loggedCsn.contains(value.getCsn())) {
					// TODO : sequence here?
					loggedCsn.add(value.getCsn());
					logger.severe("Execute " + value);
					if (value.getCsn() % 7 == 0) {
//						logger.severe(FileHelper.getFileMD5(new File(config.getLogFilename())));
						FileHelper.writeToFile(config.getLogHashFilename(),
							FileHelper.getFileMD5(new File(config.getLogFilename())) + " " + value + System.lineSeparator(), true);
			
						// TODO : ?? avoid execute then die without increasing firstUnchosenIndex
//						firstUnchosenIndex = index + 1;
					}
					persistToFile();
				}
			}
		}
	}
	
	/**
	 * Accept proposal and value at index
	 * 
	 * @param index
	 * @param proposal
	 * @param value
	 */
	private void acceptProposalAndValue(int index, ProposalNumber proposal, Value value) {
		acceptedProposal.put(index, new ProposalNumber(proposal));
		acceptedValue.put(index, value);
		lastAcceptIndex = index > lastAcceptIndex ? index : lastAcceptIndex;
		logger.finer(getMyName() + "Value is accepted : " + value);
//		System.out.println("Accept index " + index + " " + proposal + " " + value);
	}

	/**
	 * Choose value at index Remember to call updateFirstUnchosenIndex
	 * 
	 * @param index
	 * @param value
	 */
	void chooseValue(int index, Value value) {
		ProposalNumber proposal = acceptedProposal.get(index);
		// TODO : null proposal here? replica 0 sends to 0, 1, 2
		// 1 and 2 received message and accepted, but 0 loses all messages
		// but proposer 0 will call this function in this case and lead to NPE.
		if (proposal == null) {
			proposal = new ProposalNumber(0, config.getId());
		}
		if (proposal.getRoundNumber() != ProposalNumber.INFINITY) {
			proposal.setRoundNumber(ProposalNumber.INFINITY);
			acceptedProposal.put(index, proposal);
			acceptedValue.put(index, value);
			chosenCsn.add(value.getCsn());
			logger.finer(getMyName() + "Value is chosen: " + value);
		} else {
			// logger.info(getMyName() + "Value already chosen: " + acceptedValue.get(index));
		}
	}
	
	/**
	 * Get acceptor name for logging
	 * @return name
	 */
	private String getMyName() {
		return "Acceptor " + config.getId() + " ";
	}
	
	void persistToFile() {
		if (config.isAllowPersistence()) {
			String filename = config.getAcceptorPersistenceFilename();
			FileHelper.createFileIfNotExist(filename);
			AcceptorPersistenceBean bean = new AcceptorPersistenceBean(lastAcceptIndex, 
				firstUnchosenIndex, minProposal, chosenCsn, loggedCsn, acceptedProposal,acceptedValue);
			FileHelper.writeToFile(filename, new Gson().toJson(bean), false);
		}
	}
	
	void recoverFromFile() {
		if (config.isAllowPersistence()) {
			String filename = config.getAcceptorPersistenceFilename();
			FileHelper.createFileIfNotExist(filename);
			String json = FileHelper.readFromFile(filename);
			if (json == null || json.length() == 0) {
				return;
			}
			
			AcceptorPersistenceBean bean = new Gson().fromJson(json, AcceptorPersistenceBean.class);
			lastAcceptIndex = bean.getLastAcceptIndex();
			firstUnchosenIndex = bean.getFirstUnchosenIndex();
			minProposal = bean.getMinProposal();
			chosenCsn = bean.getChosenCsn();
			loggedCsn = bean.getLoggedCsn();
			acceptedProposal = bean.getAcceptedProposal();
			acceptedValue = bean.getAcceptedValue();
			
			// TODO : remove all chosen value after firstUnchosenIndex ?
		}
	}
}
