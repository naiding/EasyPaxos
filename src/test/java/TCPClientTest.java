import com.naidingz.easyPaxos.network.Client;
import com.naidingz.easyPaxos.network.ClientTCPImpl;

public class TCPClientTest {

    public static void main(String[] args) {
        Client client = new ClientTCPImpl();
        client.sendTo("localhost", 33333, new byte[]{'h', 'i'});
        client.sendTo("localhost", 33333, new byte[]{'g', 'u', 'y'});
    }
}
