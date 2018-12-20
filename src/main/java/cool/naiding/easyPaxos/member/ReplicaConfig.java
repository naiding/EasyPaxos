package cool.naiding.easyPaxos.member;

import java.util.ArrayList;
import java.util.List;

public class ReplicaConfig {
	
	private int id;
	private String host;
	private int port;
	private int prepareQuorumSize;
	private int acceptQuorumSize;
	private long timeout;
	private String proposerPersistenceFilename;
	private String acceptorPersistenceFilename;
	private String debugFilename;
	private String logFilename;
	private String logHashFilename;
	private boolean allowPersistence;
	private List<ReplicaNode> nodes = new ArrayList<>();
	
	public ReplicaConfig() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public List<ReplicaNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<ReplicaNode> nodes) {
		this.nodes = nodes;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public String getProposerPersistenceFilename() {
		return proposerPersistenceFilename;
	}

	public void setProposerPersistenceFilename(String proposerPersistenceFilename) {
		this.proposerPersistenceFilename = proposerPersistenceFilename;
	}

	public String getAcceptorPersistenceFilename() {
		return acceptorPersistenceFilename;
	}

	public void setAcceptorPersistenceFilename(String acceptorPersistenceFilename) {
		this.acceptorPersistenceFilename = acceptorPersistenceFilename;
	}

	public boolean isAllowPersistence() {
		return allowPersistence;
	}

	public void setAllowPersistence(boolean allowPersistence) {
		this.allowPersistence = allowPersistence;
	}
	
	public String getLogFilename() {
		return logFilename;
	}

	public void setLogFilename(String logFilename) {
		this.logFilename = logFilename;
	}

	public String getDebugFilename() {
		return debugFilename;
	}

	public void setDebugFilename(String debugFilename) {
		this.debugFilename = debugFilename;
	}
	
	public int getPrepareQuorumSize() {
		return prepareQuorumSize;
	}

	public void setPrepareQuorumSize(int prepareQuorumSize) {
		this.prepareQuorumSize = prepareQuorumSize;
	}

	public int getAcceptQuorumSize() {
		return acceptQuorumSize;
	}

	public void setAcceptQuorumSize(int acceptQuorumSize) {
		this.acceptQuorumSize = acceptQuorumSize;
	}

	public String getLogHashFilename() {
		return logHashFilename;
	}

	public void setLogHashFilename(String logHashFilename) {
		this.logHashFilename = logHashFilename;
	}

	@Override
	public String toString() {
		return "ReplicaConfig [id=" + id + ", host=" + host + ", port=" + port + ", prepareQuorumSize=" + prepareQuorumSize + ", acceptQuorumSize="
				+ acceptQuorumSize + ", timeout=" + timeout + ", proposerPersistenceFilename="
				+ proposerPersistenceFilename + ", acceptorPersistenceFilename=" + acceptorPersistenceFilename
				+ ", debugFilename=" + debugFilename + ", logFilename=" + logFilename + ", logHashFilename="
				+ logHashFilename + ", allowPersistence=" + allowPersistence + ", nodes=" + nodes + "]";
	}
}
