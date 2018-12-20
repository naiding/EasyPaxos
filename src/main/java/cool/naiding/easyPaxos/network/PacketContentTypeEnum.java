package cool.naiding.easyPaxos.network;

public enum PacketContentTypeEnum {
	
	CLIENT_REQUEST,
	
	SERVER_RESPONSE,
	
	HEARTBEAT, 
	
	PREPARE,
	
	PREPARE_RESPONSE,
	
	ACCEPT,
	
	ACCEPT_RESPONSE,
	
	SUCCESS,
	
	SUCCESS_RESPONSE
}
