package cool.naiding.easyPaxos.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerTCPImpl implements Server {
    private ServerSocket server;
    private int port;
    private BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
    private ExecutorService pool = Executors.newCachedThreadPool();

    public ServerTCPImpl(int port) {
        super();
        this.port = port;
        try {
            server = new ServerSocket(this.port, 128);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            while (true) {
                try {
                    Socket client = server.accept();
                    pool.execute(new ReadThread(client, queue));
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

    class ReadThread implements Runnable {
        private Socket client;
        private BlockingQueue<byte[]> queue;

        public ReadThread(Socket client, BlockingQueue<byte[]> queue) {
            super();
            this.client = client;
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                InputStream inputStream = this.client.getInputStream();
                byte[] buf = new byte[4096];
                int n;
                while ((n = inputStream.read(buf)) >= 0) {
                    stream.write(buf, 0, n);
                }
                this.queue.put(stream.toByteArray());
                inputStream.close();
                this.client.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
