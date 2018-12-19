import cool.naiding.easyPaxos.network.Client;
import cool.naiding.easyPaxos.network.ClientTCPImpl;

public class NetworkClientTest {

    public static void main(String[] args) {
        Client client = new ClientTCPImpl();
        client.sendTo("localhost", 33333, new byte[]{'h', 'i'});
        client.sendTo("localhost", 33333, new byte[]{'g', 'u', 'y'});
    }
}
