package cool.naiding.easyPaxos.member;

public class ProposerPersistenceBean {
	
	private int maxRound;

	public ProposerPersistenceBean() {
		super();
	}

	public ProposerPersistenceBean(int maxRound) {
		super();
		this.maxRound = maxRound;
	}

	public int getMaxRound() {
		return maxRound;
	}

	public void setMaxRound(int maxRound) {
		this.maxRound = maxRound;
	}

	@Override
	public String toString() {
		return "ProposerPersistenceBean [maxRound=" + maxRound + "]";
	}
}
