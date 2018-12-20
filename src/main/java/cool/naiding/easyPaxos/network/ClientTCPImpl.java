package cool.naiding.easyPaxos.network;

import cool.naiding.easyPaxos.util.Serializer;

import java.io.IOException;
import java.net.Socket;

public class ClientTCPImpl implements Client {
    @Override
    public void sendTo(String host, int port, byte[] msg) {
        try {
            Socket socket = new Socket(host, port);
            //socket.setSoTimeout(3000);
            socket.getOutputStream().write(msg);
            socket.getOutputStream().close();
            socket.close();
        } catch (IOException e) {
			System.err.println("Connection refused to " + host + ":" + port);
        }
    }

    @Override
    public void sendPacketTo(String host, int port, Packet packet) {
        sendTo(host, port, Serializer.serialize(packet));
    }
}