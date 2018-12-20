import java.io.IOException;

import cool.naiding.easyPaxos.member.Replica;

public class ServerApp {

	public static void main( String[] args ) throws IOException
    {
		int replicaNum = 5;
		Thread[] threads = new Thread[replicaNum];
		for (int i = 0; i < threads.length; i++) {
			int id = i;
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					Replica replica = new Replica("./config/replica-" + id + ".json");
					replica.start();
				}
			});
		}
		
		for (int i = 0; i < threads.length; i++) {
			System.out.println("Replica Thread " + i + " run!");
			threads[i].run();
		}
    }
}