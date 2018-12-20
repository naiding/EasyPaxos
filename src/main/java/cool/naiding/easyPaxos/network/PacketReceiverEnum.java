package cool.naiding.easyPaxos.network;

public enum PacketReceiverEnum {
	/*
	 * Client submit message to server
	 */
	SERVER, 
	
	CLIENT,
	
	/*
	 * Replica communicates with replica (HeartBeat, etc)
	 */
	REPLICA, 
	
	PROPOSER,
	
	ACCEPTOR
	
	
}
