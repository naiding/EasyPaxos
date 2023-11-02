package com.naidingz.easyPaxos.message;

import java.io.Serializable;

public class ClientResponseMessage extends ClientMessage implements Serializable {
	
	private static final long serialVersionUID = -2787264546753360043L;
	
	private int csn;
	private int code;
	private String status;
	
	public ClientResponseMessage(String host, int port, int csn, int code, String status) {
		super(host, port);
		this.csn = csn;
		this.code = code;
		this.status = status;
	}

	public int getCsn() {
		return csn;
	}

	public void setCsn(int csn) {
		this.csn = csn;
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ClientResponseMessage [csn=" + csn + ", code=" + code + ", status=" + status + ", toString()="
				+ super.toString() + "]";
	}
}