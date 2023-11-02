package com.naidingz.easyPaxos.message;

import java.io.Serializable;

public class SuccessMessage extends ReplicaMessage implements Serializable {
	
	private static final long serialVersionUID = 2611550425028903940L;
	
	private int index;
	private Value value;
	
	public SuccessMessage(int senderId, int index, Value value) {
		super(senderId);
		this.index = index;
		this.value = value;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "SuccessMessage [index=" + index + ", value=" + value + ", toString()=" + super.toString() + "]";
	}
}
