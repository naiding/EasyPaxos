import cool.naiding.easyPaxos.network.Server;
import cool.naiding.easyPaxos.network.ServerTCPImpl;

public class NetworkServerTest {
    public static void main(String[] args) {
        Server server = new ServerTCPImpl(33333);
        while (true) {
            byte[] msg = server.receive();
            System.out.print("Received message: ");
            for (byte b : msg) {
                System.out.print((char)b + " ");
            }
            System.out.println();
        }
    }
}
