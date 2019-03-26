package everyst.analytics.listner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.dataManagement.queueWriter.FileConstants;
import everyst.analytics.listner.dataManagement.queueWriter.FileManager;
import everyst.analytics.listner.dataManagement.queueWriter.StringReader;
import everyst.analytics.listner.dataManagement.queueWriter.StringWriter;
import everyst.analytics.listner.dataManagement.queueWriter.Type;
import everyst.analytics.listner.parser.EventWorker;
import everyst.analytics.listner.parser.StringWorker;
import everyst.analytics.listner.twitter.database.InitDatabase;
import everyst.analytics.listner.twitter.events.Event;
import everyst.analytics.listner.userInterface.UserInterface;
import everyst.analytics.listner.webhook.Webhook;
import everyst.analytics.mysql.MySQLConnection;
import everyst.analytics.webInterface.SimpleNumberOutput;

public class App {

	private UserInterface ui;

	private Webhook webhook;
	private StringWorker stringWorker;
	private EventWorker eventWorker;

	// data
	private KeyManager keyManager;
	private StringWriter writer;
	private StringReader reader;
	private FileManager fileManager;

	private MySQLConnection database;

	private boolean exitRequested = false;
	public static final boolean DEBUG = false;
	public static final boolean SERVER_PROTOCOL_DEBUG = false;

	public App(boolean createTables) {
		Logger.getInstance().log("now starting...");

		if (DEBUG)
			Logger.getInstance().log("WARNING: SERVER IS RUNNING IN DEBUG MODE!");

		// Init the keys
		keyManager = new KeyManager();
		if (!keyManager.readKeys(FileConstants.KEY_FILE)) {
			Logger.getInstance().log("Could not read keys! Exiting...");
			System.exit(0);
		}

		// Init the database
		try {
			database = new MySQLConnection(keyManager.getDatabaseName(), keyManager.getDatabaseUser(),
					keyManager.getDatabasePassword());
		} catch (SQLException e1) {
			Logger.getInstance().log("Could not connect to the database!");
			Logger.getInstance().handleError(e1);
			System.exit(0);
		}

		if (createTables) // if the user wants to create the tables in the database
			if (!InitDatabase.init(database)) { // could not create the tables
				System.exit(0);
			}

		// init the queues
		BlockingQueue<String> stringQueue = new LinkedBlockingQueue<>();
		BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();

		// init the file stuff
		fileManager = new FileManager(FileConstants.QUEUE_WRITER_FILE);
		writer = new StringWriter(fileManager);
		reader = new StringReader(fileManager, writer, stringQueue);

		// Init the webhook
		webhook = new Webhook(stringQueue, keyManager);
		try {
			webhook.start(keyManager.getKeyStorePassword());
		} catch (IOException e) {
			Logger.getInstance().handleError(e);
			System.exit(0);
		}

		// Add listners to the webhook
		webhook.addListner(new SimpleNumberOutput(database));

		// Init the workers
		stringWorker = new StringWorker(stringQueue, eventQueue, this, writer, 10);
		eventWorker = new EventWorker(eventQueue, this, database, writer, 10);
		stringWorker.start();
		eventWorker.start();

		System.out.println("started!");

		// Init the ui
		ui = new UserInterface(this);
		new Thread(ui).start();
	}

	public void readStrings(Type type) {
		reader.addAllStrings(Type.ALL, 0);
	}

	/**
	 * Stops all running Threads and tries to shutdown the server
	 */
	public void closeProgram() {
		exitRequested = true;
		webhook.stop();
		stringWorker.interrupt();
		eventWorker.interrupt();
	}

	public boolean isExitRequested() {
		return exitRequested;
	}

}
