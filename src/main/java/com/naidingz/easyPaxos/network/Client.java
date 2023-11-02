package com.naidingz.easyPaxos.network;

public interface Client {
    public void sendTo(String host, int port, byte[] msg);
    public void sendPacketTo(String host, int port, Packet packet);
}
