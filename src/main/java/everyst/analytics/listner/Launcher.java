package everyst.analytics.listner;

public class Launcher {

	public static void main(String[] args) {
		boolean createTables = false;
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("ct"))
				createTables = true;
		}

		new App(createTables);
	}

}
