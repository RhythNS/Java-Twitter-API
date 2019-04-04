package everyst.analytics.listner.parser;

import java.util.concurrent.BlockingQueue;

import everyst.analytics.listner.App;
import everyst.analytics.listner.dataManagement.queueWriter.StringWriter;
import everyst.analytics.listner.dataManagement.queueWriter.Type;
import everyst.analytics.listner.twitter.events.Event;

public class StringToFileWorker extends StringWorker{

	public StringToFileWorker(BlockingQueue<String> stringQueue, BlockingQueue<Event> eventQueue, App app,
			StringWriter writer, long delay) {
		super(stringQueue, eventQueue, app, writer, delay);
	}
	
	@Override
	protected void process(String json) {
		writeToFile(json, Type.NOT_CONNECTED);
	}

}
