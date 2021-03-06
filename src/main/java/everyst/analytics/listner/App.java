package everyst.analytics.listner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
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
import everyst.analytics.listner.userInterface.Commands;
import everyst.analytics.listner.userInterface.UserInterface;
import everyst.analytics.listner.webhook.Webhook;
import everyst.analytics.mysql.MySQLConnection;
import everyst.analytics.tasks.Task;
import everyst.analytics.tasks.TaskManager;
import everyst.analytics.tasks.runnables.DailyFollower;
import everyst.analytics.tasks.runnables.EventWorkerCrashChecker;
import everyst.analytics.tasks.runnables.StringWorkerCrashChecker;
import everyst.analytics.tasks.runnables.twitterFollowerTracker.Reciever;
import everyst.analytics.tasks.schedulers.IntervalScheduler;
import everyst.analytics.tasks.schedulers.OnceADayScheduler;
import everyst.analytics.telegram.TeleBot;
import everyst.analytics.webInterface.SimpleNumberOutput;

public class App {

	private UserInterface ui;
	private Commands commands;

	private Webhook webhook;
	private KeyManager keyManager;

	private StringWorker stringWorker;
	private StringWriter writer;
	private EventWorker eventWorker;
	private StringReader reader;

	private FileManager fileManager;
	private MySQLConnection database;

	private TaskManager taskManager;

	private TeleBot teleBot;

	private boolean exitRequested = false;
	public static final boolean DEBUG = false;
	public static final boolean SERVER_PROTOCOL_DEBUG = false;

	public App(boolean createTables, boolean makeSecure) {
		if (DEBUG)
			Logger.getInstance().log("WARNING: SERVER IS RUNNING IN DEBUG MODE!");

		// Init the keys
		keyManager = new KeyManager();
		if (!keyManager.readKeys(FileConstants.KEY_FILE)) {
			Logger.getInstance().log("Could not read keys!");
			throw new IllegalStateException("Could not read keys!");
		}

		// Init the database
		try {
			database = new MySQLConnection(keyManager.getDatabaseName(), keyManager.getDatabaseUser(),
					keyManager.getDatabasePassword());
		} catch (SQLException e1) {
			Logger.getInstance().log("Could not connect to the database!");
			Logger.getInstance().handleError(e1);
			throw new IllegalStateException("Could not connect to the database!");
		}

		if (createTables) // if the user wants to create the tables in the database
			if (!InitDatabase.init(database)) { // could not create the tables
				Logger.getInstance().handleError(new IllegalStateException("Database Tables could not be initilized"));
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
			webhook.start(keyManager.getKeyStorePassword(), makeSecure && !DEBUG);
		} catch (IOException e) {
			Logger.getInstance().handleError(e);
			throw new IllegalStateException("Could not initilize the webhook!");
		}

		// Add listners to the webhook
		webhook.addListner(new SimpleNumberOutput(database));

		// Init the workers
		stringWorker = new StringWorker(stringQueue, eventQueue, this, writer, 10);
		eventWorker = new EventWorker(eventQueue, this, database, writer, 10);
		stringWorker.start();
		eventWorker.start();

		// Init task manager
		taskManager = new TaskManager(this);

		// add string crash checker which checks every minute
		taskManager.addTask(new Task("StringWorkerCrashChecker",
				new StringWorkerCrashChecker(this, stringWorker, stringQueue, eventQueue, writer, 10),
				new IntervalScheduler(0, 0, 1, 0)));

		// add event crash checker which checks every minute
		taskManager.addTask(new Task("EventWorkerCrashChecker",
				new EventWorkerCrashChecker(this, eventWorker, eventQueue, database, writer, 10),
				new IntervalScheduler(0, 0, 1, 0)));

		try {
			// add DailyFollower
			taskManager.addTask(new Task("DailyFollower", new DailyFollower(FileConstants.DAILY_FOLLOWER_FILE),
					new OnceADayScheduler(1, 0, 0, 1)));

			// add Follower Tracker
			taskManager.addTask(new Task("FollowerTracker",
					new Reciever(3500, FileConstants.FOLLOWER_TRACKER_FILE, FileConstants.FOLLOWER_TRACKER_ACCOUNTS),
					new OnceADayScheduler(1, 0, 0, 1)));
		} catch (NumberFormatException | JSONException | IOException e) {
			Logger.getInstance().handleError(e);
		}

		taskManager.start();

		// init commands
		commands = new Commands(database, this);

		// init Telegram Communication
		try {
			teleBot = TeleBot.init(keyManager.getTelegramUsername(), keyManager.getTelegramToken(),
					keyManager.getTelegramTrustedIds(), commands);
		} catch (TelegramApiRequestException e) {
			Logger.getInstance().handleError(e);
		}

		Logger.getInstance().setBot(teleBot);

		// Init the ui
		ui = new UserInterface(this);
		new Thread(ui).start();

		// Everything started, we should be good to go
		Logger.getInstance().log("App started!");
	}

	public void readStrings(Type type) {
		reader.addAllStrings(type, 0);
	}

	public void setStringWorker(StringWorker stringWorker) {
		this.stringWorker = stringWorker;
	}

	public void setEventWorker(EventWorker eventWorker) {
		this.eventWorker = eventWorker;
	}

	public TaskManager getTaskManager() {
		return taskManager;
	}

	public boolean isEventWorkerAlive() {
		return eventWorker.isAlive();
	}

	public boolean isStringWorkerAlive() {
		return stringWorker.isAlive();
	}

	/**
	 * Stops all running Threads and tries to shutdown the server
	 */
	public void stop() {
		exitRequested = true;
		webhook.stop();
		stringWorker.interrupt();
		eventWorker.interrupt();
		teleBot.stop();
	}

	public boolean isExitRequested() {
		return exitRequested;
	}

}
