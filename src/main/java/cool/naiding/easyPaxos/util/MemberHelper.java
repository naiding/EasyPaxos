package cool.naiding.easyPaxos.util;

import java.util.List;

import cool.naiding.easyPaxos.member.ReplicaNode;
import cool.naiding.easyPaxos.message.ReplicaMessage;
import cool.naiding.easyPaxos.network.Client;
import cool.naiding.easyPaxos.network.Packet;

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