package everyst.analytics.listner.userInterface;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import com.github.scribejava.core.exceptions.OAuthException;

import everyst.analytics.listner.App;
import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.dataManagement.queueWriter.Type;
import everyst.analytics.twitter0Auth.AddSubscription;

public class UserInterface implements Runnable {

	private Scanner scan;
	private App app;

	public UserInterface(App app) {
		this.app = app;
		scan = new Scanner(System.in);
	}

	@Override
	public void run() {
		while (!app.isExitRequested()) {
			try {
				System.out.println(
						"---Input Command---\nread - For reading all JSONs in storage\nrun - run a task in the task manager\nadd - Add a user to the webhook\nexit - Exit the program and save all not processed JSONs");
				String request = scan.nextLine().toLowerCase();
				switch (request) {
				case "exit":
					app.stop();
					break;

				case "read":
					System.out.print("What do you want to read? Options are: "
							+ Arrays.toString(Type.values()).replaceAll("_", ""));
					request = scan.nextLine();

					boolean found = false;
					for (int i = 0; i < Type.values().length; i++) {
						if (Type.values()[i].getPath().equalsIgnoreCase(request)) {
							app.readStrings(Type.values()[i]);
							found = true;
							break;
						}
					}
					if (!found)
						System.out.println("Value not found!");
					break;

				case "run":
					System.out.println("Which task to execute? " + app.getTaskManager().getTaskNames());
					app.getTaskManager().forceExecuteTask(scan.nextLine());
					break;

				case "add":
					try {
						AddSubscription.main();
					} catch (IOException | InterruptedException | ExecutionException | OAuthException e) {
						e.printStackTrace();
					}
					break;
				default:
					System.err.println(request + " is not a valid command!");
				}
			} catch (Exception e) {
				Logger.getInstance().handleError(e);
			}
		}
		scan.close();
	}

}
