package everyst.analytics.tasks.runnables;

import java.util.concurrent.BlockingQueue;

import everyst.analytics.listner.App;
import everyst.analytics.listner.dataManagement.queueWriter.StringWriter;
import everyst.analytics.listner.parser.EventWorker;
import everyst.analytics.listner.twitter.events.Event;
import everyst.analytics.listner.utility.QueueWorker;
import everyst.analytics.mysql.MySQLConnection;

public class EventWorkerCrashChecker extends WorkerCrashChecker<Event> {

	private MySQLConnection database;
	private StringWriter writer;
	private long delay;

	public EventWorkerCrashChecker(App app, QueueWorker<Event> worker, BlockingQueue<Event> queue,
			MySQLConnection database, StringWriter writer, long delay) {
		super(app, worker, queue);
		this.database = database;
		this.writer = writer;
		this.delay = delay;
	}

	@Override
	protected QueueWorker<Event> createQueueWorker() {
		return new EventWorker(queue, app, database, writer, delay);
	}

	@Override
	protected void setWorkerInApp(App app) {
		app.setEventWorker((EventWorker) worker);
	}

}
