import java.io.IOException;

import cool.naiding.easyPaxos.member.User;

public class UserApp {

	public static void main( String[] args ) throws IOException
    {
		
		int userNum = 3;
		Thread[] threads = new Thread[userNum];
		for (int i = 0; i < threads.length; i++) {
			int id = i;
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					User user = new User("./config/user-" + id + ".json");
//					user.start();
					user.startBatch((id + 13) * 1131, (id + 1) * 1000, (id + 1) * 1000 + 300);
				}
			});
		}
		
		for (int i = 0; i < threads.length; i++) {
			System.out.println("User Thread " + i + " run!");
			threads[i].run();
		}
    }
}