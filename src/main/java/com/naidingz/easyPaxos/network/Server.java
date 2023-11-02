package com.naidingz.easyPaxos.network;

public interface Server {
    /**
     * This is the communication server for a replica
     * it will keep receiving packet.
     * @return byte array
     * @throws InterruptedException
     */
    public byte[] receive();
}
