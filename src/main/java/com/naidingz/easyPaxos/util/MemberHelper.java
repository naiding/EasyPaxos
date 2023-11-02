package com.naidingz.easyPaxos.util;

import java.util.List;

import com.naidingz.easyPaxos.member.ReplicaNode;
import com.naidingz.easyPaxos.message.ReplicaMessage;
import com.naidingz.easyPaxos.network.Client;
import com.naidingz.easyPaxos.network.Packet;

public class MemberHelper {
	public static void sendReplicaMessage(Client client, ReplicaMessage message, ReplicaNode receiver) {
		Packet packet = PacketFactory.getPacketFromReplicaMessage(message);
		client.sendPacketTo(receiver.getHost(), receiver.getPort(), packet);
	}
	
	public static void sendReplicaMessage(Client client, ReplicaMessage message, List<ReplicaNode> receivers) {
		Packet packet = PacketFactory.getPacketFromReplicaMessage(message);
		for (ReplicaNode receiver : receivers) {
			client.sendPacketTo(receiver.getHost(), receiver.getPort(), packet);
		}
	}
}