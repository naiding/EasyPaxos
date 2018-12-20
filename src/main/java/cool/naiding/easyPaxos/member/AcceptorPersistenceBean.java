package cool.naiding.easyPaxos.member;

import java.util.Map;
import java.util.Set;

import cool.naiding.easyPaxos.message.ProposalNumber;
import cool.naiding.easyPaxos.message.Value;

public class AcceptorPersistenceBean {
		
	private int lastAcceptIndex;

	private int firstUnchosenIndex;
	
	private ProposalNumber minProposal;
	
	private Set<Integer> chosenCsn;

	private Set<Integer> loggedCsn;
	
	private Map<Integer, ProposalNumber> acceptedProposal;
	
	private Map<Integer, Value> acceptedValue;

	public AcceptorPersistenceBean() {
		super();
	}

	public AcceptorPersistenceBean(int lastAcceptIndex, int firstUnchosenIndex, ProposalNumber minProposal,
			Set<Integer> chosenCsn, Set<Integer> loggedCsn, Map<Integer, ProposalNumber> acceptedProposal,
			Map<Integer, Value> acceptedValue) {
		super();
		this.lastAcceptIndex = lastAcceptIndex;
		this.firstUnchosenIndex = firstUnchosenIndex;
		this.minProposal = minProposal;
		this.chosenCsn = chosenCsn;
		this.loggedCsn = loggedCsn;
		this.acceptedProposal = acceptedProposal;
		this.acceptedValue = acceptedValue;
	}

	public int getLastAcceptIndex() {
		return lastAcceptIndex;
	}

	public void setLastAcceptIndex(int lastAcceptIndex) {
		this.lastAcceptIndex = lastAcceptIndex;
	}

	public int getFirstUnchosenIndex() {
		return firstUnchosenIndex;
	}

	public void setFirstUnchosenIndex(int firstUnchosenIndex) {
		this.firstUnchosenIndex = firstUnchosenIndex;
	}

	public ProposalNumber getMinProposal() {
		return minProposal;
	}

	public void setMinProposal(ProposalNumber minProposal) {
		this.minProposal = minProposal;
	}

	public Set<Integer> getChosenCsn() {
		return chosenCsn;
	}

	public void setChosenCsn(Set<Integer> chosenCsn) {
		this.chosenCsn = chosenCsn;
	}

	public Set<Integer> getLoggedCsn() {
		return loggedCsn;
	}

	public void setLoggedCsn(Set<Integer> loggedCsn) {
		this.loggedCsn = loggedCsn;
	}

	public Map<Integer, ProposalNumber> getAcceptedProposal() {
		return acceptedProposal;
	}

	public void setAcceptedProposal(Map<Integer, ProposalNumber> acceptedProposal) {
		this.acceptedProposal = acceptedProposal;
	}

	public Map<Integer, Value> getAcceptedValue() {
		return acceptedValue;
	}

	public void setAcceptedValue(Map<Integer, Value> acceptedValue) {
		this.acceptedValue = acceptedValue;
	}

	@Override
	public String toString() {
		return "AcceptorPersistenceBean [lastAcceptIndex=" + lastAcceptIndex + ", firstUnchosenIndex="
				+ firstUnchosenIndex + ", minProposal=" + minProposal + ", chosenCsn=" + chosenCsn + ", loggedCsn="
				+ loggedCsn + ", acceptedProposal=" + acceptedProposal + ", acceptedValue=" + acceptedValue + "]";
	}
}
