package everyst.analytics.listner;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.dataManagement.queueWriter.FileConstants;
import everyst.analytics.listner.dataManagement.queueWriter.FileManager;
import everyst.analytics.listner.dataManagement.queueWriter.StringWriter;
import everyst.analytics.listner.parser.EventWorker;
import everyst.analytics.listner.parser.StringWorker;
import everyst.analytics.listner.twitter.events.Event;
import everyst.analytics.listner.userInterface.UserInterface;
import everyst.analytics.listner.webhook.Webhook;

public class App {

	private UserInterface ui;

	private Webhook webhook;
	private StringWorker stringWorker;
	private EventWorker eventWorker;

	// data
	private KeyManager keyManager;
	private StringWriter writer;
	private FileManager fileManager;

	private boolean exitRequested = false;
	public static final boolean DEBUG = true;
	public static final boolean SERVER_PROTOCOL_DEBUG = false;

	public App() {
		Logger.getInstance().log("now starting...");

		// Init the keys
		keyManager = new KeyManager();
		if (!keyManager.readKeys(FileConstants.KEY_FILE)) {
			Logger.getInstance().log("Could not read keys! Exiting...");
			System.exit(0);
		}

		// init the queues
		BlockingQueue<Entry<String, String>> stringQueue = new LinkedBlockingQueue<>();
		BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();
		
		// init the file stuff
		fileManager = new FileManager(FileConstants.QUEUE_WRITER_FILE);
		writer = new StringWriter(fileManager);

		// Init the webhook
		webhook = new Webhook(stringQueue, keyManager);
		try {
			webhook.start(keyManager.getKeyStorePassword());
		} catch (IOException e) {
			Logger.getInstance().handleError(e);
			System.exit(0);
		}

		// Init the workers
		stringWorker = new StringWorker(stringQueue, eventQueue, this, writer, 10);
		eventWorker = new EventWorker(eventQueue, this, writer, 10);
		stringWorker.start();
		eventWorker.start();

		// Init the ui
		ui = new UserInterface(this);
		new Thread(ui).start();

		System.out.println("started!");
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
