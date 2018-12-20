package cool.naiding.easyPaxos.message;

import java.io.Serializable;

public class Value implements Serializable {
	
	private static final long serialVersionUID = 6040450825051815784L;
	
	private int csn;
	
	private String data;

	public Value(int csn, String data) {
		super();
		this.csn = csn;
		this.data = data;
	}
	
	public int getCsn() {
		return csn;
	}

	public void setCsn(int csn) {
		this.csn = csn;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Value [csn=" + csn + ", data=" + data + "]";
	}
}
