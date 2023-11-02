package com.naidingz.easyPaxos.network;

import com.naidingz.easyPaxos.util.Serializer;

import java.net.*;

public class ClientUDPImpl implements Client {
    @Override
    public void sendTo(String host, int port, byte[] msg) {
        try {
            DatagramSocket ds = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(msg, msg.length, InetAddress.getByName(host), port);
            ds.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPacketTo(String host, int port, Packet packet) {
        sendTo(host, port, Serializer.serialize(packet));
    }
}
