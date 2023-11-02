package com.naidingz.easyPaxos.message;

import java.io.Serializable;

public class HeartBeatMessage extends ReplicaMessage implements Serializable {
	
	private static final long serialVersionUID = -6240485479447125861L;

	private int view;
	
	public HeartBeatMessage(int id, int view) {
		super(id);
		this.view = view;
	}

	public int getView() {
		return view;
	}

	public void setView(int view) {
		this.view = view;
	}

	@Override
	public String toString() {
		return "HeartBeatMessage [view=" + view + ", toString()=" + super.toString() + "]";
	}
}
