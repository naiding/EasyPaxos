package cool.naiding.easyPaxos.message;

import java.io.Serializable;

public class PrepareResponseMessage extends ReplicaMessage implements Serializable {

	private static final long serialVersionUID = -6065936960560077770L;

	private int index;
	private ProposalNumber acceptedProposal;
	private Value acceptedValue;
	private boolean noMoreAccepted;
	
	public PrepareResponseMessage(int senderId, int index, ProposalNumber acceptedProposal, 
								  Value acceptedValue, boolean noMoreAccepted) {
		super(senderId);
		this.index = index;
		this.acceptedProposal = acceptedProposal;
		this.acceptedValue = acceptedValue;
		this.noMoreAccepted = noMoreAccepted;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public ProposalNumber getAcceptedProposal() {
		return acceptedProposal;
	}

	public void setAcceptedProposal(ProposalNumber acceptedProposal) {
		this.acceptedProposal = acceptedProposal;
	}

	public Value getAcceptedValue() {
		return acceptedValue;
	}

	public void setAcceptedValue(Value acceptedValue) {
		this.acceptedValue = acceptedValue;
	}

	public boolean isNoMoreAccepted() {
		return noMoreAccepted;
	}

	public void setNoMoreAccepted(boolean noMoreAccepted) {
		this.noMoreAccepted = noMoreAccepted;
	}

	@Override
	public String toString() {
		return "PrepareResponseMessage [index=" + index + ", acceptedProposal=" + acceptedProposal + ", acceptedValue="
				+ acceptedValue + ", noMoreAccepted=" + noMoreAccepted + ", toString()=" + super.toString() + "]";
	}
}
