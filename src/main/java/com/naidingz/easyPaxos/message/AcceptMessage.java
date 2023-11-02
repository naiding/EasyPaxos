package com.naidingz.easyPaxos.message;

import java.io.Serializable;

public class AcceptMessage extends ReplicaMessage implements Serializable {
	
	private static final long serialVersionUID = 5593704583774868600L;
	
	private ProposalNumber proposal;
	private int index;
	private Value value;
	private int firstUnchosenIndex;
	
	public AcceptMessage(int senderId, ProposalNumber proposal, int index, Value value, int firstUnchosenIndex) {
		super(senderId);
		this.proposal = proposal;
		this.index = index;
		this.value = value;
		this.firstUnchosenIndex = firstUnchosenIndex;
	}

	public ProposalNumber getProposal() {
		return proposal;
	}

	public void setProposal(ProposalNumber proposal) {
		this.proposal = proposal;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public int getFirstUnchosenIndex() {
		return firstUnchosenIndex;
	}

	public void setFirstUnchosenIndex(int firstUnchosenIndex) {
		this.firstUnchosenIndex = firstUnchosenIndex;
	}

	@Override
	public String toString() {
		return "AcceptMessage [proposal=" + proposal + ", index=" + index + ", value=" + value + ", firstUnchosenIndex="
				+ firstUnchosenIndex + ", toString()=" + super.toString() + "]";
	}
}
