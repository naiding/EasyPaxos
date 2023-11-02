package com.naidingz.easyPaxos.network;

import java.io.Serializable;

public class Packet implements Serializable {

	private static final long serialVersionUID = 2092012756518266798L;
	
	private PacketReceiverEnum receiver;
	
	private PacketContent content;

	public Packet(PacketReceiverEnum receiver, PacketContent content) {
		super();
		this.receiver = receiver;
		this.content = content;
	}

	public PacketReceiverEnum getReceiver() {
		return receiver;
	}

	public void setReceiver(PacketReceiverEnum receiver) {
		this.receiver = receiver;
	}

	public PacketContent getContent() {
		return content;
	}

	public void setContent(PacketContent content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Packet [receiver=" + receiver + ", content=" + content + "]";
	}
}
