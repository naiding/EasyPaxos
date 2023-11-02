package com.naidingz.easyPaxos.network;

import java.io.Serializable;

public class PacketContent implements Serializable {
	
	private static final long serialVersionUID = -1657951636027642125L;

	private PacketContentTypeEnum type;
	
	private Object message;

	public PacketContent(PacketContentTypeEnum type, Object message) {
		super();
		this.type = type;
		this.message = message;
	}

	public PacketContentTypeEnum getType() {
		return type;
	}

	public void setType(PacketContentTypeEnum type) {
		this.type = type;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "PacketContent [type=" + type + ", message=" + message + "]";
	}
}