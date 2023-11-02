package com.naidingz.easyPaxos.util;

import com.naidingz.easyPaxos.message.AcceptMessage;
import com.naidingz.easyPaxos.message.AcceptResponseMessage;
import com.naidingz.easyPaxos.message.ClientMessage;
import com.naidingz.easyPaxos.message.ClientRequestMessage;
import com.naidingz.easyPaxos.message.ClientResponseMessage;
import com.naidingz.easyPaxos.message.HeartBeatMessage;
import com.naidingz.easyPaxos.message.PrepareMessage;
import com.naidingz.easyPaxos.message.PrepareResponseMessage;
import com.naidingz.easyPaxos.message.ReplicaMessage;
import com.naidingz.easyPaxos.message.SuccessMessage;
import com.naidingz.easyPaxos.message.SuccessResponseMessage;
import com.naidingz.easyPaxos.network.Packet;
import com.naidingz.easyPaxos.network.PacketContent;
import com.naidingz.easyPaxos.network.PacketContentTypeEnum;
import com.naidingz.easyPaxos.network.PacketReceiverEnum;

public class PacketFactory {
	
	public static Packet getPacket(PacketReceiverEnum receiver, 
								   PacketContentTypeEnum contentType,
								   Object message) {
		return new Packet(receiver, new PacketContent(contentType, message));
	}
	
	public static Packet getPacketFromReplicaMessage(ReplicaMessage message) {
		Packet packet = null;
		if (message instanceof HeartBeatMessage) {
			packet = PacketFactory.getPacket(PacketReceiverEnum.REPLICA, PacketContentTypeEnum.HEARTBEAT, (HeartBeatMessage) message);
		} else if (message instanceof PrepareMessage) {
			packet = PacketFactory.getPacket(PacketReceiverEnum.ACCEPTOR, PacketContentTypeEnum.PREPARE, (PrepareMessage) message);
		} else if (message instanceof AcceptMessage) {
			packet = PacketFactory.getPacket(PacketReceiverEnum.ACCEPTOR, PacketContentTypeEnum.ACCEPT, (AcceptMessage) message);
		} else if (message instanceof SuccessMessage) {
			packet = PacketFactory.getPacket(PacketReceiverEnum.ACCEPTOR, PacketContentTypeEnum.SUCCESS, (SuccessMessage) message);
		} else if (message instanceof PrepareResponseMessage) {
			packet = PacketFactory.getPacket(PacketReceiverEnum.PROPOSER, PacketContentTypeEnum.PREPARE_RESPONSE, (PrepareResponseMessage) message);
		} else if (message instanceof AcceptResponseMessage) {
			packet = PacketFactory.getPacket(PacketReceiverEnum.PROPOSER, PacketContentTypeEnum.ACCEPT_RESPONSE, (AcceptResponseMessage) message);
		} else if (message instanceof SuccessResponseMessage) {
			packet = PacketFactory.getPacket(PacketReceiverEnum.PROPOSER, PacketContentTypeEnum.SUCCESS_RESPONSE, (SuccessResponseMessage) message);
		}
		return packet;
	}
	
	public static Packet getPacketFromClientMessage(ClientMessage message) {
		Packet packet = null;
		if (message instanceof ClientRequestMessage) {
			packet = PacketFactory.getPacket(PacketReceiverEnum.SERVER, PacketContentTypeEnum.CLIENT_REQUEST, (ClientRequestMessage) message);
		} else if (message instanceof ClientResponseMessage) {
			packet = PacketFactory.getPacket(PacketReceiverEnum.CLIENT, PacketContentTypeEnum.SERVER_RESPONSE, (ClientResponseMessage) message);
		}
		return packet;
	}
}