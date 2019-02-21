package everyst.analytics.listner.parser;

import java.util.concurrent.BlockingQueue;

import everyst.analytics.listner.App;
import everyst.analytics.listner.dataManagement.queueWriter.StringWriter;
import everyst.analytics.listner.dataManagement.queueWriter.Type;
import everyst.analytics.listner.twitter.events.Event;
import everyst.analytics.listner.utility.QueueWorker;

public class EventWorker extends QueueWorker<Event> {

	public EventWorker(BlockingQueue<Event> msqQueue, App app, StringWriter writer, long delay) {
		super(msqQueue, app, writer, delay);
	}

	@Override
	protected void process(Event x) {
		writeToFile(x, Type.DEBUG);
	}

	@Override
	protected void writeToFile(Event x, Type type) {
		writer.write(x.getData(), type);
	}

}
