package com.naidingz.easyPaxos.member;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.naidingz.easyPaxos.message.AcceptMessage;
import com.naidingz.easyPaxos.message.AcceptResponseMessage;
import com.naidingz.easyPaxos.message.ClientRequestMessage;
import com.naidingz.easyPaxos.message.ClientResponseMessage;
import com.naidingz.easyPaxos.message.PrepareMessage;
import com.naidingz.easyPaxos.message.PrepareResponseMessage;
import com.naidingz.easyPaxos.message.ProposalNumber;
import com.naidingz.easyPaxos.message.SuccessMessage;
import com.naidingz.easyPaxos.message.SuccessResponseMessage;
import com.naidingz.easyPaxos.message.Value;
import com.naidingz.easyPaxos.network.Client;
import com.naidingz.easyPaxos.network.Packet;
import com.naidingz.easyPaxos.network.PacketContent;
import com.naidingz.easyPaxos.util.FileHelper;
import com.naidingz.easyPaxos.util.MemberHelper;
import com.naidingz.easyPaxos.util.PacketFactory;

public class Proposer {

	enum IndexState {
		WAIT_PREPARE, WAIT_ACCEPT, CHOSEN, DROPPED
	}
	
	class IndexInfo {
		private ClientRequestMessage requestMessage;
		private ProposalNumber proposal;
		private IndexState state;
		
		private Map<Integer, PrepareResponseMessage> prepareResponseMap;
		private Map<Integer, AcceptResponseMessage> acceptResponseMap;
		
		private Value acceptValue;

		public IndexInfo(ClientRequestMessage requestMessage, ProposalNumber proposal, Value acceptValue, IndexState state) {
			super();
			this.requestMessage = requestMessage;
			this.proposal = proposal;
			this.state = state;
			this.prepareResponseMap = new HashMap<>();
			this.acceptResponseMap = new HashMap<>();
			this.acceptValue = acceptValue;
		}
	}

	private final String myName;

	private final Logger logger;
	
	/**
	 * Replica's client to send message
	 */
	private final Client client;
	
	/**
	 * Replica's acceptor
	 */
	private final Acceptor acceptor;
	
	/**
	 * Replica's configuration
	 */
	private final ReplicaConfig config;
	
	/**
	 * All replica nodes in the cluster
	 */
	private final Map<Integer, ReplicaNode> replicaMap;
	
	/**
	 * A blocking message queue to receive all messages to this proposer
	 */
	private BlockingQueue<PacketContent> messageQueue = new LinkedBlockingQueue<>();
	
	/**
	 * A blocking message queue to save all client requests
	 * Only take when this proposer is ready
	 */
	private BlockingQueue<ClientRequestMessage> clientRequestMessageQueue = new LinkedBlockingQueue<>();
	
	/**
	 * index -> index information
	 */
	private Map<Integer, IndexInfo> indexInfoMap = new ConcurrentHashMap<>();
	
	/**
	 * The largest round number the proposer has seen
	 */
	private volatile int maxRound = 0;
	
	/**
	 * The index of the next entry to use for a client request
	 */
	private int nextIndex = 1;
	
	/**
	 * True means there is no need to issue Prepare requests 
	 * (a majority of acceptors has responded to Prepare requests with noMoreAccepted true);
	 */
	private volatile boolean prepared = false;
	
	/**
	 * Primary id
	 */
	private volatile boolean isPrimary;
	
