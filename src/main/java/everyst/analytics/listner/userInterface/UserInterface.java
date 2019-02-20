package everyst.analytics.listner.userInterface;

import java.util.Scanner;

import everyst.analytics.listner.App;

public class UserInterface implements Runnable {

	private Scanner scan;
	private App app;
	
	public UserInterface(App app) {
		this.app = app;
		scan = new Scanner(System.in);
	}

	@Override
	public void run() {
		while (app.isExitRequested()) {
			String request = scan.nextLine();
			switch (request) {
			case "exit":
				app.closeProgram();
				break;
			default:
				System.err.println(request + " is not a valid command!");
			}
		}
		scan.close();
	}

}
