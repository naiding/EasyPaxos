package com.naidingz.easyPaxos.message;

import java.io.Serializable;

public abstract class ReplicaMessage implements Serializable {

	private static final long serialVersionUID = 4567275570167064534L;

	private int senderId;

	public ReplicaMessage(int senderId) {
		super();
		this.senderId = senderId;
	}

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	@Override
	public String toString() {
		return "ReplicaMessage [senderId=" + senderId + "]";
	}
}
