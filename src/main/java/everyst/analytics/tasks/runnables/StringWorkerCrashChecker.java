package everyst.analytics.tasks.runnables;

import java.util.concurrent.BlockingQueue;

import everyst.analytics.listner.App;
import everyst.analytics.listner.dataManagement.queueWriter.StringWriter;
import everyst.analytics.listner.parser.StringWorker;
import everyst.analytics.listner.twitter.events.Event;
import everyst.analytics.listner.utility.QueueWorker;

public class StringWorkerCrashChecker extends WorkerCrashChecker<String> {

	private BlockingQueue<Event> eventQueue;
	private StringWriter writer;
	private long delay;

	public StringWorkerCrashChecker(App app, QueueWorker<String> worker, BlockingQueue<String> queue,
			BlockingQueue<Event> eventQueue, StringWriter writer, long delay) {
		super(app, worker, queue);
		this.eventQueue = eventQueue;
		this.writer = writer;
		this.delay = delay;
	}

	@Override
	protected QueueWorker<String> createQueueWorker() {
		return new StringWorker(queue, eventQueue, app, writer, delay);
	}

	@Override
	protected void setWorkerInApp(App app) {
		app.setStringWorker((StringWorker) worker);

	}

}
