package cool.naiding.easyPaxos.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerUDPImpl implements Server {

    private DatagramSocket ds;
    private BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();

    public ServerUDPImpl(int port) {
        try {
            ds = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        byte[] receive = new byte[65535];
        new Thread(() -> {
            DatagramPacket DpReceive = null;
            while (true) {
                try {
                    DpReceive = new DatagramPacket(receive, receive.length);
                    try {
                        ds.receive(DpReceive);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.queue.put(receive);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public byte[] receive() {
        byte[] msg = null;
        try {
            msg = this.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return msg;
    }
}
