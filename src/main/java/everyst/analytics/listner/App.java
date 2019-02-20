package everyst.analytics.listner;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.dataManagement.queueWriter.FileConstants;
import everyst.analytics.listner.dataManagement.queueWriter.FileManager;
import everyst.analytics.listner.dataManagement.queueWriter.StringWriter;
import everyst.analytics.listner.twitter.events.Event;
import everyst.analytics.listner.userInterface.UserInterface;
import everyst.analytics.listner.webhook.Webhook;
import everyst.analytics.parser.EventWorker;
import everyst.analytics.parser.StringWorker;

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
		
		keyManager = new KeyManager();

		if (!keyManager.readKeys(FileConstants.KEY_FILE)) {
			Logger.getInstance().log("Could not read keys! Exiting...");
			System.exit(0);
		}

		
		BlockingQueue<String> stringQueue = new LinkedBlockingQueue<>();
		BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();
		
		fileManager = new FileManager(FileConstants.QUEUE_WRITER_FILE);
		
		writer = new StringWriter(fileManager);
		
		webhook = new Webhook(stringQueue, keyManager, this);
		try {
			webhook.start(keyManager.getKeyStorePassword());
		} catch (IOException e) {
			Logger.getInstance().handleError(e);
			System.exit(0);
		}
		
		stringWorker = new StringWorker(stringQueue, eventQueue, this, writer, 10);
		eventWorker = new EventWorker(eventQueue, this, writer, 10);
		
		stringWorker.start();
		eventWorker.start();
		
		ui = new UserInterface(this);
		new Thread(ui).start();
		
		System.out.println("started!");
	}

	public KeyManager getKeyManager() {
		return keyManager;
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
	
	public static void main(String[] args) {
		new App();
	}

}
