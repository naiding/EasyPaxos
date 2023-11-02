package com.naidingz.easyPaxos.message;

import java.io.Serializable;

public class SuccessResponseMessage extends ReplicaMessage implements Serializable {
	
	private static final long serialVersionUID = 1828805667151364975L;
	
	private int firstUnchosenIndex;

	public SuccessResponseMessage(int senderId, int firstUnchosenIndex) {
		super(senderId);
		this.firstUnchosenIndex = firstUnchosenIndex;
	}

	public int getFirstUnchosenIndex() {
		return firstUnchosenIndex;
	}

	public void setFirstUnchosenIndex(int firstUnchosenIndex) {
		this.firstUnchosenIndex = firstUnchosenIndex;
	}

	@Override
	public String toString() {
		return "SuccessResponseMessage [firstUnchosenIndex=" + firstUnchosenIndex + ", toString()=" + super.toString()
				+ "]";
	}
	
}
