package com.naidingz.easyPaxos.message;

import java.io.Serializable;

public class PrepareMessage extends ReplicaMessage implements Serializable {
	
	private static final long serialVersionUID = 2635236940952163408L;
	
	private ProposalNumber proposal;
	private int index;
	
	public PrepareMessage(int senderId, ProposalNumber proposal, int index) {
		super(senderId);
		this.proposal = proposal;
		this.index = index;
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

	@Override
	public String toString() {
		return "PrepareMessage [proposal=" + proposal + ", index=" + index + ", toString()=" + super.toString() + "]";
	}
}
