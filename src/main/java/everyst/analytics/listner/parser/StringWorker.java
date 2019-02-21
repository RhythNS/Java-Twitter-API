package everyst.analytics.listner.parser;

import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import everyst.analytics.listner.App;
import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.dataManagement.queueWriter.StringWriter;
import everyst.analytics.listner.dataManagement.queueWriter.Type;
import everyst.analytics.listner.twitter.events.Event;
import everyst.analytics.listner.utility.QueueWorker;

public class StringWorker extends QueueWorker<Entry<String, String>> {

	private BlockingQueue<Event> eventQueue;

	public StringWorker(BlockingQueue<Entry<String, String>> msqQueue, BlockingQueue<Event> eventQueue, App app,
			StringWriter writer, long delay) {
		super(msqQueue, app, writer, delay);
		this.eventQueue = eventQueue;
	}

	@Override
	protected void process(Entry<String, String> json) {
		// Parse the json to an event
		Event parsedEvent = EventParser.parse(json.getValue());
		
		// if the event is unknown write it to file to inspect it later
		if (parsedEvent == null)
			writeToFile(json, Type.ERROR);
		else {// if the event is known try to enqueue it to the eventQueue

			try {
				// if the eventQueue is already full, save the json to file
				if (!eventQueue.offer(parsedEvent, 2, TimeUnit.SECONDS)) {
					Logger.getInstance().log("EventQueue is full!");
					writeToFile(json, Type.FULL);
				}

			} catch (InterruptedException e) {
				writeToFile(json, Type.EXIT);
			}

		}
	}

	@Override
	protected void writeToFile(Entry<String, String> x, Type type) {
		writer.write(x.getValue(), type);
	}

}
