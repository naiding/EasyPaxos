package cool.naiding.easyPaxos.message;

import java.io.Serializable;

public class ClientRequestMessage extends ClientMessage implements Serializable {

	private static final long serialVersionUID = 2647520936885742568L;

	private Value value;

	public ClientRequestMessage(String host, int port, Value value) {
		super(host, port);
		this.value = value;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "ClientRequestMessage [value=" + value + ", toString()=" + super.toString() + "]";
	}
}
