package cool.naiding.easyPaxos.message;

import java.io.Serializable;

public abstract class ClientMessage implements Serializable {
	
	private static final long serialVersionUID = 4077812095770904710L;
	
	private String host;
	
	private int port;

	public ClientMessage(String host, int port) {
		super();
		this.host = host;
		this.port = port;
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

	@Override
	public String toString() {
		return "ClientMessage [host=" + host + ", port=" + port + "]";
	}
}
