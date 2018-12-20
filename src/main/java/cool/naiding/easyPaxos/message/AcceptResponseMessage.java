package cool.naiding.easyPaxos.message;

import java.io.Serializable;

public class AcceptResponseMessage extends ReplicaMessage implements Serializable {
	
	private static final long serialVersionUID = -3356187565933065838L;
	
	private int index;
	private ProposalNumber minProposal;
	private int firstUnchosenIndex;
	
	public AcceptResponseMessage(int senderId, int index, ProposalNumber minProposal, int firstUnchosenIndex) {
		super(senderId);
		this.index = index;
		this.minProposal = minProposal;
		this.firstUnchosenIndex = firstUnchosenIndex;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public ProposalNumber getMinProposal() {
		return minProposal;
	}

	public void setMinProposal(ProposalNumber minProposal) {
		this.minProposal = minProposal;
	}

	public int getFirstUnchosenIndex() {
		return firstUnchosenIndex;
	}

	public void setFirstUnchosenIndex(int firstUnchosenIndex) {
		this.firstUnchosenIndex = firstUnchosenIndex;
	}

	@Override
	public String toString() {
		return "AcceptResponseMessage [index=" + index + ", minProposal=" + minProposal + ", firstUnchosenIndex="
				+ firstUnchosenIndex + ", toString()=" + super.toString() + "]";
	}
}
