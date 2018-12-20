package cool.naiding.easyPaxos.member;

import java.util.ArrayList;
import java.util.List;

public class UserConfig {
	
	private int id;
	private String host;
	private int port;
	private long timeout;
	private String logFilename;
	private List<ReplicaNode> servers = new ArrayList<>();
	
	public UserConfig() {
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

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public String getLogFilename() {
		return logFilename;
	}

	public void setLogFilename(String logFilename) {
		this.logFilename = logFilename;
	}

	public List<ReplicaNode> getServers() {
		return servers;
	}

	public void setServers(List<ReplicaNode> servers) {
		this.servers = servers;
	}

	@Override
	public String toString() {
		return "UserConfig [id=" + id + ", host=" + host + ", port=" + port + ", timeout=" + timeout + ", logFilename="
				+ logFilename + ", servers=" + servers + "]";
	}	
}
