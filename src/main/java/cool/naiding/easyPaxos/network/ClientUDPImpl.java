package cool.naiding.easyPaxos.network;

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
}
