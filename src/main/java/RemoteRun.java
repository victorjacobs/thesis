import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFJob;
import org.jppf.server.protocol.JPPFTask;

import java.util.List;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class RemoteRun extends JPPFTask {
	private RemoteRun() {}

	public static void main(String[] args) {
		JPPFClient client = null;

		try {
			JPPFJob job = new JPPFJob();
			client = new JPPFClient();

			job.setName("Yes hello this is dog");
			job.addTask(new TestTask());
			job.setBlocking(true);

			List<JPPFTask> results = client.submit(job);

			for (JPPFTask task : results) {
				if (task.getException() != null) {
					System.out.println("Something went wrong with task " + task);
				} else {
					System.out.println(task.getResult());
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			client.close();
		}
	}

	@Override
	public void run() {
		try {
			Run.main(null);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