	public Proposer(ReplicaConfig config, Logger logger, Client client, Map<Integer, ReplicaNode> replicaMap, Acceptor acceptor) {
		this.config = config;
		this.acceptor = acceptor;
		this.client = client;
		this.logger = logger;
		this.replicaMap = replicaMap;
		this.myName = "Proposer " + this.config.getId() + " ";
		
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
		
		new Thread(() -> {
			while (true) {
				try {
					ClientRequestMessage message = clientRequestMessageQueue.take();
					logger.fine(myName + ": Take a request from queue " + message);
					beginHandleClientRequest(message);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 * Send PacketContent to Message Queue for Future Processing
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
	 * @param packetContent
	 */
	private void handlePacketContent(PacketContent packetContent) {
		switch (packetContent.getType()) {
		case CLIENT_REQUEST:
			if (isPrimary) {
				clientRequestMessageQueue.add((ClientRequestMessage) packetContent.getMessage());
			}
			break;
		case PREPARE_RESPONSE:
			handlePrepareResponse((PrepareResponseMessage) packetContent.getMessage());
			break;
		case ACCEPT_RESPONSE:
			handleAcceptResponse((AcceptResponseMessage) packetContent.getMessage());
			break;
		case SUCCESS_RESPONSE:
			handleSuccessResponse((SuccessResponseMessage) packetContent.getMessage());
			break;
		default:
			System.out.println("Unknown packet: " + packetContent);
			break;
		}
	}
	
	/**
	 * Begin handle client request  -> write(inputValue)
	 * @param clientRequestMessage
	 */
	private void beginHandleClientRequest(ClientRequestMessage clientRequestMessage) {
		if (!isPrimary) {
			logger.fine("Not primary, dropped");
			return;
		}
		if (acceptor.chosenCsn.contains(clientRequestMessage.getValue().getCsn())) {
			sendSuccessResponseToClient(clientRequestMessage);
			logger.fine("Already chosen, dropped");
			return;
		}
//		if (clientRequestMessage.getValue().getCsn() == 1073) {
//			nextIndex++;
//			prepared = true;
//			logger.fine("Skip message " + clientRequestMessage);
//			return;
//		}
 		logger.fine(myName + ": step 1 with value" + clientRequestMessage.getValue());
		handleClientRequest(clientRequestMessage);
	}
	
	/**
	 * Handle client request message
	 * @param clientRequestMessage
	 */
	private void handleClientRequest(ClientRequestMessage clientRequestMessage) {
		if (prepared) {
			logger.fine(myName + ": step 2");
			int index = nextIndex++;
			logger.fine(myName + ": step 7 with index " + index);
			// use same proposal number
			ProposalNumber proposal = new ProposalNumber(maxRound, config.getId());
			IndexInfo indexInfo = new IndexInfo(clientRequestMessage, 
				proposal, clientRequestMessage.getValue(), IndexState.WAIT_ACCEPT);
			AcceptMessage acceptMessage = new AcceptMessage(config.getId(), 
				proposal, index, indexInfo.acceptValue, acceptor.getFirstUnchosenIndex());
			// broadcast accept message
			MemberHelper.sendReplicaMessage(client, acceptMessage, config.getNodes());
			indexInfoMap.put(index, indexInfo);
			// timeout action
			handleAcceptTimeOut(index, acceptMessage);
			logger.fine(myName + ": step 8");
		} else {
			int index = acceptor.getFirstUnchosenIndex();
			nextIndex = index + 1;
			logger.fine(myName + ": step 3 with index " + index);
			// generate new proposal number
			ProposalNumber proposal = new ProposalNumber(++maxRound, config.getId());
			// persist maxRound after revise
			persistToFile();
			PrepareMessage prepareMessage = new PrepareMessage(config.getId(), proposal, index);
			// broadcast prepare message
			MemberHelper.sendReplicaMessage(client, prepareMessage, config.getNodes());
			indexInfoMap.put(index, new IndexInfo(clientRequestMessage, proposal, null, IndexState.WAIT_PREPARE));
			// timeout action
			handlePrepareTimeOut(index, prepareMessage);
			logger.fine(myName + ": step 6");
		}
	}
	
	/**
	 * Handle PrepareResponse from acceptors
	 * @param prepareResponseMessage
	 */
	private void handlePrepareResponse(PrepareResponseMessage prepareResponseMessage) {
		int index = prepareResponseMessage.getIndex();
		IndexInfo indexInfo = indexInfoMap.get(index);
		
		if (indexInfo == null || indexInfo.state != IndexState.WAIT_PREPARE) {
			return;
		}
		
		if (prepareResponseMessage.getAcceptedProposal() != null && prepareResponseMessage.getAcceptedValue() == null) {
			// prepare expired : go to step 1
			maxRound = prepareResponseMessage.getAcceptedProposal().getRoundNumber();
			prepared = false;
			indexInfoMap.get(prepareResponseMessage.getIndex()).state = IndexState.DROPPED;
			persistToFile();
			logger.fine(myName + ": step 6 go to step 1 " + prepareResponseMessage);
			beginHandleClientRequest(indexInfoMap.get(prepareResponseMessage.getIndex()).requestMessage);
			return ;
		}
		
		logger.finer(myName + ": in step 6 " + prepareResponseMessage);
		// add incoming prepare response message to indexInfo
		indexInfo.prepareResponseMap.put(prepareResponseMessage.getSenderId(), prepareResponseMessage);
		// if receive prepare response from majority, process and send accept
		if (indexInfo.prepareResponseMap.size() >= config.getPrepareQuorumSize()) {
			logger.finer(myName + ": leaving step 6");
			int preparedCount = 0, roundNumber = -1;
			for (PrepareResponseMessage message: indexInfo.prepareResponseMap.values()) {
				ProposalNumber proposal = message.getAcceptedProposal();
				if (proposal != null && proposal.getRoundNumber() > roundNumber) {
					indexInfo.acceptValue = message.getAcceptedValue();
					roundNumber = proposal.getRoundNumber();
				}
				preparedCount += (message.isNoMoreAccepted() ? 1 : 0);
			}
			prepared = preparedCount >= config.getPrepareQuorumSize();
			
			if (indexInfo.acceptValue == null) {
				if (prepared) {
					indexInfo.acceptValue = indexInfo.requestMessage.getValue();
				} else {
					// corner case for skip slot: prepared is false and proposal is null.
					// we need to jump out of this hole
					indexInfo.acceptValue = new Value(Integer.MIN_VALUE, "");
				}
			}
			
			logger.fine(myName + ": step 7");
			AcceptMessage acceptMessage = new AcceptMessage(config.getId(), indexInfo.proposal, index, 
				indexInfo.acceptValue, acceptor.getFirstUnchosenIndex());
			// broadcast accept message
			MemberHelper.sendReplicaMessage(client, acceptMessage, config.getNodes());
			indexInfo.state = IndexState.WAIT_ACCEPT;
			indexInfoMap.put(index, indexInfo);
			// timeout action
			handleAcceptTimeOut(index, acceptMessage);
			logger.fine(myName + ": step 8");
		}
		
	}
	
	/**
	 * Handle AcceptResponse from acceptors
	 * @param acceptResponseMessage
	 */
	private void handleAcceptResponse(AcceptResponseMessage acceptResponseMessage) {
		int index = acceptResponseMessage.getIndex();
		IndexInfo indexInfo = indexInfoMap.get(index);
		
		if (indexInfo == null || indexInfo.state != IndexState.WAIT_ACCEPT) {
			return;
		}
		
		if (acceptResponseMessage.getMinProposal().getRoundNumber() > maxRound) {
			maxRound = acceptResponseMessage.getMinProposal().getRoundNumber();
			prepared = false;
			indexInfoMap.get(acceptResponseMessage.getIndex()).state = IndexState.DROPPED;
			persistToFile();
			// go to step 1
			logger.fine(myName + ": step 8 go to step 1");
			beginHandleClientRequest(indexInfoMap.get(acceptResponseMessage.getIndex()).requestMessage);
			return ;
		}
		
		logger.finer(myName + ": in step 8 " + acceptResponseMessage);
		// add incoming accept response message to indexInfo
		indexInfo.acceptResponseMap.put(acceptResponseMessage.getSenderId(), acceptResponseMessage);
		// examine firstUnchosenIndex of the acceptor
		int firstUnchosenIndex = acceptResponseMessage.getFirstUnchosenIndex();
		if (firstUnchosenIndex <= acceptor.getLastAcceptIndex() && 
			acceptor.getAcceptedProposalAtIndex(firstUnchosenIndex) != null &&
			acceptor.getAcceptedProposalAtIndex(firstUnchosenIndex).getRoundNumber() == ProposalNumber.INFINITY) {
			SuccessMessage successMessage = new SuccessMessage(config.getId(),
				firstUnchosenIndex, acceptor.getAcceptedValueAtIndex(firstUnchosenIndex));
			MemberHelper.sendReplicaMessage(client, successMessage, replicaMap.get(acceptResponseMessage.getSenderId()));
		}
		
		// receive prepare response from majority
		if (indexInfo.acceptResponseMap.size() >= config.getAcceptQuorumSize()) {
			logger.fine(myName + ": step 9");

			acceptor.chooseValue(index, indexInfo.acceptValue);
			acceptor.updateFirstUnchosenIndex();
			indexInfo.state = IndexState.CHOSEN;
			
			if (indexInfo.acceptValue.equals(indexInfo.requestMessage.getValue())) {
				sendSuccessResponseToClient(indexInfo.requestMessage);
				logger.fine(myName + ": step 10 finish ~");
				return;
			} else {
				// go to step 2
				logger.fine(myName + ": step 11 go to step 2");
				handleClientRequest(indexInfo.requestMessage);
			}
		}
	}
	
	/**
	 * Handle SuccessResponse from acceptors
	 * @param successResponseMessage
	 */
	private void handleSuccessResponse(SuccessResponseMessage successResponseMessage) {
		int firstUnchosenIndex = successResponseMessage.getFirstUnchosenIndex();
		if (firstUnchosenIndex < acceptor.getFirstUnchosenIndex()) {
			SuccessMessage successMessage = new SuccessMessage(config.getId(),
				firstUnchosenIndex, acceptor.getAcceptedValueAtIndex(firstUnchosenIndex));
			MemberHelper.sendReplicaMessage(client, successMessage, replicaMap.get(successResponseMessage.getSenderId()));
		}
	}
	
	
	/**
	 * Handle PrepareMessage Timeout
	 */
	private void handlePrepareTimeOut(int index, PrepareMessage message) {
		new Thread(() -> {
			boolean cancel = false;
			while (!cancel) {
				try {
					Thread.sleep(config.getTimeout());
					IndexInfo indexInfo = indexInfoMap.get(index);
					if (!isPrimary) {
						indexInfo.state = IndexState.DROPPED;
						cancel = true;
					} else if (indexInfo.state == IndexState.WAIT_PREPARE && 
						       indexInfo.prepareResponseMap.size() < config.getPrepareQuorumSize()) {
						for (ReplicaNode node : config.getNodes()) {
							if (!indexInfoMap.get(index).prepareResponseMap.containsKey(node.getId())) {
								MemberHelper.sendReplicaMessage(client, message, node);
							}
						}
					} else {
						cancel = true;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 * Handle AcceptMessage Timeout
	 */
	private void handleAcceptTimeOut(int index, AcceptMessage message) {
		new Thread(() -> {
			boolean cancel = false;
			while (!cancel) {
				try {
					Thread.sleep(config.getTimeout());
					IndexInfo indexInfo = indexInfoMap.get(index);
					if (!isPrimary) {
						indexInfo.state = IndexState.DROPPED;
						cancel = true;
					} else if (indexInfo.state == IndexState.WAIT_ACCEPT && 
							   indexInfo.acceptResponseMap.size() < config.getAcceptQuorumSize()) {
						for (ReplicaNode node : config.getNodes()) {
							if (!indexInfoMap.get(index).acceptResponseMap.containsKey(node.getId())) {
								MemberHelper.sendReplicaMessage(client, message, node);
							}
						}
					} else {
						cancel = true;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 * Send Success Response to Client
	 * @param requestMessage
	 */
	private void sendSuccessResponseToClient(ClientRequestMessage requestMessage) {
		ClientResponseMessage clientResponseMessage = new ClientResponseMessage(
				replicaMap.get(config.getId()).getHost(),
				replicaMap.get(config.getId()).getPort(),
				requestMessage.getValue().getCsn(), 200, "submitted");
			Packet packet = PacketFactory.getPacketFromClientMessage(clientResponseMessage);
			client.sendPacketTo(requestMessage.getHost(), requestMessage.getPort(), packet);
	}
	
	private void persistToFile() {
		if (config.isAllowPersistence()) {
			String filename = config.getProposerPersistenceFilename();
			FileHelper.createFileIfNotExist(filename);
			FileHelper.writeToFile(filename, new Gson().toJson(new ProposerPersistenceBean(maxRound)), false);
		}
	}
	
	private void recoverFromFile() {
		if (config.isAllowPersistence()) {
			String filename = config.getProposerPersistenceFilename();
			FileHelper.createFileIfNotExist(filename);
			String json = FileHelper.readFromFile(filename);
			if (json == null || json.length() == 0) {
				return;
			}
			
			ProposerPersistenceBean bean = new Gson().fromJson(json, ProposerPersistenceBean.class);
			maxRound = bean.getMaxRound();
		}
	}
	
	void setIsPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
		if (!this.isPrimary) {
			prepared = false;
		}
	}
}