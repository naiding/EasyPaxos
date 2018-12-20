package cool.naiding.easyPaxos.message;

import java.io.Serializable;

public class ProposalNumber implements Serializable, Comparable<ProposalNumber> {
	
	private static final long serialVersionUID = -5742573799061601449L;
	
	public static final int INFINITY = Integer.MAX_VALUE;
	
	private int roundNumber;
	private int replicaId;
	
	public ProposalNumber(int roundNumber, int replicaId) {
		super();
		this.roundNumber = roundNumber;
		this.replicaId = replicaId;
	}
	
	public ProposalNumber(ProposalNumber proposal) {
		this(proposal.roundNumber, proposal.replicaId);
	}

	public int getRoundNumber() {
		return roundNumber;
	}

	public void setRoundNumber(int roundNumber) {
		this.roundNumber = roundNumber;
	}

	public int getReplicaId() {
		return replicaId;
	}

	public void setReplicaId(int replicaId) {
		this.replicaId = replicaId;
	}

	@Override
	public String toString() {
		return "ProposalNumber [roundNumber=" + roundNumber + ", replicaId=" + replicaId + "]";
	}

	/**
	 * The less round number, the less proposal number
	 */
	@Override
	public int compareTo(ProposalNumber that) {
		if (that == null) {
			return 1;
		}
		if (this.getReplicaId() == that.getReplicaId() && this.getRoundNumber() == that.getRoundNumber()) {
			return 0;
		}
		return Integer.compare(this.roundNumber, that.roundNumber);
	}
}
