package everyst.analytics.listner;

import java.util.Arrays;

import everyst.analytics.listner.dataManagement.Logger;

public class Launcher {

	public static void main(String[] args) {
		boolean createTables = false;
		boolean makeSecure = true;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("ct"))
				createTables = true;
			else if (args[i].equalsIgnoreCase("ns"))
				makeSecure = false;
		}
		try {
			new App(createTables, makeSecure);
		} catch (Throwable t) {
			Logger.getInstance().log("App crashed: " + Arrays.toString(t.getStackTrace()));
		}

	}

}
