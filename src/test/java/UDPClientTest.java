import cool.naiding.easyPaxos.network.Client;
import cool.naiding.easyPaxos.network.ClientUDPImpl;

public class UDPClientTest {
    public static void main(String[] args) {
        Client client = new ClientUDPImpl();
        client.sendTo("localhost", 33333, new byte[]{'h', 'i'});
        client.sendTo("localhost", 33333, new byte[]{'g', 'o'});
    }
}