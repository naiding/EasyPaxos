package cool.naiding.easyPaxos.network;

public interface Client {
    public void sendTo(String host, int port, byte[] msg);
}
